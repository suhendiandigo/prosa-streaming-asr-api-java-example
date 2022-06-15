package ai.prosa.asr.streaming.messages;

public enum Progress {
    CREATED("created"),
    QUEUED("queued"),
    IN_PROGRESS("in_progress"),
    FAILED("failed"),
    COMPLETE("complete");

    private final String status;

    Progress(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
