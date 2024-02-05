package com.kitchenreceiptprint.controller;

import com.kitchenreceiptprint.model.DatabaseModel;
import com.kitchenreceiptprint.util.ExceptionUtil;
import com.kitchenreceiptprint.util.MessageUtil;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.print.PrinterJob;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Class PdfPrinterController
 *
 * Manages the printing of PDF documents to multiple printers as specified in the application's
 * database configuration. This class leverages the Apache PDFBox library to load and print PDF
 * documents and uses the Java Print Service API to interact with the configured printers.
 *
 * Responsibilities:
 * - Load PDF documents from the specified file path.
 * - Retrieve configured printer names from the application's database.
 * - Find matching print services for the configured printers.
 * - Print the PDF document to each of the configured printers.
 *
 * Usage:
 * This class is designed to be used whenever there is a need to print a PDF document to one or
 * more printers configured within the application. It abstracts the complexities of interacting
 * with the print services and the PDF document handling.
 *
 * Author: Artisan Webmaster
 * Creation Date: 05/02/2024
 * Last Modification: 05/02/2024
 *
 * Notes:
 * - The class assumes that the printers are properly configured in the system and accessible via
 *   the Java Print Service API.
 * - Error handling is in place to manage issues related to PDF loading, printer configuration errors,
 *   or printing errors.
 */
public class PdfPrinterController {
    private final DatabaseModel model;
    private final MessageUtil messageUtil;
    public PdfPrinterController(MessageUtil messageUtil) {
        this.messageUtil = messageUtil;
        this.model = DatabaseModel.getInstance();
    }
    public void printPdfMultiplePrinters(String filePath) {
        List<String> printerNames = model.getAllPrinter();

        try (PDDocument document = Loader.loadPDF(new File(filePath))) {
            PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

            for (String printerName : printerNames) {
                PrintService selectedService = findPrintService(printerName, printServices);
                if (selectedService != null) {
                    printDocument(document, selectedService);
                } else {
                    messageUtil.appendMessage("Imprimante non trouvée : " + printerName);
                }
            }
        } catch (IOException | PrinterException e) {
            ExceptionUtil.handleException(e);
        }
    }

    private PrintService findPrintService(String printerName, PrintService[] services) {
        for (PrintService service : services) {
            if (service.getName().equalsIgnoreCase(printerName)) {
                return service;
            }
        }
        return null;
    }

    private void printDocument(PDDocument document, PrintService service) throws PrinterException {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintService(service);
        job.setPageable(new PDFPageable(document));
        job.print();
        messageUtil.appendMessage("Imprimer avec succès");
    }
}
