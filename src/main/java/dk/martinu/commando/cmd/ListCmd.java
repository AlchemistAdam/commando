package dk.martinu.commando.cmd;

import dk.martinu.commando.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ListCmd implements StaticCommand {

    private static volatile ListCmd instance = null;

    @Contract(value = "-> new", pure = true)
    @NotNull
    public static CommandInfo getInfo() {
        // TODO add optionInfo
        return new CommandInfo(ListCmd.class, "list", "l");
    }

    @NotNull
    public static ListCmd getInstance() {
        if (instance == null) {
            synchronized (ListCmd.class) {
                if (instance == null) {
                    instance = new ListCmd();
                }
            }
        }
        return instance;
    }

    @Override
    public void execute(@NotNull AbstractCliEngine engine, @NotNull Parameters parameters) throws CommandException {
        // TODO list all commands from engine
        // TODO filter commands to start with params arg if present
    }
}
