package dk.martinu.commando;

public interface StaticCommand extends Command {

    @Override
    default boolean isVolatile() {
        return false;
    }
}
