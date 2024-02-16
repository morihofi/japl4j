package de.morihofi.japl4j.packet.ieee802dot3ethernet.layer4;

import de.morihofi.japl4j.packet.ieee802dot3ethernet.layer2.EthernetPacket;
import de.morihofi.japl4j.packet.ieee802dot3ethernet.layer3.NetworkPacket;

public abstract class TransportPacket {
    private NetworkPacket networkPacket;

    public TransportPacket(NetworkPacket networkPacket) {
        this.networkPacket = networkPacket;
    }

    public NetworkPacket getNetworkPacket() {
        return networkPacket;
    }
}
