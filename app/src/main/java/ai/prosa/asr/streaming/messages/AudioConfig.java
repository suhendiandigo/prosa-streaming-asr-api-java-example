package ai.prosa.asr.streaming.messages;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class AudioConfig {
    private AudioFormat format = AudioFormat.PCM_16;
    private Integer channels;
    private Integer sample_rate;
}
