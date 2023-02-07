package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    //Возвращает текущее время в формате ЧЧ:ММ
    public static String getTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    //выводит на консоль сообщение с текущим временем в формате ЧЧ:ММ
    public static void writeTime() {
        System.out.println("Текущее время " + getTime());
    }

    //Возвращает текущее время и дату в формате HH:mm:ss dd/MM/yyy
    public static String getDateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyy");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
}
