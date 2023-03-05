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
    private DatagramSocket socketToSend;
    SocketAddress socketAddressToGet;
    DatagramChannel datagramChannel;
    CommandDistributor commandDistributor;

    public ServerSerializer(CommandDistributor commandDistributor) throws IOException {
        socketToSend = new DatagramSocket(serverPortToSend);
        socketAddressToGet = new InetSocketAddress(7000);
        this.commandDistributor = commandDistributor;
        datagramChannel = DatagramChannel.open();
        datagramChannel.bind(socketAddressToGet);
    }


    public void correspondeWithClient() {
        try {
            while (true){
//                datagramChannel.configureBlocking(false);

                // getting and formalize serialized object

                socketAddressToGet = datagramChannel.receive(ByteBuffer.wrap(byteCommandMessage));
                ByteArrayInputStream bis = new ByteArrayInputStream(byteCommandMessage);
                ObjectInputStream ois = new ObjectInputStream(bis);
                ArrayList<Object> deserializedData = (ArrayList<Object>)  ois.readObject();
//                CommandMessage deserializedCommandMessage = (CommandMessage)
                CommandMessage deserializedCommandMessage = (CommandMessage) deserializedData.get(0);
                int port = (Integer) deserializedData.get(1);

                // command execution

                Object result = commandDistributor.execution(deserializedCommandMessage);
                assert result != null;
                ResponseMessage<Object> response = new ResponseMessage<>(result.getClass().getName(), result);

                // sending
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(response);
                byte[] byteBAOS = byteArrayOutputStream.toByteArray();
                host = InetAddress.getByName("localhost");
                DatagramPacket packet = new DatagramPacket(byteBAOS, byteBAOS.length, host, port);
                socketToSend.send(packet);
            }
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            socketToSend.close();
        }
    }

    public void close() throws IOException {
        datagramChannel.close();
    }
}