package cfgService.parsers;

import cfgService.objects.*;
import cfgService.objects.collections.GrammarManager;
import commandOutputs.loggers.Logger;
import commandOutputs.loggers.OperationFailedVars;
import commandOutputs.userOutput.CFGOperationsOutput;

import java.util.*;
import java.util.stream.Collectors;

public class CFGParser {

    public static void parseRuleInput(int id, String... tokens) {
        GrammarManager grammarManager = GrammarManager.getInstance();
        Grammar grammar = grammarManager.getGrammar(id);

        NonTerminal nonTerminal = new NonTerminal(tokens[0].charAt(0), grammar);

        Character[] symbols = Arrays.stream(tokens, 1, tokens.length)
                .flatMapToInt(CharSequence::chars)
                .mapToObj(c -> (char) c)
                .toArray(Character[]::new);

        List<RightHandSide> rightSideSymbols = new ArrayList<>();        // fix for multisymbol tokens

        for (Character token : symbols) {
            if (Character.isUpperCase(token)) {
                rightSideSymbols.add(new NonTerminal(token, grammar));
            } else {
                rightSideSymbols.add(new Terminal(token, grammar));
            }
        }

        new Rule(nonTerminal, rightSideSymbols, grammar);

        if (grammar.getRules().isEmpty()) {
            List<RightHandSide> rightSideEpsilon = new ArrayList<>();
            rightSideEpsilon.add(new Terminal(Grammar.EPSILON, grammar));

            new Rule(new NonTerminal(Grammar.START_NON_TERMINAL, grammar),
                    rightSideEpsilon, grammar);
        }

        checkIsEmptyRule(grammar);  // check if all the rules eventually lead only to terminals
    }

    // check if every NonTerminal on right contains terminals / non-terminals -> every non-terminal should lead to terminals, if not signal
    public static void checkIsEmptyRule(Grammar grammar) {
        Set<Character> productiveSymbols = new HashSet<>();     // set of non-terminals that have been proven to produce terminals
        boolean changed = true;

        // loop until no new productive symbols are discovered
        while (changed) {
            changed = false;

            for (Rule rule : grammar.getRules()) {
                char nonTerminal = rule.getNonTerminal().getSymbol();

                // this non-terminal contains only terminals -> it is being passed
                if (productiveSymbols.contains(nonTerminal))
                    continue;

                // check if every symbol on the right hand side does not enter into infinite loop
                boolean rightHandSide = true;
                for (RightHandSide rhs : rule.getRightSide()) {
                    char symbol = rhs.getSymbol();

                    // if symbol is non-terminal (uppercase) and NOT in the only terminals set
                    if (Character.isUpperCase(symbol) && !productiveSymbols.contains(symbol)) {
                        rightHandSide = false;
                        break;
                    }
                }

                // if the entire right side of a rule is constituted of terminals,
                // then the non-terminal that contains them is now valid
                if (rightHandSide) {
                    productiveSymbols.add(nonTerminal);
                    changed = true;
                }
            }
        }

        verifyAllProductive(grammar, productiveSymbols);
    }

    // collect every unique non-terminal defined in the grammar
    public static void verifyAllProductive(Grammar grammar, Set<Character> productiveSymbols) {
        Set<Character> allNonTerminals = new HashSet<>();
        for (Rule rule : grammar.getRules()) {
            allNonTerminals.add(rule.getNonTerminal().getSymbol());
        }

        System.out.printf("%nGrammar productivity check%n");
        System.out.println("---");

        boolean grammarValid = true;
        for (Character character : allNonTerminals) {
            if (productiveSymbols.contains(character)) {
                System.out.printf("%c: [Passed] -> Can derive terminals.%n", character);
            } else {
                System.out.printf("%c: [Failed] -> Unproductive.%n", character);
                grammarValid = false;

                // logic to find the specific "blocking" symbols
                // blocking symbols -> these which does not lead to set of terminals
                findBlockingSymbols(character, grammar, productiveSymbols);
            }
        }

        System.out.println("---");
        if (grammarValid) {
            System.out.printf("All non-terminals lead to terminals.%n%n");
        } else {
            System.out.printf("[Warning] -> Some symbols will never finish generating a string.%n%n");
        }
    }

