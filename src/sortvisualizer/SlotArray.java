package sortvisualizer;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * @author Kryptich
 */

/* This class creates and manages the JavaFX view node (an HBox) containing
 * the array state, array visualizer and animation logic.
 */

public class SlotArray {

    private static Random rand = new Random();
    private int ANIM_SPEED_MULTIPLIER = 600;
    private int MAX_VERTICAL_ANIM_DEVIATION = 20;
    private Rectangle FOCUS_INDICATOR;
    private Queue<Pair> sortSwaps = new LinkedList<>();
    private int[] indexState;       // OLD > NEW INDEX MAPPING
    private int len = 0;            // CURRENT ARRAY LENGTH
    private boolean flipper = true; // SYNC SWAP ANIMS
    private double[] sortState;     // CURRENT ARRAY STATE
    private double[] calculatedWidths;   // WIDTHS FOR EACH TXT ITEM
    private double childHeight;          // HEIGHT OF TXT ITEMS
    private Pane root = new Pane();      // DISPLAY ELEMENT
    private HBox hBoxArray = new HBox(); // DISPLAY ELEM FOR EXPORT
    private Pair pair;                   // CURRENT OPERATION IN SORT
    private boolean sorting = false;     //
    private boolean ascending = true;
    private double[] positions;

    public SlotArray(double[] values) {
        len = values.length;

        this.indexState = new int[len];
        this.calculatedWidths = new double[len];
        this.sortState = values;
        Color THEME_COLOR = Color.color(Math.random() / 2, Math.random() / 2, Math.random() / 2);


        root.setPadding(new Insets(15, 12, 15, 12));
        int maxStrWidth = 1;
        double SORTER_BOX_HEIGHT = 130;
        for (int i = 0; i < len; i++) {
            NumberFormat nf = new DecimalFormat("##.##");
            String item = nf.format(values[i]);
            Text t = new Text();
            t.setY(SORTER_BOX_HEIGHT /2); // manually vertical-align text in parent
            t.setText(item);
            t.setFill(THEME_COLOR);
            root.getChildren().add(t);
        }

        Group g = new Group();
        g.getChildren().add(root);
        hBoxArray.getChildren().add(g);
        hBoxArray.setPrefHeight(SORTER_BOX_HEIGHT);
        double SORTER_BOX_WIDTH = 600;
        hBoxArray.setPrefWidth(SORTER_BOX_WIDTH);
        double totalArrayWidth = 0;
        double calculatedFontSize = (SORTER_BOX_WIDTH)/(len*2.5);
        System.out.println(" | Calculated font size:" + calculatedFontSize + " | ");
        childHeight = root.getChildren().get(0).getLayoutBounds().getHeight();
        for (int i = 0; i < len; i++) {
            ((Text)root.getChildren().get(i)).setFont(Font.font("Segoe UI",
                    FontWeight.BOLD,
                    FontPosture.REGULAR,
                    calculatedFontSize));
            double childWidth = root.getChildren().get(i).getLayoutBounds().getWidth() + 13;
            calculatedWidths[i] = childWidth;
            totalArrayWidth += childWidth;
            indexState[i] = i;
            root.getChildren().get(i).setTranslateX(posAt(calculatedWidths, i));
        }

//        FOCUS_INDICATOR = new Rectangle(posAt(calculatedWidths, 6)-6.5, SORTER_BOX_HEIGHT/2-(childHeight*1.5), calculatedWidths[6], childHeight*2);
        FOCUS_INDICATOR = new Rectangle(0,
                                        (SORTER_BOX_HEIGHT /2)+20,
                                        calculatedWidths[6],
                                        3);
        FOCUS_INDICATOR.setFill(Color.color(THEME_COLOR.getRed()*1.5,
                THEME_COLOR.getGreen()*1.5,
                THEME_COLOR.getBlue()*1.5
                ));
        root.getChildren().add(FOCUS_INDICATOR);
        System.out.println("root Width: " + totalArrayWidth);
        g.setTranslateX(270-(totalArrayWidth/2));
        System.out.println(Arrays.toString(calculatedWidths));

    }

    public static int getRandom(){
        int sign = 1;
        int random = rand.nextInt(20);
        if (random > 10){
            sign = -1;
        }
        random = new Random().nextInt(50);
        random *= sign;
        return random;
    }

