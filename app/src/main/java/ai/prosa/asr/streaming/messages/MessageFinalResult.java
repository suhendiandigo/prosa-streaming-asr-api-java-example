package ai.prosa.asr.streaming.messages;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageFinalResult {
    private final String transcript;
    private final double timeStart;
    private final double timeEnd;

}

