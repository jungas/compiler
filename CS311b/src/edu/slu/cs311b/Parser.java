package edu.slu.cs311b;

import java.io.File;
import java.util.*;

public class Parser {

    public static Token startSymbol;
    public static ArrayList<GrammarRule> BNF = new ArrayList();

    public static void init(String source) {
        try (
                java.util.Scanner fileIn = new java.util.Scanner(new File(source), "UTF-8");) {
            while (fileIn.hasNextLine()) {
                String[] rule = fileIn.nextLine().split("\t+| \u2192 "); // arrow sign = \u2192
//                System.out.println(Arrays.toString(rule) + " " + rule.length);
                BNF.add(new GrammarRule(rule));
            }

            startSymbol = BNF.get(1).lhs;

//            System.out.println(BNF);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Symbol parse(List<Symbol> source) {
        List<Symbol> leftMostDerivation = new LinkedList();
        boolean syntaxError = false;
        int sourceIndex = 0;
        int cursorIndex = 0;

        Symbol lookAhead = source.get(sourceIndex);
        Symbol cursor;
        Symbol root = new Symbol(Symbol.NULL, GrammarRule.startSymbol);

        String sourceString = source.toString().replaceAll("[\\[\\],]", "");
        String sourceFound = "";

        String startSymbolString;
        String skipped = "";
        String derived = "";
        String expanded = "";

        leftMostDerivation.add(root);

        startSymbolString = root.token.name;

        System.out.println();
        System.out.println("PARSING: " + sourceString);
        System.out.println("NOTE: Symbols to the left of | have been derived.\n");
        System.out.print(startSymbolString + " => ");

        while (lookAhead != Symbol.EOF) {
            cursor = leftMostDerivation.get(cursorIndex);

            if (cursor.isEpsilon()) {
                cursorIndex++;
                sourceFound += cursor.token.name + " ";
                skipped += "," + cursor.token.name;

            } else if (cursor.token == lookAhead.token) {
                sourceIndex++;
                cursorIndex++;

//                System.out.println("Found " + lookAhead.token);
                sourceFound += lookAhead.token.name + " ";
                derived += "," + lookAhead.token.name;

                cursor.lexeme = lookAhead.lexeme;

                lookAhead.parent = cursor;
                cursor.children.add(lookAhead);
                
                lookAhead = source.get(sourceIndex);

            } else {
//                System.out.println(lookAhead.token.name + " in " + source);
//                System.out.print(cursor.token.name + " in " + leftMostDerivation + " --> ");
                boolean found = false;
                ArrayList<GrammarRule> ruleList = GrammarRule.ruleMap.get(cursor.token.name);

                if (ruleList != null) {
                    for (GrammarRule rule : ruleList) {
                        
                        if (rule.predictSet.contains(lookAhead.token)) {
                            ArrayList<Token> rhs = rule.rhs;
                            Symbol parent = leftMostDerivation.remove(cursorIndex);
                            parent.ruleNo = rule.ruleNo;
                            
                            for (int i = 0; i < rhs.size(); i++) {
                                Token token = rhs.get(i);
                                Symbol child = new Symbol(parent, token);

                                leftMostDerivation.add(cursorIndex + i, child);
                                parent.addChild(child);
                            }

                            found = true;
                            expanded += "," + cursor.token.name;

                            break;
                        }
                    }
                }

                if (!found) {
                    HashSet<Token> expectedTokens = new HashSet();

                    if (ruleList != null) {
                        for (GrammarRule rule : GrammarRule.ruleMap.get(cursor.token.name)) {
                            expectedTokens.addAll(rule.predictSet);
                        }
                    } else {
                        expectedTokens.add(cursor.token);
                    }

                    Terminal terminal = (Terminal) lookAhead;

                    System.out.println();
                    System.out.println("Error on Line No. " + terminal.lineNo + ". " + expectedTokens + " expected.");
                    System.out.println(Scanner.source.get(terminal.lineNo - 1));
                    System.out.println(String.format("%" + terminal.columnNo + "s", "^"));

                    syntaxError = true;
                    break;
                }

                System.out.println(sourceFound + "| " + leftMostDerivation.toString().replaceAll("[\\[\\],]", "").replaceAll(sourceFound, ""));
//                System.out.println(" ... [" + (derived.length() > 1 ? " Derived: " + derived.substring(1) + ".": "")
//                        + (skipped.length() > 1 ? " Skipped: " + skipped.substring(1) + ".": "")
//                        + (expanded.length() > 1 ? " Expanded: " +  expanded.substring(1) + ".": "")
//                        + " ]"
//                );
                
                derived = skipped = expanded = "";
                
                System.out.print(String.format("%" + startSymbolString.length() + "s => ", ""));

//                System.out.println(leftMostDerivation);
//                System.out.println();
            }

        }

        if (syntaxError) {
            // do something
        } else {
            System.out.println(sourceFound + "|");
        }

        return root;
    }
}

class GrammarRule {

    public static HashMap<String, ArrayList<GrammarRule>> ruleMap = new HashMap();
    public static Token startSymbol = null;

    int ruleNo;
    Token lhs;
    ArrayList<Token> rhs;
    HashSet<Token> predictSet;
    String str;

    /**
     * @param rule a BNF rule defined in the predict set.
     * <p>
     * where: <br>
     * rule[0] - rule #<br>
     * rule[1] - the LHS of the rule<br>
     * rule[2] - the RHS of the rule<br>
     * rule[3] - the rule's predict set<br>
     */
    public GrammarRule(String[] rule) {
        this.ruleNo = Integer.parseInt(rule[0]);
        this.lhs = Scanner.token.get(rule[1]);

        if (GrammarRule.startSymbol == null) {
            GrammarRule.startSymbol = this.lhs;
        }

        this.rhs = new ArrayList();
        for (String symbol : rule[2].split(" ")) {
            this.rhs.add(Scanner.token.get(symbol));
        }

        this.predictSet = new HashSet();
        for (String symbol : rule[3].split(", ")) {
            this.predictSet.add(Scanner.token.get(symbol));
        }

        this.str = ruleNo + " " + lhs.name + " -> " + rhs.toString() + " " + predictSet.toString();

        ArrayList<GrammarRule> ruleList = ruleMap.get(this.lhs.name);
        if (ruleList == null) {
            ruleList = new ArrayList();
            ruleMap.put(this.lhs.name, ruleList);
        }

        ruleList.add(this);
    }

    @Override
    public String toString() {
        return str;
    }
}
