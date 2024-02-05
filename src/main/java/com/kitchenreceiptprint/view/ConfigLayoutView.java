package com.kitchenreceiptprint.view;

import com.kitchenreceiptprint.util.ExceptionUtil;
import java.io.IOException;
import java.util.Objects;
import java.util.ResourceBundle;

import com.kitchenreceiptprint.util.LocalizationUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfigLayoutView extends Stage {
    public ConfigLayoutView() {
        try {
            ResourceBundle lang = LocalizationUtil.getResourceBundle();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/kitchenreceiptprint/config_layout.fxml"), lang);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            setScene(scene);

            getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icon.png"))));
            setTitle(lang.getString("configftp"));
            initModality(Modality.APPLICATION_MODAL);
            setResizable(false);
        } catch (IOException e) {
            ExceptionUtil.handleException(e);
        }
    }
}
