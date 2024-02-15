package de.morihofi.japl4j.packet;

import de.morihofi.japl4j.PCAP_LINKTYPE;

public class PcapPacket {
    private long timestampSeconds;
    private long timestampMicroOrNanoSeconds;
    private int capturedPacketLength;
    private int originalPacketLength;
    private byte[] data;

    public PcapPacket(long timestampSeconds, long timestampMicroOrNanoSeconds, int capturedPacketLength, int originalPacketLength, byte[] data) {
        this.timestampSeconds = timestampSeconds;
        this.timestampMicroOrNanoSeconds = timestampMicroOrNanoSeconds;
        this.capturedPacketLength = capturedPacketLength;
        this.originalPacketLength = originalPacketLength;
        this.data = data;
    }

    public long getTimestampSeconds() {
        return timestampSeconds;
    }

    public long getTimestampMicroOrNanoSeconds() {
        return timestampMicroOrNanoSeconds;
    }

    public int getCapturedPacketLength() {
        return capturedPacketLength;
    }

    public int getOriginalPacketLength() {
        return originalPacketLength;
    }

    public byte[] getData() {
        return data;
    }
}
