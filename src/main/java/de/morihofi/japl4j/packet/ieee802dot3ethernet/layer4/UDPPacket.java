package de.morihofi.japl4j.packet.ieee802dot3ethernet.layer4;

import java.nio.ByteBuffer;

public class UDPPacket extends TransportPacket {
    private int sourcePort;
    private int destinationPort;
    private int length;
    private int checksum;
    private byte[] payload;

    public UDPPacket(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        sourcePort = Short.toUnsignedInt(buffer.getShort());
        destinationPort = Short.toUnsignedInt(buffer.getShort());
        length = Short.toUnsignedInt(buffer.getShort());
        checksum = Short.toUnsignedInt(buffer.getShort());

        payload = new byte[length - 8]; // UDP-Header ist 8 Bytes lang
        buffer.get(payload, 0, payload.length);
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public int getDestinationPort() {
        return destinationPort;
    }

    public int getLength() {
        return length;
    }

    public int getChecksum() {
        return checksum;
    }

    public byte[] getPayload() {
        return payload;
    }
}