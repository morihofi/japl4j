package de.morihofi.japl4j.packet.layer3;

import java.nio.ByteBuffer;

public class IPv4Packet extends NetworkPacket {
    private int version;
    private int headerLength;
    private int totalLength;
    private int protocol;
    private String sourceAddress;
    private String destinationAddress;

    public IPv4Packet(long timestampSeconds, long timestampMicroOrNanoSeconds, int capturedPacketLength, int originalPacketLength, byte[] data) {
        super(timestampSeconds, timestampMicroOrNanoSeconds, capturedPacketLength, originalPacketLength, data);
        parseIPv4Header(data);
    }

    private void parseIPv4Header(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int firstByte = buffer.get() & 0xFF;
        version = firstByte >> 4;
        headerLength = (firstByte & 0x0F) * 4; // IHL is in 32-bit words
        buffer.get(); // Skip type of service
        totalLength = Short.toUnsignedInt(buffer.getShort());
        buffer.position(buffer.position() + 5); // Skip identification, flags, fragment offset
        buffer.get(); // TTL
        protocol = buffer.get() & 0xFF;
        buffer.position(buffer.position() + 2); // Skip header checksum
        sourceAddress = parseIPv4Address(buffer.getInt());
        destinationAddress = parseIPv4Address(buffer.getInt());
    }

    private String parseIPv4Address(int address) {
        return String.format("%d.%d.%d.%d", (address >> 24) & 0xFF, (address >> 16) & 0xFF, (address >> 8) & 0xFF, address & 0xFF);
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