package edu.slu.cs311b;

import java.util.LinkedList;
import java.util.Scanner;

class Interpreter {
    static void interpret(Symbol root) {
        PROGRAM p = new PROGRAM(root);
        p.interpret();
    }
}

// 1	<program> → <program_declaration> <program_name> <begin> <stmt_list> <end>
class PROGRAM {
    private STMT_LIST stmtList;

    PROGRAM(Symbol lhs) {
        stmtList = STMT_LIST.construct(lhs.children.get(3));
    }

    void interpret() {
        stmtList.interpret();
    }
}

abstract class STMT_LIST {
    static STMT_LIST construct(Symbol sym) {
        switch (sym.ruleNo) {
            case 2:
                return new STMT_LIST_1(sym);
            case 3:
                return new STMT_LIST_2(sym);
            case 4:
                return new STMT_LIST_3();
            default:
                return null;
        }
    }

    public abstract void interpret();
}

// 2	<stmt_list> → <stmt> <stmt_list>
class STMT_LIST_1 extends STMT_LIST {
    private STMT stmt;
    private STMT_LIST stmt_list;

    STMT_LIST_1(Symbol lhs) {
        stmt = STMT.construct(lhs.children.get(0));
        stmt_list = STMT_LIST.construct(lhs.children.get(1));
    }

    public void interpret() {
        stmt.interpret();
        stmt_list.interpret();
    }
}

// 3	<stmt_list> → <stmt>
class STMT_LIST_2 extends STMT_LIST {
    private STMT stmt;

    STMT_LIST_2(Symbol lhs) {
        stmt = STMT.construct(lhs);
    }

    public void interpret() {
        stmt.interpret();
    }
}

// 4	<stmt_list> → ε
class STMT_LIST_3 extends STMT_LIST {
    STMT_LIST_3() {
    }

    public void interpret() {
    }
}

abstract class STMT {
    static STMT construct(Symbol sym) {
        switch (sym.ruleNo) {
            case 5:
                return new STMT_1(sym);
            case 6:
                return new STMT_2(sym);
            case 7:
                return new STMT_3(sym);
            case 8:
                return new STMT_4(sym);
            case 9:
                return new STMT_5(sym);
            case 10:
                return new STMT_6(sym);
            default:
                return null;
        }
    }

    abstract void interpret();
}

// 5	<stmt> → <var_list> <terminator>
class STMT_1 extends STMT {
    private VAR_LIST var_list;

    STMT_1(Symbol lhs) {
        var_list = new VAR_LIST(lhs.children.get(0));
    }

    public void interpret() {
        var_list.interpret();
    }
}

// 6	<stmt> → <assignment> <terminator>
class STMT_2 extends STMT {
    private ASSIGNMENT assignment;

    STMT_2(Symbol lhs) {
        assignment = new ASSIGNMENT(lhs.children.get(0));
    }

    public void interpret() {
        assignment.interpret();
    }
}

// 7	<stmt> → <expression> <terminator>
class STMT_3 extends STMT {
    private EXPR expr;

    STMT_3(Symbol lhs) {
        expr = new EXPR(lhs.children.get(0));
    }

    public void interpret() {
        expr.interpret();
    }
}

// 8	<stmt> → <if_stmt>
class STMT_4 extends STMT {
    private IF_STMT if_stmt;

    STMT_4(Symbol lhs) {
        if_stmt = new IF_STMT(lhs.children.get(0));
    }

    public void interpret() {
        if_stmt.interpret();
    }
}

// 9	<stmt> → <while_stmt>
class STMT_5 extends STMT {
    private WHILE_STMT while_stmt;

    STMT_5(Symbol lhs) {
        while_stmt = new WHILE_STMT(lhs.children.get(0));
    }

    public void interpret() {
        while_stmt.interpret();
    }
}

// 10	<stmt> → <output> <terminator>
class STMT_6 extends STMT {
    private OUTPUT output;

    STMT_6(Symbol lhs) {
        output = new OUTPUT(lhs.children.get(0));
    }

    public void interpret() {
        output.interpret();
    }
}

// 11	<declaration> → <data_type> <identifier> <declaration'>
class DECLARATION {
    DATA_TYPE data_type;
    private Symbol identifier;
    private DECLARATION_PRIME declaration_prime;

    DECLARATION(Symbol lhs) {
        data_type = new DATA_TYPE(lhs.children.get(0));
        identifier = lhs.children.get(1);
        declaration_prime = DECLARATION_PRIME.construct(lhs.children.get(2));
    }

    void interpret() {
        Variable v = new Variable(identifier.lexeme, data_type.interpret(), 0);
        v.value = declaration_prime.interpret();
        System.out.println("\t" + Variable.symbolTable);
    }
}

