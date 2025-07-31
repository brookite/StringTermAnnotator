package io.brookite.termannotations.utils;

public class StringUtils {
    public static String stripTags(String input) {
        return input.replaceAll("<[^>]+>", "");
    }
}
