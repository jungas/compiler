package edu.slu.cs311b;

public class Token {
    public final static Token NULL = new Token("<NULL>");
    public final static Token ERROR = new Token("<ERROR>");
    public final static Token EOF = new Token("<EOF>");

    String name;
    public boolean isTerminal;

    private Token(String name) {
        this.name = name;
        this.isTerminal = true;
    }

    public Token(String name, boolean isTerminal) {
        this.name = name;
        this.isTerminal = isTerminal;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return name.equals(((Token)other).name);
    }

    @Override
    public String toString() {
        return name;
    }
}
