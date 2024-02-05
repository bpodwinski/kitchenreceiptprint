package com.kitchenreceiptprint.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizationUtil {
    private static ResourceBundle resourceBundle;

    public static void initialize(String languageCode) {
        resourceBundle = ResourceBundle.getBundle("lang", new Locale(languageCode));
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }
}
