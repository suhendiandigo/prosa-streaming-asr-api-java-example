package ai.prosa.asr.streaming.messages;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageMetadata {
    private final double duration;
    private final int quotaUsed;
    private final boolean maxReached;
    private final double maxDuration;
}
