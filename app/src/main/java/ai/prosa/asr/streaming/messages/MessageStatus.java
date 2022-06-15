package ai.prosa.asr.streaming.messages;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageStatus {
    private final Progress status;
}