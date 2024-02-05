package com.kitchenreceiptprint.util;

public class ExceptionUtil {

    public static void handleException(Exception e) {
        handleException(e, null);
    }

    public static void handleException(Exception e, String message) {
        if (message != null && !message.isEmpty()) {
            System.err.println(message);
        }
        e.printStackTrace();
    }
}
