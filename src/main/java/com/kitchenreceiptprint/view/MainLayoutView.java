package com.kitchenreceiptprint.view;

import com.kitchenreceiptprint.util.ExceptionUtil;
import com.kitchenreceiptprint.util.LocalizationUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainLayoutView {
    public MainLayoutView(Stage primaryStage) {
        try {
            ResourceBundle lang = LocalizationUtil.getResourceBundle();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/kitchenreceiptprint/main_layout.fxml"), lang);
            Parent contentRoot = loader.load();

            VBox root = new VBox(contentRoot);

            Scene scene = new Scene(root, 800, 600);
            primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icon.png"))));
            primaryStage.setTitle(lang.getString("appname"));
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            ExceptionUtil.handleException(e);
        }
    }
}
