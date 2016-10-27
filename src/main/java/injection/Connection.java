package injection;

public class Connection implements ConnectionI {
    public final String someArg;
    final ThreadLocal threadLocal;

    public Connection(String someArg, ThreadLocal threadLocal) {
        this.someArg = someArg;
        this.threadLocal = threadLocal;
    }

    public void say() {
        System.out.println("This connection says: " + someArg);
    }

    @Override //don't love this... not sure how else to test
    public ConnectionI test(){
        return (Connection) threadLocal.get();
    }


    @Override
    public Connection getConnection() {
        return this;
    }

    @Override
    public void releaseConnection() {
        threadLocal.remove();
//        System.out.println("releasing connection for: " + someArg);
    }

    @Override
    public String getConnectionID() {
       return someArg;
    }
}
