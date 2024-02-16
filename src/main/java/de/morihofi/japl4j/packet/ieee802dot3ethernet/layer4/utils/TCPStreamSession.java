package de.morihofi.japl4j.packet.ieee802dot3ethernet.layer4.utils;

import de.morihofi.japl4j.packet.ieee802dot3ethernet.layer4.TCPPacket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class TCPStreamSession {
    private ByteArrayOutputStream clientToServerStream = new ByteArrayOutputStream();
    private ByteArrayOutputStream serverToClientStream = new ByteArrayOutputStream();

    public void addPacket(TCPPacket packet, String srcIP, int srcPort, String destIP, int destPort) throws IOException {
        // Determine the direction of the packet
        String streamKey = TCPStreamBuilder.getStreamKey(srcIP, srcPort, destIP, destPort);
        if (streamKey.equals(TCPStreamBuilder.getStreamKey(packet.getNetworkPacket().getSourceAddress(), packet.getSourcePort(), packet.getNetworkPacket().getDestinationAddress(), packet.getDestinationPort()))) {
            // This packet is from client to server
            clientToServerStream.write(packet.getPayload());
        } else {
            // This packet is from server to client
            serverToClientStream.write(packet.getPayload());
        }

    }

    public ByteBuffer getClientToServerStream() {
        return ByteBuffer.wrap(clientToServerStream.toByteArray());
    }

    public ByteBuffer getServerToClientStream() {
        return ByteBuffer.wrap(serverToClientStream.toByteArray());
    }
}
