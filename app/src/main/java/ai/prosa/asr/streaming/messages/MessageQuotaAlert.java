package ai.prosa.asr.streaming.messages;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageQuotaAlert {
    private final boolean active;
    private final double timestamp;
    private final int quotaUsed;
}