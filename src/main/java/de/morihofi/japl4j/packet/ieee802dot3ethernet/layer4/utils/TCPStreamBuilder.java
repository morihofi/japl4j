package de.morihofi.japl4j.packet.ieee802dot3ethernet.layer4.utils;

import de.morihofi.japl4j.packet.ieee802dot3ethernet.layer4.TCPPacket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.TreeMap;

public class TCPStreamBuilder {
    private TreeMap<Long, TCPPacket> packets = new TreeMap<>();

    /**
     * Collects TCP packets while checking for duplicates and handling retransmissions.
     * Packets are stored in a sorted map to maintain the correct order based on sequence numbers.
     *
     * @param packet The TCPPacket to be collected.
     */
    public void collectPackets(TCPPacket packet) {
        // Check for existing packet with the same sequence number
        long seqNum = packet.getSequenceNumber();
        if (packets.containsKey(seqNum)) {
            TCPPacket existingPacket = packets.get(seqNum);

            // Check for retransmission (same sequence number but different data or length)
            if (!arePacketsIdentical(existingPacket, packet)) {
                handleRetransmission(existingPacket, packet);
            }
            // If packets are identical, it's a duplicate and can be ignored
        } else {
            // If no packet with the same sequence number exists, add the new packet
            packets.put(seqNum, packet);
        }
    }

    /**
     * Compares two TCP packets to determine if they are identical.
     * This method can be expanded to compare additional attributes if necessary.
     *
     * @param packet1 The first TCPPacket to compare.
     * @param packet2 The second TCPPacket to compare.
     * @return true if packets are identical, false otherwise.
     */
    private boolean arePacketsIdentical(TCPPacket packet1, TCPPacket packet2) {
        if(packet1.getPayload() == null || packet2.getPayload() == null){
            return false;
        }
        return packet1.getPayload().length == packet2.getPayload().length &&
                ByteBuffer.wrap(packet1.getPayload()).compareTo(ByteBuffer.wrap(packet2.getPayload())) == 0;
    }

    /**
     * Handles TCP packet retransmissions. The current implementation simply logs the occurrence.
     * This method can be expanded to implement more sophisticated handling logic.
     *
     * @param existingPacket The packet already in the collection.
     * @param newPacket      The retransmitted packet.
     */
    private void handleRetransmission(TCPPacket existingPacket, TCPPacket newPacket) {
        // Log or handle the retransmission as needed
        System.out.println("Retransmission detected. Sequence Number: " + newPacket.getSequenceNumber());
        // Example action: Replace the existing packet with the new one
        packets.put(newPacket.getSequenceNumber(), newPacket);
    }

    public ByteBuffer compileStream() throws IOException {
        // Initialisiere einen ByteArrayOutputStream zur flexiblen Speicherung der Daten
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        for (TCPPacket packet : packets.values()) {
            if (packet.getPayload() != null) { // Überprüfung hinzugefügt, um NullPointerException zu vermeiden
                stream.write(packet.getPayload());
            } else {
                System.out.println("Warning: A TCP packet without payload was skipped.");
            }
        }

        // Erstelle einen ByteBuffer aus dem gesammelten Stream
        return ByteBuffer.wrap(stream.toByteArray());
    }

    public static String getStreamKey(String srcIP, int srcPort, String destIP, int destPort) {
        return srcIP + ":" + srcPort + "->" + destIP + ":" + destPort;
    }

}
