package sortvisualizer;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Random;

import static javafx.application.Application.launch;

/**
 * @author Kryptich
 */

/* This class creates and manages the JavaFX main stage and interface logic.
 * It creates a SlotArray instance, from which it receives the array sort
 * visualizer pane.
 *
 * Controls are present to allow for ascending/descending sort, and choosing
 * between insertion / selection algorithm.
 *
 * An option is given to generate a new array.
 */

public class SortVisualizer extends Application {
    private Random rand = new Random();
    private double[] valueBuffer = new double[]{0,0,0,0,0,0,0,0,0,0};
    private SlotArray sA = new SlotArray(valueBuffer);
    private void refreshArray(){
        int animStoredDuration = sA.getAnimDuration();
        double[] freshList = new double[rand.nextInt(5)+10];
        for (int i = 0; i < freshList.length; i++) {
            freshList[i] = (double) SlotArray.getRandom();
        }
        sA = new SlotArray(freshList);
        sA.setAnimDuration(animStoredDuration);
    }

    @Override
    public void start(Stage stage) {

        //Setting title and icon to the Stage
        stage.getIcons().add(new Image(SortVisualizer.class.getResourceAsStream("icon.png")));
        stage.setTitle("Sort Algorithm Visualizer");

        // Buttons for generating and sorting array
        Button button = new Button("Generate new array");
        button.setPrefSize(150, 30);
        button.setFont(Font.font("Segoe UI", FontWeight.LIGHT, FontPosture.REGULAR, 14));

        Button button2 = new Button("Sort!");
        button2.setPrefSize(50, 30);
        button2.setFont(Font.font("Segoe UI", FontWeight.LIGHT, FontPosture.REGULAR, 14));

        // SPEED CONTROL SETUP
        Label sliderLabel = new Label("Speed");
        sliderLabel.setFont(new Font("Segoe UI", 12));
        Slider slider = new Slider();
        slider.setMin(0);
        slider.setMax(100);
        slider.setValue(90);
        slider.setPrefWidth(360.0);
        sA.setAnimDuration((int)((((100-slider.getValue())/100))*900)+100);
        slider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number >
                                                observable, Number oldValue, Number newValue)
                    {
                        int newSpeed = (int)((((100-newValue.doubleValue())/100))*450)+50;
                        System.out.println("New anim speed: " + newSpeed);
                        // set individual anim speed within range of 50ms-500ms
                        sA.setAnimDuration(newSpeed);
                    }
                });

        Button ascDesc= new Button("▲");
        ascDesc.setPrefSize(50, 30);
        ascDesc.setFont(Font.font("Segoe UI", FontWeight.NORMAL, FontPosture.REGULAR, 14));

        ascDesc.setOnAction(value ->  {
            if (ascDesc.getText().equals("▲")){
                ascDesc.setText("▼");
            } else {
                ascDesc.setText("▲");
            }
        });

        // SETUP SORT ALGORITHM CHOOSER BUTTON
        final ToggleGroup tGroup = new ToggleGroup();

        RadioButton rb1 = new RadioButton("Selection");
        rb1.setToggleGroup(tGroup);
        rb1.setSelected(true);

        RadioButton rb2 = new RadioButton("Insertion");
        rb2.setToggleGroup(tGroup);

        // BOX UP THE CONTROLS IN A NEAT FORMAT
        HBox sliderBox = new HBox(24,sliderLabel,slider);
        sliderBox.setAlignment(Pos.CENTER);

        VBox radioBox = new VBox(24,rb1,rb2);
        radioBox.setAlignment(Pos.CENTER);

        HBox hBoxTopBtns = new HBox(24,button,button2,ascDesc);
        hBoxTopBtns.setAlignment(Pos.CENTER);

        VBox topBtnsAndSlider = new VBox(24,hBoxTopBtns,sliderBox);
        topBtnsAndSlider.setAlignment(Pos.CENTER);

        HBox controls = new HBox(24,topBtnsAndSlider,radioBox);
        controls.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(20, controls, sA.getRootBox());
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(30));
        // spacing = 8

        button.setOnAction(value ->  {
            refreshArray();
            System.out.println(vBox.getChildren().set(1,sA.getRootBox()));
        });


        button2.setOnAction(value ->  {
            sA.setSorting(false);
            System.out.println();
            if (((RadioButton)tGroup.getSelectedToggle()).getText().equals("Selection")){
                if (ascDesc.getText().equals("▲")){
                    sA.setAscending(true);
                } else {
                    sA.setAscending(false);
                }
                sA.selectionSort();

            } else {
                if (ascDesc.getText().equals("▲")){
                    sA.setAscending(true);
                } else {
                    sA.setAscending(false);
                }
                sA.insertionSort();
            }
        });

        //Displaying the contents of the stage
        Scene scene = new Scene(vBox, 600, 250);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String args[]) {
        launch(args);
    }
}
