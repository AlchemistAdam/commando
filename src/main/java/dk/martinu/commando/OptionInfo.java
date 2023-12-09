package dk.martinu.commando;

import org.jetbrains.annotations.*;

import java.util.Objects;

public record OptionInfo(@NotNull String name, @NotNull Option.Type type, boolean required, @Nullable String description) {

    public OptionInfo(@NotNull String name) {
        this(name, null);
    }

    public OptionInfo(@NotNull String name, @Nullable String description) {
        this(name, Option.Type.NONE, false);
    }

    public OptionInfo(@NotNull String name, @NotNull Option.Type type, boolean required) {
        this(name, type, required, null);
    }

    public OptionInfo(@NotNull String name, @NotNull Option.Type type, boolean required, @Nullable String description) {
        this.name = Objects.requireNonNull(name, "name is null");
        if (Util.isNameInvalid(name)) {
            throw new IllegalArgumentException("name is invalid {" + name + "}");
        }
        this.type = Objects.requireNonNull(type, "type is null");
        this.required = required;
        this.description = description != null ? description : "";
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(@Nullable Object o) {
        if (o == this) {
            return true;
        }
        else if (o instanceof OptionInfo f) {
            return f.name.equalsIgnoreCase(name);
        }
        else {
            return false;
        }
    }
}
