package edu.slu.cs311b;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

public class ParseTreeViewer extends Application {

    private static final Map<Integer, Integer> RIGHTMOST_COORDINATE_PER_LEVEL = new HashMap();
    private static final Map<Integer, ArrayList<Node>> NODES_PER_LEVEL = new HashMap();

    private static Node rootNode;
    private static Symbol rootSymbol;
    private static int WIDTH;
    private static int HEIGHT;
    private static double SCALED_HEIGHT;

    private static double translateX = 0.0;
    private static double translateY = 0.0;

    GraphicsContext gc;
    public static Text text = new Text();

    public static void show(Symbol root) {
        ParseTreeViewer.rootSymbol = root;

        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Parse Tree");

        Group root = new Group();
        Canvas canvas = new Canvas(3000, 1500);
        gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.save();
//        gc.scale(0.8, 0.8);

        rootNode = createParseTree(ParseTreeViewer.rootSymbol);

        canvas.setHeight(HEIGHT);
        canvas.setWidth(WIDTH);

        SCALED_HEIGHT = HEIGHT;

        drawParseTree(rootNode);

//        Tooltip tooltip = new Tooltip("CS 311B");
//        Tooltip.install(canvas, tooltip);
//
//        canvas.setOnMouseMoved((MouseEvent me) -> {
//            tooltip.setText(me.getX() + "\n" + me.getY());
//            tooltip.setX(me.getSceneX() - 25);
//            tooltip.setY(me.getSceneY() + 25);
//        });

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(canvas);

        scrollPane.setFocusTraversable(true);
        scrollPane.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.isControlDown() ) {
                switch (ke.getText()) {
                    case "+":
                        SCALED_HEIGHT += 10;
                        break;

                    case "-":
                        SCALED_HEIGHT -= 10;
                        break;

                    case "0":
                        SCALED_HEIGHT = HEIGHT;
                        break;

                    default:
                        return;
                }

                double scale = SCALED_HEIGHT / HEIGHT;
                double SCALED_WIDTH = SCALED_HEIGHT * WIDTH / HEIGHT;

                translateX = (SCALED_WIDTH - WIDTH) / 2;
                translateY = (SCALED_HEIGHT - HEIGHT) / 2;

                canvas.setWidth(Math.max(WIDTH, SCALED_WIDTH));
                canvas.setHeight(Math.max(HEIGHT, SCALED_HEIGHT));
                canvas.setScaleX(scale);
                canvas.setScaleY(scale);
                canvas.setTranslateX(translateX);
                canvas.setTranslateY(translateY);

////                gc.clearRect(0, 0, WIDTH, HEIGHT);
//                gc.restore();
//                gc.save();
//                gc.scale(scale, scale);
//                drawParseTree(rootNode);
//                canvas.getGraphicsContext2D().transform(new Affine(new Scale(scale, scale, scale, 0, 0, 0)));
            }
        });

        root.getChildren().add(scrollPane);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setMaximized(true);

        scrollPane.setPrefSize(primaryStage.getWidth() - 18, primaryStage.getHeight() - 38);

    }

    private void drawParseTree(Node current) {
        if (current.children.isEmpty()) {
            gc.setFill(Color.PALEGREEN.desaturate());
            gc.fillRoundRect(current.x - current.width / 2, current.y, current.width + 1, 18, 7, 7);
            gc.setStroke(Color.DARKGREEN);
            gc.strokeRoundRect(current.x - current.width / 2, current.y, current.width + 1, 18, 7, 7);
            gc.setFill(Color.BLACK);
            if (!current.symbol.isEpsilon()) {
                gc.fillText(current.symbol.lexeme, current.x + 3 - current.width / 2, current.y + 13);
            } else {
                gc.fillText(current.label, current.x + 3 - current.width / 2, current.y + 13);
            }

        } else {
            gc.setFill(Color.BLANCHEDALMOND);
            gc.fillRoundRect(current.x - current.width / 2, current.y, current.width + 1, 18, 7, 7);
            gc.setStroke(Color.ORANGE);
            gc.strokeRoundRect(current.x - current.width / 2, current.y, current.width + 1, 18, 7, 7);
            gc.setFill(Color.BLACK);
            gc.fillText(current.label, current.x + 3 - current.width / 2, current.y + 13);
        }

        gc.setStroke(Color.DARKORANGE);
        for (Node child : current.children) {
            gc.strokeLine(current.x, current.y + 18, child.x, child.y);
            drawParseTree(child);
        }
    }

    private Node createParseTree(Symbol root) {
        rootNode = initializeCoordinates(root, null, 0, 100);
        compressCoordinates();

        return rootNode;
    }

    private Node initializeCoordinates(Symbol current, Node parent, int level, int parentX) {
        int width;
        Integer rightmostX = RIGHTMOST_COORDINATE_PER_LEVEL.get(level);

        text.setText(current.children.isEmpty() && !current.isEpsilon() ? current.lexeme : current.token.name);
        width = (int) text.getBoundsInLocal().getWidth() + 6;

        if (rightmostX == null) {
            rightmostX = parentX;

        } else {
            rightmostX = Math.max(rightmostX + width / 2, parentX);
        }

        Node n = new Node(current, parent, rightmostX, level * 50 + 10);
        for (Symbol child : current.children) {
            Node c = initializeCoordinates(child, n, level + 1, rightmostX);

            n.addChild(c);
        }

        if (!n.children.isEmpty()) {
            rightmostX = (n.children.getFirst().x + n.children.getLast().x) / 2;
            n.x = rightmostX;
        }

        RIGHTMOST_COORDINATE_PER_LEVEL.put(level, rightmostX + width / 2 + 15);

        ArrayList<Node> nodeList = NODES_PER_LEVEL.get(level);
        if (nodeList == null) {
            nodeList = new ArrayList();
            NODES_PER_LEVEL.put(level, nodeList);
        }

        nodeList.add(n);
//
//        System.out.println(current.token.name + ": @" + n.x + "," + n.y);
        return n;
    }

    private void compressCoordinates() {
        boolean adjusted;

        do {
            adjusted = false;

            centerParent(rootNode);

            for (int level = 1; level < NODES_PER_LEVEL.size(); level++) {
                ArrayList<Node> list = NODES_PER_LEVEL.get(level);

                for (int i = 1; i < list.size(); i++) {
                    Node thisNode = list.get(i);
                    Node previousNode = list.get(i - 1);

                    if (thisNode.parent == previousNode.parent) {
                        int gap = (thisNode.x - thisNode.width / 2)
                                - (previousNode.x + previousNode.width / 2);

                        gap = Math.min(gap, descendantGap(thisNode.children, level + 1));

                        if (gap != 10) {
                            adjust(thisNode, 10 - gap);
                            adjusted = true;
                        }
                    }
                }
            }

        } while (adjusted);

        Node leftMostNode = rootNode;
        Node rightMostNode = rootNode;

        for (ArrayList<Node> list : NODES_PER_LEVEL.values()) {
            if (list.get(0).x - list.get(0).width / 2
                    < leftMostNode.x - leftMostNode.width / 2) {
                leftMostNode = list.get(0);
            }

            if (list.get(list.size() - 1).x + list.get(list.size() - 1).width / 2
                    > rightMostNode.x + rightMostNode.width / 2) {
                rightMostNode = list.get(list.size() - 1);
            }
        }

        ParseTreeViewer.WIDTH = (rightMostNode.x + rightMostNode.width / 2)
                - (leftMostNode.x - leftMostNode.width / 2) + 40;
        ParseTreeViewer.HEIGHT = NODES_PER_LEVEL.size() * 50 + 10 + 18 + 10;

        adjust(rootNode, 20 - (leftMostNode.x - leftMostNode.width / 2));
    }

    private void adjust(Node n, int offset) {
        n.x += offset;
        for (Node child : n.children) {
            adjust(child, offset);
        }
    }

    private int descendantGap(LinkedList<Node> children, int level) {
        int gap = Integer.MAX_VALUE;

        if (!children.isEmpty()) {
            ArrayList<Node> list = NODES_PER_LEVEL.get(level);
            int i = list.indexOf(children.get(0));

            if (i > 0) {
                Node thisNode = list.get(i);
                Node previousNode = list.get(i - 1);

                gap = (thisNode.x - thisNode.width / 2) - (previousNode.x + previousNode.width / 2);
                gap = Math.min(gap, descendantGap(thisNode.children, level + 1));
            }
        }

        return gap;
    }

    private void centerParent(Node current) {
        if (!current.children.isEmpty()) {
            for (Node child : current.children) {
                centerParent(child);
            }

            current.x = (current.children.getFirst().x + current.children.getLast().x) / 2;
        }
    }
}

class Node {

    private static final Text TEXT = ParseTreeViewer.text;

    int x;
    int y;
    Symbol symbol;

    String label;
    int width;

    Node parent;
    LinkedList<Node> children;

    public Node(Symbol symbol, Node parent, int x, int y) {

        this.symbol = symbol;
        this.label = symbol.children.isEmpty() && !symbol.isEpsilon()? symbol.lexeme : symbol.token.name;
//        this.label = symbol.lexeme != null && symbol != Symbol.NULL ? symbol.lexeme : symbol.token.name;
        this.parent = parent;

        TEXT.setText(this.label);
        this.width = (int) TEXT.getBoundsInLocal().getWidth() + 6;

        this.x = x;
        this.y = y;
        this.children = new LinkedList();
    }

    public void addChild(Node child) {
        this.children.add(child);
    }

    public void setCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return label + ": " + x + " " + y;
    }
}
