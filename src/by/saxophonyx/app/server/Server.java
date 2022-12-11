package by.saxophonyx.app.server;

import by.saxophonyx.app.server.contracts.IStudentDb;
import by.saxophonyx.app.server.contracts.exceptions.GeneralServerException;
import by.saxophonyx.data.entities.Student;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server {
    private IStudentDb _db;
    private ServerSocket _serverSocket;
    private boolean _isRunning;

    public Server(IStudentDb db, InetAddress address, int port) throws GeneralServerException {
        _db = db;
        _isRunning = false;

        try {
            _serverSocket = new ServerSocket(port, 100, address);
        }
        catch (IOException e) {
            throw new GeneralServerException(e.getMessage());
        }
    }


    public void start() throws GeneralServerException {
        _isRunning = true;
        try {
            _db.load();
            while (_isRunning) {
                Socket client = _serverSocket.accept();
                Thread thread = new Thread(() -> processClient(client));
                thread.start();
            }
        }
        catch (Exception e) {
            _isRunning = false;
            throw new GeneralServerException(e.getMessage());
        }
        finally {
            try {
                _serverSocket.close();
            }
            catch (Exception e) {

            }
        }
    }

    public void stop() {
        try {
            _serverSocket.close();
            _isRunning = false;
        }
        catch (Exception e) {

        }
    }

    private void processClient(Socket client) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            ObjectOutputStream outObj = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream inObj = new ObjectInputStream(client.getInputStream());
            PrintWriter msgWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);

            boolean authFlag = false;
            List<Student> students = _db.getAll();
            outObj.writeObject(students);

            while (!client.isClosed()){
                msgWriter.println(authFlag);
                String req = reader.readLine();
                switch (req) {
                    case "login":
                        authFlag = true;
                        break;
                    case "add":
                    case "edit":
                        students = (List<Student>) inObj.readObject();
                        _db.save(students);
                        break;
                    case "quit":
                        reader.close();
                        outObj.close();
                        inObj.close();
                        msgWriter.close();
                        client.close();
                        break;
                }
            }
        }
        catch (Exception e) {

        }
        finally {
            try {
                client.close();
            } catch (Exception e) {

            }
        }
    }
}
