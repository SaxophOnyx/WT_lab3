package by.saxophonyx.data.entities;

import java.io.Serializable;

public class Student implements Serializable {
    public int ID;
    public String name;
    public String surname;
    public int age;
    public int studentID;

    public Student(int ID, String name, String surname, int age, int studentID) {
        this.ID = ID;
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.studentID = studentID;
    }

    @Override
    public String toString() {
        return "Student{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", age=" + age +
                ", studentID=" + studentID +
                '}';
    }
}
