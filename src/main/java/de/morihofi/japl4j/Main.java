package de.morihofi.japl4j;

import de.morihofi.japl4j.packet.layer2.EthernetPacket;
import de.morihofi.japl4j.packet.PcapPacket;
import de.morihofi.japl4j.packet.layer3.IPv4Packet;
import de.morihofi.japl4j.packet.layer3.IPv6Packet;

import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws Exception {
        PcapFile pcapFile = PcapFile.openPcapFile(Paths.get("C:\\Users\\Moritz\\Documents\\test.pcap"));
        System.out.println(pcapFile.getFileVersion());
        System.out.println(pcapFile.getLinkType());

        for (PcapPacket packet : pcapFile) {
            // Process every Packet
            System.out.println("Timestamp: " + packet.getTimestampSeconds() + "s " + packet.getTimestampMicroOrNanoSeconds() + "μs");
            System.out.println("Captured Length: " + packet.getCapturedPacketLength());
            System.out.println("Original Length: " + packet.getOriginalPacketLength());
            if (packet instanceof EthernetPacket) {
                EthernetPacket ethernetPacket = (EthernetPacket) packet;
                System.out.println("Destination MAC: " + getBytesAsHex(ethernetPacket.getDestinationMac()));
                switch (ethernetPacket.getEtherType()) {
                    case EthernetPacket.TYPE_IPV4:
                        IPv4Packet ipv4Packet = (IPv4Packet) ethernetPacket.getIPPacket();
                        System.out.println("Destination IPv4: " + ipv4Packet.getDestinationAddress());
                        // Verarbeiten des IP-Pakets ...
                        break;
                    case EthernetPacket.TYPE_IPV6:
                        IPv6Packet ipv6Packet = (IPv6Packet) ethernetPacket.getIPPacket();
                        System.out.println("Destination IPv6: " + ipv6Packet.getDestinationAddress());
                        break;
                }
            }

        }
    }

    static String getBytesAsHex(byte[] bytes) {
        StringBuilder bobTheBuilder = new StringBuilder();
        for (byte b : bytes) {
            String st = String.format("%02X", b);
            bobTheBuilder.append(st);
        }
        return bobTheBuilder.toString();
    }
}