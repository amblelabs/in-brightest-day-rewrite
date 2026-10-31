package dev.amblelabs.lib.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class CaseUtil {

    @Contract(value = "null->null", pure = true)
    public static @Nullable String snakeToCamel(@Nullable String str) {
        if (str == null) return null;

        StringBuilder builder = new StringBuilder();
        boolean nextUpper = false;

        for (char c : str.toCharArray()) {
            if (c == '_') {
                nextUpper = true;
            } else if (nextUpper) {
                builder.append(Character.toUpperCase(c));
                nextUpper = false;
            } else {
                builder.append(Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }

    @Contract("null->null")
    public static @Nullable String camelToSnake(@Nullable String str) {
        if (str == null) return null;

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);

            if (Character.isUpperCase(ch)) {
                if (i > 0) result.append('_');
                result.append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }

        return result.toString();
    }
}
