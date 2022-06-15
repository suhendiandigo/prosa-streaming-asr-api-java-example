package ai.prosa.asr.streaming.messages;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageCreated {
    private final String id;
}

