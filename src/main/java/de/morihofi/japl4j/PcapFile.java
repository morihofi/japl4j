package de.morihofi.japl4j;

import de.morihofi.japl4j.packet.ieee802dot3ethernet.layer2.EthernetPacket;
import de.morihofi.japl4j.packet.PcapPacket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class PcapFile implements Iterable<PcapPacket> {
    private final int magicNumber;
    private final String fileVersion;
    private final int snapLen;
    private final PCAP_LINKTYPE linkType;
    private final ByteOrder byteOrder;
    private final Path filePath;

    public int getMagicNumber() {
        return magicNumber;
    }

    public String getFileVersion() {
        return fileVersion;
    }

    public int getSnapLen() {
        return snapLen;
    }

    public PCAP_LINKTYPE getLinkType() {
        return linkType;
    }

    public Path getFilePath() {
        return filePath;
    }

    private PcapFile(int magicNumber, String fileVersion, int snapLen, PCAP_LINKTYPE linkType, Path filePath, ByteOrder byteOrder) {
        this.magicNumber = magicNumber;
        this.fileVersion = fileVersion;
        this.snapLen = snapLen;
        this.linkType = linkType;
        this.filePath = filePath;
        this.byteOrder = byteOrder;
    }


    public static PcapFile openPcapFile(Path pcapFile) throws IOException {

        try (SeekableByteChannel fileChannel = Files.newByteChannel(pcapFile, StandardOpenOption.READ);) {

            // Read File Header (24 Bytes)
            ByteBuffer headerBuffer = ByteBuffer.allocate(24);
            fileChannel.read(headerBuffer);
            headerBuffer.flip();

            // Check byte order based on magic number
            int magicNumber = headerBuffer.getInt();
            if (magicNumber == 0xA1B2C3D4) {
                headerBuffer.order(ByteOrder.BIG_ENDIAN);
            } else if (magicNumber == 0xD4C3B2A1) {
                headerBuffer.order(ByteOrder.LITTLE_ENDIAN);
            } else {
                throw new IllegalArgumentException("Invalid Magic Number: " + Integer.toHexString(magicNumber));
            }

            // Extract further header information
            int majorVersion = headerBuffer.getShort();
            int minorVersion = headerBuffer.getShort();
            // Skip Reserved1, Reserved2
            headerBuffer.getInt();
            headerBuffer.getInt();
            int snapLen = headerBuffer.getInt();
            int linkType = headerBuffer.getShort();

            return new PcapFile(
                    magicNumber,
                    (majorVersion + "." + minorVersion),
                    snapLen,
                    PCAP_LINKTYPE.findByValue(linkType),
                    pcapFile,
                    headerBuffer.order()
            );
        }
    }


    @Override
    public Iterator<PcapPacket> iterator() {
        try {
            SeekableByteChannel channel = Files.newByteChannel(filePath, StandardOpenOption.READ);
            // Skip the file header, as this was already read when it was opened.
            channel.position(24);
            ByteBuffer buffer = ByteBuffer.allocate(16); // Größe des Packet Headers
            buffer.order(byteOrder);

            return new Iterator<>() {
                private SeekableByteChannel fileChannel = channel;
                private ByteBuffer packetHeaderBuffer = buffer;
                private PcapPacket nextPacket = null;

                @Override
                public boolean hasNext() {
                    if (nextPacket != null) {
                        return true;
                    }

                    try {
                        nextPacket = readNextPacket();
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                    return nextPacket != null;
                }

                @Override
                public PcapPacket next() {
                    if (nextPacket != null || hasNext()) {
                        PcapPacket packet = nextPacket;
                        nextPacket = null;
                        return packet;
                    } else {
                        throw new NoSuchElementException("No more packets.");
                    }
                }

                private PcapPacket readNextPacket() throws IOException {
                    packetHeaderBuffer.clear();
                    if (fileChannel.read(packetHeaderBuffer) < 16) {
                        // Not enough data for another header; probably end of file
                        fileChannel.close();
                        return null;
                    }
                    packetHeaderBuffer.flip();

                    long timestampSeconds = packetHeaderBuffer.getInt() & 0xFFFFFFFFL;
                    long timestampMicroOrNano = packetHeaderBuffer.getInt() & 0xFFFFFFFFL;
                    int capturedPacketLength = packetHeaderBuffer.getInt();
                    int originalPacketLength = packetHeaderBuffer.getInt();

                    ByteBuffer packetDataBuffer = ByteBuffer.allocate(capturedPacketLength);
                    fileChannel.read(packetDataBuffer);
                    packetDataBuffer.flip();

                    byte[] data = new byte[capturedPacketLength];
                    packetDataBuffer.get(data);

                    switch (linkType) {
                        case LINKTYPE_ETHERNET:
                            return new EthernetPacket(timestampSeconds, timestampMicroOrNano, capturedPacketLength, originalPacketLength, data);
                        default:
                            // Return raw packet, cause no parser is implemented
                            return new PcapPacket(timestampSeconds, timestampMicroOrNano, capturedPacketLength, originalPacketLength, data);
                    }

                }
            };
        } catch (IOException e) {
            throw new RuntimeException("Failed to create iterator for PcapFile", e);
        }
    }
}
