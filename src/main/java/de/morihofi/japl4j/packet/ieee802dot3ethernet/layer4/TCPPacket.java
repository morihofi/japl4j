package de.morihofi.japl4j.packet.ieee802dot3ethernet.layer4;

import java.nio.ByteBuffer;

public class TCPPacket extends TransportPacket{
    private int sourcePort;
    private int destinationPort;
    private long sequenceNumber;
    private long acknowledgmentNumber;
    private int dataOffset;
    private boolean ack;
    private boolean syn;
    private boolean fin;
    private boolean rst;
    private boolean psh;
    private boolean urg;
    private int windowSize;
    private int checksum;
    private int urgentPointer;
    private byte[] optionsAndPadding; // Optional, kann variieren
    private byte[] payload;

    public TCPPacket(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        sourcePort = Short.toUnsignedInt(buffer.getShort());
        destinationPort = Short.toUnsignedInt(buffer.getShort());
        sequenceNumber = Integer.toUnsignedLong(buffer.getInt());
        acknowledgmentNumber = Integer.toUnsignedLong(buffer.getInt());
        int offsetAndFlags = Short.toUnsignedInt(buffer.getShort());
        dataOffset = (offsetAndFlags >> 12) * 4; // in Bytes
        ack = (offsetAndFlags & 0x10) != 0;
        syn = (offsetAndFlags & 0x02) != 0;
        fin = (offsetAndFlags & 0x01) != 0;
        rst = (offsetAndFlags & 0x04) != 0;
        psh = (offsetAndFlags & 0x08) != 0;
        urg = (offsetAndFlags & 0x20) != 0;
        windowSize = Short.toUnsignedInt(buffer.getShort());
        checksum = Short.toUnsignedInt(buffer.getShort());
        urgentPointer = Short.toUnsignedInt(buffer.getShort());

        int optionsLength = dataOffset - 20; // Grund-TCP-Header ist 20 Bytes lang
        if (optionsLength > 0) {
            optionsAndPadding = new byte[optionsLength];
            buffer.get(optionsAndPadding, 0, optionsLength);
        }

        int payloadLength = data.length - dataOffset;
        if (payloadLength > 0) {
            payload = new byte[payloadLength];
            buffer.get(payload, 0, payloadLength);
        }
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public int getDestinationPort() {
        return destinationPort;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public long getAcknowledgmentNumber() {
        return acknowledgmentNumber;
    }

    public int getDataOffset() {
        return dataOffset;
    }

    public boolean isAck() {
        return ack;
    }

    public boolean isSyn() {
        return syn;
    }

    public boolean isFin() {
        return fin;
    }

    public boolean isRst() {
        return rst;
    }

    public boolean isPsh() {
        return psh;
    }

    public boolean isUrg() {
        return urg;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public int getChecksum() {
        return checksum;
    }

    public int getUrgentPointer() {
        return urgentPointer;
    }

    /**
     * Returns the options and padding of the TCP Packet, can be null if there is no options and padding
     * @return
     */
    public byte[] getOptionsAndPadding() {
        return optionsAndPadding;
    }

    /**
     * Returns the Payload of the TCP Packet, can be null if there is no payload
     * @return Payload of the TCP Packet
     */
    public byte[] getPayload() {
        return payload;
    }
}