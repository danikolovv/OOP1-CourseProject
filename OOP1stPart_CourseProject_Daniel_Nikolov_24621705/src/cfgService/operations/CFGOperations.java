package cfgService.operations;

import cfgService.objects.Grammar;
import cfgService.objects.collections.GrammarManager;
import cfgService.parsers.CFGParser;
import commandOutputs.loggers.Logger;
import commandOutputs.userOutput.CFGOperationsOutput;

public class CFGOperations {

    // list
    public static void list() {
        // outputs list with the identifiers of all read grammars
        CFGOperationsOutput.listOutput();
    }

    // print <id>
    public static void print(int id) {
        // prints all rules in a grammar
        GrammarManager grammarManager = GrammarManager.getInstance();
        Grammar grammar = grammarManager.getGrammar(id);

        CFGOperationsOutput.printOutput(grammar);
    }

    // save <id> <filename>
    public static void save(int id, String filename) {
        CFGFileOperations.save(id, filename);   // saves a grammar into file

    }

    // addRule <id> <rule>
    public static void addRule(int id, String... tokens) {
        CFGParser.parseRuleInput(id, tokens);    // add new rule to a grammar (identified by id) ->
                                                  // creates all non-terminal and terminal object instances and writes it into the Grammar
    }

    // removeRule <id> <n>
    public static void removeRule(int id, int n) {
        // removes a rule from a grammar by a concrete number
        CFGParser.parseRemoveRule(id, n);

    }

    // union <id1> <id2>
    public static void union(int id1, int id2) {
        CFGParser.union(id1, id2);
    }

    // concat <id1> <id2>
    public static void concat(int id1, int id2) {
        CFGParser.concat(id1, id2);
    }

    // chomsky <id>
    public static void chomsky(int id) {
        boolean isCNF = CFGParser.chomsky(id);
        if (isCNF) {
            CFGOperationsOutput.chomsky(id);
        } else {
            CFGOperationsOutput.nonChomskyForm(id);
        }
    }

    // cyk <id>
    public static void cyk(int id, String input) {
        CFGParser.cyk(id, input);
    }

    // iter <id>
    public static void iter(int id) {
        CFGParser.iter(id);
    }

    // empty <id>
    public static void empty(int id) {
        CFGParser.empty(id);
    }

    // chomskify <id>
    public static void chomskify(int id) {
        int newGrammarId = CFGParser.chomskify(id);

        CFGOperationsOutput.chomskifiedGrammar(newGrammarId);
    }

}
