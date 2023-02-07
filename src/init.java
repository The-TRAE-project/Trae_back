import utils.DateUtils;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class init {
    static final String fileCategories = "C:\\java\\IdeaProjects\\Trae_back\\files\\categories.bin";
    static final String fileEmployees = "C:\\java\\IdeaProjects\\Trae_back\\files\\employees.bin";
    static final String fileWorkTime = "C:\\java\\IdeaProjects\\Trae_back\\files\\01_2023_worktime.bin";

    //запись категорий в файл
    public static void writeCategories() {
        if (!new File(fileCategories).exists()) {
            List<String> list = new ArrayList<>();
            list.add("раскрой");
            list.add("кромка");
            list.add("присадка");
            list.add("фрезеровка");
            list.add("сборка");
            list.add("склейка");
            list.add("подготовка к покраске");
            list.add("шлифовка / покраска");
            list.add("подготовка к отгрузке / упаковка");
            list.add("отгрузка");
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileCategories))) {
                oos.writeObject(list);
            } catch (IOException e) {
                throw new RuntimeException("Can't write/create file", e);
            }
        }
    }

    //чтение категорий из файла
    public static List<String> readCategories() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileCategories))) {
            return (List<String>) ois.readObject();
        } catch (IOException e) {
            throw new RuntimeException("Can't read file", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class exception", e);
        }
        //TODO проверить категории у работников, соответствие считанных категорий и категорий у работников
    }

    //создание двух работников, работ, которые они могут выполнять, один из них конструктор, запись в файл
    public static void writeEmployees() {
//        если файл не существует
        if (!new File(fileEmployees).exists()) {
            List<Employee> employees = new ArrayList<>();

            Employee emp1 = new Employee();
            emp1.setUuid(111);
            emp1.setName("Иван");
            emp1.setSurname("Петров");
            Map<String, Boolean> op1 = new TreeMap<>();
            op1.put("раскрой", true);
            op1.put("кромка", false);
            op1.put("присадка", true);
            op1.put("фрезеровка", false);
            op1.put("сборка", false);
            op1.put("склейка", false);
            op1.put("подготовка к покраске", true);
            op1.put("шлифовка / покраска", false);
            op1.put("подготовка к отгрузке / упаковка", true);
            op1.put("отгрузка", false);
            emp1.setOperations(op1);
            emp1.setConstructor(false);
            emp1.setAdmin(false);
            emp1.setWorkNow(false);
            employees.add(emp1);

            Employee emp2 = new Employee();
            emp2.setUuid(222);
            emp2.setName("Петр");
            emp2.setSurname("Иванов");
            Map<String, Boolean> op2 = new TreeMap<>();
            op2.put("раскрой", false);
            op2.put("кромка", true);
            op2.put("присадка", true);
            op2.put("фрезеровка", false);
            op2.put("сборка", false);
            op2.put("склейка", false);
            op2.put("подготовка к покраске", true);
            op2.put("шлифовка / покраска", false);
            op2.put("подготовка к отгрузке / упаковка", true);
            op2.put("отгрузка", true);
            emp2.setOperations(op2);
            emp2.setConstructor(true);
            emp2.setAdmin(false);
            emp2.setWorkNow(true);
            employees.add(emp2);

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileEmployees))) {
                oos.writeObject(employees);
            } catch (IOException e) {
                throw new RuntimeException("Can't write/create file", e);
            }
        }
    }

    //Чтение списка пользователей из файла
    public static List<Employee> readEmployees() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileEmployees))) {
            return (List<Employee>) ois.readObject();
        } catch (IOException e) {
            throw new RuntimeException("Can't read file", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class exception", e);
        }
    }

    /**
     * Ометка пришел и ушел с работы и запись в файл
     */
    public static void workCheck() {
        boolean exit = false;
        boolean checkIn = false;
        boolean checkOut = false;
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nОтметка прихода или ухода с работы");
        System.err.println("Посмотрите на камеру / приложите палец");
        DateUtils.writeTime();

        System.out.println("Введите номер пользователя или 0 для выхода");
        System.out.println("Список всех пользователей:");
        for (Employee emp : Main.employees) {
            System.out.print(emp.getUuid() + " " + emp.getSurname() + " " + emp.getName());
            System.out.println(emp.isWorkNow() ? " - на работе" : " - НЕ на работе");
        }
        while (!exit) {
            int input = scanner.nextInt();
            // если номер>=0, то дописать в файл номер + ФИ + StartDate + StopDate в зависимости от флагов
            int employeeNumber = getEmployeeNumber(Main.employees, input);
            if (employeeNumber >= 0) {

                System.out.println("Определен пользователь " + Main.employees.get(employeeNumber).getSurname());
                if (Main.employees.get(employeeNumber).isWorkNow()) {
                    System.out.println("До встречи " + Main.employees.get(employeeNumber).getName());
                    //break;
                }
                if (!Main.employees.get(employeeNumber).isWorkNow()) {
                    System.out.println("Добро пожаловать на работу " + Main.employees.get(employeeNumber).getName());
                    //break;
                }
                Main.employees.get(employeeNumber).changeWorkStatus();
            } else if (employeeNumber == -1 & input != 0) {
                System.err.println("Сотрудник не определен, обратитесь к администратору");
            }
            if (input == 0) {
                System.out.println("Выход из ввода отметки рабочего времени");
                exit = true;
            }
        }
    }

    private static int getEmployeeNumber(List<Employee> list, int key) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUuid() == key) {
                return i;
            }
        }
        return -1;
    }
}

