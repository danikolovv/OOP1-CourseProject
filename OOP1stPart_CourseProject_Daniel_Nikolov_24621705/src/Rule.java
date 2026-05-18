package cfgService.objects;

import exceptions.InvalidInputValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Rule {
    //private Map<Terminal, List<NonTerminal>> rules;

    private NonTerminal nonTerminal;
    private List<Language> rightSide;
    private int consecutiveNumber;

    public Rule(NonTerminal nonTerminal, List<Language> rightSide, Grammar grammar) {
        this.consecutiveNumber = grammar.getRules().size() + 1;
        this.rightSide = new ArrayList<>();
        this.setNonTerminal(nonTerminal);
        this.addRightSideSymbols(rightSide);
        grammar.addRule(this);
    }

    // getters
    public NonTerminal getNonTerminal() {
        return this.nonTerminal;
    }

    public List<Language> getRightSide() {
        return this.rightSide;
    }

    public int getConsecutiveNumber() {
        return this.consecutiveNumber;
    }

    // setters
    public void setNonTerminal(NonTerminal nonTerminal) {
        if (nonTerminal == null) {
            throw new NullPointerException("Error! NonTerminal cannot be set to null. -> class Rule, method setNonTerminal");
        }
        this.nonTerminal = nonTerminal;
    }



    public void addRightSideSymbols(List<Language> rightSide) {
        if (rightSide == null) {
            throw new NullPointerException("Error! Terminals passed when creating a rule were null. -> class Rule, List<Terminal> terminals");
        }
        this.rightSide.addAll(rightSide);
    }

    public void setConsecutiveNumber(int number) {
        if (number > 0)
            this.consecutiveNumber = number;
        else
            throw new InvalidInputValue("Error! Invalid value input for consecutive number of a rule. -> class Rule, method setConsecutiveNumber");
    }


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Rule rule))
            return false;

        return nonTerminal.equals(rule.nonTerminal) &&
                rightSide.equals(rule.rightSide);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nonTerminal, rightSide);
    }
}
