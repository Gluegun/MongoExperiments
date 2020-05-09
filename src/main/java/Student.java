import lombok.Data;

import java.util.Arrays;

@Data
public class Student {


    private String name;
    private int age;
    private String[] courses;

    public Student(String name, int age, String[] courses) {
        this.name = name;
        this.age = age;
        this.courses = courses;
    }

    public Student() {

    }

    public String getCourses() {
        return Arrays.toString(courses);
    }

    public void setCourses(String[] courses) {
        this.courses = courses;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", courses=" + Arrays.toString(courses) +
                '}';
    }
}
