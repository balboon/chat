package com.spontivly.chat.utils;

import android.widget.EditText;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Shawn on 22/01/2018.
 */

public class TextUtils {

    public static String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }

    public static boolean isEmpty(EditText txt) {
            return isEmpty(txt.getText().toString());
    }

    public static boolean isEmpty(String value){
        return value == null || value.trim().length() == 0;
    }

    public static double roundTo1Dec(double val) {
        DecimalFormat df2 = new DecimalFormat("###.#");
        return Double.valueOf(df2.format(val));
    }


    public static double roundTo2Dec(double val) {
        DecimalFormat df2 = new DecimalFormat("###.##");
        return Double.valueOf(df2.format(val));
    }


    public static String join(String s, ArrayList<Integer> idList) {
        return android.text.TextUtils.join(s, idList);
    }
}
