package de.morihofi.japl4j.packet.ieee802dot3ethernet.layer3;

import de.morihofi.japl4j.packet.ieee802dot3ethernet.layer4.TCPPacket;
import de.morihofi.japl4j.packet.ieee802dot3ethernet.layer4.UDPPacket;
import de.morihofi.japl4j.packet.ieee802dot3ethernet.layer4.TransportPacket;

import java.nio.ByteBuffer;

public class IPv6Packet extends NetworkPacket {
    private int version;
    private int payloadLength;
    private int nextHeader; // This corresponds to the "Protocol" field in IPv4
    private String sourceAddress;
    private String destinationAddress;
    private byte[] payload;

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
        // The IPv6 header is 40 bytes long. Everything after that up to the end of the payloadLength is the payload.
        // However, we must ensure that the total length of the data packet also supports this.
        int totalHeaderLength = 40; // Feste Länge des IPv6-Headers
        if (data.length >= totalHeaderLength + payloadLength) {
            payload = new byte[payloadLength];
            buffer.get(payload, 0, payloadLength);
        } else {
            // If the data length is not sufficient, we set the payload as an empty array,
            // which could indicate a problem with the data.
            payload = new byte[0];
        }
    }

    public byte[] getPayload() {
        return payload;
    }

    public TransportPacket getTransportLayerPacket() {
        if (this.nextHeader == 6) { // TCP
            return new TCPPacket(this, this.payload);
        } else if (this.nextHeader == 17) { // UDP
            return new UDPPacket(this, this.payload);
        } else {
            return null; // For other protocols or unsupported cases
        }
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

    @Override
    public String getSourceAddress() {
        return sourceAddress;
    }
    @Override
    public String getDestinationAddress() {
        return destinationAddress;
    }
}