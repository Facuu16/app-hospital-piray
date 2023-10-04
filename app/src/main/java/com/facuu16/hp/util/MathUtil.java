package com.facuu16.hp.util;

import java.util.Random;

public class MathUtil {

    private static final Random RANDOM = new Random();

    private MathUtil() {
        throw new UnsupportedOperationException();
    }

    public static Random getRandom() {
        return RANDOM;
    }
}
