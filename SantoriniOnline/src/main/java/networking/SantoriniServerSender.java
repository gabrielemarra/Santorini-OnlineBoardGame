package networking;

import event.gameEvents.GameEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * thread created by the virtual view when an event must be sent to the client
 */
public class SantoriniServerSender implements Runnable {

    private Socket client;
    private GameEvent event;

    public SantoriniServerSender(Socket client, GameEvent event) {
        this.client = client;
        this.event = event;
    }

    @Override
    public void run() {
        try {
            sendEvent();
        } catch (IOException e) {
            System.out.println("send event thread: unable to send event");
            e.printStackTrace();
        }
    }

    private void sendEvent() throws IOException {
        //Opens the streams
        ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
        output.writeObject(event);
    }
}
