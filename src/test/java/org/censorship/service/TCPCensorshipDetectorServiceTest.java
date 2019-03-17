package org.censorship.service;

import org.censorship.CensorshipDetectorApp;
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
}
