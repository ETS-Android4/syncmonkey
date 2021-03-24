package com.chesapeaketechnology.syncmonkey.helpers;

import java.util.Random;

public class TestUtils
{

    public static class Generate
    {
        public static int getRandomNumberUsingInts(int min, int max)
        {
            Random random = new Random();
            return random.ints(min, max)
                    .findFirst()
                    .getAsInt();
        }
    }
}
