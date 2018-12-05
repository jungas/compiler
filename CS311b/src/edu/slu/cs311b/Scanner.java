package edu.slu.cs311b;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Scanner {

    public static ArrayList<String> source = new ArrayList();
    public static Map<String, Token> token = new HashMap();
    public static Map<Token, Pattern> tokenPatterns = new LinkedHashMap();

    public static void init(String source) {

        token.clear();
        try (
                java.util.Scanner fileIn = new java.util.Scanner(new File("first.txt"));) {
            while (fileIn.hasNextLine()) {
                String[] temp = fileIn.nextLine().split("\t");

                token.put(temp[0], new Token(temp[0], temp[0].equals(temp[1])));
            }
        } catch (Exception e) {
            System.out.println("\t" + e.getMessage());
            System.exit(1);
        }

        tokenPatterns.clear();

        try (
                java.util.Scanner fileIn = new java.util.Scanner(new File(source));
        ) {
            while (fileIn.hasNextLine()) {
                String textIn = fileIn.nextLine();

                if (!textIn.isEmpty()) {
                    String[] temp = textIn.split(" *-> *");
                    System.out.println(temp[0]);
                    Token t = token.get(temp[0]);

                    if (t != null) {
                        tokenPatterns.put(t, Pattern.compile(temp[1]));
                    } else {
                        throw new Exception("Unrecognized token: " + temp[0]);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("\t" + e.getMessage());
            System.exit(1);
        }
    }

    public static List<Symbol> scan(String sourceProgram) {
        if (tokenPatterns.isEmpty()) {
            init("token-lexeme.txt");
        }

        ArrayList<Symbol> list = new ArrayList();

        try {
            java.util.Scanner programScanner = new java.util.Scanner(new File(sourceProgram));
            Matcher matcher;
            StringBuilder statement = new StringBuilder();

            int lineNo = 1;
            int columnNo;
            int startIndex;

            System.out.println("SCANNING: \"" + sourceProgram + "\"");

            source.clear();
            while (programScanner.hasNextLine()) {
                statement.setLength(0);
                statement.append(programScanner.nextLine().replaceAll("\t", "    "));
                source.add(statement.toString());

                System.out.println("Line #" + lineNo + ".");
                System.out.println("Statement scanned: \"" + statement.toString().trim() + "\"");
                System.out.print("Tokens found: ");
                // remove trailing whitespaces
                for (int i = statement.length() - 1;
                     i >= 0 && Character.isWhitespace(statement.charAt(i));
                     i--) {
                    statement.deleteCharAt(i);
                }

                columnNo = 1;

                while (statement.length() > 0) {
                    startIndex = 0;

                    // skip leading whitespaces
                    while (Character.isWhitespace(statement.charAt(startIndex))) {
                        startIndex++;
                        columnNo++;
                    }

                    boolean found = false;

                    for (Map.Entry<Token, Pattern> e : tokenPatterns.entrySet()) {
                        matcher = e.getValue().matcher(statement);

                        if (matcher.find() && matcher.start() == startIndex) {
                            String lexeme = statement.substring(matcher.start(), matcher.end());

                            list.add(new Terminal(e.getKey(), lexeme, lineNo, columnNo));
//                            statement = statement.substring(matcher.end());
                            statement.delete(0, matcher.end());

//                            System.out.println(e.getKey().name + " (" + lexeme + ") found at ln " + lineNo + ", col " + columnNo);
                            System.out.print(e.getKey().name + ":\"" + lexeme + "\" ");

                            columnNo += lexeme.length();
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        System.out.println();
                        System.out.println("Error on Line No. " + lineNo + ": Unknown symbol.");
                        System.out.println(source.get(lineNo - 1));
                        System.out.println(String.format("%" + columnNo + "s", "^"));

                        list.add(new Terminal(Token.ERROR, "", lineNo, columnNo));
                        break;
                    }
                }

                System.out.println();
                System.out.println();

                lineNo++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        list.add(Symbol.EOF);

        System.out.println("OUTPUT: " + list.toString().replaceAll("[\\[\\],]", ""));

        return list;
    }

// epsilon (i.e. 'ε') == 0x03B5
}
