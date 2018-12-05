/**
 * Original Grammar:
 *   <program> -> <begin> <stmt-list> <end> <period>
 *   <stmt-list> -> <stmt> <stmt-list'>
 *   <stmt-list'> -> <stmt> <stmt-list'>
 *   <stmt-list'> -> EPSILON
 *   <stmt> -> <assign>
 *   <stmt> -> <declaration>
 *   <assign> -> <iden> <eq> <expr1> <semi>
 *   <expr1> -> <expr2> <expr1'>
 *   <expr1'> -> <low> <expr2> <expr1'>
 *   <expr1'> -> EPSILON
 *   <expr2> -> <term> <expr2'>
 *   <expr2'> -> <high> <term> <expr2'>
 *   <expr2'> -> EPSILON
 *   <declaration> -> <var> <iden> <colon> <type> <initial_value> <semi>
 *   <initial_value> -> <eq> <term>
 *   <initial_value> -> EPSILON
 *   <term> -> <iden>
 *   <term> -> <const>
 *
 * Predict Set (c/o Hackingoff.com)
 *  1	<program> → <begin> <stmt-list> <end> <period>	<begin>
 *  2	<stmt-list> → <stmt> <stmt-list'>	<iden>, <var>
 *  3	<stmt-list'> → <stmt> <stmt-list'>	<iden>, <var>
 *  4	<stmt-list'> → ε	<end>
 *  5	<stmt> → <assign>	<iden>
 *  6	<stmt> → <declaration>	<var>
 *  7	<assign> → <iden> <eq> <expr1> <semi>	<iden>
 *  8	<expr1> → <expr2> <expr1'>	<iden>, <const>
 *  9	<expr1'> → <low> <expr2> <expr1'>	<low>
 *  10	<expr1'> → ε	<semi>
 *  11	<expr2> → <term> <expr2'>	<iden>, <const>
 *  12	<expr2'> → <high> <term> <expr2'>	<high>
 *  13	<expr2'> → ε	<low>, <semi>
 *  14	<declaration> → <var> <iden> <colon> <type> <initial_value> <semi>	<var>
 *  15	<initial_value> → <eq> <term>	<eq>
 *  16	<initial_value> → ε	<semi>
 *  17	<term> → <iden>	<iden>
 *  18	<term> → <const>	<const>
 */

import edu.slu.cs311b.EXPRESSION;
import edu.slu.cs311b.Symbol;
import edu.slu.cs311b.Variable;

import java.util.LinkedList;

/**
 * 
 * @author Laurence F. Balmeo
 */
public class MyInterpreter {

    public static void interpret(Symbol root) {
        PROGRAM p = new PROGRAM(root);

        p.interpret();
    }
}

// Rule No. 1: <program> → <begin> <stmt-list> <end>
class PROGRAM {

    STMT_LIST stmt_list;

    public PROGRAM(Symbol lhs) {
        stmt_list = new STMT_LIST(lhs.children.get(1));
    }

    public void interpret() {
        stmt_list.interpret();
    }
}

// Rule No. 2: <stmt-list> → <stmt> <stmt-list'>
class STMT_LIST {

    STMT stmt;
    STMT_LIST_PRIME stmt_list_prime;

    public STMT_LIST(Symbol lhs) {
        stmt = STMT.construct(lhs.children.get(0));
        stmt_list_prime = STMT_LIST_PRIME.construct(lhs.children.get(1));
    }

    public void interpret() {
        stmt.interpret();
        stmt_list_prime.interpret();
    }
}

abstract class STMT_LIST_PRIME {

    public static STMT_LIST_PRIME construct(Symbol sym) {
        switch (sym.ruleNo) {
            case 3:
                return new STMT_LIST_PRIME_1(sym);

            case 4:
                return new STMT_LIST_PRIME_2(sym);

            default:
                // do nothing
                return null;
        }
    }

    public abstract void interpret();
}

// Rule No. 3: <stmt-list'> → <stmt> <stmt-list'>
class STMT_LIST_PRIME_1 extends STMT_LIST_PRIME {

    STMT stmt;
    STMT_LIST_PRIME stmt_list_prime;

    public STMT_LIST_PRIME_1(Symbol lhs) {
        stmt = STMT.construct(lhs.children.get(0));
        stmt_list_prime = STMT_LIST_PRIME.construct(lhs.children.get(1));
    }

    @Override
    public void interpret() {
        stmt.interpret();
        stmt_list_prime.interpret();
    }
}

// Rule No. 4: <stmt-list'> → ε
class STMT_LIST_PRIME_2 extends STMT_LIST_PRIME {

    public STMT_LIST_PRIME_2(Symbol lhs) {
        // rhs is EPSILON - do nothing
    }

    @Override
    public void interpret() {
        // do nothing
    }
}

abstract class STMT {

    public static STMT construct(Symbol sym) {
        switch (sym.ruleNo) {
            case 5:
                return new STMT_1(sym);
            case 6:
                return new STMT_2(sym);
            default:
                return null;
        }
    }

    public abstract void interpret();
}

// Rule No. 5: <stmt> → <assign>
class STMT_1 extends STMT {