abstract class DECLARATION_PRIME {
    static DECLARATION_PRIME construct(Symbol sym) {
        switch (sym.ruleNo) {
            case 12:
                return new DECLARATION_PRIME_1(sym);
            case 13:
                return new DECLARATION_PRIME_2();
            default:
                return null;
        }
    }

    public abstract Object interpret();
}

// 12	<declaration'> → <assignment_operator> <expression>
class DECLARATION_PRIME_1 extends DECLARATION_PRIME {
    private EXPR expr;

    DECLARATION_PRIME_1(Symbol lhs) {
        expr = new EXPR(lhs.children.get(1));
    }

    public Object interpret() {
        return expr.interpret();
    }
}

// 13	<declaration'> → ε
class DECLARATION_PRIME_2 extends DECLARATION_PRIME {
    DECLARATION_PRIME_2() {
    }

    public Object interpret() {
        return null;
    }
}

// 14	<assignment> → <identifier> <assignment_operator> <expression>
class ASSIGNMENT {
    private Symbol identifier;
    private EXPR expr;

    ASSIGNMENT(Symbol lhs) {
        identifier = lhs.children.get(0);
        expr = new EXPR(lhs.children.get(2));
    }

    void interpret() {
        Variable var = Variable.symbolTable.get(identifier.lexeme);
        var.value = expr.interpret();
        System.out.println("\tAssigned " + var.name + " = " + var.value);
    }
}

// 15	<if_stmt> → <if> <open_parenthesis> <expression> <close_parenthesis> <open_curly_brace> <stmt_list> <close_curly_brace>
class IF_STMT {
    private EXPR expr;
    private STMT_LIST stmt_list;

    IF_STMT(Symbol lhs) {
        expr = new EXPR(lhs.children.get(2));
        stmt_list = STMT_LIST.construct(lhs.children.get(5));
    }

    void interpret() {
        if ((boolean) expr.interpret()) {
            stmt_list.interpret();
        }
    }
}

// 16	<while_stmt> → <while> <open_parenthesis> <expression> <close_parenthesis> <open_curly_brace> <stmt_list> <close_curly_brace>
class WHILE_STMT {
    private EXPR expr;
    private STMT_LIST stmt_list;

    WHILE_STMT(Symbol lhs) {
        expr = new EXPR(lhs.children.get(2));
        stmt_list = STMT_LIST.construct(lhs.children.get(5));
    }

    void interpret() {
        while ((boolean) expr.interpret()) {
            stmt_list.interpret();
        }
    }
}

// 17	<output> → <print> <open_parenthesis> <expression> <close_parenthesis>
class OUTPUT {
    private EXPR expr;

    public OUTPUT(Symbol lhs) {
        expr = new EXPR(lhs.children.get(2));
    }

    void interpret() {
        System.out.println(expr.interpret());
    }
}

// 18	<input> → <input_keyword> <open_parenthesis> <expression> <close_parenthesis>
class INPUT {
    private EXPR expr;

    INPUT(Symbol lhs) {
        expr = new EXPR(lhs.children.get(2));
    }

    String interpret() {
        System.out.println(expr.interpret());
        return new Scanner(System.in).nextLine();
    }
}

// 19	<var_list> → <declaration> <identifier_list>
class VAR_LIST {
    private DECLARATION declaration;
    private IDENTIFIER_LIST identifier_list;

    VAR_LIST(Symbol lhs) {
        declaration = new DECLARATION(lhs.children.get(0));
        identifier_list = IDENTIFIER_LIST.construct(lhs.children.get(1));
    }

    void interpret() {
        declaration.interpret();
        identifier_list.interpret(declaration.data_type);
    }
}

abstract class IDENTIFIER_LIST {
    static IDENTIFIER_LIST construct(Symbol sym) {
        switch (sym.ruleNo) {
            case 20:
                return new IDENTIFIER_LIST_1(sym);
            case 21:
                return new IDENTIFIER_LIST_2(sym);
            case 22:
                return new IDENTIFIER_LIST_3();
            default:
                return null;
        }
    }

    public abstract void interpret(DATA_TYPE data_type);
}

// 20	<identifier_list> → <identifier>
class IDENTIFIER_LIST_1 extends IDENTIFIER_LIST {
    private Symbol identifier;

    IDENTIFIER_LIST_1(Symbol lhs) {
        identifier = lhs.children.get(0);
    }

    public void interpret(DATA_TYPE data_type) {
        Variable v = new Variable(identifier.lexeme, data_type.interpret(), 0);
        v.value = null;
        System.out.println("\t" + Variable.symbolTable);
    }
}

