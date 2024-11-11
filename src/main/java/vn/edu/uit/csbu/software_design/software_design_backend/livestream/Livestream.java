package vn.edu.uit.csbu.software_design.software_design_backend.livestream;

public class Livestream {
    protected String streamerName;
    protected int streamId;
    public String getStreamerName() {
        return streamerName;
    }
    public void setStreamerName(String streamerName) {
        this.streamerName = streamerName;
    }
    public int getStreamId() {
        return streamId;
    }
    public void setStreamId(int streamId) {
        this.streamId = streamId;
    }


    public Livestream(String streamerName, int streamId){
        this.streamId = streamId;
        this.streamerName = streamerName;
    }
}