    ASSIGN assign;

    public STMT_1(Symbol lhs) {
        assign = new ASSIGN(lhs.children.get(0));
    }

    @Override
    public void interpret() {
        System.out.println("assign.interpret()");
        assign.interpret();
    }
}

// Rule No. 6: <stmt> → <declaration>
class STMT_2 extends STMT {

    DECLARATION declaration;

    public STMT_2(Symbol lhs) {
        declaration = new DECLARATION(lhs.children.get(0));
    }

    @Override
    public void interpret() {
        System.out.println("declaration.interpret()");
        declaration.interpret();
    }
}

// Rule No. 7: <assign> → <iden> <eq> <expr1> <semi>
class ASSIGN {

    Symbol iden;
    EXPR_1 expr1;

    public ASSIGN(Symbol lhs) {
        iden = lhs.children.get(0);
        expr1 = new EXPR_1(lhs.children.get(2));
    }

    public void interpret() {
        Variable var = Variable.symbolTable.get(iden.lexeme);
        var.value = expr1.interpret();

        System.out.println("\tASSIGNed " + var.name + " = " + var.value);

    }
}

// Rule No. 8: <expr1> → <expr2> <expr1'>
// Rule No. 9: <expr1'> → <low> <expr2> <expr1'>
// Rule No. 10: <expr1'> → ε
// Rule No. 11: <expr2> → <term> <expr2'>
// Rule No. 12: <expr2'> → <high> <term> <expr2'>
// Rule No. 13: <expr2'> → ε
class EXPR_1 {

    LinkedList<Symbol> expression;
    java.util.Deque<Integer> operands = new LinkedList();

    public EXPR_1(Symbol lhs) {
        expression = EXPRESSION.getExpression(lhs);
    }

    public int interpret() {
        Variable var;
        int result = 0;

        while (!expression.isEmpty()) {
            Symbol sym = expression.removeFirst();

            switch (sym.type) {
                // sym is an operand
                case "<const>":
                    operands.push(Integer.parseInt(sym.lexeme));
                    break;

                case "<iden>":
                    var = Variable.symbolTable.get(sym.lexeme);

                    operands.push((Integer) var.value);
                    break;

                // sym is an operator
                default:
                    int operand2 = operands.pop();
                    int operand1 = operands.pop();

                    switch (sym.lexeme) {
                        case "+":
                            operands.push(operand1 + operand2);
                            break;

                        case "-":
                            operands.push(operand1 - operand2);
                            break;

                        case "*":
                            operands.push(operand1 * operand2);
                            break;

                        case "/":
                            operands.push(operand1 / operand2);
                            break;
                    }
            }
        }

        return operands.pop();
    }
}

// Rule No. 14: <declaration> → <var> <iden> <colon> <type> <initial_value> <semi>
class DECLARATION {

    Symbol iden;
    Symbol type;
    INITIAL_VALUE initial_value;

    public DECLARATION(Symbol lhs) {
        iden = lhs.children.get(1);
        type = lhs.children.get(3);
        initial_value = INITIAL_VALUE.construct(lhs.children.get(4));
    }

    public void interpret() {
        Variable v = new Variable(iden.lexeme, type.lexeme, 0);
        v.value = initial_value.interpret();

        System.out.println("\t" + Variable.symbolTable);
    }
}

abstract class INITIAL_VALUE {

    public static INITIAL_VALUE construct(Symbol sym) {
        switch (sym.ruleNo) {
            case 15:
                return new INITIAL_VALUE_1(sym);

            case 16:
                return new INITIAL_VALUE_2(sym);

            default:
                return null;
        }
    }

    public abstract Object interpret();
}

// Rule No. 15: <initial_value> → <eq> <term>
class INITIAL_VALUE_1 extends INITIAL_VALUE {

    TERM term;

    public INITIAL_VALUE_1(Symbol lhs) {
        term = TERM.construct(lhs.children.get(1));
    }

    @Override
    public Object interpret() {
        return term.interpret();
    }
}

// Rule No. 16: <initial_value> → ε
class INITIAL_VALUE_2 extends INITIAL_VALUE {

    public INITIAL_VALUE_2(Symbol lhs) {
        // rhs is EPSILON - default to null(???)
    }

    @Override
    public Object interpret() {
        return null;
    }
}

abstract class TERM {

    public static TERM construct(Symbol sym) {
        switch (sym.ruleNo) {
            case 17:
                return new TERM_1(sym);

            case 18:
                return new TERM_2(sym);

            default:
                return null;
        }
    }

    public abstract Object interpret();
}

// Rule No. 17: <term> → <iden>
class TERM_1 extends TERM {

    Variable iden;

    public TERM_1(Symbol lhs) {
        iden = Variable.symbolTable.get(lhs.children.get(0).lexeme);
    }

    public Object interpret() {
        return iden.value;
    }
}

// Rule No. 18: <term> → <const>
class TERM_2 extends TERM {

    String const1;

    public TERM_2(Symbol lhs) {
        const1 = lhs.children.get(0).lexeme;
    }

    @Override
    public Object interpret() {
        return Integer.parseInt(const1);
    }
}
