package commandOutputs.userOutput;

import cfgService.objects.Grammar;
import cfgService.objects.RightHandSide;
import cfgService.objects.collections.GrammarManager;
import commandOutputs.loggers.MathematicalLogger;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class CFGOperationsOutput {
    // user informative output (UI)

    /**
     * <span>
     * Iterates over the stored map entries, sorts them numerically by key, and prints all active grammar identifiers to the console.
     * </span>
     * <br><br>
     * Params: (void)
     * @return (void)
     */
    public static void listOutput() {
        GrammarManager grammarManager = GrammarManager.getInstance();

        System.out.println("All currently read grammars / <id>: ");
        grammarManager.getAllGrammars().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .forEach(grammar ->
                        System.out.printf("\t %d%n", grammar.getKey())
                );
        System.out.println();
    }

    /**
     * <span>
     * Displays a granular, structured breakdown of a specific grammar including its unique non-terminals, terminals, and numbered production rules.
     * </span>
     * <br><br>
     * Params: Grammar grammar
     * @return (void)
     */
    public static void printOutput(Grammar grammar) {
        System.out.printf("Grammar with id (%d): \n\t Non-terminals: ", grammar.getId());
        grammar.getNonTerminals().forEach(nonTerminal ->
                System.out.printf("%c, ", nonTerminal.getSymbol())
        );

        System.out.print("\n\t Terminals: ");
        grammar.getTerminals().forEach(terminal ->
                System.out.printf("%c, ", terminal.getTerminalSymbol())
        );

        System.out.println("\n\t Rules: ");
        grammar.getRules().forEach(rule ->
                System.out.printf("\t\t\t" + "№" + rule.getConsecutiveNumber() + " " +  rule.getNonTerminal().getSymbol()
                + " -> "
                        + rule.getRightSide().stream()
                        .map(RightHandSide::getSymbol)
                        .map(String::valueOf)
                        .collect(Collectors.joining(""))
                + "\n"
        ));

        if (grammar.getRules().isEmpty())
            MathematicalLogger.emptySet();

        System.out.println();
    }

    /**
     * <span>
     * Prints a feedback confirmation message containing the auto-allocated integer identifier for a newly combined language union grammar.
     * </span>
     * <br><br>
     * Params: int id
     * @return (void)
     */
    public static void unionFeedback(int id) {
        System.out.printf("The identifier of the newly created Grammar (after union) is: %d%n%n", id);
    }

    /**
     * <span>
     * Prints a feedback confirmation message containing the auto-allocated integer identifier for a newly combined language concatenation grammar.
     * </span>
     * <br><br>
     * Params: int id
     * @return (void)
     */
    public static void concatFeedback(int id) {
        System.out.printf("The identifier of the newly created Grammar (after concatenation) is: %d%n%n", id);
    }

    /**
     * <span>
     * Outputs a successful verification alert indicating that the evaluated grammar matches all criteria for Chomsky Normal Form.
     * </span>
     * <br><br>
     * Params: int id
     * @return (void)
     */
    public static void chomsky(int id) {
        System.out.printf("The Grammar №%d is in Chomsky Normal Form.%n%n", id);
    }

    /**
     * <span>
     * Outputs a non-compliance error warning indicating that the evaluated grammar violates the standard syntax rules of Chomsky Normal Form.
     * </span>
     * <br><br>
     * Params: int id
     * @return (void)
     */
    public static void nonChomskyForm(int id) {
        System.out.printf("Grammar №%d DOES NOT cover the Chomsky Normal Form criteria.%n%n", id);
    }

    /**
     * <span>
     * Prints a successful status notification showing the system runtime identifier assigned to a newly normalized context-free grammar model.
     * </span>
     * <br><br>
     * Params: int id
     * @return (void)
     */
    public static void chomskifiedGrammar(int id) {
        System.out.printf("%nThe identifier of the newly created Grammar that has been transferred in CNF: %d%n%n", id);
    }

}
