package com.kitchenreceiptprint.controller;

import com.kitchenreceiptprint.util.ExceptionUtil;
import com.kitchenreceiptprint.util.MessageUtil;

import java.util.List;

/**
 * Class ProcessPdfFilesController
 *
 * Facilitates the processing of PDF files retrieved from an FTP server, including downloading,
 * printing, and deleting the files. This class acts as a coordinator between the FTP downloader,
 * message utility, and PDF printer components to execute a series of operations on PDF files
 * specified by certain criteria (e.g., file naming convention).
 *
 * Responsibilities:
 * - Retrieve a list of PDF files from an FTP server based on predefined criteria.
 * - Download the listed PDF files to a local directory for processing.
 * - Print each downloaded PDF file to configured printers.
 * - Delete the PDF files from the FTP server after successful processing.
 *
 * Usage:
 * This static method is intended to be called when there's a need to automatically process PDF files
 * from an FTP server. It can be scheduled to run at regular intervals or triggered by specific application events.
 *
 * Author: Artisan Webmaster
 * Creation Date: 05/02/2024
 * Last Modification: 05/02/2024
 *
 * Notes:
 * - This class is designed for use in environments where PDF files are regularly received via FTP for processing.
 * - Utilizes the `FtpDownloaderController` for FTP operations, `MessageUtil` for logging, and `PdfPrinterController`
 *   for printing functionality.
 */
public class ProcessPdfFilesController {

    public static boolean processPdfFiles(FtpDownloaderController ftpDownloader,
                                       MessageUtil message,
                                       PdfPrinterController PdfPrinter,
                                       String ftp_server,
                                       String ftp_username,
                                       String ftp_password,
                                       String directoryPath) {
        try {
            List<String> pdfFiles = ftpDownloader.getLatestPdfListFromFtp("/", "order", ftp_server, ftp_username, ftp_password);

            if (pdfFiles == null || pdfFiles.isEmpty()) {
                return false;
            }

            for (String pdfFile : pdfFiles) {
                message.appendMessage("Traitement du fichier : " + pdfFile);
                ftpDownloader.downloadPdfFile(ftp_server, ftp_username, ftp_password, "/", pdfFile, directoryPath);
                PdfPrinter.printPdfMultiplePrinters(directoryPath + "/" + pdfFile);
                ftpDownloader.deletePdfFile(ftp_server, ftp_username, ftp_password, "/", pdfFile);
            }
            return true;

        } catch (Exception e) {
            ExceptionUtil.handleException(e);
            return false;
        }
    }
}
