package jp.gcreate.sample.daggersandbox;

import org.junit.Test;

import jp.gcreate.sample.daggersandbox.util.StringUtil;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Copyright 2016 G-CREATE
 */

public class StringUtilTest {
    @Test
    public void 文字列の結合() {
        String[] target = {"abc", "def"};
        String actual = StringUtil.concatenateStringArray(target);
        assertThat(actual, is("abc,def"));
    }

    @Test
    public void 結合記号を指定した場合() {
        String[] target = {"abc", "def"};
        String actual = StringUtil.concatenateStringArray(target, "/");
        assertThat(actual, is("abc/def"));
    }

    @Test
    public void 結合記号が2文字以上の長さの場合() {
        String[] target = {"abc", "def"};
        String actual = StringUtil.concatenateStringArray(target, "##");
        assertThat(actual, is("abc##def"));
    }
}
