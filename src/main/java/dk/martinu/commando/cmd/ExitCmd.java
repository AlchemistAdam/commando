package dk.martinu.commando.cmd;

import dk.martinu.commando.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ExitCmd implements StaticCommand {

    private static volatile ExitCmd instance = null;

    @Contract(value = "-> new", pure = true)
    @NotNull
    public static CommandInfo getInfo() {
        // TODO create info
        return null;
    }

    @NotNull
    public static ExitCmd getInstance() {
        if (instance == null) {
            synchronized (ExitCmd.class) {
                if (instance == null) {
                    instance = new ExitCmd();
                }
            }
        }
        return instance;
    }

    @Override
    public void execute(@NotNull AbstractCliEngine engine, @NotNull Parameters parameters) {
        engine.stopEngine();
    }
}
