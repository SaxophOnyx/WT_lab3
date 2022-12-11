package by.saxophonyx.app.server.contracts;

import by.saxophonyx.data.entities.Student;

import java.util.List;

public interface IStudentDb {
    public void save(List<Student> studentList);
    public List<Student> getAll();
    public void load();
}
