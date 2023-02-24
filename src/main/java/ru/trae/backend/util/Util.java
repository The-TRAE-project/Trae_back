package ru.trae.backend.util;

import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;

import java.security.SecureRandom;
import java.util.Date;

public class Util {
    public static int generateRandomInteger(int min, int max) {
        SecureRandom random = new SecureRandom();
        random.setSeed(new Date().getTime());
        return random.nextInt((max - min) + 1) + min;
    }

    public static int getPeriodForFirstOperation(int period, int size) {
        return (int) Math.floor(((double) period / (double) size));
    }

    public static int dateSorting(Project p1, Project p2) {
        if (p1.getPlannedEndDate().isAfter(p2.getPlannedEndDate())) {
            return 1;
        } else if (p1.getPlannedEndDate().isEqual(p2.getPlannedEndDate())) {
            return 0;
        } else {
            return -1;
        }
    }

    public static int prioritySorting(Operation o1, Operation o2) {
        return Integer.compare(o1.getPriority(), o2.getPriority());
    }
}
