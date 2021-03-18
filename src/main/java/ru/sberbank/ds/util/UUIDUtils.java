package ru.webfluxExample.ds.util;

import java.util.regex.Pattern;

public class UUIDUtils {
    private static final Pattern UID_REGEX_PATTERN = Pattern.compile("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}");
    public static final int UID_LENGTH = 36;

    private UUIDUtils() {
    }

    public static boolean validateUUID(String uid) {
        return uid != null && UID_REGEX_PATTERN.matcher(uid).matches();
    }
}
