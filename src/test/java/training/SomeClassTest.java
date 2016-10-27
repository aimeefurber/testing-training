package training;

import org.junit.Test;

import static org.junit.Assert.fail;

public class SomeClassTest {

    @Test
    public void does_something_anything_at_all() {
        final SomeClass someClass = new SomeClass("main thread");
        someClass.doTheThing();
    }

    @Test
    public void does_different_things(){
        final SomeClass someClass = new SomeClass("main thread");
        someClass.doAnotherThing();
    }
}