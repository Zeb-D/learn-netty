package com.yd.netty;

import org.junit.Test;

public class StringTest {
    @Test
    public void testString() {
        String a= "abc";
        System.out.println(a.codePointAt(1));
        System.out.println(a.codePointBefore(1));
        System.out.println(a.codePointCount(1,3));
        System.out.println(Character.MIN_LOW_SURROGATE+" -> "+Character.MAX_HIGH_SURROGATE);
    }

}
