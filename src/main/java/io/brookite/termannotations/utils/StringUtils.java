package io.brookite.termannotations.utils;

import java.util.Locale;

public class StringUtils {
    public static String stripTags(String input) {
        return input.replaceAll("<[^>]+>", "");
    }

    public static String upperLangTag(Locale locale) {
        return locale.getLanguage().toUpperCase();
    }
}