    private static void slidingSwapPair(double[] target, Pair pair){
        double keyBuffer = target[pair.first];
        for (int i = pair.first; i > pair.second; i--) {
            target[i]=target[i-1];
        }
        target[pair.second] = keyBuffer;
    }

    private static void slidingSwapPair(int[] target, Pair pair){
        int keyBuffer = target[pair.first];
        if (pair.first-pair.second == 1) { // special case: swap adjacent
            target[pair.first] = target[pair.second];
        } else {
            for (int i = pair.first; i > pair.second; i--) {
                target[i]=target[i-1];
            }
        }
        target[pair.second] = keyBuffer;
    }

    public void setSorting(boolean sorting) {
        this.sorting = sorting;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    public int getAnimDuration() {
        return ANIM_SPEED_MULTIPLIER;
    }

    public void setAnimDuration(int SHORTEST_TRANSITION_DURATION) {
        this.ANIM_SPEED_MULTIPLIER = SHORTEST_TRANSITION_DURATION;
    }

    public HBox getRootBox() {
        return hBoxArray;
    }

    public void selectionSort() {
        if (!sorting){ // Get a new list of instructions for sorting the array
            sortSwaps = SelectionSortVerbose.sort(sortState,ascending);
        }
        if (!sortSwaps.isEmpty()) {  // If there are still instructions left
            pair = sortSwaps.remove(); // Dequeue one pair of instructions

            FOCUS_INDICATOR.setTranslateX(posAt(calculatedWidths, pair.first)-6.5);
            FOCUS_INDICATOR.setWidth(calculatedWidths[pair.first]);
            PauseTransition pauseT = new PauseTransition();
            pauseT.setDuration(Duration.millis(ANIM_SPEED_MULTIPLIER));

            System.out.println("a pair should indeed be swapped at indices: (" + pair.first + ", " + pair.second + ")");
            swapPair(sortState, pair);
            swapPair(indexState, pair);
            swapPair(calculatedWidths, pair);
            System.out.println(Arrays.toString(this.sortState) + "<<< THE ARRAY SORT STATE IN UPDATEPOSITIONS");

            ParallelTransition parallelTransition = new ParallelTransition();
            for (int i = 0; i < len; i++) {
                TranslateTransition moveX = new TranslateTransition();
                TranslateTransition moveY = new TranslateTransition();
                moveY.setDuration(Duration.millis(ANIM_SPEED_MULTIPLIER));

                if (i == indexState[pair.first] || i == indexState[pair.second]){

                    moveY.setFromY(0);
                    int verticalDeviation = rand.nextInt(MAX_VERTICAL_ANIM_DEVIATION)+(int)childHeight;
                    /* This boolean ticker ensures the array pair being swapped
                    go in opposite directions vertically (avoiding collisions) */
                    if (pair.first==pair.second){
                        System.out.println("no swap");
                        flipper = true;
                    } else {
                        if (flipper){ verticalDeviation *= -1; }
                        flipper = !flipper;
                        moveY.setToY(verticalDeviation);
                    }

                    moveY.setInterpolator(Interpolator.EASE_IN);
                    moveY.setCycleCount(2);
                    moveY.setAutoReverse(true);
                }
                moveY.setNode(root.getChildren().get(i));

                moveX.setDuration(Duration.millis(ANIM_SPEED_MULTIPLIER *2));
                moveX.setToX(posAt(calculatedWidths,i));
                moveX.setCycleCount(1);
                moveX.setAutoReverse(false);
                moveX.setNode(root.getChildren().get(indexState[i]));
                ParallelTransition pT = new ParallelTransition();
                pT.getChildren().addAll(moveX,moveY);
                parallelTransition.getChildren().add(pT);
            }
            SequentialTransition sT = new SequentialTransition(pauseT,parallelTransition);
            sT.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    selectionSort();
                }
            });
            sT.play();
        }
        sorting = true; // A sort is already in progress if we made it this far
    }

    public void insertionSort() {
        if (!sorting){ // Get a new list of instructions for sorting the array
            sortSwaps = InsertionSortVerbose.sort(sortState,ascending);
        }
        if (!sortSwaps.isEmpty()) { // If there are still instructions left

            pair = sortSwaps.remove(); // Dequeue one pair of instructions

            System.out.println("A pair should indeed be swapped at indices: (" + pair.first + ", " + pair.second + ")");
            System.out.println("Therefore, any entries from " + pair.second + " to " + (pair.first-1) + " must be shifted to the right by 1 space." );

            FOCUS_INDICATOR.setTranslateX(posAt(calculatedWidths, pair.first)-6.5);
            FOCUS_INDICATOR.setWidth(calculatedWidths[pair.first]);
            PauseTransition pauseT = new PauseTransition();
            pauseT.setDuration(Duration.millis(ANIM_SPEED_MULTIPLIER*2));

            slidingSwapPair(sortState, pair);
            slidingSwapPair(indexState, pair);
            slidingSwapPair(calculatedWidths, pair);
            System.out.println(Arrays.toString(this.sortState) + "<<< THE ARRAY SORT STATE IN UPDATEPOSITIONS");
            ParallelTransition pTMother = new ParallelTransition();
            ParallelTransition pTSwapFirst,pTSwapSecond;
            if (pair.first == pair.second){
                pTSwapFirst = moveNodeTo(root.getChildren().get(indexState[pair.first]),
                        posAt(calculatedWidths,pair.first), -1);
                pTSwapSecond = moveNodeTo(root.getChildren().get(indexState[pair.second]),
                        posAt(calculatedWidths,pair.second), -1);
            } else {
                pTSwapFirst = moveNodeTo(root.getChildren().get(indexState[pair.first]),
                        posAt(calculatedWidths,pair.first), 0);
                pTSwapSecond = moveNodeTo(root.getChildren().get(indexState[pair.second]),
                        posAt(calculatedWidths,pair.second), 1);
            }


            ParallelTransition pTInner = new ParallelTransition();
            for (int i = pair.second+1; i < pair.first; i++) {

                ParallelTransition pT = moveNodeTo(root.getChildren().get(indexState[i]),
                                                        posAt(calculatedWidths,i), 0);
                pTInner.getChildren().add(pT);

            }


            pTMother.getChildren().addAll(pTSwapFirst,pTSwapSecond,pTInner);
            SequentialTransition sT = new SequentialTransition(pauseT,pTMother);

            sT.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    insertionSort();
                }
            });
            sT.play();
        }
        sorting = true;
    }

    private ParallelTransition moveNodeTo(Node n, double xTarget, int yBounce){
        ParallelTransition pT = new ParallelTransition();
        TranslateTransition moveX = new TranslateTransition();

        moveX.setDuration(Duration.millis(ANIM_SPEED_MULTIPLIER *2));
        moveX.setToX(xTarget);
        moveX.setCycleCount(1);
        moveX.setAutoReverse(false);
        moveX.setNode(n);
        pT.getChildren().add(moveX);

        if (yBounce != 0){
            TranslateTransition moveY = new TranslateTransition();
            moveY.setDuration(Duration.millis(ANIM_SPEED_MULTIPLIER));
            moveY.setFromY(0);
            int verticalDeviation = rand.nextInt(MAX_VERTICAL_ANIM_DEVIATION)+(int)childHeight;
            /* This global boolean ticker ensures the array pair being swapped
            go in opposite directions vertically (avoiding collisions) */
            if (yBounce == -1){
                verticalDeviation *= -1;
            } else {
                if (flipper){ verticalDeviation *= -1; }
                flipper = !flipper;
                moveY.setToY(verticalDeviation);
            }
            moveY.setInterpolator(Interpolator.EASE_IN);
            moveY.setCycleCount(2);
            moveY.setAutoReverse(true);
            moveY.setNode(n);
            pT.getChildren().add(moveY);
        }
        return pT;
    }

    /* Returns */
    private double posAt(double[] calculatedWidths, int range) {
        int accum = 0;
        for (int j = 0; j < range; j++) {
            accum += calculatedWidths[j];
        }
        return accum + 10;
    }

    private void swapPair(double[] target, Pair pair) {
        double buffer = target[pair.first]; // temp
        target[pair.first] = target[pair.second]; // first swap
        target[pair.second] = buffer;          // second swap
    }
    
    private void swapPair(int[] target, Pair pair) {
        int buffer = target[pair.first]; // temp
        target[pair.first] = target[pair.second]; // first swap
        target[pair.second] = buffer;          // second swap
    }

}