// 21	<identifier_list> → <comma> <identifier> <identifier_list>
class IDENTIFIER_LIST_2 extends IDENTIFIER_LIST {
    private Symbol identifier;
    private IDENTIFIER_LIST identifier_list;

    IDENTIFIER_LIST_2(Symbol lhs) {
        identifier = lhs.children.get(1);
        identifier_list = IDENTIFIER_LIST.construct(lhs.children.get(2));
    }

    public void interpret(DATA_TYPE data_type) {
        Variable v = new Variable(identifier.lexeme, data_type.interpret(), 0);
        v.value = null;
        identifier_list.interpret(data_type);
        System.out.println("\t" + Variable.symbolTable);
    }
}

// 22	<identifier_list> → ε	<terminator>
class IDENTIFIER_LIST_3 extends IDENTIFIER_LIST {
    IDENTIFIER_LIST_3() {
    }

    public void interpret(DATA_TYPE data_type) {
    }
}

// 23	<expression> → <relational_expression> <expression'>
class EXPR {
    private RELATIONAL_EXPR relational_expr;
    private EXPR_PRIME expr_prime;

    EXPR(Symbol lhs) {
        relational_expr = new RELATIONAL_EXPR(lhs.children.get(0));
        expr_prime = EXPR_PRIME.construct(lhs.children.get(1));
    }

    Object interpret() {
        Object result = expr_prime.interpret(relational_expr);
        if (result == null) {
            result = relational_expr.interpret();
        }
        return result;
    }
}

abstract class EXPR_PRIME {
    static EXPR_PRIME construct(Symbol sym) {
        switch (sym.ruleNo) {
            case 24:
                return new EXPR_PRIME_1(sym);
            case 25:
                return new EXPR_PRIME_2();
            default:
                return null;
        }
    }

    public abstract Object interpret(RELATIONAL_EXPR relational_expr);
}

// 24	<expression'> → <logical_operator> <relational_expression> <expression'>
class EXPR_PRIME_1 extends EXPR_PRIME {
    private Symbol logical_operator;
    private RELATIONAL_EXPR relational_expr;
    private EXPR_PRIME expr_prime;

    EXPR_PRIME_1(Symbol lhs) {
        logical_operator = lhs.children.get(0);
        relational_expr = new RELATIONAL_EXPR(lhs.children.get(1));
        expr_prime = EXPR_PRIME.construct(lhs.children.get(0));
    }

    public Object interpret(RELATIONAL_EXPR relational_expr) {
        switch (logical_operator.lexeme) {
            case "and":
                return (Boolean) this.relational_expr.interpret() && (Boolean) relational_expr.interpret();
            case "or":
                return (Boolean) this.relational_expr.interpret() || (Boolean) relational_expr.interpret();
            default:
                return expr_prime.interpret(relational_expr);
        }
    }
}

// 25	<expression'> → ε
class EXPR_PRIME_2 extends EXPR_PRIME {
    EXPR_PRIME_2() {
    }

    public Object interpret(RELATIONAL_EXPR relational_expr) {
        return null;
    }
}

// 26	<relational_expression> → <relational_operand> <relational_expression'>
class RELATIONAL_EXPR {
    private RELATIONAL_OPERAND relational_operand;
    private RELATIONAL_EXPR_PRIME relational_expr_prime;

    RELATIONAL_EXPR(Symbol lhs) {
        relational_operand = RELATIONAL_OPERAND.construct(lhs.children.get(0));
        relational_expr_prime = RELATIONAL_EXPR_PRIME.construct(lhs.children.get(1));
    }

    Object interpret() {
        Object result = relational_expr_prime.interpret(relational_operand);
        if (result == null) {
            result = relational_operand.interpret();
        }
        return result;
    }
}

abstract class RELATIONAL_EXPR_PRIME {
    static RELATIONAL_EXPR_PRIME construct(Symbol sym) {
        switch (sym.ruleNo) {
            case 27:
                return new RELATIONAL_EXPR_PRIME_1(sym);
            case 28:
                return new RELATIONAL_EXPR_PRIME_2();
            default:
                return null;
        }
    }

    public abstract Object interpret(RELATIONAL_OPERAND relational_operand);
}

// 27	<relational_expression'> → <relational_operator> <relational_operand> <relational_expression'>
class RELATIONAL_EXPR_PRIME_1 extends RELATIONAL_EXPR_PRIME {
    private Symbol relational_operator;
    private RELATIONAL_OPERAND relational_operand;

    RELATIONAL_EXPR_PRIME_1(Symbol lhs) {
        relational_operator = lhs.children.get(0);
        relational_operand = RELATIONAL_OPERAND.construct(lhs.children.get(1));
    }

