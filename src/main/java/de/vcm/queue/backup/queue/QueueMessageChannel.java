package de.vcm.queue.backup.queue;

public enum QueueMessageChannel {
    STATION("station"),
    STATION_SLOT_INDIVIDUAL("stationslot-individual"),
    STATION_SLOT("stationslot");
    private String channelName;

    QueueMessageChannel(String channelName){
        this.channelName = channelName;
    }

    public String getChannelName() {
        return channelName;
    }
}