    private static void findBlockingSymbols(Character character, Grammar grammar, Set<Character> productiveSymbols) {
        for (Rule rule : grammar.getRules()) {
            if (rule.getNonTerminal().getSymbol() == character) {
                System.out.printf("   Rule: %c -> ", character);

                List<Character> blockingNonTerminals = new ArrayList<>();
                for (RightHandSide rhs : rule.getRightSide()) {
                    System.out.print(rhs.getSymbol());

                    // if it's a non-terminal and not in the non-terminals leading to terminals set,
                    // it's a blocker -> not leading to a set of terminals
                    if (Character.isUpperCase(rhs.getSymbol()) && !productiveSymbols.contains(rhs.getSymbol())) {
                        blockingNonTerminals.add(rhs.getSymbol());
                    }
                }
                System.out.println(" | Blocked by: " + blockingNonTerminals);
            }
        }
    }




    public static void parseRemoveRule(int id, int displayIndex) {
        Grammar grammar = GrammarManager.getInstance().getGrammar(id);
        if (grammar == null) return;

        Set<Rule> ruleSet = grammar.getRules();
        List<Rule> ruleList = new ArrayList<>(ruleSet);

        // convert to collections indexing
        int internalIndex = displayIndex - 1;

        if (internalIndex >= 0 && internalIndex < ruleList.size()) {
            Rule toRemove = ruleList.get(internalIndex);    // identify the object and remove it from the set
            ruleSet.remove(toRemove);

            refreshGrammarSymbols(grammar);     // update grammar metadata
            grammar.updateRules();

            System.out.printf("Info (removeRule): Rule № %d removed.%n%n", displayIndex);
        } else {
            // index N does not exist -> signal error
            System.out.printf("%nInfo (removeRule): Error! -> Rule № %d not found. %n\t\t\t\t\t\t\tCurrent rule count: %d%n%n", displayIndex, ruleList.size());
        }
    }


    private static void refreshGrammarSymbols(Grammar grammar) {
        grammar.getTerminals().clear();
        grammar.getNonTerminals().clear();

        for (Rule rule : grammar.getRules()) {
            grammar.getNonTerminals().add(rule.getNonTerminal());

            for (RightHandSide rhs : rule.getRightSide()) {
                char s = rhs.getSymbol();

                if (Character.isLowerCase(s) || Character.isDigit(s))
                {
                    grammar.getTerminals().add((Terminal) rhs);
                } else
                {
                    grammar.getNonTerminals().add((NonTerminal) rhs); // If your RHS holds non-terminals too
                }
            }
        }
    }




    public static void union(int id1, int id2) {

        GrammarManager grammarManager = GrammarManager.getInstance();
        List<Map.Entry<Integer, Grammar>> grammars = grammarManager.getAllGrammars();
        Map.Entry<Integer, Grammar> lastGrammar = grammars.getLast();
        int id = lastGrammar.getValue().getId();
        int newIdentifier = ++id;

        new Grammar(newIdentifier, grammarManager);

        Grammar unionGrammar = grammarManager.getGrammar(newIdentifier);

        // add intersection of terminals
        // get a Set of all symbols from the second grammar for fast lookup
        Set<Character> terminalSymbolsId2 = grammarManager.getGrammar(id2).getTerminals().stream()
                .map(Terminal::getTerminalSymbol)
                .collect(Collectors.toSet());

        // filter the first set of objects by checking if their symbol is in the second set
        Set<Terminal> terminalSymbols = grammarManager.getGrammar(id1).getTerminals().stream()
                .filter(t -> terminalSymbolsId2.contains(t.getTerminalSymbol()))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        unionGrammar.addTerminals(terminalSymbols);

        // add intersection of non-terminals
        // get a Set of all symbols from the second grammar for fast lookup
        Set<Character> nonTerminalsId2 = grammarManager.getGrammar(id2).getNonTerminals().stream()
                .map(NonTerminal::getSymbol)
                .collect(Collectors.toSet());

        // filter the first set of objects by checking if their symbol is in the second set
        Set<NonTerminal> nonTerminalsSymbols = grammarManager.getGrammar(id1).getNonTerminals().stream()
                .filter(t -> nonTerminalsId2.contains(t.getSymbol()))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        unionGrammar.addNonTerminals(nonTerminalsSymbols);

        // add intersection of rules
        // compare intersected non-terminals, after that the intersected terminals if one of the rules in any of the two grammars are equal
        // -> return the rule and set it to the grammar
        Set<Rule> unionRules = getUnionRules(grammarManager, unionGrammar, id1, id2);

        if (!unionRules.isEmpty())
        {
            unionGrammar.addRules(unionRules);

            // reset indexing
            unionGrammar.updateRules();

            CFGOperationsOutput.unionFeedback(unionGrammar.getId());
        }
        else
        {
            Logger.unionOperationFailed();
        }
    }

