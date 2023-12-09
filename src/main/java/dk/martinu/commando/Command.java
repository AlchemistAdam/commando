package dk.martinu.commando;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Command {

    default boolean isVolatile() {
        return true;
    }

    void execute(@NotNull AbstractCliEngine engine, @NotNull Parameters parameters) throws CommandException;
}
