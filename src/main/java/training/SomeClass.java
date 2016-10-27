package training;

import injection.ConnectionI;
import injection.Dependencies;

public class SomeClass {
    final String something;
    public final ConnectionI connection;

    public SomeClass(String s) {
        this.something = s;
        this.connection = Dependencies.connectionPool.make(something).getConnection();
    }

    public void doTheThing() {
        try {
            connection.say();
            final AnotherClass anotherClass = new AnotherClass();
            anotherClass.doSomethingElse();
        } finally {
            done();
        }
    }

    public void doAnotherThing() {
        try {
            connection.say();
            final AnotherClass anotherClass = new AnotherClass();
            anotherClass.doSomethingDifferent();
        } finally {
            done();
        }
    }

    public void done() {
        connection.releaseConnection();
    }

    public String makeAnotherThing() {
        connection.say();
        final AnotherClass anotherClass = new AnotherClass();
        return anotherClass.connection.getConnectionID();
    }
}