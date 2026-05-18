package cfgService.parsers;

import cfgService.objects.*;
import cfgService.objects.collections.GrammarManager;
import commandOutputs.loggers.Logger;
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

        List<Language> rightSideSymbols = new ArrayList<>();        // fix for multisymbol tokens

        for (Character token : symbols) {
            if (Character.isUpperCase(token)) {
                rightSideSymbols.add(new NonTerminal(token, grammar));
            } else {
                rightSideSymbols.add(new Terminal(token, grammar));
            }
        }

        if (grammar.getRules().isEmpty()) {
            List<Language> rightSideEpsilon = new ArrayList<>();
            rightSideEpsilon.add(new Terminal(Grammar.EPSILON, grammar));

            new Rule(new NonTerminal(Grammar.START_NON_TERMINAL, grammar),
                    rightSideEpsilon, grammar);
        }

        new Rule(nonTerminal, rightSideSymbols, grammar);
    }

    public static void parseRemoveRule(int id, int n) {

        GrammarManager grammarManager = GrammarManager.getInstance();
        Grammar grammar = grammarManager.getGrammar(id);
        Rule rule = grammar.getRule(n);

        if (rule != null) {
            grammar.getRules().remove(rule);

            grammar.getTerminals().removeAll(rule.getRightSide());
            grammar.getNonTerminals().remove(rule.getNonTerminal());

            grammar.updateRules();
        }
        else throw new NullPointerException("Error!");
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
            Logger.operationFailed();
        }
    }

    public static Set<Rule> getUnionRules(GrammarManager grammarManager, Grammar unionGrammar, int id1, int id2) {
        boolean isContained = false;
        Set<Rule> ruleUnion = new LinkedHashSet<>();

        for (Rule rule : grammarManager.getGrammar(id1).getRules()) {
            if (rule != null) {
                NonTerminal nonTerminal = rule.getNonTerminal();
                List<Language> terminals = rule.getRightSide();

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
                List<Language> terminals = rule.getRightSide();

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

    public static boolean chomsky(int id) {     // TODO: To be tested  -> does not work!!!  (debug the checks)
        GrammarManager grammarManager = GrammarManager.getInstance();

        Set<Rule> rules = grammarManager.getGrammar(id).getRules();
        boolean isChomskyNormalForm = false;

        // 1st rule -> check each rule for having a single lowercase symbol (terminal)
        for (Rule rule : rules) {
            List<Language> rightSideSymbols = rule.getRightSide();

            int symbolCounter = 0;
            for (Language language : rightSideSymbols) {
                ++symbolCounter;
                if (symbolCounter == 2 && Character.isUpperCase(language.getSymbol())) {
                    isChomskyNormalForm = true;
                }
            }
        }

        // 2nd rule -> right side consists of exactly one terminal
        for (Rule rule : rules) {
            List<Language> rightSideSymbols = rule.getRightSide();

            int symbolCounter = 0;
            for (Language language : rightSideSymbols) {
                ++symbolCounter;
                if (symbolCounter == 1 && Character.isLowerCase(language.getSymbol())) {
                    isChomskyNormalForm = true;
                }
            }
        }

        // 3nd rule -> only the start symbol can lead to epsilon
        for (Rule rule : rules) {
            List<Language> rightSideSymbols = rule.getRightSide();

            if (rule.getNonTerminal().getSymbol().equals(Grammar.START_NON_TERMINAL)) {
                for (Language language : rightSideSymbols) {
                    if (language.getSymbol().equals(Grammar.EPSILON) &&
                        !language.getSymbol().equals(Grammar.START_NON_TERMINAL)) {
                        isChomskyNormalForm = true;
                    }
                }
            }

        }

        return isChomskyNormalForm;
    }

    public static void cyk(int id) {

    }
}





/*
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
                .map(NonTerminal::getNonTerminalSymbol)
                .collect(Collectors.toSet());

        // filter the first set of objects by checking if their symbol is in the second set
        Set<NonTerminal> nonTerminalsSymbols = grammarManager.getGrammar(id1).getNonTerminals().stream()
                .filter(t -> nonTerminalsId2.contains(t.getNonTerminalSymbol()))
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
            Logger.operationFailed();
        }
    }

    public static Set<Rule> getUnionRules(GrammarManager grammarManager, Grammar unionGrammar, int id1, int id2) {
        boolean isContained = false;
        Set<Rule> ruleUnion = new LinkedHashSet<>();

        for (Rule rule : grammarManager.getGrammar(id1).getRules()) {
            if (rule != null) {
                NonTerminal nonTerminal = rule.getNonTerminal();
                List<Terminal> terminals = rule.getTerminals();

                isContained = unionGrammar.getNonTerminals().stream()
                        .anyMatch(nonTerm -> nonTerm.equals(nonTerminal));

                if (terminals.stream().allMatch(terminal ->
                        unionGrammar.getTerminals().stream()
                                .anyMatch(terminalUnion ->
                                        terminal.getTerminalSymbol().equals(terminalUnion.getTerminalSymbol())
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
                List<Terminal> terminals = rule.getTerminals();

                isContained = unionGrammar.getNonTerminals().stream()
                        .anyMatch(nonTerm -> nonTerm.equals(nonTerminal));

                if (terminals.stream().allMatch(terminal ->
                        unionGrammar.getTerminals().stream()
                                .anyMatch(terminalUnion ->
                                        terminal.getTerminalSymbol().equals(terminalUnion.getTerminalSymbol())
                                )) && isContained)
                {
                    ruleUnion.add(rule);
                    isContained = false;
                }
            }
        }

        return ruleUnion;
    }
*/



/*

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
                .map(NonTerminal::getNonTerminalSymbol)
                .collect(Collectors.toSet());

        // filter the first set of objects by checking if their symbol is in the second set
        Set<NonTerminal> nonTerminalsSymbols = grammarManager.getGrammar(id1).getNonTerminals().stream()
                .filter(t -> nonTerminalsId2.contains(t.getNonTerminalSymbol()))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        concatGrammar.addNonTerminals(nonTerminalsSymbols);

        // add terminals
        concatGrammar.addTerminals(grammarManager.getGrammar(id1).getTerminals());
        concatGrammar.addTerminals(grammarManager.getGrammar(id2).getTerminals());

        // add non-terminals
        concatGrammar.addNonTerminals(grammarManager.getGrammar(id1).getNonTerminals());
        concatGrammar.addNonTerminals(grammarManager.getGrammar(id2).getNonTerminals());*/

        // reset indexing
        //concatGrammar.updateRules();

        //CFGOperationsOutput.concatFeedback(concatGrammar.getId());
        //}
//
