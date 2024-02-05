package com.kitchenreceiptprint.view;

import com.kitchenreceiptprint.util.ExceptionUtil;
import com.kitchenreceiptprint.util.LocalizationUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;
import java.util.ResourceBundle;

public class AboutLayoutView extends Stage {
    public AboutLayoutView() {
        try {
            ResourceBundle lang = LocalizationUtil.getResourceBundle();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/kitchenreceiptprint/about_layout.fxml"), lang);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            setScene(scene);

            getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icon.png"))));
            setTitle(lang.getString("about") + " " + lang.getString("appname"));
            initModality(Modality.APPLICATION_MODAL);
            setResizable(false);
        } catch (IOException e) {
            ExceptionUtil.handleException(e);
        }
    }
}