    public static Set<Rule> getUnionRules(GrammarManager grammarManager, Grammar unionGrammar, int id1, int id2) {
        boolean isContained = false;
        Set<Rule> ruleUnion = new LinkedHashSet<>();

        for (Rule rule : grammarManager.getGrammar(id1).getRules()) {
            if (rule != null) {
                NonTerminal nonTerminal = rule.getNonTerminal();
                List<RightHandSide> terminals = rule.getRightSide();

                isContained = unionGrammar.getNonTerminals().stream()
                        .anyMatch(nonTerm -> nonTerm.equals(nonTerminal));

                if (terminals.stream().allMatch(terminal ->
                        unionGrammar.getTerminals().stream()
                                .anyMatch(terminalUnion ->
                                        terminal.getSymbol().equals(terminalUnion.getTerminalSymbol())
                                )) && isContained)
                {
                    ruleUnion.add(rule);
                    isContained = false;
                }
            }
        }

        isContained = false;
        for (Rule rule : grammarManager.getGrammar(id2).getRules()) {
            if (rule != null) {
                NonTerminal nonTerminal = rule.getNonTerminal();
                List<RightHandSide> terminals = rule.getRightSide();

                isContained = unionGrammar.getNonTerminals().stream()
                        .anyMatch(nonTerm -> nonTerm.equals(nonTerminal));

                if (terminals.stream().allMatch(terminal ->
                        unionGrammar.getTerminals().stream()
                                .anyMatch(terminalUnion ->
                                        terminal.getSymbol().equals(terminalUnion.getTerminalSymbol())
                                )) && isContained)
                {
                    ruleUnion.add(rule);
                    isContained = false;
                }
            }
        }

        return ruleUnion;
    }





    public static void concat(int id1, int id2) {

        GrammarManager grammarManager = GrammarManager.getInstance();
        List<Map.Entry<Integer, Grammar>> grammars = grammarManager.getAllGrammars();
        Map.Entry<Integer, Grammar> lastGrammar = grammars.getLast();
        int id = lastGrammar.getValue().getId();
        int newIdentifier = ++id;

        new Grammar(newIdentifier, grammarManager);

        Grammar concatGrammar = grammarManager.getGrammar(newIdentifier);

        concatGrammar.addRules(grammarManager.getGrammar(id1).getRules());
        concatGrammar.addRules(grammarManager.getGrammar(id2).getRules());

        // add intersection of terminals
        // get a Set of all symbols from the second grammar for fast lookup
        Set<Character> terminalSymbolsId2 = grammarManager.getGrammar(id2).getTerminals().stream()
                .map(Terminal::getTerminalSymbol)
                .collect(Collectors.toSet());

        // filter the first set of objects by checking if their symbol is in the second set
        Set<Terminal> terminalSymbols = grammarManager.getGrammar(id1).getTerminals().stream()
                .filter(t -> terminalSymbolsId2.contains(t.getTerminalSymbol()))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        concatGrammar.addTerminals(terminalSymbols);

        // add intersection of non-terminals
        // get a Set of all symbols from the second grammar for fast lookup
        Set<Character> nonTerminalsId2 = grammarManager.getGrammar(id2).getNonTerminals().stream()
                .map(NonTerminal::getSymbol)
                .collect(Collectors.toSet());

        // filter the first set of objects by checking if their symbol is in the second set
        Set<NonTerminal> nonTerminalsSymbols = grammarManager.getGrammar(id1).getNonTerminals().stream()
                .filter(t -> nonTerminalsId2.contains(t.getSymbol()))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        concatGrammar.addNonTerminals(nonTerminalsSymbols);

        // add terminals
        concatGrammar.addTerminals(grammarManager.getGrammar(id1).getTerminals());
        concatGrammar.addTerminals(grammarManager.getGrammar(id2).getTerminals());

        // add non-terminals
        concatGrammar.addNonTerminals(grammarManager.getGrammar(id1).getNonTerminals());
        concatGrammar.addNonTerminals(grammarManager.getGrammar(id2).getNonTerminals());

        // reset indexing
        concatGrammar.updateRules();

        CFGOperationsOutput.concatFeedback(concatGrammar.getId());
    }




