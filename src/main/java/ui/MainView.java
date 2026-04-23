package ui;

import core.TypeChar;
import core.TypedStatus;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Builder;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

import static javafx.scene.text.Font.font;

public class MainView implements Builder<Region> {

    public static boolean isMainActive;
    private static CustomTimer timer;
    private static FlowPane pane;
    private static Label timerDisplay;
    private Runnable paneInitializer;
    private Runnable runRestarter;
    private Runnable runResetter;

    //in the constructor for the main view (right side of screen)
    //I'm passing it some runnables for its buttons and one of the Controller's functions to initialize the text pane on the first load.

    public MainView(Runnable paneInitializer, Runnable forButton1, Runnable forButton2) {
        this.timerDisplay = new Label("enter any key to begin.");
        this.paneInitializer = paneInitializer;
        this.runResetter = forButton1;
        this.runRestarter = forButton2;
        this.isMainActive = true;
    }

    /**
     *
     * @return a Region, in this case a borderPane, fully initialized.
     */
    @Override
    public Region build() {

        Font bigFont = font("Courier New", 80);
        Label titletext = new Label("Q W E R T Y");
        titletext.setTextFill(Style.darkred);
        titletext.setFont(bigFont);
        titletext.setPadding(new Insets(50, 0, 0, 10));
        titletext.setAlignment(Pos.BASELINE_LEFT);

        Label line = new Label("--------------------------------------------------------------------------------------------------------");
        line.setBackground(Background.fill(Style.lightred));
        line.setFont(new Font("Montserrat", 18));
        line.setTextFill(Color.ANTIQUEWHITE);
        line.setAlignment(Pos.BASELINE_LEFT);
        line.setPrefWidth(750);
        line.setPrefHeight(40);

        pane = new FlowPane();
        pane.setPrefSize(200, 300);
        pane.setPadding(new Insets(5));
        pane.setBackground(Background.fill(Style.textBkgrndPaint));//getting text pane...
        pane.setPadding(new Insets(20, 40, 0, 40));

        timerDisplay.setPadding(new Insets(20));
        timerDisplay.setFont(Style.DEFAULT_MENU_FONT);
        timerDisplay.setTextFill(Style.lightred);

        timer = new CustomTimer();

        //buttons.
        Button b = new Button("RESTART THIS RUN");
        b.setBackground(Background.fill(Style.mainBkgrndPaint));
        Button b2 = new Button("GET NEW TEXT");
        b2.setBackground(Background.fill(Style.mainBkgrndPaint));
        b.setOnMouseClicked(e -> runRestarter.run());
        b2.setOnMouseClicked(e -> runResetter.run());


        HBox buttonarea = new HBox( b, b2);
        buttonarea.setSpacing(20);
        VBox right = new VBox();
        right.setBackground(Background.fill(Color.WHITE));
        right.getChildren().addAll(titletext, line, buttonarea, pane, timerDisplay);
        right.setSpacing(20);


        paneInitializer.run(); //this sets up the pane to display text.

        return (right);

    }

    /**
     *
     * @param typeCharList List of typeChars used to determine how to render the text pane.
     * @param template the "template" string the user is trying to complete.
     */
    public static void renderTextData(List<TypeChar> typeCharList, String template) {

        pane.getChildren().removeAll(pane.getChildren()); //clearing the pane of its hboxes, if it had any before.

        //need as many Hboxes as we have words in the template...
        HBox[] holder = new HBox[template.split(" ").length];
        for (int i = 0; i < holder.length; i++) {
            holder[i] = new HBox();
        }

        int h = 0;

        for (int j = 0; j < typeCharList.size(); j++) {

            var currentTypeChar = typeCharList.get(j);
            String strValue;
            if (currentTypeChar.getStatus() == TypedStatus.INCORRECT && currentTypeChar.typed()!=(' ')) { //if it's a space we don't want to just slap a space over it
                strValue = String.valueOf(currentTypeChar.typed());
            } else {
                strValue = String.valueOf(currentTypeChar.expected());
            }

            var textFromChar = new Text(strValue);

            textFromChar.setFill(switch (currentTypeChar.getStatus()) {
                case INCORRECT -> Style.mistakeTextPaint;
                case CORRECT -> Style.filledTextPaint;
                case UNREACHED -> Style.blankTextPaint;
            });
            textFromChar.setFont(Style.FontFaces.COURIER);

            holder[h].getChildren().add(textFromChar);
            //checking to see if we're at the end or reached what's supposed to be space
            // (need to add pane and move on to next hbox!)
            boolean needNextbox = (j == typeCharList.size()-1 || String.valueOf(currentTypeChar.expected()).matches("\\s"));

            if (needNextbox) {
                pane.getChildren().add(holder[h]);
                if (h < holder.length - 1) {  //making sure we're still in range/
                    h += 1;
                } else {
                    break;
                }
            }
        }
    }

    public static void startTimer(){
        timer.start();
    }

    public static double stopTimerandGetTime() {
        timer.stop();
        return(timer.getElapsed());
    };

    public static void resetTimer(){
        timer.resetStartTime();
        timerDisplay.setText("enter any key to begin.");
        timer.stop();
    }

    public class CustomTimer extends AnimationTimer {
        private long startTime = -1;
        double elapsed;

        @Override
        public void handle(long l) {

            if (startTime == -1) { //only useful for first time being called.
                startTime = l; //
            }
            elapsed = (l - startTime) / 1000000000.0; //dividing to get value as a double.
            timerDisplay.setText(new DecimalFormat("0.0").format(elapsed));
        }

        public double getElapsed(){
            return(this.elapsed);
        }

        //resets time to -1
        public void resetStartTime(){
            this.startTime = -1;
        }


    }


    /**
     * prompts user to enter name, displays time of run.
     * @param time the time to display
     * @return Optional of a string (name entered)
     */
    public static Optional<String> promptToSend(double time) {

        TextInputDialog d = new TextInputDialog("Milkshake");
        d.setGraphic(null);
        d.setTitle("Run complete!");
        d.setHeaderText("");
        d.getDialogPane().setBackground(Background.fill(Style.mainBkgrndPaint));
        d.setContentText("You're done! And in only " + new DecimalFormat("0.00").format(time) + " seconds. Enter your name and register your run!");
        Optional<String> maybeName = d.showAndWait();

        return (maybeName);
    }


    public static void setStatus(boolean b){
        isMainActive = b;
    }
}



