package com.kitchenreceiptprint.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.kitchenreceiptprint.util.ExceptionUtil;
import com.kitchenreceiptprint.util.MessageUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
 * Class FtpDownloaderController
 *
 * Facilitates FTP operations for the Kitchen Receipt Printing application, including listing,
 * downloading, and deleting PDF files from an FTP server. This class uses the Apache Commons Net
 * library to interact with FTP servers and provides methods to list PDF files based on a specific
 * prefix and date pattern, download PDF files to a local directory, and delete PDF files after
 * processing.
 *
 * Responsibilities:
 * - Connect to an FTP server using provided credentials.
 * - List all PDF files on the server that match a specified naming pattern.
 * - Download selected PDF files to a local directory for further processing.
 * - Delete PDF files from the server after successful download and processing.
 *
 * Usage:
 * This class is utilized by other components of the application that require interaction with
 * an FTP server for handling PDF files. It abstracts the complexity of FTP operations and provides
 * a simple interface for file management.
 *
 * Author: Artisan Webmaster
 * Creation Date: 05/02/2024
 * Last Modification: 05/02/2024
 *
 * Notes:
 * - The class relies on the Apache Commons Net library for FTP operations.
 * - It is designed to work specifically with PDF files but could be adapted for other file types.
 * - Error handling is implemented to manage connectivity issues, file access errors, and FTP command failures.
 */
public class FtpDownloaderController {
    private final MessageUtil messageUtil;

    public FtpDownloaderController(MessageUtil messageUtil) {
        this.messageUtil = messageUtil;
    }

    public List<String> getLatestPdfListFromFtp(String remoteDir, String prefix, String server,
                                                String username, String password) throws IOException {

        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(server);
            ftpClient.login(username, password);

            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                messageUtil.appendMessage("Échec de la connexion au serveur FTP, code de réponse: " + replyCode);
            }

            FTPFile[] files = ftpClient.listFiles(remoteDir);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

            return Arrays.stream(files)
                    .filter(file -> file.getName().matches(prefix + "_\\d{14}\\.pdf"))
                    .sorted(Comparator.comparing(ftpFile -> {
                        try {
                            String dateTimeStr = ftpFile.getName()
                                    .replaceAll(".*" + prefix + "_(\\d{14})\\.pdf.*", "$1");
                            return dateFormat.parse(dateTimeStr);
                        } catch (ParseException e) {
                            messageUtil.appendMessage("Erreur lors de l'analyse de la date du fichier" + e);
                        }
                        return null;
                    }))
                    .map(FTPFile::getName)
                    .collect(Collectors.toList());
        } catch (UnknownHostException e) {
            // Gérer spécifiquement l'UnknownHostException
            messageUtil.appendMessage("Serveur FTP  introuvable : " + e.getMessage());
        } catch (IOException e) {
            // Gestion spécifique des IOExceptions
            throw e;
        } catch (Exception e) {
            // Gestion des autres exceptions
            ExceptionUtil.handleException(e);
        } finally {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        }
        return null;
    }

    public void downloadPdfFile(String server, String username, String password,
                                String remoteDir, String fileName, String localDir) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(server);
            ftpClient.login(username, password);

            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                messageUtil.appendMessage("Échec de la connexion au serveur FTP, code de réponse: " + replyCode);
                return;
            }

            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

            String remoteFilePath = remoteDir + "/" + fileName;
            try (OutputStream outputStream = new FileOutputStream(localDir + "/" + fileName)) {
                boolean success = ftpClient.retrieveFile(remoteFilePath, outputStream);
                if (success) {
                    messageUtil.appendMessage("Téléchargement réussi de : " + fileName);
                } else {
                    messageUtil.appendMessage("Échec du téléchargement de : " + fileName);
                }
            }
        } catch (IOException e) {
            messageUtil.appendMessage("Erreur lors du téléchargement : " + e.getMessage());
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException e) {
                ExceptionUtil.handleException(e);
            }
        }
    }

    public void deletePdfFile(String server, String username, String password, String remoteDir, String fileName) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(server);
            ftpClient.login(username, password);

            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                messageUtil.appendMessage("Échec de la connexion au serveur FTP, code de réponse : " + replyCode);
                return;
            }

            ftpClient.enterLocalPassiveMode();

            String remoteFilePath = remoteDir + "/" + fileName;
            boolean success = ftpClient.deleteFile(remoteFilePath);
            if (success) {
                messageUtil.appendMessage("Suppression réussie de : " + fileName);
            } else {
                messageUtil.appendMessage("Échec de la suppression de : " + fileName);
            }
        } catch (IOException e) {
            messageUtil.appendMessage("Erreur lors de la suppression : " + e.getMessage());
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException e) {
                ExceptionUtil.handleException(e);
            }
        }
    }
}
