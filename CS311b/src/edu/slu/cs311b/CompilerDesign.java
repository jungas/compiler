package edu.slu.cs311b;

import java.util.List;

public class CompilerDesign {

    public static void main(String[] args) {
        Scanner.init("token-lexeme.txt");
        Parser.init("predict.txt");

        List<Symbol> source = Scanner.scan("program.txt");

        Symbol root = Parser.parse(source);

        ParseTreeViewer.show(root);
        Interpreter.interpret(root);
    }

}
