package org.censorship.service;

import org.censorship.domain.PacketInformation;
import org.censorship.repository.PacketInformationRepository;
import org.pcap4j.core.*;
import org.pcap4j.packet.ArpPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.util.NifSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class TCPCensorshipDetectorService {

    private final Logger log = LoggerFactory.getLogger(TCPCensorshipDetectorService.class);

    private PacketInformationRepository packetInformationRepository;

    public TCPCensorshipDetectorService(PacketInformationRepository packetInformationRepository) {
        this.packetInformationRepository = packetInformationRepository;
    }

    public List<PcapNetworkInterface> detectNetworkDevices() throws Exception, PcapNativeException, NotOpenException {
        List<PcapNetworkInterface> allDevices = null;
        allDevices = Pcaps.findAllDevs();
        return allDevices;
    }

    @Async
    public void sniffPackets() throws Exception{
        int snapShotLength = 65536;
        int readTimeout = 50;
        final PcapHandle handle;
        PcapNetworkInterface device = detectNetworkDevices().get(0);
        handle = device.openLive(snapShotLength, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, readTimeout);
        handle.setFilter("tcp", BpfProgram.BpfCompileMode.OPTIMIZE);
        PacketListener listener = (p)->{
               /* log.info(handle.getTimestamp().toString());
                log.info(p.toString());*/
            };
        int maxPackets = 50;
        handle.loop(maxPackets, listener);
        PcapStat ps = handle.getStats();

        /*log.info(("################## status ##########################"));
        System.out.println("ps_recv: " + ps.getNumPacketsReceived());
        System.out.println("ps_drop: " + ps.getNumPacketsDropped());
        System.out.println("ps_ifdrop: " + ps.getNumPacketsDroppedByIf());
        log.info(("################## End Status ######################"));*/


        Packet packet;
        while(handle.getNextPacket()!=null){
            packet = handle.getNextPacketEx();
            if(packet.contains(ArpPacket.class)){
                ArpPacket arpPacket = packet.get(ArpPacket.class);
                log.info(packet.toString());
            }
            /*if (packet.contains(IpV4Packet.class)) {
                IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);

                log.info("IPV4 packets");
//                log.info(ipV4Packet.toString());
                log.info(ipV4Packet.getHeader().getTos().toString());
                log.info(ipV4Packet.getHeader().getIdentification()+"");
                log.info(ipV4Packet.getHeader().getIdentificationAsInt()+"");
                log.info(ipV4Packet.getHeader().getIhl()+"");
                log.info(ipV4Packet.getHeader().getIhlAsInt()+"");

                log.info(ipV4Packet.getPayload().getBuilder().toString());

            }*/
            if(packet.contains(TcpPacket.class) && packet.contains(IpV4Packet.class)){
                TcpPacket tcpPacket = packet.get(TcpPacket.class);
                IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);
                insertIntoPacketInformation(tcpPacket, ipV4Packet);
//                log.info(tcpPacket.getHeader().);
            }
            //log.info(packet.toString());
        }
        handle.close();
    }

    void insertIntoPacketInformation(TcpPacket tcpPacket, IpV4Packet ipV4Packet){
        PacketInformation packetInformation = new PacketInformation();
        packetInformation.setSourceAddress(ipV4Packet.getHeader().getSrcAddr().toString());
        packetInformation.setDestinationAddress(ipV4Packet.getHeader().getDstAddr().toString());
        packetInformation.setWindow(tcpPacket.getHeader().getWindowAsInt());
        packetInformation.setIdentificationNumber(ipV4Packet.getHeader().getIdentificationAsInt());
        packetInformation.setSequenceNumber(tcpPacket.getHeader().getSequenceNumber());
        packetInformation.setSourcePort(tcpPacket.getHeader().getSrcPort().valueAsInt());
        packetInformation.setDestinationPort(tcpPacket.getHeader().getDstPort().valueAsInt());
        packetInformation.setAcknowledgeNumber(tcpPacket.getHeader().getAcknowledgmentNumber());
        packetInformation.setTtl(ipV4Packet.getHeader().getTtlAsInt());
        packetInformation.setSyn(tcpPacket.getHeader().getSyn()==true?"true":"false");
        packetInformation.setFin(tcpPacket.getHeader().getFin()==true?"true":"false");
        packetInformation.setAck(tcpPacket.getHeader().getAck()==true?"true":"false");
        packetInformation.setProtocol(ipV4Packet.getHeader().getProtocol().valueAsString());
        packetInformation.setLastModified(new Date().toInstant());
        packetInformationRepository.save(packetInformation);
    }

}
