package controllers;

import com.sun.tools.javac.Main;
import core.RunData;
import core.RunTracker;

import core.TextToType;
import io.database.DBControl;
import io.database.DatabaseInteractor;

import io.database.QueryFilter;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import core.TypeChar;
import ui.MainView;
import ui.ResultScreen;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Control {
    private static RunTracker run;
    private static String expected;
    private static String actual;
    private RunData endData; //only exists if

    public Control() {
        run = new RunTracker();
        expected = TextToType.getRandomtxt();
        actual = "";

    }

    /// ////I kind of view this as the switchboard for the game process. It triggers appropriate functions to change both the data
    /// and the view in response to the view's event listeners.
    /**
     *
     * @param e Keyevent
     */
    public void delegateKeyEvents(KeyEvent e) {

        //checking if main is active. Otherwise, we don't need to be triggering this.
        // this allows other input fields to receive KeyEvents!

        if (getMainStatus()){

        e.consume(); //consuming the event. yum.
        //only dealing with key pressed + typed events to avoid duplicates, bc
        //multiple events are triggered by a key press (pressed/typed/released),
        if (e.getEventType().equals(KeyEvent.KEY_PRESSED) || e.getEventType().equals(KeyEvent.KEY_TYPED)) {

            //is this the first keystroke? if yes, start tracking + tell the view to start the timer!
            if (run.getKeystrokes() == 0) {
                run.logKeyStroke();
                MainView.startTimer();
            }

            if (e.getCode().equals(KeyCode.BACK_SPACE)) {
                e.consume();
                run.logBackspace();
                run.logKeyStroke();
                //making sure we're not "going off the edge" before clipping the user string
                actual = (actual.isEmpty()) ? "" : actual.substring(0, actual.length() - 1);

                //is it a character/punctuation?
            } else if (e.getEventType() == KeyEvent.KEY_TYPED && e.getCharacter().matches("[a-zA-Z\\s\\h\\p{P}]")) {
                actual += e.getCharacter(); //add character to the user string.
                run.logKeyStroke();
            }
            drawPane();

/// end of game behaviour.
            if (expected.equals(actual)) {
                drawPane();
                var finaltime = MainView.stopTimerandGetTime();
                System.out.println(finaltime);
                var maybeString = MainView.promptToSend(finaltime);
                //if the user pressed continue.
                endData = getDataToSend(finaltime, maybeString, run); //gathering data.

                //testing if the connection is available to send data to..
                if (DBControl.isConnectionValid()) {
                    DBControl.passDataToInteractor(endData);
                } else {
                    displayDBAlert();
                }
                ResultScreen.updateTable();
                resetNewRun();
                //so when the user gets back from the popup, they already have a new run loaded!
            }

        }}

    }

    public static List<TypeChar> generateTextData(String template, String actual) {
        var textInfo = new ArrayList<TypeChar>(); //starting off as an ArrayList
        for (int j = 0; j < template.length(); j++) {
            //whether the user has typed something can be determined by whether the index is still within
            //the size of the user (actual) text.
            char entered = (j < actual.length()) ? actual.charAt(j) : ' '; //cannot have empty char. so this will have to do...I think it should be fine
            textInfo.add(new TypeChar(entered, template.charAt(j)));
        }
        return ((textInfo).stream().toList()); //listifying as per that one lecture to make it immutable.
    }

    /// //////////////////////////////////////////////////////////////////////////////////////////////////
    /// these functions deal with interacting with a DatabaseInteractor object we use to access a database.

    public static RunData getDataToSend(double time, Optional<String> result, RunTracker tracker) {
        String name = result.orElse("Milkshake");
        return (new RunData(time, name, tracker.getKeystrokes(), tracker.getBacktracks(), expected.split(" ").length, expected.length()));
    }

    /**
     *
     * @param name           -> name to query for.
     * @param selectedFilter -> data filter applied by user on the toggle group in the results view screen.
     */
    public static void applyFilters(String name, QueryFilter selectedFilter) {
        DBControl.setInteractorSQLString(name, selectedFilter);
        ResultScreen.updateTable();
    }

    public static void displayDBAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.contentTextProperty().set("error connecting to the database. try again...another time...maybe....");
        alert.showAndWait();
    }

    /**
     * provides formatted strings containing data retrieved from RunData records.
     * for use by the Result Screen, mainly.
     *
     * @return List of Lists of Strings.
     */
    public static List<List<String>> stringifyRunData(List<RunData> rawRunData) {

        var all = new ArrayList<List<String>>();
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        for (RunData entry : rawRunData) {

            ArrayList<String> alist = new ArrayList<>();
            alist.add(String.valueOf(decimalFormat.format(entry.time())));
            alist.add(entry.name());
            alist.add(String.valueOf(decimalFormat.format(entry.getWordsPerMinute())));
            alist.add(String.valueOf(decimalFormat.format(entry.getCharsPerSecond())));
            alist.add(String.valueOf(entry.getFaults()));

            all.add(alist.stream().toList());
        }
        return (all.stream().toList());
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * function for rendering the pane in the view based on the current state of the model.
     * this is kind of a two-parter, which isn't ideal...but
     * it also makes it reusable both as a runnable function during the
     * initialization of the view.
     * might not follow SRP??, but it abbreviates things and is easy to understand imo.
     */
    public void drawPane() {
        var data = generateTextData(expected, actual);
        MainView.renderTextData(data, expected);
    }

    public void resetSameRun() {
        run = new RunTracker();
        actual = "";
        drawPane();
        MainView.resetTimer();
    }

    public void resetNewRun() {
        run = new RunTracker();
        actual = "";
        var oldtxt = expected;

        while (true) {
            //making sure the new text is different.
            var newtxt = TextToType.getRandomtxt();

            if (!oldtxt.equals(newtxt)) {
                expected = newtxt;
                break;
            }
        }
        drawPane();
        MainView.resetTimer();
    }


    /// /////////////////////////////////////////////////////////////////////////////

    public void setMainStatus(boolean b) {
        MainView.setStatus(b);
    }

    public boolean getMainStatus(){
        return(MainView.isMainActive);
    }

}




