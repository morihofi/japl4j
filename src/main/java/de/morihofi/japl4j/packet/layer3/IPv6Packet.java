package de.morihofi.japl4j.packet.layer3;

import java.nio.ByteBuffer;

public class IPv6Packet extends NetworkPacket {
    private int version;
    private int payloadLength;
    private int nextHeader;
    private String sourceAddress;
    private String destinationAddress;

    public IPv6Packet(long timestampSeconds, long timestampMicroOrNanoSeconds, int capturedPacketLength, int originalPacketLength, byte[] data) {
        super(timestampSeconds, timestampMicroOrNanoSeconds, capturedPacketLength, originalPacketLength, data);
        parseIPv6Header(data);
    }

    private void parseIPv6Header(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int firstFourBytes = buffer.getInt();
        version = firstFourBytes >>> 28;
        // Skip 4 bits of traffic class and 20 bits of flow label
        payloadLength = buffer.getShort() & 0xFFFF;
        nextHeader = buffer.get() & 0xFF;
        buffer.get(); // Hop limit
        byte[] sourceAddrBytes = new byte[16];
        buffer.get(sourceAddrBytes, 0, 16);
        sourceAddress = parseIPv6Address(sourceAddrBytes);
        byte[] destinationAddrBytes = new byte[16];
        buffer.get(destinationAddrBytes, 0, 16);
        destinationAddress = parseIPv6Address(destinationAddrBytes);
    }

    private String parseIPv6Address(byte[] address) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < address.length; i += 2) {
            sb.append(String.format("%02x%02x", address[i], address[i + 1]));
            if (i < address.length - 2) {
                sb.append(":");
            }
        }
        return sb.toString();
    }

    public int getVersion() {
        return version;
    }

    public int getPayloadLength() {
        return payloadLength;
    }

    public int getNextHeader() {
        return nextHeader;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }
}