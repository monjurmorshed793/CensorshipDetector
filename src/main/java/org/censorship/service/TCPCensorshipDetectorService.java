package org.censorship.service;

import org.pcap4j.core.*;
import org.pcap4j.packet.ArpPacket;
import org.pcap4j.packet.IpV4Packet;
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
        handle.setFilter("tcp", BpfProgram.BpfCompileMode.OPTIMIZE);
        PacketListener listener = (p)->{
                log.info(handle.getTimestamp().toString());
                log.info(p.toString());
            };
        int maxPackets = 50;
        handle.loop(maxPackets, listener);
        PcapStat ps = handle.getStats();

        log.info(("################## status ##########################"));
        System.out.println("ps_recv: " + ps.getNumPacketsReceived());
        System.out.println("ps_drop: " + ps.getNumPacketsDropped());
        System.out.println("ps_ifdrop: " + ps.getNumPacketsDroppedByIf());
        log.info(("################## End Status ######################"));


        Packet packet;
        while(handle.getNextPacket()!=null){
            log.info("************Next Packet******************************");
            packet = handle.getNextPacketEx();
            if(packet.contains(ArpPacket.class)){
                ArpPacket arpPacket = packet.get(ArpPacket.class);
                log.info(packet.toString());
            }
            if (packet.contains(IpV4Packet.class)) {
                IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);

                log.info("IPV4 packets");
                log.info(ipV4Packet.toString());

            }
            //log.info(packet.toString());
        }
        handle.close();
    }

}