    public static boolean chomsky(int id) {
        GrammarManager grammarManager = GrammarManager.getInstance();

        Set<Rule> rules = grammarManager.getGrammar(id).getRules();
        //boolean isChomskyNormalForm = true;

        for (Rule rule : rules) {
            List<RightHandSide> rightSide = rule.getRightSide();

            boolean isValidRule = false;

            // case 1: A -> BC (exactly two non-terminals)
            if (rightSide.size() == 2) {
                if (Character.isUpperCase(rightSide.get(0).getSymbol()) &&
                        Character.isUpperCase(rightSide.get(1).getSymbol())) {
                    isValidRule = true;
                }
            }

            // case 2: A -> a (exactly one terminal)
            else if (rightSide.size() == 1) {
                char symbol = rightSide.getFirst().getSymbol();

                if (Character.isLowerCase(symbol) || Character.isDigit(symbol)) {
                    isValidRule = true;
                }

                // case 3: S -> ε (e) (Only if start symbol)
                else if (symbol == Grammar.EPSILON &&
                          rule.getNonTerminal().getSymbol().equals(Grammar.START_NON_TERMINAL))
                {
                    isValidRule = true;
                }
            }

            // if a single rule fails all CNF valid forms
            //     => the whole grammar fails
            if (!isValidRule) {
                return false;
            }
        }

        return true;
    }




    public static void cyk(int id, String input) {
        GrammarManager grammarManager = GrammarManager.getInstance();
        Grammar grammar = grammarManager.getGrammar(id);

        if (!CFGParser.chomsky(id)) {
            Logger.operationFailed(OperationFailedVars.CYK);
        }
        else
        {
            boolean accepted = executeCYK(grammar, input);
            if (accepted) {
                System.out.printf("The string \"%s\" is ACCEPTED by the grammar.%n%n", input);
            } else {
                System.out.printf("The string \"%s\" is REJECTED by the grammar.%n%n", input);
            }
        }
    }

