package de.morihofi.japl4j.packet.ieee802dot3ethernet.layer3;

import de.morihofi.japl4j.packet.ieee802dot3ethernet.layer4.TCPPacket;
import de.morihofi.japl4j.packet.ieee802dot3ethernet.layer4.UDPPacket;
import de.morihofi.japl4j.packet.ieee802dot3ethernet.layer4.TransportPacket;

import java.nio.ByteBuffer;

public class IPv4Packet extends NetworkPacket {
    private int version;
    private int headerLength;
    private int totalLength;
    private int protocol;
    private String sourceAddress;
    private String destinationAddress;
    private byte[] payload;

    public IPv4Packet(long timestampSeconds, long timestampMicroOrNanoSeconds, int capturedPacketLength, int originalPacketLength, byte[] data) {
        super(timestampSeconds, timestampMicroOrNanoSeconds, capturedPacketLength, originalPacketLength, data);
        parseIPv4Header(data);
    }

    private void parseIPv4Header(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int firstByte = buffer.get() & 0xFF;
        version = firstByte >> 4;
        headerLength = (firstByte & 0x0F) * 4; // IHL in 32-bit words, converted to bytes

        buffer.get(); // Skips the type of service byte
        totalLength = Short.toUnsignedInt(buffer.getShort()); // Correct reading of the totalLength

        buffer.position(9); // Jump directly to the protocol
        protocol = buffer.get() & 0xFF;

        buffer.position(12); // Jump to the source IP address
        byte[] srcAddr = new byte[4];
        buffer.get(srcAddr);
        sourceAddress = parseIPv4Address(srcAddr);

        byte[] dstAddr = new byte[4];
        buffer.get(dstAddr);
        destinationAddress = parseIPv4Address(dstAddr);

        // Jump to Payload
        buffer.position(headerLength);

        if (totalLength > headerLength) {
            int payloadLength = totalLength - headerLength;
            payload = new byte[payloadLength];
            buffer.get(payload);
        } else {
            payload = new byte[0];
        }
    }

    public String parseIPv4Address(byte[] ip) {
        return String.format("%d.%d.%d.%d", ip[0] & 0xFF, ip[1] & 0xFF, ip[2] & 0xFF, ip[3] & 0xFF);
    }


    public TransportPacket getTransportLayerPacket() {
        if (this.protocol == 6) { // TCP
            return new TCPPacket(this.payload);
        } else if (this.protocol == 17) { // UDP
            return new UDPPacket(this.payload);
        } else {
            return null; // For other protocols or unsupported cases
        }
    }

    public int getVersion() {
        return version;
    }

    public int getHeaderLength() {
        return headerLength;
    }

    public int getTotalLength() {
        return totalLength;
    }

    public int getProtocol() {
        return protocol;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }
}