package dk.martinu.commando;

import dk.martinu.commando.cmd.*;
import org.jetbrains.annotations.*;

import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractCliEngine extends Thread {

    private final Scanner scanner;
    private final HashMap<String, CommandInfo> cmdMap = new HashMap<>();
    private WeakReference<List<CommandInfo>> commands = new WeakReference<>(null);

    public AbstractCliEngine(@NotNull Scanner scanner, boolean daemon) {
        this.scanner = Objects.requireNonNull(scanner, "scanner is null");
        setDaemon(daemon);
        initCmdMap();
    }

    public void addCommand(@NotNull CommandInfo commandInfo) {
        Objects.requireNonNull(commandInfo, "commandInfo is null");
        synchronized (cmdMap) {
            commandInfo.aliases.forEach(alias -> {
                CommandInfo prev = cmdMap.put(alias, commandInfo);
                if (prev != null && !prev.cls.equals(commandInfo.cls)) {
                    // TODO log warning or throw when alias is replaced
                }
            });
            commands.clear();
        }
    }

    @NotNull
    public List<CommandInfo> getCommands() {
        synchronized (cmdMap) {
            List<CommandInfo> list = commands.get();
            if (list == null) {
                // TESTME with ListCmd
                //noinspection SimplifyStreamApiCallChains
                list = cmdMap.values().stream().sorted().collect(Collectors.toUnmodifiableList());
                commands = new WeakReference<>(list);
            }
            return list;
        }
    }

    public void printf(@NotNull String format, Object... args) {
        PrintWriter out = out();
        synchronized (out) {
            out().printf(format, args).println();
            out().flush();
        }
    }

    @Nullable
    public CommandInfo removeCommand(@NotNull String name) {
        Objects.requireNonNull(name, "name is null");
        synchronized (cmdMap) {
            CommandInfo rv = cmdMap.remove(name.toLowerCase(Locale.ROOT));
            commands.clear();
            return rv;
        }
    }

    @Override
    public void run() {
        CommandLine cmdl;
        while ((cmdl = getLine()) != null) {
            CommandInfo cmdInfo;
            synchronized (cmdMap) {
                cmdInfo = cmdMap.get(cmdl.name.toLowerCase(Locale.ROOT));
            }
            if (cmdInfo != null) {
                try {
                    Parameters p = Parameters.from(cmdl.args);
                    // FIXME value options lost first char
                    cmdInfo.resolveOptions(p.options);
                    cmdInfo.getCommand().execute(this, p);
                }
                catch (OptionException | CommandException e) {
                    // TODO check cause
                    printf("error: %s", e.getMessage());
                }
            }
            else {
                printf("unknown command '%s'", cmdl.name);
            }
            checkError();
        }
        freeResources();
    }

    public synchronized void stopEngine() {
        scanner.close();
    }

    protected abstract void freeResources();

    protected void initCmdMap() {
        addCommand(ListCmd.getInfo());
        addCommand(HelpCmd.getInfo());
        addCommand(ExitCmd.getInfo());
    }

    @NotNull
    protected abstract PrintWriter out();

    private void checkError() {
        if (scanner.ioException() != null) {
            System.err.println("terminating engine; scanner encountered an IOException");
            scanner.ioException().printStackTrace();
            stopEngine();
        }
        else if (out().checkError()) {
            System.err.println("terminating engine; output encountered an error");
            stopEngine();
        }
    }

    @Nullable
    private synchronized CommandLine getLine() {
        try {
            if (scanner.hasNextLine()) {
                return CommandLine.from(scanner.nextLine().trim());
            }
        }
        catch (IllegalStateException ignore) { }
        return null;
    }

    private static final class CommandLine {

        @Contract(value = "_ -> new", pure = true)
        static CommandLine from(@NotNull String line) {
            int index = line.indexOf(' ');
            if (index == -1) {
                return new CommandLine(line);
            }
            else {
                return new CommandLine(line.substring(0, index), line.substring(index + 1));
            }
        }

        @NotNull
        final String name;
        @Nullable
        final String args;

        public CommandLine(@NotNull String name) {
            this(name, null);
        }

        public CommandLine(@NotNull String name, @Nullable String args) {
            this.name = name;
            this.args = args;
        }
    }
}
