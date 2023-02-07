import utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    static List<Employee> employees;// = init.readEmployees();

    public static void main(String[] args) {

        System.out.println("Hello TRAE!\n");
        init.writeEmployees();
        init.writeCategories();
        employees = init.readEmployees();
        List<Project> projects = new ArrayList<>();
        List<String> categories = init.readCategories();

//        вывод категорий в консоль
        System.out.println("  Возможные операции:");
        for (String s:categories){
            System.out.println(s);
        }
//        вывод служащих в консоль
        for (Employee employee : employees) {
            System.out.println(employee);
        }
        boolean exit = false;
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n\n\n");
        DateUtils.writeTime();
        System.out.println("\nГлавная страница");
        while (!exit) {
            System.err.println("\nВведите цифру 0-3");
            System.out.println("1 Этапы в работе");
            System.out.println("2 Проекты");
            System.out.println("3 Сотрудники");
            System.out.println("0 Выход");
            switch (scanner.nextInt()) {
                case 1 -> System.err.println("в разработке");
                case 2 -> System.err.println("в разработке");
                case 3 -> init.workCheck();
                case 0 -> exit = true;
            }
        }

        // можно ДОписывать данные о чекине м чекауте в файл, объект типа workingTime. если пришел, то время ухода = null
        // брать данные из списка employees и потом по запросу собирать все эти данные в файл отчета
        // сортировка по uuid потом по фамилии потом по имени

        //https://javarush.com/groups/posts/2318-kompiljacija-v-java
        //В конструктор FileOutputStream можно также передать второй аргумент true. В этом случае, если файл существует, данные в него будут добавляться. Перезаписи файла не произойдет.
    }

//        List<Employee> getEmployees(){
//            return employees;
//        }
//        LinkedHashMap в порядке записи
}