package dk.martinu.commando;

import org.jetbrains.annotations.*;

import java.util.*;

public class Parameters {

    public static final Parameters EMPTY_PARAMETERS = new Parameters();

    @Contract(value = "!null -> new", pure = true)
    @NotNull
    public static Parameters from(@Nullable String args) throws OptionException {
        if (args == null) {
            return EMPTY_PARAMETERS;
        }
        else {
            return new Parameters(args);
        }
    }

    @Unmodifiable
    @NotNull
    public final List<Option> options;
    @NotNull
    public final String args;

    public Parameters(@Nullable String args) throws OptionException {
        if (args == null) {
            args = "";
        }
        Options options = getOptions(args);
        this.options = List.copyOf(options.list());
        this.args = options.isEmpty() ? args : args.substring(options.endIndex());
    }

    private Parameters() {
        //noinspection unchecked
        this.options = Collections.EMPTY_LIST;
        this.args = "";
    }

    @Contract(value = "_ -> new", pure = true)
    @NotNull
    private Options getOptions(@NotNull String args) throws OptionException {
        List<Option> opList = new ArrayList<>();
        int i = 0;
        int len = args.length();
        while (i < len) {
            char c = args.charAt(i);
            if (c == ' ') {
                i++;
            }
            else if (c == '-') {
                i++;
                String opName;
                String opValue = null;
                int index = args.indexOf(':', i);
                if (index != -1) {
                    opName = args.substring(i + 1, index);
                    if (Util.isNameInvalid(opName)) {
                        throw new OptionException("invalid option name at index " + i + " {" + opName + "}");
                    }
                    i += opName.length() + 1;
                    c = args.charAt(i);
                    if (c == '"') {
                        while ((index = args.indexOf('"', i + 1)) != -1) {
                            if (args.charAt(index - 1) != '\\') {
                                break;
                            }
                        }
                        if (index == -1) {
                            throw new OptionException("invalid option value at index " + i + " {" + opName + "}");
                        }
                        opValue = args.substring(i + 1, index);
                        i += opValue.length() + 2;
                    }
                    else {
                        index = args.indexOf(' ', i);
                        opValue = index != -1 ? args.substring(i, index) : args.substring(i);
                        if (opValue.isEmpty()) {
                            throw new OptionException("missing option value at index " + i + " {" + opName + "}");
                        }
                        i += opValue.length();
                    }
                }
                else {
                    index = args.indexOf(' ', i);
                    opName = index != -1 ? args.substring(i, index) : args.substring(i);
                    if (Util.isNameInvalid(opName)) {
                        throw new OptionException("invalid option name at index " + i + " {" + opName + "}");
                    }
                    i += opName.length();
                }

                opList.add(new Option(opName, opValue));
            }
            else {
                break;
            }
        }
        return new Options(opList, i);
    }

    private record Options(@NotNull List<Option> list, int endIndex) {

        @Contract(pure = true)
        public boolean isEmpty() { return list.isEmpty(); }
    }
}
