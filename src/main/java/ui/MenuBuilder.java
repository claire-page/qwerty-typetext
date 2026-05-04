package ui;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Builder;

import javafx.scene.control.Button;


//made this into a separate class to separate the passing of navigation (really all there is for the menu)from the other functionality.

public class MenuBuilder implements Builder<Region> {

   final Button[] buttons;
   final Runnable[] runnables;
    /**
     * constructor makes menubuilder object based on recieved titles.
     * @param buttonTitles
     * @param runnables
     */
    public MenuBuilder(String[] buttonTitles, Runnable[] runnables){
        assert(buttonTitles.length==runnables.length);

        this.buttons = new Button[buttonTitles.length];

        for (int i = 0; i < buttonTitles.length; i++){
            buttons[i] = new Button(buttonTitles[i]);
        }
        this.runnables = runnables;

    }

    @Override
    public Region build() {

        VBox left = new VBox();

        for (int i2 = 0; i2< buttons.length; i2++){

            int finalI = i2; //bc lambda.

            buttons[i2].setOnAction(e -> runnables[finalI].run());
            buttons[i2].setPadding(new Insets(50, 20, 50, 10));
            buttons[i2].setBackground(Background.EMPTY);
            buttons[i2].setFont(Font.font("Consolas", 24));

        left.getChildren().add(buttons[i2]);}

        left.setBackground(Background.fill(Style.menuBkgrndPaint));
        left.setPrefHeight(Style.DEFAULT_SCENEHEIGHT);
        left.setPrefWidth(200);
        left.setMaxWidth(200);
        left.setMinWidth(200);
        left.setSpacing(70);
        return(left);
    }
}
