package de.morihofi.japl4j;

import de.morihofi.japl4j.packet.ieee802dot3ethernet.layer2.EthernetPacket;
import de.morihofi.japl4j.packet.PcapPacket;
import de.morihofi.japl4j.packet.ieee802dot3ethernet.layer3.IPv4Packet;
import de.morihofi.japl4j.packet.ieee802dot3ethernet.layer4.TCPPacket;
import de.morihofi.japl4j.packet.ieee802dot3ethernet.layer4.utils.TCPSession;
import de.morihofi.japl4j.packet.ieee802dot3ethernet.layer4.utils.TCPStreamBuilder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        PcapFile pcapFile = PcapFile.openPcapFile(Paths.get("C:\\Users\\Moritz\\Downloads\\telnet-raw.pcap"));
        System.out.println(pcapFile.getFileVersion());
        System.out.println(pcapFile.getLinkType());

        // Map to manage TCP sessions
        Map<String, TCPSession> sessions = new HashMap<>();

        for (PcapPacket packet : pcapFile) {
            if (packet instanceof EthernetPacket) {
                EthernetPacket ethernetPacket = (EthernetPacket) packet;
                if (ethernetPacket.getEtherType() == EthernetPacket.TYPE_IPV4) {
                    IPv4Packet ipv4Packet = (IPv4Packet) ethernetPacket.getIPPacket();
                    if (ipv4Packet.getTransportLayerPacket() instanceof TCPPacket) {
                        TCPPacket tcpPacket = (TCPPacket) ipv4Packet.getTransportLayerPacket();
                        String sessionKey = TCPStreamBuilder.getStreamKey(ipv4Packet.getSourceAddress(), tcpPacket.getSourcePort(), ipv4Packet.getDestinationAddress(), tcpPacket.getDestinationPort());
                        boolean isClientToServer = determineDirection(tcpPacket, ipv4Packet); // Implement this method based on your criteria

                        TCPSession session = sessions.computeIfAbsent(sessionKey, k -> new TCPSession());
                        session.addPacket(tcpPacket, isClientToServer);
                    }
                }
            }
        }

        // Compile and print the streams at the end of packet processing
        sessions.forEach((sessionKey, session) -> {
            try {
                System.out.println("--- NEW STREAM ---");
                System.out.println("Session: " + sessionKey);
                System.out.println("Client to Server Stream:");
                printStreamContent(session.compileClientToServerStream());
                System.out.println("Server to Client Stream:");
                printStreamContent(session.compileServerToClientStream());

            } catch (IOException e) {
                System.err.println("Failed to compile stream for session: " + sessionKey);
                e.printStackTrace();
            }
        });

        System.out.println("Finish!");
    }

    private static boolean determineDirection(TCPPacket tcpPacket, IPv4Packet ipv4Packet) {
        // Implement logic to determine the direction of the traffic
        // This is a placeholder implementation
        return true; // Assume all traffic is client to server for demonstration
    }

    private static void printStreamContent(ByteBuffer stream) {
        String content = new String(stream.array(), StandardCharsets.UTF_8);
        System.out.println(content);
    }

    static String getBytesAsHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = String.format("%02X", b);
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
