package functional_classes.threads;

import auxiliary_classes.CommandMessage;
import auxiliary_classes.ResponseMessage;
import functional_classes.commands_executors.CommandDistributor;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Objects;


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
    private String stage = "get";


    public ServerSerializer(CommandDistributor commandDistributor) throws IOException {
        socketToSend = new DatagramSocket(serverPortToSend);
        socketAddressToGet = new InetSocketAddress(7000);
        this.commandDistributor = commandDistributor;
        datagramChannel = DatagramChannel.open();
        datagramChannel.bind(socketAddressToGet);
    }


    public void waitForRequest() {
        try {
            while (Objects.equals(stage, "get")) {
                System.out.println("in waitForRequest");
//                datagramChannel.configureBlocking(false);
                socketAddressToGet = datagramChannel.receive(ByteBuffer.wrap(byteCommandMessage));
                if (socketAddressToGet != null) {
                    System.out.println("got message");
                    ByteArrayInputStream bis = new ByteArrayInputStream(byteCommandMessage);
                    ObjectInputStream ois = new ObjectInputStream(bis);
                    ArrayList<Object> deserializedData = (ArrayList<Object>) ois.readObject();
                    deserializedCommandMessage = (CommandMessage) deserializedData.get(0);
                    clientPort = (Integer) deserializedData.get(1);
                    System.out.println("deserializedCommandMessage: " + deserializedCommandMessage);
                    stage = "execute";
                }
            }
        }
        catch (ClassCastException e) {
            System.out.println("Неверный тип полученных данных. Убедитесь, что сообщение с клиента приводится к классу CommandMessage");
        }catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void executeCommand() {
        // command execution
        Object result = commandDistributor.execution(deserializedCommandMessage);
        assert result != null;
        response = new ResponseMessage<>(result.getClass().getName(), result);
        stage = "send";
    }

    public void sendResponse() throws IOException {
        // sending
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(response);
        byte[] byteBAOS = byteArrayOutputStream.toByteArray();
        host = InetAddress.getByName("localhost");
        DatagramPacket packet = new DatagramPacket(byteBAOS, byteBAOS.length, host, clientPort);
        stage = "get";
        socketToSend.send(packet);
//        socketToSend.close();
    }

    public String getStage() {
        return stage;
    }

    public void close() throws IOException {
        datagramChannel.close();
    }
}