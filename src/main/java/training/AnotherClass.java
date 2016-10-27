package training;

import injection.Connection;
import injection.ConnectionI;
import injection.Dependencies;

public class AnotherClass {
    final String something = "another class";
    final ConnectionI connection;

    public AnotherClass() {
        connection = Dependencies.connectionPool.make(something).getConnection();
    }
    public void doSomethingElse() {
        connection.say();
    }

    public void doSomethingDifferent() {
        final Thread someOtherThread = new Thread() {
            @Override
            public void run() {
                final Connection connection = Dependencies.connectionPool.make(something).getConnection();
                try {
                    connection.say();
                } finally {
                    connection.releaseConnection();
                }
            }
        };
        someOtherThread.start();
    }
}
