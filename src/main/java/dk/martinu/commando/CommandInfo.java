package dk.martinu.commando;

import org.jetbrains.annotations.*;

import java.lang.ref.WeakReference;
import java.lang.reflect.*;
import java.util.*;

public class CommandInfo {

    @NotNull
    public final Class<? extends Command> cls;
    @Unmodifiable
    @NotNull
    public final Set<String> aliases;
    @Unmodifiable
    @NotNull
    public final Set<OptionInfo> options;
    @NotNull
    public final ArgsInfo argsInfo;
    private WeakReference<Command> ref = new WeakReference<>(null);

    public CommandInfo(@NotNull Class<? extends Command> cls, @NotNull String... aliases) {
        this(cls, aliases, new OptionInfo[0], ArgsInfo.NONE);
    }

    // FIXME aliases starting/ending with whitespace can never be called
    public CommandInfo(@NotNull Class<? extends Command> cls, @NotNull String[] aliases,
            @NotNull OptionInfo[] options, @NotNull ArgsInfo argsInfo) {
        this.cls = Objects.requireNonNull(cls, "cls is null");

        Objects.requireNonNull(aliases, "aliases array is null");
        if (aliases.length == 0) {
            throw new IllegalArgumentException("aliases array is empty");
        }
        try {
            this.aliases = OrderedSet.of(aliases);
        }
        catch (NullPointerException e) {
            throw new NullPointerException("aliases array contains null elements");
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("duplicate alias", e);
        }
        for (String alias : this.aliases) {
            if (Util.isNameInvalid(alias)) {
                throw new IllegalArgumentException("alias is invalid {" + alias + "}");
            }
        }

        Objects.requireNonNull(options, "options array is null");
        try {
            this.options = OrderedSet.of(options);
        }
        catch (NullPointerException e) {
            throw new NullPointerException("options array contains null elements");
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("duplicate option", e);
        }

        this.argsInfo = Objects.requireNonNull(argsInfo, "argsInfo is null");
    }

    private CommandInfo(@NotNull Class<? extends Command> cls, @NotNull Set<String> aliases,
            @NotNull Set<OptionInfo> options, @NotNull ArgsInfo argsInfo) {
        this.cls = cls;
        this.aliases = aliases;
        this.options = options;
        this.argsInfo = argsInfo;
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        else if (obj instanceof CommandInfo info) {
            return cls.equals(info.cls)
                    && aliases.equals(info.aliases)
                    && options.equals(info.options)
                    && argsInfo == info.argsInfo;
        }
        else {
            return false;
        }
    }

    @Contract(pure = true)
    @NotNull
    public String getName() {
        //noinspection OptionalGetWithoutIsPresent
        return aliases.stream().findFirst().get().toLowerCase(Locale.ROOT);
    }

    public void resolveOptions(@NotNull Collection<Option> options) throws CommandException, OptionException {
        Objects.requireNonNull(options, "options is null");
        for (Option option : options) {
            // optional containing option info if present
            Optional<OptionInfo> optional = this.options.stream()
                    .filter(op -> op.name().equalsIgnoreCase(option.name))
                    .findAny();
            // resolve option value so it is ready for command execution
            if (optional.isPresent()) {
                OptionInfo opInfo = optional.get();
                option.resolve(opInfo.type(), opInfo.required());
            }
            else {
                throw new CommandException("command does not have option " + option.name);
            }
        }
    }

    @NotNull
    Command getCommand() throws CommandException {
        // return cached instance if possible
        Command cmd = ref.get();
        if (cmd != null) {
            return cmd;
        }

        // else create new instance
        try {
            if (StaticCommand.class.isAssignableFrom(cls)) {
                Method m = cls.getMethod("getInstance");
                if (!Modifier.isStatic(m.getModifiers())) {
                    throw new CommandException("missing static instance method for " + cls);
                }
                cmd = (StaticCommand) m.invoke(null);
            }
            else {
                //noinspection unchecked
                Constructor<Command> c = (Constructor<Command>) cls.getConstructor();
                cmd = c.newInstance();
            }
        }
        catch (ReflectiveOperationException | SecurityException e) {
            throw new CommandException("failed to create command instance", e);
        }

        // cache instance if possible
        if (!cmd.isVolatile()) {
            ref.clear();
            ref = new WeakReference<>(cmd);
        }

        return cmd;
    }

    public static final class Builder {

        private Class<? extends Command> cls;
        @Unmodifiable
        private Set<String> aliases;
        @Unmodifiable
        private Set<OptionInfo> options = OrderedSet.of(new OptionInfo[0]);
        private ArgsInfo argsInfo = ArgsInfo.NONE;

        @Contract(value = "-> new", pure = true)
        @NotNull
        public CommandInfo get() {
            return new CommandInfo(cls, aliases, options, argsInfo);
        }

        @Contract(value = "_ -> this")
        @NotNull
        public Builder setAliases(@NotNull String... aliases) {
            Objects.requireNonNull(aliases, "aliases array is null");
            if (aliases.length == 0) {
                throw new IllegalArgumentException("aliases array is empty");
            }
            try {
                this.aliases = OrderedSet.of(aliases);
            }
            catch (NullPointerException e) {
                throw new NullPointerException("aliases array contains null elements");
            }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("duplicate alias", e);
            }
            for (String alias : this.aliases) {
                if (Util.isNameInvalid(alias)) {
                    throw new IllegalArgumentException("alias is invalid {" + alias + "}");
                }
            }
            return this;
        }

        @Contract(value = "_ -> this")
        @NotNull
        public Builder setArgsInfo(@NotNull ArgsInfo argsInfo) {
            Objects.requireNonNull(argsInfo, "argsInfo is null");
            this.argsInfo = argsInfo;
            return this;
        }

        @Contract(value = "_ -> this")
        @NotNull
        public Builder setCls(@NotNull Class<? extends Command> cls) {
            this.cls = Objects.requireNonNull(cls, "cls is null");
            return this;
        }

        @Contract(value = "_ -> this")
        @NotNull
        public Builder setOptions(@NotNull OptionInfo... options) {
            Objects.requireNonNull(options, "options array is null");
            try {
                this.options = OrderedSet.of(options);
            }
            catch (NullPointerException e) {
                throw new NullPointerException("options array contains null elements");
            }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("duplicate option", e);
            }
            return this;
        }
    }
}
