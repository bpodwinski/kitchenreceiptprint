package com.kitchenreceiptprint.controller;

import com.kitchenreceiptprint.App;
import com.kitchenreceiptprint.model.DatabaseModel;
import com.kitchenreceiptprint.util.CryptoUtil;
import com.kitchenreceiptprint.util.ExceptionUtil;
import com.kitchenreceiptprint.util.MessageUtil;
import com.kitchenreceiptprint.view.AboutLayoutView;
import com.kitchenreceiptprint.view.ConfigLayoutView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import java.io.File;

/**
 * Class MainLayoutController
 *
 * Manages user interactions with the main layout of the application.
 * This class controls user actions such as starting, pausing, and stopping periodic tasks,
 * as well as navigating to different configuration screens.
 *
 * Responsibilities:
 * - Launch and stop periodic tasks via PeriodicTaskRunnerController.
 * - Show and hide UI elements based on task state.
 * - Manage navigation to configuration and information screens.
 *
 * Usage:
 * This class is bound to buttons and other UI elements defined in the associated FXML file.
 *
 * Author: Artisan Webmaster
 * Creation Date: 05/20/2024
 * Last Modification: 05/20/2024
 *
 */
public class MainLayoutController {

    private DatabaseModel model;

    private MessageUtil message;

    private void showLaunchButton(boolean show) {
        launchButton.setVisible(show);
    }

    private void showResumeButton(boolean show) {
        resumeButton.setVisible(show);
    }

    void showLoader(boolean show) {
        loader.setVisible(show);
    }

    @FXML
    private ProgressIndicator loader;

    @FXML
    private void handleAbout() {
        AboutLayoutView aboutView = new AboutLayoutView();
        aboutView.show();
    }

    @FXML
    private void handlePause() {
        if (PeriodicTaskRunnerController.isTaskRunning()) {
            PeriodicTaskRunnerController.pause();
            showResumeButton(true);
            textArea.setVisible(false);
            System.out.println("Tâche en pause");
        }
    }

    @FXML
    private void handleExit() {
        new App().stop();
    }

    @FXML
    private void handleSettings() {
        PeriodicTaskRunnerController.killTask();

        launchButton.setVisible(true);
        textArea.setVisible(false);

        ConfigLayoutView ftpConfigView = new ConfigLayoutView();
        ftpConfigView.show();
    }

    @FXML
    private TextArea textArea;

    @FXML
    private Label emptyConfLabel;

    @FXML
    private Button launchButton;

    @FXML
    private Button resumeButton;

    @FXML
    private void handleResumeButtonAction() {
        if (!PeriodicTaskRunnerController.isTaskRunning()) {
            PeriodicTaskRunnerController.resume();
            showResumeButton(false);
            textArea.setVisible(true);
            System.out.println("Tâche relancé");
        }
    }

    @FXML
    private void handleLaunchButtonAction() {
        showLaunchButton(false);
        textArea.setVisible(true);

        if (!validateFtpConfig()) {
            showLaunchButton(true);
            emptyConfLabel.setVisible(true);
            return;
        }

        String ftp_server = model.getConfiguration("ftp_server");
        String ftp_username = model.getConfiguration("ftp_username");
        String ftp_password = CryptoUtil.decrypt(model.getConfiguration("ftp_password"));

        String directoryPath = System.getProperty("java.io.tmpdir") + "pdf";
        File directory = new File(directoryPath);

        if (!directory.exists()) {
            boolean isCreated = directory.mkdirs();
            if (isCreated) {
                System.out.println("Dossier créé : " + directoryPath);
            } else {
                System.out.println("Impossible de créer le dossier : " + directoryPath);
            }
        }

        FtpDownloaderController ftpDownloader = new FtpDownloaderController(message);
        PdfPrinterController PdfPrinter = new PdfPrinterController(message);

        // check if configuration if empty
        if (model.getConfiguration("ftp_server") == null ||
                model.getConfiguration("ftp_username") == null ||
                model.getConfiguration("ftp_password") == null
        ) {
            emptyConfLabel.setVisible(true);
            launchButton.setVisible(false);
        } else {
            try {
                message.appendMessage("La tâche sera executée toutes les " + model.getConfiguration("interval") + " secondes");

                Runnable task = () -> {
                    boolean result = ProcessPdfFilesController.processPdfFiles(ftpDownloader,
                            message,
                            PdfPrinter,
                            ftp_server,
                            ftp_username,
                            ftp_password,
                            directoryPath);
                    if (!result && !PeriodicTaskRunnerController.messageDisplayed) {
                        Platform.runLater(() -> {
                            message.appendMessage("Aucune commande trouvée pour le moment ...");
                        });
                        PeriodicTaskRunnerController.messageDisplayed = true; // Empêche l'affichage futur
                    }
                };

                PeriodicTaskRunnerController.task(task, Integer.parseInt(model.getConfiguration("interval")));

            } catch (Exception e) {
                ExceptionUtil.handleException(e);
            }
        }
    }

    private boolean validateFtpConfig() {
        String ftpServer = model.getConfiguration("ftp_server");
        String ftpUsername = model.getConfiguration("ftp_username");
        String ftpPassword = CryptoUtil.decrypt(model.getConfiguration("ftp_password"));

        return ftpServer != null &&
                ftpUsername != null &&
                ftpPassword != null;
    }

    public void initialize() {
        message = new MessageUtil(textArea);
        this.model = DatabaseModel.getInstance();

        // Initialize the database
        DatabaseModel.createDatabase();

        // Set default interval value
        if (model.getConfiguration("interval") == null ||
                model.getConfiguration("interval").isEmpty()) {
            model.addConfiguration("interval", "60");
        }
    }
}
