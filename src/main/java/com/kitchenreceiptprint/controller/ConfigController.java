package com.kitchenreceiptprint.controller;

import com.kitchenreceiptprint.model.DatabaseModel;
import com.kitchenreceiptprint.util.CryptoUtil;
import com.kitchenreceiptprint.util.ExceptionUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class ConfigController
 *
 * Manages the configuration settings of the Kitchen Receipt Printing application. This controller
 * is responsible for initializing the configuration view with current settings from the database,
 * allowing the user to update these settings, and saving the changes back to the database. It covers
 * settings such as FTP server details, polling interval, and printer selections.
 *
 * Responsibilities:
 * - Load and display current configuration settings from the database on the UI.
 * - Capture user updates to configuration settings such as FTP server info, credentials, polling interval,
 *   and selected printers.
 * - Save updated configuration settings back to the database and apply changes.
 *
 * Usage:
 * This class is linked to an FXML file defining the configuration view layout and is instantiated
 * by the JavaFX framework when navigating to the configuration screen.
 *
 * Author: Artisan Webmater
 * Creation Date: 05/02/2024
 * Last Modification: 05/02/2024
 *
 * Notes:
 * - The class utilizes CryptoUtil for encrypting and decrypting sensitive information like FTP passwords.
 * - Handles the dynamic listing and selection of available printers for receipt printing.
 * - Incorporates exception handling to manage any issues during the configuration process.
 */
public class ConfigController {
    @FXML
    private ChoiceBox<String> languageChoiceBox;

    @FXML
    private TextField serverField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField intervalField;

    @FXML
    private ListView<String> printerList;

    @FXML
    private Button save;

    private final DatabaseModel model;

    public ConfigController() {
        this.model = DatabaseModel.getInstance();
    }

    @FXML
    public void initialize() {

        try {
            //languageChoiceBox.setValue(model.getConfiguration("language") != null ? model.getConfiguration("language") : "");
            serverField.setText(model.getConfiguration("ftp_server") != null ? model.getConfiguration("ftp_server") : "");
            usernameField.setText(model.getConfiguration("ftp_username") != null ? model.getConfiguration("ftp_username") : "");
            passwordField.setText(model.getConfiguration("ftp_password") != null ? CryptoUtil.decrypt(model.getConfiguration("ftp_password")) : "");
            intervalField.setText(model.getConfiguration("interval") != null ? model.getConfiguration("interval") : "");

            printerList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            printerList.setItems(PrintController.getPrinters());

            List<String> savedPrinters = model.getAllPrinter();
            Set<String> savedPrintersSet = new HashSet<>(savedPrinters);

            for (String printer : printerList.getItems()) {
                if (savedPrintersSet.contains(printer)) {
                    printerList.getSelectionModel().select(printer);
                }
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
    }

    @FXML
    private void saveConfiguration() {
        try {
            //model.addConfiguration("language", languageChoiceBox.getValue());
            model.addConfiguration("ftp_server", serverField.getText());
            model.addConfiguration("ftp_username", usernameField.getText());
            model.addConfiguration("ftp_password", CryptoUtil.encrypt(passwordField.getText()));
            model.addConfiguration("interval", intervalField.getText());

            ObservableList<String> selectedPrinters = printerList.getSelectionModel().getSelectedItems();

            if (selectedPrinters != null &&!selectedPrinters.isEmpty()) {
                model.delPrinters();
                model.addPrinters(selectedPrinters);
            } else {
                model.delPrinters();
            }

            if (PeriodicTaskRunnerController.isTaskRunning()) {
                PeriodicTaskRunnerController.killTask();
            }

            Stage stage = (Stage) save.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
    }
}
