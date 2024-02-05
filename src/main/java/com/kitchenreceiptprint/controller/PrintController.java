package com.kitchenreceiptprint.controller;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Class PrintController
 *
 * Provides utility methods for interacting with print services within the Kitchen Receipt Printing application.
 * This class leverages the Java Print Service API to enumerate available print services and retrieves their names,
 * making them accessible to other parts of the application for user selection or configuration purposes.
 *
 * Responsibilities:
 * - List the names of all available printers on the system.
 *
 * Usage:
 * This class is designed to be a utility/helper class that can be called statically to fetch a list of printer names.
 * It is used whenever the application needs to display or utilize the list of available printers, such as in configuration
 * settings for selecting a printer or for checking available printers before printing.
 *
 * Author: Artisan Webmaster
 * Creation Date: 05/02/2024
 * Last Modification: 05/02/2024
 *
 * Notes:
 * - This class does not handle the actual printing of documents but rather assists in the selection of printers
 *   by listing their names.
 * - It uses the JavaFX collections library to return an ObservableList<String>, making it suitable for data binding
 *   in JavaFX applications.
 */
public class PrintController {
    public static ObservableList<String> getPrinters() {
        ObservableList<String> printerNames = FXCollections.observableArrayList();
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

        for (PrintService printer : printServices) {
            printerNames.add(printer.getName());
        }

        return printerNames;
    }
}
