package dk.martinu.commando.cmd;

import dk.martinu.commando.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class HelpCmd implements StaticCommand {

    private static volatile HelpCmd instance = null;

    @Contract(value = "-> new", pure = true)
    @NotNull
    public static CommandInfo getInfo() {
        // TODO add optionInfo
        return new CommandInfo.Builder()
                .setCls(HelpCmd.class)
                .setAliases("help", "h")
                .setArgsInfo(ArgsInfo.OPTIONAL)
                .get();
    }

    @NotNull
    public static HelpCmd getInstance() {
        if (instance == null) {
            synchronized (HelpCmd.class) {
                if (instance == null) {
                    instance = new HelpCmd();
                }
            }
        }
        return instance;
    }

    @Override
    public void execute(@NotNull AbstractCliEngine engine, @NotNull Parameters parameters) throws CommandException {
        // TODO show usage of command specified by parameter
    }
}
