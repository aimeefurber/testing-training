package injection;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InstanceFactoryTest {

    Object lockItUp = new Object();

    class OneRunnable implements Runnable {
        final InstanceFactory<String> factory;
        final String overrideValue;
        String unicorn;

        OneRunnable(InstanceFactory<String> factory, String overrideValue) {
            this.factory = factory;
            this.overrideValue = overrideValue;
        }

        @Override
        public void run() {
            synchronized (this) { // 2 -- lock on this, make sure notifies will only happen while listening for one
                synchronized (lockItUp) { // 3 -- wait for lock before overriding
                    factory.override(() -> overrideValue);
                    lockItUp.notify(); // 4 -- notify finished, release lock
                }

                try {
                    wait(); // 6 -- release lock and wait until it can be re-obtained to continue
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                unicorn = factory.make();
            }
        }
    }

    class TwoRunnable implements Runnable {
        final InstanceFactory<String> factory;
        String unicorn;

        TwoRunnable(InstanceFactory<String> factory) {
            this.factory = factory;
        }

        @Override
        public void run() {
            unicorn = factory.make();
        }
    }

    @Test
    public void overrides_build_function_only_on_the_thread_that_requested_the_override() throws InterruptedException {
        final String unicorn = "unicorn";
        final String rainbows = "unicorn poop";
        final InstanceFactory<String> factory = new InstanceFactory<>(() -> unicorn);
        final OneRunnable one = new OneRunnable(factory, rainbows);
        final TwoRunnable two = new TwoRunnable(factory);
        final Thread thread1 = new Thread(one);
        final Thread thread2 = new Thread(two);

        synchronized (lockItUp) { // 1 --  get the lock, make sure notifies will only happen while listening for one
            thread1.start();
            lockItUp.wait(); // 3 -- release lock and wait until lock can be re-obtained before continuing

            // thread 1 has now overridden the unicorn factory with a unicorn poop factory -- makin' rainbows!

            synchronized (one) { // 6 -- wait for lock, before notifying thread1 that thread2 is done
                thread2.start(); // thread 2 should be makin' unicorns
                thread2.join(); // 5 -- wait until thread2 runs to completion

                one.notify(); // 7 -- notify thread1 (thread2 is finished), release lock
            }
        }
        thread1.join(); // 8 -- wait for thread1 to finish pooping rainbows

        assertEquals("Thread one should override with:", rainbows, one.unicorn);
        assertEquals("Thread two should retain factory value with:", unicorn, two.unicorn);
    }

    @Test
    public void overrides_the_override_build_function_on_a_particular_thread() throws InterruptedException {
        final String unicorn = "unicorn";
        final String rainbows = "unicorn poop";
        final String glitter = "glitter";
        final InstanceFactory<String> factory = new InstanceFactory<>(() -> unicorn);

        factory.override(() -> rainbows);
        final String firstUnicorn = factory.make();

        factory.override(() -> glitter);
        final String secondUnicorn = factory.make();

        assertEquals("The first override should be:", rainbows, firstUnicorn);
        assertEquals("The second override should be:", glitter, secondUnicorn);
    }

    @Test
    public void pops_to_the_previous_override_value_when_close_is_called() {
        final String unicorn = "unicorn";
        final String rainbows = "unicorn poop";
        final String glitter = "glitter";
        final InstanceFactory<String> factory = new InstanceFactory<>(() -> unicorn);

        factory.override(() -> rainbows);
        factory.override(() -> glitter);
        factory.close();

        final String firstUnicorn = factory.make();

        assertEquals("The override should go back to the first value:", rainbows, firstUnicorn);
    }

}