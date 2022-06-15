package ai.prosa.asr.streaming;
import ai.prosa.asr.streaming.messages.*;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;

public class StreamingAsrClient {

    public enum State {
        INITIAL,
        STARTING,
        READY,
        IN_PROGRESS,
        WAITING,
        ENDING,
        ENDED
    }

    private class AsrWsClient extends WebSocketClient{
        public AsrWsClient(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakeData) {
            sendConfig();
        }

        @Override
        public void onMessage(String message) {
            receiveMessage(message);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            state = State.ENDED;
            if (waitingThread != null) {
                synchronized(waitingThread) {
                    waitingThread.notifyAll();
                }
            }
            if (code != 1000) {
                throw new RuntimeException("WS ended with code " + code);
            }
        }

        @Override
        public void onError(Exception ex) {
            handler.onError(ex);
        }
    }

    private final Gson gson;
    private final StreamingAsrConfig config;
    private final AsrWsClient ws;
    private final StreamingAsrHandler handler;
    private boolean isAudioSent = false;

    private final Thread waitingThread;

    @Getter
    private State state;

    public StreamingAsrClient(URI serverUri, String apiKey, StreamingAsrConfig config, StreamingAsrHandler handler) {
        this.config = config;
        this.handler = handler;
        this.ws = new AsrWsClient(serverUri);
        ws.addHeader("x-api-key", apiKey);

        state = State.INITIAL;

        gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

        waitingThread = Thread.currentThread();
    }

    /** Start the streaming ASR process. */
    public void start() throws InterruptedException {
        if (state == State.INITIAL) {
            ws.connectBlocking();
            if (state == State.INITIAL) {
                state = State.STARTING;
            }
        } else {
            throw new IllegalStateException();
        }
    }

    /** Send a chunk of audio. */
    public void send(byte[] audioData) {
        if (state == State.ENDED) {
            throw new IllegalStateException();
        }
        ws.send(audioData);
        isAudioSent = true;
    }

    /** Signify the end of audio stream. */
    public void end() {
        ws.send(new byte[0]);
        state = State.WAITING;
    }

    /** Cancel and interrupt the currently running process. */
    public void cancel() throws InterruptedException {
        if (state != State.ENDED) {
            ws.closeBlocking();
        } else {
            throw new IllegalStateException();
        }
    }

    public void waitEnd() throws InterruptedException {
        synchronized(waitingThread) {
            waitingThread.wait();
        }
    }

    private void sendConfig() {
        this.ws.send(gson.toJson(config));
        if (state == State.STARTING) {
            if (isAudioSent) {
                state = State.IN_PROGRESS;
            } else {
                state = State.READY;
            }
        }
    }

    private void receiveMessage(String stringMessage) {
        Map<String, Object> mapping = gson.fromJson(stringMessage, Map.class);
        String type = (String) mapping.get("type");

        Object message = null;

        switch (type) {
            case "created":
                message = new MessageCreated((String) mapping.get("id"));
                break;
            case "status":
                message = new MessageStatus(Progress.valueOf(((String) mapping.get("status")).toUpperCase()));
                break;
            case "partial":
                message = new MessagePartialResult((String) mapping.get("transcript"));
                break;
            case "result":
                message = new MessageFinalResult(
                    (String) mapping.get("transcript"),
                    (double) mapping.get("time_start"),
                    (double) mapping.get("time_end")
                );
                break;
            case "metadata":
                message = new MessageMetadata(
                    (double) mapping.get("duration"),
                    ((Double) mapping.get("quota_used")).intValue(),
                    (boolean) mapping.get("max_reached"),
                    (double) mapping.get("max_duration")
                );
                break;
            case "quota":
                message = new MessageQuotaAlert(
                    (boolean) mapping.get("active"),
                    (double) mapping.get("timestamp"),
                    (int) mapping.get("quota_used")
                );
                break;
        };

        if (message == null) {
            return;
        }

        handleMessage(message);
    }

    private void handleMessage(Object message) {
        if (message instanceof MessageCreated) {
            handler.onCreated((MessageCreated) message);
        } else if (message instanceof MessageStatus) {
            handler.onStatus((MessageStatus) message);
        } else if (message instanceof MessagePartialResult) {
            handler.onPartialResult((MessagePartialResult) message);
        } else if (message instanceof MessageFinalResult) {
            handler.onFinalResult((MessageFinalResult) message);
        } else if (message instanceof MessageMetadata) {
            handler.onClosingMetadata((MessageMetadata) message);
        } else if (message instanceof MessageQuotaAlert) {
            handler.onClosingQuotaAlert((MessageQuotaAlert) message);
        }
        if ((message instanceof MessageMetadata) || (message instanceof MessageQuotaAlert)) {
            if (state == State.WAITING) {
                state = State.ENDING;
            }
        }
    }

}
