package injection;

import org.junit.BeforeClass;
import org.junit.Test;
import training.SomeClass;
import training.TestIDClass;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class DependenciesTest {
    private static int threadCount = 3;
    private static final ExecutorService workers1 = Executors.newFixedThreadPool(threadCount);
    private static final ExecutorService workers2 = Executors.newFixedThreadPool(threadCount);
    private static final ExecutorService workers3 = Executors.newFixedThreadPool(threadCount);

    static Collection<Callable<TestIDClass>> tasks = new ArrayList<>();
    static Collection<Callable<ConnectionI>> cleanupTasks = new ArrayList<>();
    static Collection<String> ids = new ArrayList<>(Arrays.asList("id1", "id2", "id3"));

    @BeforeClass
    public static void setup() {
        ids.stream().forEach(id -> {
            tasks.add(new Callable<TestIDClass>() {
                @Override
                public TestIDClass call() throws Exception {
                    final SomeClass someClass = new SomeClass(id);
                    final String theOtherThingID = someClass.makeAnotherThing();
                    return new TestIDClass(someClass.connection.getConnectionID(), theOtherThingID);
                }
            });
        });

        ids.stream().forEach(id -> {
            cleanupTasks.add(new Callable<ConnectionI>() {
                @Override
                public ConnectionI call() throws Exception {
                    final SomeClass someClass = new SomeClass(id);
                    someClass.doTheThing();
                    return someClass.connection.test();
                }
            });
        });
    }

    @Test
    public void gets_the_same_connection_for_the_same_thread() throws InterruptedException {
        final List<Future<TestIDClass>> results = workers1.invokeAll(tasks);

        results.stream().forEach(r -> {
            try {
                final TestIDClass ids = r.get();
                assertEquals(ids.id1, ids.id2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void gets_a_different_connection_for_each_thread() throws InterruptedException {
        final List<Future<TestIDClass>> results = workers2.invokeAll(tasks);

        final Set<String> uniqueThings = results.stream().map(r -> {
            try {
                return r.get().id1;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toSet());

        assertEquals(threadCount, uniqueThings.size());
        assertFalse(uniqueThings.contains(null));
    }

    @Test
    public void releases_connection_when_thread_is_finished_with_work() throws InterruptedException {
        final List<Future<ConnectionI>> results = workers3.invokeAll(cleanupTasks);

        final Set<ConnectionI> remainingConnections = results.stream().map(r -> {
            try {
                return r.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toSet());

        assertEquals(1, remainingConnections.size());
        assertTrue(remainingConnections.contains(null));
    }
}