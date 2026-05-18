package cfgService.objects;

import exceptions.InvalidInputValue;
import cfgService.objects.collections.GrammarManager;

import java.util.LinkedHashSet;
import java.util.Set;

public class Grammar {
    public static final Character START_NON_TERMINAL = 'S';
    public static final Character EPSILON = 'e';
    private int id;                                                 // may be non-consecutive number
    private Set<Rule> rules;                                        // contains rules
    private Set<Terminal> terminals;
    private Set<NonTerminal> nonTerminals;

    public Grammar(int id, GrammarManager grammarManager) {
        this.rules = new LinkedHashSet<>();
        this.terminals = new LinkedHashSet<>();
        this.nonTerminals = new LinkedHashSet<>();
        this.setId(id);
        grammarManager.addGrammar(id, this);
    }

    // getters
    public int getId() {
        return this.id;
    }

    public Set<Rule> getRules() {
        return this.rules;
    }

    public Rule getRule(int n) {
        Rule returnRule = null;

        for (Rule rule : rules) {
            if (rule.getConsecutiveNumber() == n) {
                returnRule = rule;
                break;
            }
        }

        return returnRule;
    }

    public Set<Terminal> getTerminals() {
        return this.terminals;
    }

    public Set<NonTerminal> getNonTerminals() {
        return this.nonTerminals;
    }

    // setters
    public void setId(int id) {
        if (id < 0) {
            throw new InvalidInputValue("Error! Invalid input value for Grammar.id. -> class Grammar");
        }
        this.id = id;
    }

    public void addRule(Rule rule) {
        if (rule != null)
            this.rules.add(rule);
        else
            throw new NullPointerException("Error! The Rule being passed is not valid. -> class Grammar, method addRule");
    }

    public void addRules(Set<Rule> rules) {
        if (!rules.isEmpty())
            this.rules.addAll(rules);
        /*else
            throw new InvalidInputValue("Error! Collection of Rule being passed cannot be empty. -> class Grammar, method addRules");*/
    }

    public void updateRules() {
        int counter = 0;
        for (Rule rule : this.rules) {
            rule.setConsecutiveNumber(++counter);
        }
    }

    public void addTerminals(Set<Terminal> terminals) {
        if (!terminals.isEmpty())
            this.terminals.addAll(terminals);
        else
            throw new InvalidInputValue("Error! Collection of Terminal being passed cannot be empty. -> class Grammar, method addTerminals");
    }

    // when Terminal object gets created add it to the set of terminals
    public void addTerminals(Terminal terminal) {
        if (terminal != null)
            this.terminals.add(terminal);
        else
            throw new NullPointerException("Error! The Terminal being passed is not valid. -> class Grammar, method addTerminals");
    }

    // when NonTerminal object gets created add it to the set of non-terminals
    public void addNonTerminals(NonTerminal nonTerminal) {
        if (nonTerminal != null)
            this.nonTerminals.add(nonTerminal);
        else
            throw new NullPointerException("Error! The NonTerminal being passed is not valid. -> class Grammar, method addNonTerminals");
    }

    public void addNonTerminals(Set<NonTerminal> nonTerminals) {
        if (!nonTerminals.isEmpty())
            this.nonTerminals.addAll(nonTerminals);
        else
            throw new InvalidInputValue("Error! Collection of NonTerminal being passed cannot be empty. -> class Grammar, method addNonTerminals");
    }

}
