package com.manywho.services.sql.utilities;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ManyWhoSpecialCommentTest {
    @Test
    public void commentNameTest() {
        assertEquals("jaja", ManyWhoSpecialComment.nameComment("aaa {{ManyWhoName:jaja}} bb"));
    }

    @Test
    public void commentNameTestMalFormeded() {
        assertEquals(null, ManyWhoSpecialComment.nameComment("aaa {{ ManyWhoName:jaja}} bb"));
    }
}
