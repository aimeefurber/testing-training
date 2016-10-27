package injection;

public class Dependencies {
    public static final InstanceFactoryThreadLocal<ConnectionI, String> connectionPool
            = new InstanceFactoryThreadLocal<>((arg, arg2) -> new Connection(arg, arg2));
}