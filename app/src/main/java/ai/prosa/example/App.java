/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package ai.prosa.example;

import ai.prosa.asr.streaming.messages.*;
import ai.prosa.asr.streaming.StreamingAsrClient;
import ai.prosa.asr.streaming.StreamingAsrHandler;

import java.io.*;
import java.net.URI;
import java.util.Arrays;

public class App {

    public static final String defaultAsrUri = "wss://s-api.prosa.ai/v2/speech/stt";
    public static final int CHUNK_SIZE = 16000;

    public static void main(String[] args) {
        String filename = args[0];

        String asrUri = defaultAsrUri;

        if (args.length >= 2) {
            asrUri = args[1];
        }

        String apiKey;
        if (args.length < 3) {
            apiKey = System.getenv("API_KEY");
        } else {
            apiKey = args[2];
        }

        System.out.printf("Attempting connection to %s%n", asrUri);

        StreamingAsrClient client = new StreamingAsrClient(URI.create(asrUri), apiKey, new StreamingAsrConfig(
                "example",
                "stt-general-online",
                false,
                true,
                null
        ), new StreamingAsrHandler() {
            @Override
            public void onCreated(MessageCreated message) {
                System.out.println(message);
            }

            @Override
            public void onStatus(MessageStatus message) {
                System.out.println(message);
            }

            @Override
            public void onPartialResult(MessagePartialResult message) {
                System.out.println(message);
            }

            @Override
            public void onFinalResult(MessageFinalResult message) {
                System.out.println(message);
            }

            @Override
            public void onClosingMetadata(MessageMetadata message) {
                System.out.println(message);
            }

            @Override
            public void onClosingQuotaAlert(MessageQuotaAlert message) {
                System.out.println(message);
            }

            @Override
            public void onError(Exception exc) {
                System.out.println(exc);
            }
        });

        File file = new File(filename);
        FileInputStream fileInputStream = null;
        byte[] bFile = new byte[CHUNK_SIZE];

        try {
            client.start();

            fileInputStream = new FileInputStream(file);
            while (true) {
                int read = fileInputStream.read(bFile);
                if (read == -1) {
                    break;
                }
                if (read != CHUNK_SIZE) {
                    bFile = Arrays.copyOfRange(bFile,0, read);
                }
                client.send(bFile);
             }

            client.end();
            fileInputStream.close();

            client.waitEnd();

        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

    }
}
