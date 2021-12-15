package de.vcm.queue.backup.queue;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.queue.CloudQueue;
import com.microsoft.azure.storage.queue.CloudQueueClient;
import com.microsoft.azure.storage.queue.CloudQueueMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.security.InvalidKeyException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j

public class QueueManagement {

    final static String storageConnectionString =
            "DefaultEndpointsProtocol=https;" +
                    "AccountName=storageaccountdevrga54a;" +
                    "AccountKey=UUdTAWYvjfgIkWl55ppAazakiNKWi5mF/pImFFc3vSVHFKA8Hsz2EaUy9OWOPIdnLfjTSae7yYuDVd2S5GOmBA==";

    final static String storageConnectionStringProd ="DefaultEndpointsProtocol=https;AccountName=storageaccountprodvcm;AccountKey=KyC3JOOFk+D9aF2QN6jCf6rQvjumE0Xs8ssbTQ2pgAs21NfnsieKlfO6vgwiPBbTGz27gmgdDBFDt6X/UbzM9g==;EndpointSuffix=core.windows.net";

    final static String filePath="C:\\Users\\karti\\velocity\\queue-backup\\prod.txt";
    final static String queueName = "stationslot";




    public static void main (String args[]) throws URISyntaxException, InvalidKeyException, StorageException, IOException {
        getCount();
        retrieveMessagesFromQueue(1);
//        peekMessagesFromQueue();
//        getCount();
//        updateFromFile();
        getCount();
    }

    private static void peekMessagesFromQueue() throws URISyntaxException, InvalidKeyException, StorageException, IOException {
        CloudQueue queue = null;
        int countCopied = 0;
        queue = getOrCreateQueue(queueName);
        queue.downloadAttributes();
        long cachedMessageCount = queue.getApproximateMessageCount();
        StringBuilder builder = new StringBuilder("");
        List<CloudQueueMessage> retrievedMessage = (List<CloudQueueMessage>) queue.peekMessages(32);
        for(CloudQueueMessage message: retrievedMessage){
            builder.append(message.getMessageContentAsString() +"\n");
        }
        writeToFile(builder.toString());
    }

    private static void insert(String data){
        for(String row: data.split("\n")){
            addMessage(row,queueName);
        }
        System.out.println("done creating ");
    }

    public static  void addMessage(String message, String channel)  {
        CloudQueue queue = null;
        CloudQueueMessage queueMessage = new CloudQueueMessage(message);
        try {
            queue = getOrCreateQueue(channel);
            queue.addMessage(queueMessage);
        } catch (StorageException | InvalidKeyException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static  CloudQueue getOrCreateQueue(String name) throws URISyntaxException, InvalidKeyException, StorageException {
        boolean created = false;
        CloudQueue queue = null;

        CloudStorageAccount storageAccount = null;
        storageAccount = getCloudStorageAccount();

        CloudQueueClient queueClient = storageAccount.createCloudQueueClient();
        queue = queueClient.getQueueReference(name);
        queue.createIfNotExists();
        return queue;
    }
    private static CloudStorageAccount getCloudStorageAccount() throws URISyntaxException, InvalidKeyException {
        return CloudStorageAccount.parse(storageConnectionStringProd);
    }


    private static long retrieveMessagesFromQueue(int numberOfmessages) throws URISyntaxException, InvalidKeyException, StorageException, IOException {
        CloudQueue queue = null;
        int countCopied = 0;
        queue = getOrCreateQueue(queueName);
        queue.downloadAttributes();
        long cachedMessageCount = queue.getApproximateMessageCount();
        StringBuilder builder = new StringBuilder("");

        while(cachedMessageCount>0 && countCopied<numberOfmessages) {
            try {
                CloudQueueMessage retrievedMessage = queue.retrieveMessage();
                CloudQueue finalQueue = queue;
                System.out.println(retrievedMessage.getMessageContentAsString());
                builder.append(retrievedMessage.getMessageContentAsString() + ";"+ retrievedMessage.getInsertionTime()+"\n");

                queue.deleteMessage(retrievedMessage);
            } catch (StorageException e) {
                e.printStackTrace();
            }
            queue.downloadAttributes();
            countCopied++;
            cachedMessageCount = queue.getApproximateMessageCount();
        }
        writeToFile(builder.toString());
        return countCopied;
    }


    private static long getCount() throws URISyntaxException, InvalidKeyException, StorageException {
        CloudQueue queue = null;
        queue = getOrCreateQueue(queueName);
        queue.downloadAttributes();
        long cachedMessageCount = queue.getApproximateMessageCount();
        System.out.println("count: "+ cachedMessageCount);
        return cachedMessageCount;
    }

    private static void updateFromFile() throws IOException {
        Path path = Paths.get(filePath);

        Stream<String> lines = Files.lines(path);
        String data = lines.collect(Collectors.joining("\n"));
        lines.close();
        insert(data);
    }

    private static void writeToFile(String message) throws IOException {
        String str = "";
        Path path = Paths.get(filePath);
        byte[] strToBytes = message.getBytes();
        Files.write(path, strToBytes, StandardOpenOption.APPEND);
    }
}