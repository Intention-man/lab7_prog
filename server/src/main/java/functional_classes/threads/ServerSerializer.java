package functional_classes.threads;

import auxiliary_classes.CommandMessage;
import auxiliary_classes.ResponseMessage;
import functional_classes.commands_executors.CommandDistributor;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;


public class ServerSerializer {
    private byte[] byteCommandMessage = new byte[1024 * 16];
    InetAddress host;
    int serverPortToSend = 7777;
    int clientPort;
    private DatagramSocket socketToSend;
    SocketAddress socketAddressToGet;
    DatagramChannel datagramChannel;
    CommandDistributor commandDistributor;
    CommandMessage deserializedCommandMessage;
    ResponseMessage<Object> response;
    private boolean readyToExecute = false;


    public ServerSerializer(CommandDistributor commandDistributor) throws IOException {
        socketToSend = new DatagramSocket(serverPortToSend);
        socketAddressToGet = new InetSocketAddress(7000);
        this.commandDistributor = commandDistributor;
        datagramChannel = DatagramChannel.open();
        datagramChannel.bind(socketAddressToGet);
    }


    public void waitForRequest() {
        System.out.println("waitForRequest " + Thread.currentThread());
        try {
            while (true) {
//                datagramChannel.configureBlocking(false);

                // getting and formalize serialized object
                socketAddressToGet = datagramChannel.receive(ByteBuffer.wrap(byteCommandMessage));
                if (socketAddressToGet != null) {
                    System.out.println("got message");
                    ByteArrayInputStream bis = new ByteArrayInputStream(byteCommandMessage);
                    ObjectInputStream ois = new ObjectInputStream(bis);
                    ArrayList<Object> deserializedData = (ArrayList<Object>) ois.readObject();
//                CommandMessage deserializedCommandMessage = (CommandMessage)
                    CommandMessage deserializedCommandMessage = (CommandMessage) deserializedData.get(0);
                    clientPort = (Integer) deserializedData.get(1);
                    this.deserializedCommandMessage = deserializedCommandMessage;
                    readyToExecute = true;
                    System.out.println("finish getting and formalize");
                    break;
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            socketToSend.close();
        }
    }

    public void executeCommand() {
        System.out.println("executeCommand " + Thread.currentThread());
        // command execution

        Object result = commandDistributor.execution(deserializedCommandMessage);
        assert result != null;
        response = new ResponseMessage<>(result.getClass().getName(), result);
    }

    public void sendResponse() throws IOException {
        System.out.println("waitForRequest " + Thread.currentThread());
        // sending
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(response);
        byte[] byteBAOS = byteArrayOutputStream.toByteArray();
        host = InetAddress.getByName("localhost");
        DatagramPacket packet = new DatagramPacket(byteBAOS, byteBAOS.length, host, clientPort);
        socketToSend.send(packet);
        readyToExecute = false;
    }

    public boolean getReadyToExecute(){
        return readyToExecute;
    }

    public void close() throws IOException {
        datagramChannel.close();
    }
}