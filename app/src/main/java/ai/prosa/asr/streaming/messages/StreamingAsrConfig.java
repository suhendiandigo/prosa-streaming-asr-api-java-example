package ai.prosa.asr.streaming.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class StreamingAsrConfig {
    private String label;
    @NonNull
    private String model;
    private boolean includeFiller = false;
    private boolean includePartial = true;
    private AudioConfig audio = null;
}
