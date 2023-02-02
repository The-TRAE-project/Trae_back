import java.util.ArrayList;
import java.util.List;

public class Main {

    static List<Employee> employees = init.readEmployees();

    public static void main(String[] args) {
        init.readCategories();
        init.readEmployees();
        System.out.println("Hello TRAE!\n");
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

        init.workCheck();
        // можно ДОписывать данные о чекине м чекауте в файл, объект типа workingTime. если пришел, то время ухода = null
        // брать данные из списка employees и потом по запросу собирать все эти данные в файл отчета
        // сортировка по uuid потом по фамилии потом по имени

        //https://javarush.com/groups/posts/2318-kompiljacija-v-java
        //В конструктор FileOutputStream можно также передать второй аргумент true. В этом случае, если файл существует, данные в него будут добавляться. Перезаписи файла не произойдет.
    }

//        List<Employee> getEmployees(){
//            return employees;
//        }
}