    public Object interpret(RELATIONAL_OPERAND relational_operand) {
        Object temp1, temp2;
        temp1 = relational_operand.interpret();
        temp2 = this.relational_operand.interpret();
        if (temp1 instanceof Integer) {
            if (temp2 instanceof Integer) {
                switch (relational_operator.lexeme) {
                    case ">":
                        return (Integer) temp1 > (Integer) temp2;
                    case "<":
                        return (Integer) temp1 < (Integer) temp2;
                    case ">=":
                        return (Integer) temp1 >= (Integer) temp2;
                    case "<=":
                        return (Integer) temp1 <= (Integer) temp2;
                    case "==":
                        return temp1.equals(temp2);
                    case "!=":
                        return !(temp1.equals(temp2));
                }
            } else if (temp2 instanceof Float) {
                switch (relational_operator.lexeme) {
                    case ">":
                        return (Integer) temp1 > (Float) temp2;
                    case "<":
                        return (Integer) temp1 < (Float) temp2;
                    case ">=":
                        return (Integer) temp1 >= (Float) temp2;
                    case "<=":
                        return (Integer) temp1 <= (Float) temp2;
                    case "==":
                        return ((Integer) temp1).intValue() == ((Float) temp2).floatValue();
                    case "!=":
                        return ((Integer) temp1).intValue() != ((Float) temp2).floatValue();
                }
            } else if (temp2 instanceof Character) {
                switch (relational_operator.lexeme) {
                    case ">":
                        return (Integer) temp1 > (Character) temp2;
                    case "<":
                        return (Integer) temp1 < (Character) temp2;
                    case ">=":
                        return (Integer) temp1 >= (Character) temp2;
                    case "<=":
                        return (Integer) temp1 <= (Character) temp2;
                    case "==":
                        return ((Integer) temp1).intValue() == (Character) temp2;
                    case "!=":
                        return ((Integer) temp1).intValue() != (Character) temp2;
                }
            } else if (temp2 instanceof String) {
                switch (relational_operator.lexeme) {
                    case ">":
                        return (Integer) temp1 > ((String) temp2).length();
                    case "<":
                        return (Integer) temp1 < ((String) temp2).length();
                    case ">=":
                        return (Integer) temp1 >= ((String) temp2).length();
                    case "<=":
                        return (Integer) temp1 <= ((String) temp2).length();
                    case "==":
                        return (Integer) temp1 == ((String) temp2).length();
                    case "!=":
                        return (Integer) temp1 != ((String) temp2).length();
                }
            } else {
                System.out.println("Error");
                return "";
            }
        } else if (temp1 instanceof Float) {
            if (temp2 instanceof Integer) {
                switch (relational_operator.lexeme) {
                    case ">":
                        return (Float) temp1 > (Integer) temp2;
                    case "<":
                        return (Float) temp1 < (Integer) temp2;
                    case ">=":
                        return (Float) temp1 >= (Integer) temp2;
                    case "<=":
                        return (Float) temp1 <= (Integer) temp2;
                    case "==":
                        return ((Float) temp1).floatValue() == ((Integer) temp2).intValue();
                    case "!=":
                        return ((Float) temp1).floatValue() != ((Integer) temp2).intValue();
                }
            } else if (temp2 instanceof Float) {
                switch (relational_operator.lexeme) {
                    case ">":
                        return (Float) temp1 > (Float) temp2;
                    case "<":
                        return (Float) temp1 < (Float) temp2;
                    case ">=":
                        return (Float) temp1 >= (Float) temp2;
                    case "<=":
                        return (Float) temp1 <= (Float) temp2;
                    case "==":
                        return temp1.equals(temp2);
                    case "!=":
                        return !temp1.equals(temp2);
                }
            } else if (temp2 instanceof Character) {
                switch (relational_operator.lexeme) {
                    case ">":
                        return (Float) temp1 > (Character) temp2;
                    case "<":
                        return (Float) temp1 < (Character) temp2;
                    case ">=":
                        return (Float) temp1 >= (Character) temp2;
                    case "<=":
                        return (Float) temp1 <= (Character) temp2;
                    case "==":
                        return ((Float) temp1).floatValue() == (Character) temp2;
                    case "!=":
                        return ((Float) temp1).floatValue() != (Character) temp2;
                }
            } else if (temp2 instanceof String) {
                switch (relational_operator.lexeme) {
                    case ">":
                        return (Float) temp1 > ((String) temp2).length();
                    case "<":
                        return (Float) temp1 < ((String) temp2).length();
                    case ">=":
                        return (Float) temp1 >= ((String) temp2).length();
                    case "<=":
                        return (Float) temp1 <= ((String) temp2).length();
                    case "==":
                        return (Float) temp1 == ((String) temp2).length();
                    case "!=":
                        return (Float) temp1 != ((String) temp2).length();
                }
            } else {
                System.out.println("Error");
                return "";
            }
        } else if (temp1 instanceof Character) {
            if (temp2 instanceof Integer) {
                switch (relational_operator.lexeme) {
                    case ">":
                        return (Character) temp1 > (Integer) temp2;
                    case "<":
                        return (Character) temp1 < (Integer) temp2;
                    case ">=":
                        return (Character) temp1 >= (Integer) temp2;
                    case "<=":
                        return (Character) temp1 <= (Integer) temp2;
                    case "==":
                        return temp1 == temp2;
                    case "!=":
                        return temp1 != temp2;
                }
            } else if (temp2 instanceof Float) {
                switch (relational_operator.lexeme) {
                    case ">":
                        return (Character) temp1 > (Float) temp2;
                    case "<":
                        return (Character) temp1 < (Float) temp2;
                    case ">=":
                        return (Character) temp1 >= (Float) temp2;
                    case "<=":
                        return (Character) temp1 <= (Float) temp2;
                    case "==":
                        return (Character) temp1 == ((Float) temp2).floatValue();
                    case "!=":
                        return (Character) temp1 != ((Float) temp2).floatValue();
                }
            } else if (temp2 instanceof Character) {
                switch (relational_operator.lexeme) {
                    case ">":
                        return (Character) temp1 > (Character) temp2;
                    case "<":
                        return (Character) temp1 < (Character) temp2;
                    case ">=":
                        return (Character) temp1 >= (Character) temp2;
                    case "<=":
                        return (Character) temp1 <= (Character) temp2;
                    case "==":
                        return temp1 == temp2;
                    case "!=":
                        return temp1 != temp2;
                }
            } else {
                System.out.println("Error");
                return "";
            }
        } else if (temp1 instanceof String) {
            if (temp2 instanceof Integer) {
                switch (relational_operator.lexeme) {
                    case ">":
                        return ((String) temp1).length() > (Integer) temp2;
                    case "<":
                        return ((String) temp1).length() < (Integer) temp2;
                    case ">=":
                        return ((String) temp1).length() >= (Integer) temp2;
                    case "<=":
                        return ((String) temp1).length() <= (Integer) temp2;
                    case "==":
                        return ((String) temp1).length() == (Integer) temp2;
                    case "!=":
                        return ((String) temp1).length() != (Integer) temp2;
                }
            } else if (temp2 instanceof Float) {
                switch (relational_operator.lexeme) {
                    case ">":
                        return ((String) temp1).length() > (Float) temp2;
                    case "<":
                        return ((String) temp1).length() < (Float) temp2;
                    case ">=":
                        return ((String) temp1).length() >= (Float) temp2;
                    case "<=":
                        return ((String) temp1).length() <= (Float) temp2;
                    case "==":
                        return ((String) temp1).length() == (Float) temp2;
                    case "!=":
                        return ((String) temp1).length() != (Float) temp2;
                }
            } else {
                System.out.println("Error");
                return "";
            }
        } else {
            return "";
        }
        return "";
    }
}

