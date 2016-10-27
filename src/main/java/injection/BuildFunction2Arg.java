package injection;

public interface BuildFunction2Arg<RT, A1, A2> {
    RT build(A1 arg, A2 arg2);
}
