package commandOutputs.loggers;

public class MathematicalLogger {

    /**
     * G = (V, T, P, S)
     * <br><br>
     * V (Variables or Non-terminals)
     * <br>
     * T (Terminals)
     * <br>
     * P (Productions or Rules)
     * <br>
     * S (Start Symbol) -> it does not contain any terminals, thus it is not operated (as a variable)
     * <br>
     */
    public static void emptySet() {
        System.out.println("This grammar does not contain rules, nor terminals/non-terminals\n\t=> Empty Set G(V,S,P,S) = {∅}");
    }
}