// 28	<relational_expression'> → ε
class RELATIONAL_EXPR_PRIME_2 extends RELATIONAL_EXPR_PRIME {
    RELATIONAL_EXPR_PRIME_2() {
    }

    public Object interpret(RELATIONAL_OPERAND relational_operand) {
        return null;
    }
}

abstract class RELATIONAL_OPERAND {
    static RELATIONAL_OPERAND construct(Symbol sym) {
        switch (sym.ruleNo) {
            case 29:
                return new RELATIONAL_OPERAND_1(sym);
            case 30:
                return new RELATIONAL_OPERAND_2(sym);
            case 31:
                return new RELATIONAL_OPERAND_3(sym);
            case 32:
                return new RELATIONAL_OPERAND_4(sym);
            case 33:
                return new RELATIONAL_OPERAND_5(sym);
            case 34:
                return new RELATIONAL_OPERAND_6(sym);
            default:
                return null;
        }
    }

    public abstract Object interpret();
}

// 29	<relational_operand> → <boolean_constant>
class RELATIONAL_OPERAND_1 extends RELATIONAL_OPERAND {
    private Symbol boolean_constant;

    RELATIONAL_OPERAND_1(Symbol lhs) {
        boolean_constant = lhs.children.get(0);
    }

    public Boolean interpret() {
        return Boolean.parseBoolean(boolean_constant.lexeme);
    }
}

// 30	<relational_operand> → <math_expression>
class RELATIONAL_OPERAND_2 extends RELATIONAL_OPERAND {
    private MATH_EXPRESSION math_expression;

