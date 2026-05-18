package cfgService.objects;

import java.util.Objects;

public class NonTerminal extends Language {
    private Character nonTerminalSymbol;    // capital letter

    public NonTerminal(Character nonTerminalSymbol, Grammar grammar) {
        this.setNonTerminalSymbol(nonTerminalSymbol);
        grammar.addNonTerminals(this);
    }

    // getters
    /*public Character getNonTerminalSymbol() {
        return this.nonTerminalSymbol;
    }*/

    // setters
    public void setNonTerminalSymbol(Character nonTerminalSymbol) {
        if (nonTerminalSymbol == null ||
            nonTerminalSymbol < 'A' || nonTerminalSymbol > 'Z') {
            throw new NullPointerException("Error! NonTerminal symbol's value is not set! -> class NonTerminal, Character nonTerminalSymbol");
        }
        this.nonTerminalSymbol = nonTerminalSymbol;

    }

    @Override
    public Character getSymbol() {
        return this.nonTerminalSymbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof NonTerminal that))
            return false;
        return Objects.equals(nonTerminalSymbol, that.nonTerminalSymbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nonTerminalSymbol);
    }

}
