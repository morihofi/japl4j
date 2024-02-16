package de.morihofi.japl4j.packet.ieee802dot3ethernet.layer3;

import de.morihofi.japl4j.packet.PcapPacket;

public abstract class NetworkPacket extends PcapPacket {
    public NetworkPacket(long timestampSeconds, long timestampMicroOrNanoSeconds, int capturedPacketLength, int originalPacketLength, byte[] data) {
        super(timestampSeconds, timestampMicroOrNanoSeconds, capturedPacketLength, originalPacketLength, data);
    }

    public abstract String getSourceAddress();

    public abstract String getDestinationAddress();

}