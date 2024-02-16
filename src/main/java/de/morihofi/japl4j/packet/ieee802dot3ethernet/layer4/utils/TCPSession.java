package de.morihofi.japl4j.packet.ieee802dot3ethernet.layer4.utils;

import de.morihofi.japl4j.packet.ieee802dot3ethernet.layer4.TCPPacket;

import java.io.IOException;
import java.nio.ByteBuffer;

public class TCPSession {
    private TCPStreamBuilder clientToServerBuilder = new TCPStreamBuilder();
    private TCPStreamBuilder serverToClientBuilder = new TCPStreamBuilder();

    // Fügt ein TCP-Paket zum entsprechenden Stream hinzu, basierend auf der Richtung
    public void addPacket(TCPPacket packet, boolean isClientToServer) {
        if (isClientToServer) {
            clientToServerBuilder.collectPackets(packet);
        } else {
            serverToClientBuilder.collectPackets(packet);
        }
    }

    // Kompiliert den Stream vom Client zum Server
    public ByteBuffer compileClientToServerStream() throws IOException {
        return clientToServerBuilder.compileStream();
    }

    // Kompiliert den Stream vom Server zum Client
    public ByteBuffer compileServerToClientStream() throws IOException {
        return serverToClientBuilder.compileStream();
    }
}