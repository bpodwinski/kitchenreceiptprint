package com.kitchenreceiptprint;

import com.kitchenreceiptprint.controller.PeriodicTaskRunnerController;
import com.kitchenreceiptprint.util.LocalizationUtil;
import com.kitchenreceiptprint.util.ExceptionUtil;
import com.kitchenreceiptprint.model.DatabaseModel;
import com.kitchenreceiptprint.view.MainLayoutView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Class App
 *
 * Serves as the entry point for the Kitchen Receipt Printing application. This class extends
 * the JavaFX Application class and sets up the initial stage of the application. It initializes
 * the application's localization settings, main layout view, and handles graceful shutdown
 * by stopping any periodic tasks and closing database connections.
 *
 * Responsibilities:
 * - Initialize application-wide settings such as localization.
 * - Display the main layout view to the user.
 * - Ensure a graceful shutdown by terminating background tasks and closing resources.
 *
 * Usage:
 * This class is meant to be run as the starting point of the application and does not require
 * manual instantiation as its lifecycle is managed by the JavaFX framework.
 *
 * Author: Artisan Webmaster
 * Creation Date: 05/02/2024
 * Last Modification: 05/02/2024
 *
 * Notes:
 * - The application's primary language is set to French by default (`LocalizationUtil.initialize("fr")`).
 * - Ensures that all resources are properly released on application exit by overriding the `stop` method.
 */
public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            LocalizationUtil.initialize("fr");
            new MainLayoutView(primaryStage);
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
    }

    @Override
    public void stop() {
        PeriodicTaskRunnerController.killTask();
        DatabaseModel.getInstance().closeConnection();
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
