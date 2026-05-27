package cfgService.operations;

import cfgService.objects.Grammar;
import cfgService.objects.collections.GrammarManager;
import cfgService.parsers.CFGParser;
import commandOutputs.userOutput.CFGOperationsOutput;

public class CFGOperations {

    /**
     * <span>
     * Demands the informative user interface subsystem to aggregate and print out all active grammar identity integers.
     * </span>
     * <br><br>
     * Params: (void)
     * @return (void)
     */
    // list
    public static void list() {
        // outputs list with the identifiers of all read grammars
        CFGOperationsOutput.listOutput();
    }

    /**
     * <span>
     * Retrieves a single grammar configuration container from memory storage arrays and passes its metadata out to be rendered in detail.
     * </span>
     * <br><br>
     * Params: int id
     * @return (void)
     */
    // print <id>
    public static void print(int id) {
        // prints all rules in a grammar
        GrammarManager grammarManager = GrammarManager.getInstance();
        Grammar grammar = grammarManager.getGrammar(id);

        CFGOperationsOutput.printOutput(grammar);
    }

    /**
     * <span>
     * Routes a single, specific isolated grammar identity out to specialized text files without affecting the active primary session context.
     * </span>
     * <br><br>
     * Params: int id, String filename
     * @return (void)
     */
    // save <id> <filename>
    public static void save(int id, String filename) {
        CFGFileOperations.save(id, filename);   // saves a grammar into file

    }

    /**
     * <span>
     * Passes a variable stream of string tokens to the core parser components to initialize object parsing and append a rule to a target grammar.
     * </span>
     * <br><br>
     * Params: int id, String... tokens
     * @return (void)
     */
    // addRule <id> <rule>
    public static void addRule(int id, String... tokens) {
        CFGParser.parseRuleInput(id, tokens);    // add new rule to a grammar (identified by id) ->
                                                  // creates all non-terminal and terminal object instances and writes it into the Grammar
    }

    /**
     * <span>
     * Submits a targeted grammar context integer alongside a line index selector to cleanly purge a precise production row out of application records.
     * </span>
     * <br><br>
     * Params: int id, int n
     * @return (void)
     */
    // removeRule <id> <n>
    public static void removeRule(int id, int n) {
        // removes a rule from a grammar by a concrete number
        CFGParser.parseRemoveRule(id, n);

    }

    /**
     * <span>
     * Calls upon structural parsing layers to execute an intersect evaluation and generate a new shared union system entity.
     * </span>
     * <br><br>
     * Params: int id1, int id2
     * @return (void)
     */
    // union <id1> <id2>
    public static void union(int id1, int id2) {
        CFGParser.union(id1, id2);
    }

    /**
     * <span>
     * Requests the combinatorial parsing layer to stitch two individual language alphabets and rule charts together into a fresh joint entity.
     * </span>
     * <br><br>
     * Params: int id1, int id2
     * @return (void)
     */
    // concat <id1> <id2>
    public static void concat(int id1, int id2) {
        CFGParser.concat(id1, id2);
    }

    /**
     * <span>
     * Initiates structural compliance tests on rules to discover if a target production map adheres to Chomsky Normal Form guidelines.
     * </span>
     * <br><br>
     * Params: int id
     * @return (void)
     */
    // chomsky <id>
    public static void chomsky(int id) {
        boolean isCNF = CFGParser.chomsky(id);
        if (isCNF) {
            CFGOperationsOutput.chomsky(id);
        } else {
            CFGOperationsOutput.nonChomskyForm(id);
        }
    }

    /**
     * <span>
     * Passes tracking values and string input arrays down to parsing tools to perform formal membership calculation checks.
     * </span>
     * <br><br>
     * Params: int id, String input
     * @return (void)
     */
    // cyk <id>
    public static void cyk(int id, String input) {
        CFGParser.cyk(id, input);
    }

    /**
     * <span>
     * Commands algorithmic layers to generate a brand new Kleene iteration closure wrap based upon an existing formal dictionary framework.
     * </span>
     * <br><br>
     * Params: int id
     * @return (void)
     */
    // iter <id>
    public static void iter(int id) {
        CFGParser.iter(id);
    }

    /**
     * <span>
     * Starts deep variable tracing sequences on syntax elements to check if a language layout resolves to an empty state.
     * </span>
     * <br><br>
     * Params: int id
     * @return (void)
     */
    // empty <id>
    public static void empty(int id) {
        CFGParser.empty(id);
    }

    /**
     * <span>
     * Triggers restructuring workflows that convert loose variable lists into standard binary formulas matching Chomsky criteria.
     * </span>
     * <br><br>
     * Params: int id
     * @return (void)
     */
    // chomskify <id>
    public static void chomskify(int id) {
        int newGrammarId = CFGParser.chomskify(id);

        CFGOperationsOutput.chomskifiedGrammar(newGrammarId);
    }

}