    RELATIONAL_OPERAND_2(Symbol lhs) {
        math_expression = new MATH_EXPRESSION(lhs.children.get(0));
    }

    public Object interpret() {
        return math_expression.interpret();
    }
}

// 31	<relational_operand> → <integer_constant>
class RELATIONAL_OPERAND_3 extends RELATIONAL_OPERAND {
    private Symbol integer_constant;

    RELATIONAL_OPERAND_3(Symbol lhs) {
        integer_constant = lhs.children.get(0);
    }

    public String interpret() {
        return integer_constant.lexeme;
    }
}

// 32	<relational_operand> → <integer_constant>
class RELATIONAL_OPERAND_4 extends RELATIONAL_OPERAND {
    private Symbol float_constant;

    RELATIONAL_OPERAND_4(Symbol lhs) {
        float_constant = lhs.children.get(0);
    }

    public String interpret() {
        return float_constant.lexeme;
    }
}

// 33	<relational_operand> → <string_literal>
class RELATIONAL_OPERAND_5 extends RELATIONAL_OPERAND {
    private Symbol string_literal;

    RELATIONAL_OPERAND_5(Symbol lhs) {
        string_literal = lhs.children.get(0);
    }

    public String interpret() {
        return string_literal.lexeme.substring(1, string_literal.lexeme.length() - 1);
    }
}

// 34	<relational_operand> → <character_constant>
class RELATIONAL_OPERAND_6 extends RELATIONAL_OPERAND {
    private Symbol character_constant;

    RELATIONAL_OPERAND_6(Symbol lhs) {
        character_constant = lhs.children.get(0);
    }

    public String interpret() {
        return character_constant.lexeme;
    }
}

// 35	<data_type> → <integer>
// 36	<data_type> → <float>
// 37	<data_type> → <boolean>
// 38	<data_type> → <character>
// 39	<data_type> → <character_string>
class DATA_TYPE {
    private Symbol type;

    DATA_TYPE(Symbol lhs) {
        type = lhs.children.get(0);
    }

    String interpret() {
        return type.lexeme;
    }
}

// 40	<math_expression> → <multiplicative_expression> <math_expression'>
// 41	<math_expression'> → <additive_operator> <multiplicative_expression> <math_expression'
// 42	<math_expression'> → ε
// 43	<multiplicative_expression> → <term> <multiplicative_expression'>
// 44	<multiplicative_expression'> → <multiplicative_operator> <term> <multiplicative_expression'>
// 45	<multiplicative_expression'> → ε
class MATH_EXPRESSION {
    private LinkedList<Symbol> expression;
    private java.util.Deque<Object> operands = new LinkedList();

    MATH_EXPRESSION(Symbol lhs) {
        expression = EXPRESSION.getExpression(lhs);
    }

