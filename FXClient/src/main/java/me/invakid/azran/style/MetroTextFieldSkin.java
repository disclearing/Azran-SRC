package me.invakid.azran.style;

import javafx.scene.control.TextField;

public class MetroTextFieldSkin extends TextFieldWithButtonSkin {

    public MetroTextFieldSkin(TextField textField) {
        super(textField);
    }

    protected void rightButtonPressed() {
        getSkinnable().setText("");
    }

}