package ai.prosa.asr.streaming;

import ai.prosa.asr.streaming.messages.*;

public interface StreamingAsrHandler {

    void onCreated(MessageCreated message);

    void onStatus(MessageStatus message);

    void onPartialResult(MessagePartialResult message);

    void onFinalResult(MessageFinalResult message);

    void onClosingMetadata(MessageMetadata message);

    void onClosingQuotaAlert(MessageQuotaAlert message);

    void onError(Exception exc);

}
