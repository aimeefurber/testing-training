package injection;

public interface ConnectionI {
    ConnectionI test(); //don't love this... not sure how else to test

    Connection getConnection();

    void releaseConnection();

    String getConnectionID();

    void say();
}
