package by.saxophonyx.main;

import by.saxophonyx.app.client.Client;
import by.saxophonyx.app.server.Server;
import by.saxophonyx.app.server.contracts.IStudentDb;
import by.saxophonyx.app.server.contracts.exceptions.GeneralServerException;
import by.saxophonyx.data.StudentDb;

import java.net.InetAddress;

public class Main {
    private static IStudentDb _db;
    private static InetAddress _serverAddress;
    private static int _serverPort;
    private static Server _server;

    private static InetAddress _clientAddress;
    private static int _clientPort;
    private static Client _client;

    public static void main(String[] args) {
        try {
            _serverAddress = InetAddress.getByName("127.0.0.1");
            _serverPort = 8001;
            _db = new StudentDb("src/datafiles/data.xml");

            _server = new Server(_db, _serverAddress, _serverPort);
            Thread serverThread = new Thread(() -> {
                try {
                    _server.start();
                }
                catch (GeneralServerException e) {
                    System.out.println("[Error]: Internal server error");
                    return;
                }
            });

            serverThread.join();
            serverThread.start();

            _clientAddress = InetAddress.getByName("127.0.0.2");
            _clientPort = 8002;
            _client = new Client(_clientAddress, _clientPort);
            _client.start(_serverAddress, _serverPort);
        }
        catch (Exception e) {
            System.out.println("[Error]: Unknown error");
            return;
        }
        finally {
            try {
                _client.disconnect();
                _server.stop();
            }
            catch (Exception e) {
                System.out.println("[Error]: Cannot close the connections");
                return;
            }
        }
    }
}