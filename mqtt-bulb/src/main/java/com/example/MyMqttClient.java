package com.example;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import java.util.UUID;
import java.util.Scanner;
import java.util.ArrayList;
import java.time.Instant;

public class MyMqttClient {
    private static final String BROKER_ENDPOINT = "tcp://x.x.x.x:1883";
    private static final String CLIENT_ID = "mqtt-java-client" + UUID.randomUUID();
    private static final String TOPIC = "java";

    public static void main(String[] args) {
        ArrayList<String> receivedMessages = new ArrayList<>();

        try {
            IMqttClient client = new MqttClient(BROKER_ENDPOINT, CLIENT_ID);
            // Connect to the MQTT broker
            client.connect();
            System.out.println("\nConnected to MQTT broker!");
            System.out.println("Broker endpoint: " + BROKER_ENDPOINT);
            System.out.println("Client ID: " + CLIENT_ID);
            System.out.println("Topic: " + TOPIC);

            // Subscribe to a topic
            System.out.println("\nSubscribing to topic " + TOPIC + ".");
            client.subscribe(TOPIC, (tpc, msg) -> {
                // Add a timestamp and the received message to an array list
                receivedMessages.add(Instant.now().toString() + " : " + new String(msg.getPayload()));
            });

            Scanner scanner = new Scanner(System.in);
            boolean exit = false;

            // List a menu of options. Loop until the user chooses to exit.
            while (!exit) {

                // Display menu
                System.out.println("\n-------------------------");
                System.out.println("1. Publish message");
                System.out.println("2. List received messages");
                System.out.println("3. Toggle light");
                System.out.println("4. Disconnect, quit");
                System.out.print("Choose an option: ");

                // Get user input
                int option = 0;
                if (scanner.hasNextInt()) {
                    option = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline character
                } else {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.nextLine(); // Consume the newline character
                    continue;
                }

                // Publish a message to a topic
                if (option == 1) {
                    System.out.print("\nEnter message to publish to topic " + TOPIC + ": ");
                    String message = scanner.nextLine();
                    client.publish(TOPIC, message.getBytes(), 0, false);
                    System.out.println("Published message: " + message);

                // List received messages
                } else if (option == 2) {
                    System.out.println("\nReceived messages for topic " + TOPIC + ":");
                    for (String msg : receivedMessages) {
                        System.out.println(msg);
                    }

                // Toggle light
                } else if (option == 3) {
                    System.out.println("\nToggling light.");
                    client.publish("light", "toggle".getBytes(), 0, false);
                    
                // Disconnect and exit
                } else if (option == 4) {
                    System.out.println("\nDisconnecting from MQTT broker and exiting.");
                    client.disconnect();
                    client.close();
                    scanner.close();
                    exit = true;
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