    public static boolean executeCYK(Grammar grammar, String input) {
        int n = input.length();
        if (n == 0)
            return false; // handle of empty string

        // CYK table: T[length][start_index]
        Set<Character>[][] table = new HashSet[n][n];

        // initialize the table sets
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                table[i][j] = new HashSet<>();
            }
        }

        // fill the base row
        // looking for rules like A -> {'a'}
        for (int i = 0; i < n; i++) {
            char terminal = input.charAt(i);
            for (Rule rule : grammar.getRules()) {
                // Check if RHS is a single terminal matching the input char
                if (rule.getRightSide().size() == 1 &&
                        rule.getRightSide().get(0).getSymbol() == terminal) {
                    table[0][i].add(rule.getNonTerminal().getSymbol());
                }
            }
        }

        // fill the rest of the table (lengths 2 to n)
        for (int len = 2; len <= n; len++) {                    // length of substring
            for (int start = 0; start <= n - len; start++) {    // start index
                for (int split = 1; split < len; split++) {     // split point

                    // looking for rules A -> BC
                    // where B generates the left part and C generates the right part
                    Set<Character> leftSet = table[split - 1][start];
                    Set<Character> rightSet = table[len - split - 1][start + split];

                    for (Rule rule : grammar.getRules()) {
                        if (rule.getRightSide().size() == 2) {
                            char B = rule.getRightSide().get(0).getSymbol();
                            char C = rule.getRightSide().get(1).getSymbol();

                            if (leftSet.contains(B) && rightSet.contains(C)) {
                                table[len - 1][start].add(rule.getNonTerminal().getSymbol());
                            }
                        }
                    }
                }
            }
        }

        // check -> if the Start Symbol (usually 'S') is in the top cell, the string is valid
        char startSymbol = 'S';
        return table[n - 1][0].contains(startSymbol);
    }




    public static void iter(int id) {

    }





    public static void empty(int id) {
        GrammarManager grammarManager = GrammarManager.getInstance();
        Grammar grammar = grammarManager.getGrammar(id);

        Set<Character> productiveSymbols = getProductiveSymbols(grammar);

        // identify the likely Start Symbol
        List<Rule> ruleList = new ArrayList<>(grammar.getRules());

        if (ruleList.isEmpty()) {
            Logger.emptyFormalLanguageSet();
            return;
        }

        char startSymbol = ruleList.getFirst().getNonTerminal().getSymbol();

        // verify productivity (not being infinite call, dead end)
        System.out.println("-- Language Emptiness Check --");
        if (productiveSymbols.contains(startSymbol)) {
            System.out.printf("Result: Not empty! -> Start symbol '%c' is productive.%n", startSymbol);
        } else {
            System.out.printf("Result: Empty (∅)! -> Start symbol '%c' cannot reach terminals.%n%n", startSymbol);
        }

    }

    private static Set<Character> getProductiveSymbols(Grammar grammar) {
        Set<Character> productive = new HashSet<>();
        boolean changed = true;

        while (changed) {
            changed = false;
            for (Rule rule : grammar.getRules()) {
                char lhs = rule.getNonTerminal().getSymbol();
                if (productive.contains(lhs)) continue;

                boolean allRhsProductive = true;
                for (RightHandSide rhs : rule.getRightSide()) {
                    char s = rhs.getSymbol();
                    // A symbol is "productive" if it's a terminal OR a productive non-terminal
                    if (Character.isUpperCase(s) && !productive.contains(s)) {
                        allRhsProductive = false;
                        break;
                    }
                }

                if (allRhsProductive) {
                    productive.add(lhs);
                    changed = true;
                }
            }
        }
        return productive;
    }



    public static int chomskify(int id) {
        GrammarManager grammarManager = GrammarManager.getInstance();
        Grammar original = grammarManager.getGrammar(id);
        if (original == null)
            return -1;

        // create new Grammar
        int newId = grammarManager.getAllGrammars().getLast().getValue().getId() + 1;
        Grammar cnfGrammar = new Grammar(newId, grammarManager);

        // clone original Rules
        Set<Rule> currentRules = new LinkedHashSet<>(original.getRules());

        // simplify Terminals (A -> bC becomes A -> Tb C)
        currentRules = simplifyTerminals(currentRules, cnfGrammar);

        // binary splitting (A -> BCD, becomes A -> B X, X -> CD)
        currentRules = binarySplit(currentRules, cnfGrammar);

        cnfGrammar.addRules(currentRules);
        cnfGrammar.updateRules();

        return newId;
    }

    private static Set<Rule> simplifyTerminals(Set<Rule> rules, Grammar grammar) {
        Set<Rule> nextRules = new LinkedHashSet<>();

        for (Rule rule : rules) {
            List<RightHandSide> rhs = rule.getRightSide();

            // skip if already A -> a
            if (rhs.size() == 1 && Character.isLowerCase(rhs.getFirst().getSymbol())) {
                nextRules.add(rule);
                continue;
            }

            List<RightHandSide> newRhs = new ArrayList<>();
            for (RightHandSide part : rhs) {
                char s = part.getSymbol();
                if (Character.isLowerCase(s) || Character.isDigit(s)) {

                    // create substitute non-terminal: A -> aB becomes A -> X B and X -> a
                    char substituteNonTerminalName = Character.toUpperCase(s);
                    NonTerminal substituteNonTerminalValue = new NonTerminal(substituteNonTerminalName, grammar);
                    newRhs.add(substituteNonTerminalValue);

                    // add the mapping rule: X -> a
                    nextRules.add(new Rule(substituteNonTerminalValue, List.of(new Terminal(s, grammar)), grammar));
                } else {
                    newRhs.add(part);
                }
            }
            //nextRules.add(new Rule(rule.getNonTerminal(), newRhs, grammar));
            NonTerminal newLhs = new NonTerminal(rule.getNonTerminal().getSymbol(), grammar);
            nextRules.add(new Rule(newLhs, newRhs, grammar));
        }
        return nextRules;
    }

    private static Set<Rule> binarySplit(Set<Rule> rules, Grammar grammar) {
        Set<Rule> nextRules = new LinkedHashSet<>();
        int dummyCount = 1;

        for (Rule rule : rules) {
            List<RightHandSide> rhs = rule.getRightSide();
            if (rhs.size() <= 2) {
                nextRules.add(rule);
                continue;
            }

            // binary chain: A -> BCD becomes A -> B X1, X1 -> CD
            NonTerminal currentLHS = rule.getNonTerminal();
            for (int i = 0; i < rhs.size() - 2; i++) {
                // using numbers or unique chars for dummy variables
                char dummyName = (char) ('X' + (dummyCount++ % 3));
                NonTerminal newVar = new NonTerminal(dummyName, grammar);

                nextRules.add(new Rule(currentLHS, List.of(rhs.get(i), newVar), grammar));
                currentLHS = newVar;
            }
            nextRules.add(new Rule(currentLHS, List.of(rhs.get(rhs.size() - 2), rhs.getLast()), grammar));
        }
        return nextRules;
    }

}




