package by.saxophonyx.app.client;

import by.saxophonyx.app.client.contracts.exceptions.GeneralClientException;
import by.saxophonyx.data.entities.Student;

import javax.net.SocketFactory;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;
import java.util.Scanner;

public class Client {
    private Socket _socket;

    public Client(InetAddress localAddress, int localPort) throws GeneralClientException{
        try {
            _socket = SocketFactory.getDefault().createSocket();
            _socket.bind(new InetSocketAddress(localAddress, localPort));
        }
        catch (IOException e) {
            throw new GeneralClientException(e.getMessage());
        }
    }


    public void start(InetAddress remoteAddress, int remotePort) throws GeneralClientException{
        SocketAddress remote = new InetSocketAddress(remoteAddress, remotePort);
        try {
            _socket.connect(remote);
            BufferedReader authReader = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
            PrintWriter outMess = new PrintWriter(new BufferedWriter(new OutputStreamWriter(_socket.getOutputStream())), true);
            ObjectOutputStream outObj = new ObjectOutputStream (_socket.getOutputStream());
            ObjectInputStream inObj = new ObjectInputStream(_socket.getInputStream());

            List<Student> students = (List<Student>) inObj.readObject();
            boolean authFlag = false;

            String requestType;
            do {
                System.out.println("Available commands:");
                System.out.println
                        (
                                "login\n" +
                                        "add\n" +
                                        "edit\n" +
                                        "quit\n"
                        );
                Scanner inputClient = new Scanner(System.in);
                requestType = inputClient.next();

                switch (requestType) {
                    case "edit":
                        authFlag = Boolean.parseBoolean(authReader.readLine());
                        if (!authFlag) {
                            System.out.println("Log in first\n");
                            break;
                        }

                        System.out.println("Select student's id");
                        int id = inputClient.nextInt();
                        id = id < students.size() ? id : students.size() - 1;

                        Student temp = students.get(id);
                        System.out.println("Write your changes:");
                        System.out.print(temp.name + " --> ");
                        temp.name = inputClient.next();
                        System.out.print(temp.surname + " --> ");
                        temp.surname = inputClient.next();
                        System.out.print(temp.age + " --> ");
                        temp.age = inputClient.nextInt();
                        System.out.print(temp.studentID + " --> ");
                        temp.studentID = inputClient.nextInt();

                        System.out.println("Done\n");

                        outMess.println(requestType);
                        outObj.writeObject(students);
                        break;
                    case "add":
                        authFlag = Boolean.parseBoolean(authReader.readLine());
                        if (!authFlag) {
                            System.out.println("Log in first\n");
                            break;
                        }

                        System.out.println("Type student's info:");
                        System.out.print("Name --> ");
                        String name = inputClient.next();
                        System.out.print("Surname --> ");
                        String surname = inputClient.next();
                        System.out.print("Age --> ");
                        int age = inputClient.nextInt();
                        System.out.print("StudentID --> ");
                        int studentID = inputClient.nextInt();
                        Student newStud = new Student(students.size(), name, surname, age, studentID);
                        students.add(newStud);
                        System.out.println("Done");
                        outMess.println(requestType);
                        outObj.writeObject(students);
                        break;
                    case "login":
                        outMess.println(requestType);
                        System.out.println("Success\n");
                        break;
                    case "quit":
                        outMess.println(requestType);
                        inputClient.close();
                        break;
                }
            } while (!requestType.equals("quit"));
        } catch (Exception e) {
            throw new GeneralClientException(e.getMessage());
        }
        finally {
            try {
                disconnect();
            }
            catch (Exception e) {

            }
        }
    }

    public void send() throws GeneralClientException{
        try {
            OutputStream out = _socket.getOutputStream();
            OutputStreamWriter outWriter = new OutputStreamWriter(out);
        } catch (Exception e) {
            throw new GeneralClientException(e.getMessage());
        }
    }

    public void disconnect() throws GeneralClientException{
        try {
            _socket.close();
        }
        catch (Exception e) {
            throw new GeneralClientException(e.getMessage());
        }
    }
}
