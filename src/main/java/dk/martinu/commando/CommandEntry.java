package dk.martinu.commando;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record CommandEntry(String alias, CommandInfo commandInfo) implements Comparable<CommandEntry> {

    @Contract(pure = true)
    @Override
    public int compareTo(@NotNull CommandEntry commandEntry) {
        return commandInfo.compareTo(commandEntry.commandInfo);
    }
}
