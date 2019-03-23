package org.censorship.service;

import org.censorship.CensorshipDetectorApp;
import org.censorship.domain.PacketInformation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pcap4j.core.PcapNetworkInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CensorshipDetectorApp.class)
@Transactional
public class TCPCensorshipDetectorServiceTest {
    @Autowired
    private TCPCensorshipDetectorService tcpCensorshipDetectorService;

    @Test
    public void init(){

    }

    @Test
    public void networkDeviceTest() throws Exception{
        List<PcapNetworkInterface> networkInterface = tcpCensorshipDetectorService.detectNetworkDevices();

    }

    @Test
    public void sniffPacketsTest() throws Exception{
        tcpCensorshipDetectorService.sniffPackets();
    }

    @Test
    public void fetchPacketInformationOfAWebAddress() throws Exception{
        tcpCensorshipDetectorService.sniffPackets();
        List<PacketInformation> packetInformationList = tcpCensorshipDetectorService.fetchPacketInformationOfAWebAddress("bdpolitico.com");
        packetInformationList.forEach(p->{
            System.out.println(p.toString());
        });
    }


    @Test
    public void sendArpRequestTest() throws Exception{
        tcpCensorshipDetectorService.sendArpRequest("192.168.1.103", "54.251.166.58");
    }
}
