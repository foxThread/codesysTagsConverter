package com.myplant.tagsimport;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;

public class RegsFinder {
    private Pattern pattern;

    @Getter
    private Matcher matcher;

    public RegsFinder(String regString) {
        pattern = Pattern.compile(regString);
    }

    public void findFirst(String sourceString) {
        matcher = pattern.matcher(sourceString);
    }

    public boolean findNext() {
        if (matcher == null) {
            return false;
        }

        return matcher.find();

    }

}
