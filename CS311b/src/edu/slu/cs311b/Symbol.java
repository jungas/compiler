package edu.slu.cs311b;

import java.util.ArrayList;
import java.util.Objects;

public class Symbol {

    public final static Symbol NULL = new Symbol(Token.NULL);
    public final static Symbol EOF = new Symbol(Token.EOF);

    Symbol parent;
    String type;
    int ruleNo;

    Token token;
    String lexeme;

    ArrayList<Symbol> children;

    private Symbol(Token token) {
        this.token = token;
        this.lexeme = "";
        this.children = new ArrayList();
    }

    public Symbol(Symbol parent, Token token) {
        this.parent = parent;
        this.token = token;
        this.children = new ArrayList();
        this.type = token.name;
    }

    public void addChild(Symbol child) {
        this.children.add(child);;
    }

    public boolean isEpsilon() {
        return this.token.name.charAt(0) == 0x03B5;
    }

    public boolean matches(String regex) {
        return this.token.name.matches(regex);
    }

    @Override
    public boolean equals(Object other) {
        try {
            return this.token.name.equals(((Symbol) other).token.name);

        } catch (Exception e) {
            return this.token.name.equals(((Token) other).name);
        }
    }

    @Override
    public int hashCode() {
        return this.token.name.hashCode();
    }

    @Override
    public String toString() {
        return token.name;
    }
}

class Terminal extends Symbol {

    int lineNo;
    int columnNo;

    public Terminal(Token token, String lexeme, int lineNo, int columnNo) {
        super(Symbol.NULL, token);

        this.lexeme = lexeme;
        this.lineNo = lineNo;
        this.columnNo = columnNo;
    }

    public Terminal(Symbol parent, Token token, String lexeme, int lineNo, int columnNo) {
        super(parent, token);

        this.lexeme = lexeme;
        this.lineNo = lineNo;
        this.columnNo = columnNo;
    }
}
