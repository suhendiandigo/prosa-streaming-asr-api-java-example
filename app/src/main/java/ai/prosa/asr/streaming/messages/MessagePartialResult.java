package ai.prosa.asr.streaming.messages;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessagePartialResult {
    private String transcript;
}