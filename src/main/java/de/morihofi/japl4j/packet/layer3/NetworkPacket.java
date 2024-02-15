package de.morihofi.japl4j.packet.layer3;

import de.morihofi.japl4j.packet.PcapPacket;

public abstract class NetworkPacket extends PcapPacket {
    public NetworkPacket(long timestampSeconds, long timestampMicroOrNanoSeconds, int capturedPacketLength, int originalPacketLength, byte[] data) {
        super(timestampSeconds, timestampMicroOrNanoSeconds, capturedPacketLength, originalPacketLength, data);
    }
}