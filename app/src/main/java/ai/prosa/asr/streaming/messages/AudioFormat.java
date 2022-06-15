package ai.prosa.asr.streaming.messages;

import com.google.gson.annotations.SerializedName;

public enum AudioFormat {
    @SerializedName("s16le")
    PCM_16("s16le"),
    @SerializedName("wav")
    WAV("wav"),
    @SerializedName("mp3")
    MP3("mp3");

    private final String name;

    AudioFormat(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
