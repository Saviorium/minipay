package ru.minipay.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println(
                    "Usage: java java.ru.minipay.client.Client <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
                Socket echoSocket = new Socket(hostName, portNumber);
                PrintWriter out =
                        new PrintWriter(echoSocket.getOutputStream(), true);
                BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(echoSocket.getInputStream()));
                BufferedReader stdIn =
                        new BufferedReader(
                                new InputStreamReader(System.in))
        ) {
            /*out.println("{\"type\":\"CreateAccount\"}");
            String response = in.readLine();
            System.out.println("Server: " + response);*/

            StringBuilder requestStr = new StringBuilder("{\"type\":\"FundTransfer\",\"fromAccId\":\"");
            System.out.println("Enter id from:");
            requestStr.append(stdIn.readLine()).append("\",\"toAccId\":\"");
            System.out.println("Enter id to:");
            requestStr.append(stdIn.readLine()).append("\",\"currency\":\"RUB\",\"amount\":");
            System.out.println("Enter amount RUB:");
            requestStr.append(stdIn.readLine()).append("}");

            System.out.println(requestStr);

            out.println(requestStr);
            String response = in.readLine();
            System.out.println("Server: " + response);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
    }
}
