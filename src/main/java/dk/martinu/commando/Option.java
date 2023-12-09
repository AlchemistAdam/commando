package dk.martinu.commando;

import org.jetbrains.annotations.*;

import java.util.Locale;
import java.util.Objects;

public final class Option {

    @NotNull
    public final String name;
    @Nullable
    private final String value;
    @Nullable
    private Object resolvedValue = null;

    public Option(@NotNull String name, @Nullable String value) {
        this.name = Objects.requireNonNull(name, "name is null");
        this.value = value;
    }

    @Contract(pure = true)
    @Nullable
    public Object getResolvedValue() {
        return resolvedValue;
    }

    void resolve(@NotNull Type type, boolean required) throws OptionException {
        // value is not required or optional, and must be null
        if (type == Type.NONE) {
            if (value == null) {
                resolvedValue = null;
                return;
            }
            else {
                throw new OptionException("option " + name + " does not accept a value");
            }
        }

        // value is null and must not be required
        if (value == null) {
            if (!required) {
                resolvedValue = null;
                return;
            }
            else {
                throw new OptionException("option " + name + " requires a value of type " + type);
            }
        }

        // value is present and must be resolved
        switch (type) {
            case STRING -> resolvedValue = String.copyValueOf(Util.unescape(value.toCharArray()));

            case BOOLEAN -> {
                if (value.equalsIgnoreCase("true")) {
                    resolvedValue = Boolean.TRUE;
                }
                else if (value.equalsIgnoreCase("false")) {
                    resolvedValue = Boolean.FALSE;
                }
                else {
                    throw new OptionException("cannot resolve boolean value for option " + name + " {" + value + "}");
                }
            }

            case INT -> {
                try {
                    resolvedValue = Integer.valueOf(value);
                }
                catch (NumberFormatException e) {
                    throw new OptionException("cannot resolve int value for option " + name + " {" + value + "}");
                }
            }

            case FLOAT -> {
                try {
                    resolvedValue = Float.valueOf(value);
                }
                catch (NumberFormatException e) {
                    throw new OptionException("cannot resolve float value for option " + name + " {" + value + "}");
                }
            }
        }
    }

    public enum Type {

        NONE,
        STRING,
        BOOLEAN,
        INT,
        FLOAT;

        @Contract(pure = true)
        @NotNull
        @Override
        public String toString() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
