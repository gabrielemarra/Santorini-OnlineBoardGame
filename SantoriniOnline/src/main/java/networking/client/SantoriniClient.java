package networking.client;

import com.google.gson.Gson;
import event.core.EventListener;
import event.core.EventSource;
import event.gameEvents.GameEvent;
import event.gameEvents.lobby.*;
import event.gameEvents.prematch.*;
import networking.SantoriniServer;
import view.CLIView;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static event.core.ListenerType.VIEW;


public class SantoriniClient extends EventSource implements Runnable {

    private CLIView cli;

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket serverSocket;


    public void begin() {

        cli = new CLIView(this);
        Scanner systemIn = new Scanner(System.in);
        System.out.println("WELCOME, Client Started.");
        System.out.println("Insert server IP Address: ");
        String IP = systemIn.nextLine();

        serverSocket = null;
        //Open a connection with the server
        try {
            serverSocket = new Socket(IP, SantoriniServer.SOCKET_PORT);
        } catch (IOException e) {
            System.err.println("Client: Unable to open a socket");
            e.printStackTrace();
        }

        System.out.println("CLIENT: connected ");

        //open the in/out stream from the server
        try {

            in = new ObjectInputStream(new BufferedInputStream(serverSocket.getInputStream()));
            out = new ObjectOutputStream(serverSocket.getOutputStream());
            cli.start(); //starts


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendEvent(GameEvent event){
        try {
            out.writeObject(event); //event is serializable
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void run() {
        while (true){
            try {
                GameEvent event = (GameEvent) in.readObject();
                notifyAllObserverByType(VIEW, event);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}
