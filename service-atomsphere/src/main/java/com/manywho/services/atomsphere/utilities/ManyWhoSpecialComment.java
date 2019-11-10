package com.manywho.services.atomsphere.utilities;

import com.google.common.base.Strings;

public class ManyWhoSpecialComment {

    /**
     * {{ManyWhoName:}}
     * @param comment
     * @return
     */
    public static String nameComment(String comment) {
        if (!Strings.isNullOrEmpty(comment) && comment.contains("{{ManyWhoName:") && comment.contains("}}")) {
            String withoutManWhoName = comment.replaceFirst(".*\\{\\{ManyWhoName:", "");

            if (!Strings.isNullOrEmpty(withoutManWhoName)) {
                return withoutManWhoName.replaceFirst("\\}\\}.*", "");
            }
        }

        return null;
    }
}
