package de.morihofi.japl4j;

import de.morihofi.japl4j.packet.ieee802dot3ethernet.layer2.EthernetPacket;
import de.morihofi.japl4j.packet.PcapPacket;
import de.morihofi.japl4j.packet.ieee802dot3ethernet.layer3.IPv4Packet;
import de.morihofi.japl4j.packet.ieee802dot3ethernet.layer3.IPv6Packet;
import de.morihofi.japl4j.packet.ieee802dot3ethernet.layer4.TCPPacket;
import de.morihofi.japl4j.packet.ieee802dot3ethernet.layer4.TransportPacket;
import de.morihofi.japl4j.packet.ieee802dot3ethernet.layer4.UDPPacket;
import de.morihofi.japl4j.packet.ieee802dot3ethernet.layer4.utils.TCPStreamBuilder;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        PcapFile pcapFile = PcapFile.openPcapFile(Paths.get("C:\\Users\\Moritz\\Downloads\\http.cap"));
        System.out.println(pcapFile.getFileVersion());
        System.out.println(pcapFile.getLinkType());

        Map<String, TCPStreamBuilder> tcpStreamBuilders = new HashMap<>();

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
                        System.out.println("Source IPv4: " + ipv4Packet.getSourceAddress());
                        System.out.println("Destination IPv4: " + ipv4Packet.getDestinationAddress());
                        // Verarbeiten des IP-Pakets ...

                        TransportPacket ipv4PacketTransportLayerPacket = ipv4Packet.getTransportLayerPacket();
                        if (ipv4PacketTransportLayerPacket instanceof TCPPacket tcpPacket) {
                            System.out.println("Transport Protocol: TCP");
                            if(tcpPacket.getPayload() != null){
                                String streamKey = TCPStreamBuilder.getStreamKey(ipv4Packet.getSourceAddress(), tcpPacket.getSourcePort(), ipv4Packet.getDestinationAddress(), tcpPacket.getDestinationPort());
                                TCPStreamBuilder streamBuilder = tcpStreamBuilders.computeIfAbsent(streamKey, k -> new TCPStreamBuilder());
                                //System.out.println(new String(tcpPacket.getPayload()));
                                streamBuilder.collectPackets(tcpPacket);
                            }else {
                                System.out.println("TCP Packet has no payload");
                            }

                        } else if (ipv4PacketTransportLayerPacket instanceof UDPPacket udpPacket) {
                            System.out.println("Transport Protocol: UDP");
                            System.out.println(getBytesAsHex(udpPacket.getPayload()));
                        }


                        break;
                    case EthernetPacket.TYPE_IPV6:
                        IPv6Packet ipv6Packet = (IPv6Packet) ethernetPacket.getIPPacket();
                        System.out.println("Destination IPv6: " + ipv6Packet.getDestinationAddress());
                        break;
                }
            }else {
                System.out.println("Got packet of currently unsupported type");
            }
            System.out.println("--------------------------");
        }


        for (TCPStreamBuilder tcpStreamBuilder : tcpStreamBuilders.values()){
            //File end reached, complete all streams
            System.out.println("-- NEW STREAM ------");
            ByteBuffer completeStream = tcpStreamBuilder.compileStream();
            String streamContent = new String(completeStream.array(), StandardCharsets.UTF_8);
            System.out.println(streamContent);
        }

        System.out.println("Finish!");


    }

    static String getBytesAsHex(byte[] bytes) {
        if(bytes == null){
            return null;
        }
        StringBuilder bobTheBuilder = new StringBuilder();
        for (byte b : bytes) {
            String st = String.format("%02X", b);
            bobTheBuilder.append(st);
        }
        return bobTheBuilder.toString();
    }
}