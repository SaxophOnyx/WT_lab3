package by.saxophonyx.data;

import by.saxophonyx.app.server.contracts.IStudentDb;
import by.saxophonyx.data.entities.Student;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class StudentDb implements IStudentDb {
    private String _filename;
    private List<Student> _students;

    public StudentDb(String filename) {
        _students = new ArrayList<>();
        _filename = filename;
    }


    @Override
    public void load() {
        Document doc = null;

        try {
            doc = BuildDocument(_filename);
        } catch (Exception e) {

        }

        Node rootNode = doc.getFirstChild();
        NodeList dataListStud = rootNode.getChildNodes();

        int ID = 0;
        String name = "";
        String surname = "";
        int age = 0;
        int studentID = 0;
        for (int i = 0; i < dataListStud.getLength(); i++) {
            if (dataListStud.item(i).getNodeType() != Node.ELEMENT_NODE) continue;

            NodeList element = dataListStud.item(i).getChildNodes();

            for (int j = 0; j < element.getLength(); j++) {
                if (element.item(j).getNodeType() != Node.ELEMENT_NODE) continue;

                Node currentItem = element.item(j);

                switch (currentItem.getNodeName()) {
                    case "ID": {
                        ID = Integer.parseInt(currentItem.getTextContent());
                        break;
                    }
                    case "name": {
                        name = currentItem.getTextContent();
                        break;
                    }
                    case "surname": {
                        surname = currentItem.getTextContent();
                        break;
                    }
                    case "age": {
                        age = Integer.parseInt(currentItem.getTextContent());
                        break;
                    }
                    case "studentID": {
                        studentID = Integer.parseInt(currentItem.getTextContent());
                        break;
                    }
                }
            }

            _students.add(new Student(ID, name, surname, age, studentID));
        }
    }

    @Override
    public void save(List<Student> studentList) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element rootElement = doc.createElement("Data");
            doc.appendChild(rootElement);
            for (Student stud : studentList) rootElement.appendChild(createNode(doc, stud));
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult fileNew = new StreamResult(new File(_filename));
            transformer.transform(source, fileNew);
        } catch (Exception e) {
            System.out.println("Open error: " + e);
        }
    }

    private Node createNode(Document file, Student stud) {
        Element elem = file.createElement("student");
        elem.appendChild(createNodeElem(file, "ID", String.valueOf(stud.ID)));
        elem.appendChild(createNodeElem(file, "name", stud.name));
        elem.appendChild(createNodeElem(file, "surname", stud.surname));
        elem.appendChild(createNodeElem(file, "age", String.valueOf(stud.age)));
        elem.appendChild(createNodeElem(file, "studentID", String.valueOf(stud.studentID)));
        return elem;
    }

    private Node createNodeElem(Document file, String nameTag, String valTag) {
        Element node = file.createElement(nameTag);
        node.appendChild(file.createTextNode(valTag));
        return node;
    }

    @Override
    public List<Student> getAll() {
        return _students;
    }

    private static Document BuildDocument(String filename) throws Exception {
        File file = new File(filename);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        return dbf.newDocumentBuilder().parse(file);
    }
}