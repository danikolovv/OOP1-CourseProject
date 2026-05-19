package cfgService.objects;

import java.util.Objects;

public class Terminal extends RightHandSide {
    private Character terminalSymbol;  // lower letter or number

    public Terminal(Character terminalSymbol, Grammar grammar) {
        this.setTerminalSymbol(terminalSymbol);

        if (grammar != null)
            grammar.addTerminals(this);
    }

    // getters
    public Character getTerminalSymbol() {
        return this.terminalSymbol;
    }

    // setters
    public void setTerminalSymbol(Character terminalSymbol) {
        if (terminalSymbol == null ||
            terminalSymbol < '0' || terminalSymbol > '9' &&
            terminalSymbol < 'a' || terminalSymbol > 'z') {
            throw new NullPointerException("Error! Terminal symbol's value is not set! -> class Terminal, Character terminalSymbol");
        }
        this.terminalSymbol = terminalSymbol;
    }

    @Override
    public Character getSymbol() {
        return this.terminalSymbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Terminal terminal))
            return false;
        return Objects.equals(terminalSymbol, terminal.terminalSymbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(terminalSymbol);
    }
}
