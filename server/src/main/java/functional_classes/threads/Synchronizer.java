package functional_classes.threads;

public class Synchronizer {

    public void consoleThread(ServerReader serverReader) throws InterruptedException {
            serverReader.readConsole();
    }

    public void clientChannelThread(ServerSerializer serverSerializer) throws InterruptedException {
        serverSerializer.correspondeWithClient();
    }
}