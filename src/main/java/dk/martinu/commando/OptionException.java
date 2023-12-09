package dk.martinu.commando;

import org.jetbrains.annotations.NotNull;

public class OptionException extends Exception {

    public OptionException(@NotNull String message) {
        super(message);
    }
}
