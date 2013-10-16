package com.radiumone.sdk.testemitter;

import java.util.Random;
import java.util.UUID;

public class Util {

    private static Random random = new Random();

    public static String getRandomString(){

        String randomString = UUID.randomUUID().toString().replace("-", "2");

        int num = Math.abs(random.nextInt()%10);
        if ( num > randomString.length()|| num < 3){
            num = randomString.length();
            if ( num > 10 ){
                num = 10;
            }
        }

        randomString = randomString.substring(0,num);
        return randomString;
    }

    public static long getRandomLong(){
        return random.nextLong();
    }

    public static double getRandomDouble(){
        return random.nextDouble();
    }

    public static float getRandomFloat(){
        return random.nextFloat();
    }

    public static int getRandomInteger(){
        return random.nextInt();
    }
}
