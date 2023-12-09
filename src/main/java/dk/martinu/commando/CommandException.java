package dk.martinu.commando;

import org.jetbrains.annotations.NotNull;

public class CommandException extends Exception {

    public CommandException(@NotNull String message) {
        super(message);
    }

    public CommandException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}