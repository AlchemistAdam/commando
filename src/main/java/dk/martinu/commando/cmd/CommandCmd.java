package dk.martinu.commando.cmd;

import dk.martinu.commando.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class CommandCmd implements StaticCommand {

    private static volatile CommandCmd instance = null;

    @Contract(value = "-> new", pure = true)
    @NotNull
    public static CommandInfo getInfo() {
        return new CommandInfo.Builder()
                .setCls(CommandCmd.class)
                .setAliases("command", "cmd")
                .setArgsInfo(ArgsInfo.REQUIRED)
                .get();
    }

    @NotNull
    public static CommandCmd getInstance() {
        if (instance == null) {
            synchronized (CommandCmd.class) {
                if (instance == null) {
                    instance = new CommandCmd();
                }
            }
        }
        return instance;
    }

    @Override
    public void execute(@NotNull AbstractCliEngine engine, @NotNull Parameters parameters) throws CommandException {
        if (parameters.args.isEmpty()) {
            throw new CommandException("no command specified");
        }
        CommandInfo commandInfo = engine.getCommand(parameters.args);
        if (commandInfo == null) {
            throw new CommandException("command \"" + parameters.args + "\" not found");
        }
        StringBuilder sb = new StringBuilder(128);

        // class info
        sb.append(commandInfo.cls.getName());
        sb.append(" (static=").append(StaticCommand.class.isAssignableFrom(commandInfo.cls)).append(")\n");

        // alias
        {
            Iterator<String> iterator = commandInfo.aliases.iterator();
            sb.append(iterator.next());
            if (iterator.hasNext()) {
                sb.append(" (\"").append(iterator.next()).append('\"');
                while (iterator.hasNext()) {
                    sb.append(", \"").append(iterator.next()).append('\"');
                }
                sb.append(')');
            }
            sb.append('\n');
        }

        // option info
        for (OptionInfo optionInfo : commandInfo.options) {
            sb.append('-').append(optionInfo.name());

            if (!optionInfo.type().equals(Option.Type.NONE)) {
                if (optionInfo.required()) {
                    sb.append(':').append(optionInfo.type()).append('\n');
                }
                else {
                    sb.append("[:").append(optionInfo.type()).append("]\n");
                }
            }
        }

        // args info
        sb.append("args: ").append(commandInfo.argsInfo);

        engine.println(sb.toString());
    }
}