    Object interpret() {
        LinkedList<Symbol> copy = new LinkedList<>(expression);
        while (!copy.isEmpty()) {
            Symbol sym = copy.removeFirst();
            if (sym.parent.type.equals("<term>")) {
                //noinspection ConstantConditions
                operands.push(TERM.construct(sym).interpret());
            } else if (sym.parent.type.equals("<input>")) {
                return new INPUT(sym.parent).interpret();
            } else {
                Object temp2 = operands.pop();
                Object temp1 = operands.pop();
                if (temp1 instanceof Integer) {
                    if (temp2 instanceof Integer) {
                        switch (sym.lexeme) {
                            case "+":
                                operands.push((Integer) temp1 + (Integer) temp2);
                                break;
                            case "-":
                                operands.push((Integer) temp1 - (Integer) temp2);
                                break;
                            case "*":
                                operands.push((Integer) temp1 * (Integer) temp2);
                                break;
                            case "/":
                                operands.push((Integer) temp1 / (Integer) temp2);
                                break;
                            case "mod":
                                operands.push((Integer) temp1 % (Integer) temp2);
                                break;
                        }
                    } else if (temp2 instanceof Float) {
                        switch (sym.lexeme) {
                            case "+":
                                operands.push((Integer) temp1 + (Float) temp2);
                                break;
                            case "-":
                                operands.push((Integer) temp1 - (Float) temp2);
                                break;
                            case "*":
                                operands.push((Integer) temp1 * (Float) temp2);
                                break;
                            case "/":
                                operands.push((Integer) temp1 / (Float) temp2);
                                break;
                            case "mod":
                                operands.push((Integer) temp1 % (Float) temp2);
                                break;
                        }
                    } else if (temp2 instanceof Character) {
                        switch (sym.lexeme) {
                            case "+":
                                operands.push((Integer) temp1 + (Character) temp2);
                                break;
                            case "-":
                                operands.push((Integer) temp1 - (Character) temp2);
                                break;
                            case "*":
                                operands.push((Integer) temp1 * (Character) temp2);
                                break;
                            case "/":
                                operands.push((Integer) temp1 / (Character) temp2);
                                break;
                            case "mod":
                                operands.push((Integer) temp1 % (Character) temp2);
                                break;
                        }
                    } else if (temp2 instanceof String) {
                        switch (sym.lexeme) {
                            case "+":
                                operands.push((String) temp2 + temp1);
                                break;
                            case "*":
                                StringBuilder str = new StringBuilder();
                                for (int i = 0; i < (Integer) temp1; i++) {
                                    str.append((String) temp2);
                                }
                                operands.push(str.toString());
                                break;
                            default:
                                operands.push("Invalid arithmetic");
                        }
                    } else {
                        operands.push("Invalid arithmetic");
                    }
                } else if (temp1 instanceof Float) {
                    if (temp2 instanceof Integer) {
                        switch (sym.lexeme) {
                            case "+":
                                operands.push((Float) temp1 + (Integer) temp2);
                                break;
                            case "-":
                                operands.push((Float) temp1 - (Integer) temp2);
                                break;
                            case "*":
                                operands.push((Float) temp1 * (Integer) temp2);
                                break;
                            case "/":
                                operands.push((Float) temp1 / (Integer) temp2);
                                break;
                            case "mod":
                                operands.push((Float) temp1 % (Integer) temp2);
                                break;
                        }
                    } else if (temp2 instanceof Float) {
                        switch (sym.lexeme) {
                            case "+":
                                operands.push((Float) temp1 + (Float) temp2);
                                break;
                            case "-":
                                operands.push((Float) temp1 - (Float) temp2);
                                break;
                            case "*":
                                operands.push((Float) temp1 * (Float) temp2);
                                break;
                            case "/":
                                operands.push((Float) temp1 / (Float) temp2);
                                break;
                            case "mod":
                                operands.push((Float) temp1 % (Float) temp2);
                                break;
                        }
                    } else if (temp2 instanceof Character) {
                        switch (sym.lexeme) {
                            case "+":
                                operands.push((Float) temp1 + (Character) temp2);
                                break;
                            case "-":
                                operands.push((Float) temp1 - (Character) temp2);
                                break;
                            case "*":
                                operands.push((Float) temp1 * (Character) temp2);
                                break;
                            case "/":
                                operands.push((Float) temp1 / (Character) temp2);
                                break;
                            case "mod":
                                operands.push((Float) temp1 % (Character) temp2);
                                break;
                        }
                    } else if (temp2 instanceof String) {
                        switch (sym.lexeme) {
                            case "+":
                                operands.push(temp1 + (String) temp2);
                                break;
                            default:
                                operands.push("Invalid arithmetic");
                        }
                    } else {
                        operands.push("Invalid arithmetic");
                    }
                } else if (temp1 instanceof Character) {
                    if (temp2 instanceof Integer) {
                        switch (sym.lexeme) {
                            case "+":
                                operands.push((Character) temp1 + (Integer) temp2);
                                break;
                            case "-":
                                operands.push((Character) temp1 - (Integer) temp2);
                                break;
                            case "*":
                                operands.push((Character) temp1 * (Integer) temp2);
                                break;
                            case "/":
                                operands.push((Character) temp1 / (Integer) temp2);
                                break;
                            case "mod":
                                operands.push((Character) temp1 % (Integer) temp2);
                                break;
                        }
                    } else if (temp2 instanceof Float) {
                        switch (sym.lexeme) {
                            case "+":
                                operands.push((Character) temp1 + (Float) temp2);
                                break;
                            case "-":
                                operands.push((Character) temp1 - (Float) temp2);
                                break;
                            case "*":
                                operands.push((Character) temp1 * (Float) temp2);
                                break;
                            case "/":
                                operands.push((Character) temp1 / (Float) temp2);
                                break;
                            case "mod":
                                operands.push((Character) temp1 % (Float) temp2);
                                break;
                        }
                    } else if (temp2 instanceof Character) {
                        switch (sym.lexeme) {
                            case "+":
                                operands.push((Character) temp1 + (Character) temp2);
                                break;
                            case "-":
                                operands.push((Character) temp1 - (Character) temp2);
                                break;
                            case "*":
                                operands.push((Character) temp1 * (Character) temp2);
                                break;
                            case "/":
                                operands.push((Character) temp1 / (Character) temp2);
                                break;
                            case "mod":
                                operands.push((Character) temp1 % (Character) temp2);
                                break;
                        }
                    } else {
                        ifString(sym, temp2, temp1);
                    }
                } else if (temp1 instanceof String) {
                    if (temp2 instanceof Integer) {
                        switch (sym.lexeme) {
                            case "+":
                                operands.push((String) temp1 + temp2);
                                break;
                            case "*":
                                StringBuilder str = new StringBuilder();
                                for (int i = 0; i < (Integer) temp2; i++) {
                                    str.append((String) temp1);
                                }
                                operands.push(str.toString());
                                break;
                            default:
                                operands.push("Invalid Arithmetic");
                                break;
                        }
                    } else if (temp2 instanceof Float) {
                        switch (sym.lexeme) {
                            case "+":
                                operands.push((String) temp1 + temp2);
                                break;
                            default:
                                operands.push("Invalid Arithmetic");
                                break;
                        }
                    } else if (temp2 instanceof Character) {
                        switch (sym.lexeme) {
                            case "+":
                                operands.push((String) temp1 + temp2);
                                break;
                            default:
                                operands.push("Invalid Arithmetic");
                                break;
                        }
                    } else if (temp2 instanceof Boolean) {
                        switch (sym.lexeme) {
                            case "+":
                                operands.push((String) temp1 + temp2);
                                break;
                            default:
                                operands.push("Invalid Arithmetic");
                                break;
                        }
                    } else ifString(sym, temp2, temp1);
                } else {
                    operands.push("Invalid arithmetic");
                }
            }
        }
        return operands.pop();
    }

