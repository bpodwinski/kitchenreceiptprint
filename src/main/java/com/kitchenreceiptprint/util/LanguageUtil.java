package com.kitchenreceiptprint.util;

import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class LanguageUtil {

    private final Map<String, String> langues = new HashMap<>();

    public List<String> loadLanguageNames() {
        try {
            Path resourcesPath = Paths.get("src/main/resources");

            return Files.walk(resourcesPath)
                    .filter(path -> path.toString().endsWith(".properties") && path.getFileName().toString().startsWith("lang_"))
                    .map(path -> {
                        String langCode = path.getFileName().toString().substring(5, path.getFileName().toString().lastIndexOf("."));
                        String baseName = "lang_" + langCode;
                        try {
                            ResourceBundle bundle = ResourceBundle.getBundle(baseName, new Locale(langCode));
                            String langName = bundle.getString("lang");
                            return langCode + ";" + langName;
                        } catch (Exception e) {
                            ExceptionUtil.handleException(e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
            return List.of();
        }
    }
}
