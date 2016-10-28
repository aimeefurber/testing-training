package injection;

public interface Resettable extends AutoCloseable {
    @Override
    public void close();
}
