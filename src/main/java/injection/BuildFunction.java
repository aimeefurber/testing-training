package injection;

public interface BuildFunction<T> {
    static <S> BuildFunction singletonBuilder(Class<? extends S> clz) {
        return new BuildFunction() {
            private S value = null;

            @Override
            public synchronized S build() {
                if (value != null) return value;
                try {
                    value = clz.newInstance();
                    return value;
                } catch (Exception e) {
                    System.err.println("FACTORY FAILED! Make sure you have a no-arg constructor for class " + clz.getName());
                    return null;
                }
            }
        };
    }

    T build();
}
