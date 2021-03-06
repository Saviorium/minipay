package ru.minipay.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ru.minipay.api.Request;
import ru.minipay.api.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.*;

public class ClientMultiThread {
    private final String hostName;
    private final int port;
    private final ObjectMapper jsonParser = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    private static final int NTHREADS = 10;
    private final ExecutorService exec = Executors.newFixedThreadPool(NTHREADS);

    private final static int DEFAULT_PORT = 12345;

    public ClientMultiThread(String hostName) {
        this(hostName, DEFAULT_PORT);
    }

    public ClientMultiThread(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    public Future<Response> addRequest(Request request) {
        return exec.submit(new ClientWorker(request));
    }

    public void awaitTermination() throws InterruptedException {
        exec.shutdown();
        exec.awaitTermination(10L, TimeUnit.SECONDS);
    }

    private Response send(Request request) {
        Response response = null;

        try (
                Socket socket = new Socket(hostName, port);
                PrintWriter out =
                        new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(socket.getInputStream()))
        ) {
            out.println(jsonParser.writeValueAsString(request));
            String responseStr = in.readLine();
            response = jsonParser.readValue(responseStr, Response.class);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return response;
    }

    private class ClientWorker implements Callable<Response> {
        private final Request request;

        private ClientWorker(Request request) {
            this.request = request;
        }

        @Override
        public Response call() {
            return send(request);
        }
    }

}
