/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.slu.cs311b;

import java.util.Deque;
import java.util.LinkedList;

/**
 *
 * @author d524lab
 */
public class EXPRESSION {

    public static int order = 0;
    public static Deque<Symbol> operatorStack = new LinkedList();
    public static Deque<Integer> depthStack = new LinkedList();
    public static LinkedList<Symbol> postfixExpression = new LinkedList();

    public static LinkedList<Symbol> getExpression(Symbol expr) {
        operatorStack.clear();
        depthStack.clear();
        postfixExpression.clear();
        order = 0;

        traverseExpression(expr, 0);

        postfixExpression.addAll(operatorStack);

        return (LinkedList<Symbol>) postfixExpression.clone();
    }

    private static void traverseExpression(Symbol current, int depth) {
        if (current.lexeme != null) {
            if (order % 2 == 0) {
                postfixExpression.add(current);

            } else if (operatorStack.isEmpty()) {
                operatorStack.push(current);
                depthStack.push(depth);

            } else {
                if (current.equals(operatorStack.peek())) {
                    postfixExpression.add(operatorStack.pop());
                    depthStack.pop();

                } else {
                    while (!depthStack.isEmpty()
                            && (depth < depthStack.peek() || current.equals(operatorStack.peek()))) {

                        postfixExpression.add(operatorStack.pop());
                        depthStack.pop();
                    }
                }

                operatorStack.push(current);
                depthStack.push(depth);
            }

            order++;

        } else {
            for (Symbol child : current.children) {
                traverseExpression(child, depth + 1);
            }
        }
    }
}