    private void ifString(Symbol sym, Object temp2, Object temp1) {
        if (temp2 instanceof String) {
            if (sym.lexeme.equals("+")) {
                operands.push((String) temp2 + temp1);
            } else {
                operands.push("Invalid arithmetic");
            }
        } else {
            operands.push("Invalid arithmetic");
        }
    }
}

abstract class TERM {
    static TERM construct(Symbol sym) {
        switch (sym.type) {
            case "<identifier>":
                return new TERM_1(sym);
            case "<input>":
                return new TERM_2(sym);
            case "<float_constant>":
                return new TERM_3(sym);
            case "<integer_constant>":
                return new TERM_4(sym);
            case "<character_constant>":
                return new TERM_5(sym);
            case "<string_literal>":
                return new TERM_6(sym);
            case "<boolean_constant>":
                return new TERM_7(sym);
            default:
                return null;
        }
    }

    abstract Object interpret();
}

// 46	<term> → <identifier>
class TERM_1 extends TERM {
    private Symbol identifier;

    TERM_1(Symbol lhs) {
        identifier = lhs.children.get(0);
    }

    Object interpret() {
        String value = (String) Variable.symbolTable.get(identifier.lexeme).value;
        switch (Variable.symbolTable.get(identifier.lexeme).type) {
            case "integer":return Integer.parseInt(value);
            case "float": return Float.parseFloat(value);
            case "character": return value.charAt(0);
            case "boolean": return Boolean.parseBoolean(value);
            default: return value;
        }
    }
}

// 47	<term> → <input>
class TERM_2 extends TERM {
    private INPUT input;

    TERM_2(Symbol lhs) {
        input = new INPUT(lhs.children.get(0));
    }

    Object interpret() {
        return input.interpret();
    }
}

// 48	<term> → <float_constant>
class TERM_3 extends TERM {
    private Symbol float_constant;

    TERM_3(Symbol lhs) {
        float_constant = lhs.children.get(0);
    }

    Float interpret() {
        return Float.parseFloat(float_constant.lexeme);
    }
}

// 49	<term> → <integer_constant>
class TERM_4 extends TERM {
    private Symbol integer_constant;

    TERM_4(Symbol lhs) {
        integer_constant = lhs.children.get(0);
    }

    Integer interpret() {
        return Integer.parseInt(integer_constant.lexeme);
    }
}

// 50	<term> → <character_constant>
class TERM_5 extends TERM {
    private Symbol character_constant;

    TERM_5(Symbol lhs) {
        character_constant = lhs.children.get(0);
    }

    Character interpret() {
        return character_constant.lexeme.charAt(1);
    }
}

// 51	<term> → <string_literal>
class TERM_6 extends TERM {
    private Symbol string_literal;

    TERM_6(Symbol lhs) {
        string_literal = lhs.children.get(0);
    }

    String interpret() {
        return string_literal.lexeme.substring(1, string_literal.lexeme.length() - 1);
    }
}

// 52	<term> → <boolean_constant>
class TERM_7 extends TERM {
    private Symbol boolean_constant;

    TERM_7(Symbol lhs) {
        boolean_constant = lhs.children.get(0);
    }

    Boolean interpret() {
        return Boolean.parseBoolean(boolean_constant.lexeme);
    }
}