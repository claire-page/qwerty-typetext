package ui;

import controllers.Control;
import io.database.DBControl;
import io.database.QueryFilter;
import javafx.scene.Parent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Builder;

//TODO- TEST!!!
public class BasicLayoutBuilder implements Builder<Parent> {

    static Control control;
    static DBControl dbControl;
    static Region results;
    static Region main;

    public BasicLayoutBuilder(Control ctrl){
        control = ctrl;

    }

    /**
     * builds borderpane - left side is the menu,
     * right is a stackPane with two panes on top of one another.
     * What appears to be scene switching is actually just toggling the visibility of
     * the main (topmost) pane.
     * @return initialized borderpane
     */
    @Override
    public Parent build() {
        BorderPane bp = new BorderPane();

        //the things down below doesn't need to be an array, this is kind of an artifact from when I had a different layout in mind.
        //initially where were supposed to be more buttons, hence the existence of the whole MenuBuilder class.

        String[] HomeMenuOptions = {"View Results"};
        Runnable[] Runnables = {BasicLayoutBuilder::hideMain};

        //the whole point of this pattern is that I'm implementing an interface that allows you to provide a reference to a component;s build
        //method as a component itself.
        Region left = new MenuBuilder(HomeMenuOptions, Runnables).build();

        StackPane stack = new StackPane();
        main = new MainView(control::drawPane, control::resetNewRun,  control::resetSameRun).build();
        results = new ResultScreen(() -> Control.stringifyRunData(DBControl.getEntries()), BasicLayoutBuilder::showMain).build();
        stack.getChildren().addAll(results, main);
        stack.requestLayout();
        bp.setLeft(left);
        bp.setRight(stack);
        bp.addEventFilter(KeyEvent.ANY, e -> control.delegateKeyEvents(e));
        bp.setBackground(Background.fill(Color.WHITE));
        bp.setPrefSize(Style.DEFAULT_SCENEWIDTH, Style.DEFAULT_SCENEHEIGHT);
        return(bp);
    }

    //some functions to do with showing and hiding the screens.
    public static void hideMain(){

        if (main.isVisible()){
            main.setVisible(false);
            control.setMainStatus(false);
            control.resetSameRun(); //making sure it's reset when the user returns.
        }
    }
    public static void showMain(){
        if (!main.isVisible()){
            main.setVisible(true);
            control.setMainStatus(true);
        }
    }
}
