package org.censorship.service;

import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.NifSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TCPCensorshipDetectorService {

    private final Logger log = LoggerFactory.getLogger(TCPCensorshipDetectorService.class);

    public List<PcapNetworkInterface> detectNetworkDevices() throws Exception, PcapNativeException, NotOpenException {
        List<PcapNetworkInterface> allDevices = null;
        allDevices = Pcaps.findAllDevs();
        return allDevices;
    }

    public void sniffPackets() throws Exception{
        int snapShotLength = 65536;
        int readTimeout = 50;
        final PcapHandle handle;
        PcapNetworkInterface device = detectNetworkDevices().get(0);
        handle = device.openLive(snapShotLength, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, readTimeout);
        PacketListener listener = new PacketListener() {
            @Override
            public void gotPacket(Packet packet) {
                log.info(handle.getTimestamp().toString());
                log.info(packet.toString());
            }
        };
        int maxPackets = 50;
        handle.loop(maxPackets, listener);
        handle.close();
    }

}
