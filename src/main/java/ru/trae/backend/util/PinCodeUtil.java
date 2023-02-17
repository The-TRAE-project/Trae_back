package ru.trae.backend.util;

import java.security.SecureRandom;
import java.util.Date;

public class PinCodeUtil {
    public static int generateRandomInteger(int min, int max) {
        SecureRandom random = new SecureRandom();
        random.setSeed(new Date().getTime());
        return random.nextInt((max - min) + 1) + min;
    }
}
