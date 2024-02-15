package de.morihofi.japl4j.packet.layer2;

import de.morihofi.japl4j.PCAP_LINKTYPE;
import de.morihofi.japl4j.packet.PcapPacket;
import de.morihofi.japl4j.packet.layer3.IPv4Packet;
import de.morihofi.japl4j.packet.layer3.IPv6Packet;
import de.morihofi.japl4j.packet.layer3.NetworkPacket;

/**
 * Ethernet OSI Layer 2
 */
public class EthernetPacket extends PcapPacket {
    public EthernetPacket(long timestampSeconds, long timestampMicroOrNanoSeconds, int capturedPacketLength, int originalPacketLength, byte[] data) {
        super(timestampSeconds, timestampMicroOrNanoSeconds, capturedPacketLength, originalPacketLength, data);
        parseEthernetHeader(data);
    }

    private byte[] destinationMac;
    private byte[] sourceMac;
    private int etherType;
    private byte[] payload;

    // Ethernet Frame Types
    public static final int TYPE_IPV4 = 0x0800;
    public static final int TYPE_IPV6 = 0x86DD;


    private void parseEthernetHeader(byte[] data) {
        destinationMac = new byte[6];
        sourceMac = new byte[6];
        System.arraycopy(data, 0, destinationMac, 0, 6);
        System.arraycopy(data, 6, sourceMac, 0, 6);
        etherType = ((data[12] & 0xFF) << 8) | (data[13] & 0xFF);
        payload = new byte[data.length - 14];
        System.arraycopy(data, 14, payload, 0, payload.length);
    }

    public byte[] getDestinationMac() {
        return destinationMac;
    }

    public byte[] getSourceMac() {
        return sourceMac;
    }

    public int getEtherType() {
        return etherType;
    }

    public byte[] getPayload() {
        return payload;
    }

    public NetworkPacket getIPPacket() {
        switch (this.etherType) {
            case TYPE_IPV4:
                return new IPv4Packet(this.getTimestampSeconds(), this.getTimestampMicroOrNanoSeconds(), this.getCapturedPacketLength(), this.getOriginalPacketLength(), this.payload);
            case TYPE_IPV6:
                return new IPv6Packet(this.getTimestampSeconds(), this.getTimestampMicroOrNanoSeconds(), this.getCapturedPacketLength(), this.getOriginalPacketLength(), this.payload);
            default:
                throw new IllegalArgumentException("Unsupported EtherType for IP parsing: " + this.etherType);
        }
    }
}
