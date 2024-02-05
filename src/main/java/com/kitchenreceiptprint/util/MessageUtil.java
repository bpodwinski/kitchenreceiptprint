package com.kitchenreceiptprint.util;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class MessageUtil {
    private final TextArea textArea;

    public MessageUtil(TextArea textArea) {
        this.textArea = textArea;
    }

    public void appendMessage(String message) {
        Platform.runLater(() -> textArea.appendText(message + "\n"));
    }
}
