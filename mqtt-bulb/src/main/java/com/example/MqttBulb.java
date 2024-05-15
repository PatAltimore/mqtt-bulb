package com.example;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import javax.swing.*;
import java.awt.*;
import java.util.UUID;

public class MqttBulb {

    private static final String BROKER_ENDPOINT = "tcp://x.x.x.x:1883";
    private static final String CLIENT_ID = "mqtt-java-bulb" + UUID.randomUUID();
    private static final String TOPIC = "light";

    private static boolean isLit = false; // Initial state: unlit

    public static void main(String[] args) {

        try {
            IMqttClient client = new MqttClient(BROKER_ENDPOINT, CLIENT_ID);
            try {
                // Connect to the MQTT broker
                client.connect();
                System.out.println("\nConnected to MQTT broker!");
                System.out.println("Broker endpoint: " + BROKER_ENDPOINT);
                System.out.println("Client ID: " + CLIENT_ID);
                System.out.println("Topic: " + TOPIC);


            } finally {
                client.disconnect(); // Close the client connection
            }

            // Display the light bulb.
            JButton toggleButton = new JButton(toggleLightBulb("off"));
            
            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("MQTT light bulb");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(300, 300);
    
                // Create a button with the light bulb icon

                toggleButton.addActionListener(e -> toggleButton.setIcon(toggleLightBulb("toggle"))); // Toggle the light bulb on button click
    
                frame.add(toggleButton, BorderLayout.CENTER);
                frame.setVisible(true);
            });

            // Subscribe to a topic

            System.out.println("\nSubscribing to topic " + TOPIC + ".");
            client.subscribe(TOPIC, (tpc, msg) -> {
                String message = new String(msg.getPayload());
                // Display the light bulb icon based on the received message
                SwingUtilities.invokeLater(() -> {
                    toggleButton.setIcon(toggleLightBulb(message));
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    private static ImageIcon toggleLightBulb(String message) {
        if (message.equals("toggle")) {
            isLit = !isLit;
        }
        else if (message.equals("on")) {
            isLit = true;
        }
        else if (message.equals("off")) {
            isLit = false;
        }
        String iconPath = isLit ? "resources/lit-bulb.png" : "resources/unlit-bulb.png";
        return new ImageIcon(iconPath);
    }

}
