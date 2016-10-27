package injection;

public class InstanceFactoryThreadLocal<RT, A> {

    private final BuildFunction2Arg<RT, A, ThreadLocal<RT>> builder;
    final ThreadLocal<RT> threadConenction = new ThreadLocal<>();

    public InstanceFactoryThreadLocal(BuildFunction2Arg<RT, A, ThreadLocal<RT>> builder) {
        this.builder = builder;
    }

    public RT make(A arg) {
        if (threadConenction.get() == null)
            threadConenction.set(builder.build(arg, threadConenction));
        return threadConenction.get();
    }
}
