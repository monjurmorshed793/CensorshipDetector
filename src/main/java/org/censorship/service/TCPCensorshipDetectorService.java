package org.censorship.service;

import org.censorship.domain.PacketInformation;
import org.censorship.domain.WebAddress;
import org.censorship.repository.PacketInformationRepository;
import org.censorship.repository.WebAddressRepository;
import org.checkerframework.checker.units.qual.A;
import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import org.pcap4j.packet.namednumber.*;
import org.pcap4j.util.ByteArrays;
import org.pcap4j.util.IpV4Helper;
import org.pcap4j.util.MacAddress;
import org.pcap4j.util.NifSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class TCPCensorshipDetectorService {

    private final Logger log = LoggerFactory.getLogger(TCPCensorshipDetectorService.class);

    private PacketInformationRepository packetInformationRepository;
    private WebAddressRepository webAddressRepository;
    private DNSCensorshipDetectorService dnsCensorshipDetectorService;



    private static final String TU_KEY = TCPCensorshipDetectorService.class.getName() + ".tu";
    private static final int TU = Integer.getInteger(TU_KEY, 4000); // [bytes]

    private static final String MTU_KEY = TCPCensorshipDetectorService.class.getName() + ".mtu";
    private static final int MTU = Integer.getInteger(MTU_KEY, 1403); // [bytes]


    private static final String COUNT_KEY = TCPCensorshipDetectorService.class.getName() + ".count";
    private static final int COUNT = 20000;

    private static final String READ_TIMEOUT_KEY = TCPCensorshipDetectorService.class.getName() + ".readTimeout";
    private static final int READ_TIMEOUT = Integer.getInteger(READ_TIMEOUT_KEY, 10); // [ms]

    private static final String SNAPLEN_KEY = TCPCensorshipDetectorService.class.getName() + ".snaplen";
    private static final int SNAPLEN = Integer.getInteger(SNAPLEN_KEY, 65536); // [bytes]

    private static MacAddress resolvedAddr;


    public TCPCensorshipDetectorService(PacketInformationRepository packetInformationRepository, WebAddressRepository webAddressRepository, DNSCensorshipDetectorService dnsCensorshipDetectorService) {
        this.packetInformationRepository = packetInformationRepository;
        this.webAddressRepository = webAddressRepository;
        this.dnsCensorshipDetectorService = dnsCensorshipDetectorService;
    }

    public List<PcapNetworkInterface> detectNetworkDevices() throws Exception, PcapNativeException, NotOpenException {
        List<PcapNetworkInterface> allDevices = null;
        allDevices = Pcaps.findAllDevs();
        return allDevices;
    }


    public boolean sniffStoredWebAddress() throws Exception{
        PcapNetworkInterface networkInterface = detectNetworkDevices().get(0);
        NetworkDevice networkDevice = new NetworkDevice(networkInterface.getLinkLayerAddresses().get(0).toString(), networkInterface.getAddresses());
        PcapHandle handle = networkInterface.openLive(SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
        ExecutorService pool = Executors.newSingleThreadExecutor();

        PacketListener listener = packet -> {
          if(packet.contains(TcpPacket.class) || packet.contains(IpV4Packet.class)){
              TcpPacket tcpPacket = packet.get(TcpPacket.class);
              IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);
              insertIntoPacketInformation(tcpPacket, ipV4Packet);
          }
        };

        Task task = new Task(handle, listener);
        pool.execute(task);

        packetInformationRepository.deleteAll();
        List<WebAddress> webAddressList = webAddressRepository.findAll();
        Boolean finishStatus = true;
        try{
            webAddressList.parallelStream().forEach(a->{
                try{
                    List<String> ipList = dnsCensorshipDetectorService.resolveIpAddresses(a.getName());
                    for(String ipAddress: ipList){
                        try{
                            int port= a.getName().contains("https")? 443: 80;
                            Socket clientSocket = new Socket(InetAddress.getByName(ipAddress), port);
                            if(clientSocket.isConnected())
                                clientSocket.close();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            finishStatus = false;
        }
        finally {
            if(handle!=null && handle.isOpen())
                handle.close();
            if(pool!=null  && !pool.isShutdown())
                pool.shutdown();
        }

        return finishStatus;
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
            if(p.contains(ArpPacket.class))
            {
                log.info("Arp packet found :D :D :D :D");
            }
               /* log.info(handle.getTimestamp().toString());
                log.info(p.toString());*/
            };
        int maxPackets = 50;
//        handle.loop(1, listener);
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
                log.info(arpPacket.toString());
            }else{
                log.info("No Arp Packet");
                IpV4Packet packets = packet.get(IpV4Packet.class);
                log.info("source --->"+packets.getHeader().getSrcAddr().toString());
                log.info("destination --->"+packets.getHeader().getDstAddr().toString());

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
            if(packet.contains(TcpPacket.class) || packet.contains(IpV4Packet.class)){
                TcpPacket tcpPacket = packet.get(TcpPacket.class);
                IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);
                if(ipV4Packet.getHeader().getDstAddr().toString().replace("/","").equals("54.251.166.58")){
                    log.info("Found info");
                    log.info(packet.toString());
                }
                //insertIntoPacketInformation(tcpPacket, ipV4Packet);
//                log.info(tcpPacket.getHeader().);
            }
            //log.info(packet.toString());
        }
        handle.close();
    }

    void insertIntoPacketInformation(TcpPacket tcpPacket, IpV4Packet ipV4Packet){
        PacketInformation packetInformation = new PacketInformation();
        packetInformation.setSourceAddress(ipV4Packet.getHeader().getSrcAddr().toString().replace("/",""));
        packetInformation.setDestinationAddress(ipV4Packet.getHeader().getDstAddr().toString().replace("/",""));
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

    public void callAddress(String pWebAddress) {
        try{
            URL url = new URL(pWebAddress);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.disconnect();
        }catch (Exception e){
            log.info("Web address--->"+pWebAddress+" is not accessible");
        }

    }

    public List<PacketInformation> fetchPacketInformationOfAWebAddress(String pWebAddress) throws Exception{
        callAddress(pWebAddress);
        TimeUnit.SECONDS.sleep(10);
        List<String> ipList = dnsCensorshipDetectorService.resolveIpAddresses(pWebAddress);
        List<PacketInformation> totalPacketInformationList = new ArrayList<>();
        for(String ipAddress: ipList){
            List<PacketInformation> packetInformationList = packetInformationRepository.findBySourceAddress(ipAddress);
            if(packetInformationList.size()>0)
                 totalPacketInformationList.addAll(packetInformationList);
        }

        totalPacketInformationList.forEach(p->{
            log.info(p.toString());
        });
        return totalPacketInformationList;
    }

    @Async
    public void testAllWebAddress() throws  Exception{
        List<WebAddress> webAddressList = webAddressRepository.findAll();
        for(WebAddress webAddress: webAddressList){
            try{
                fetchPacketInformationOfAWebAddress(webAddress.getName());
            }catch (Exception e){
                log.info("Error in checking--->"+webAddress.getName());
            }
        }
    }


    public void sendFrangmentedEcho(String destinationIpAddress) throws Exception{
        PcapNetworkInterface nif = detectNetworkDevices().get(0);
        NetworkDevice networkDevice = new NetworkDevice(nif.getLinkLayerAddresses().get(0).toString(), nif.getAddresses());
        PcapHandle handle = nif.openLive(SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
        PcapHandle sendHandle = nif.openLive(SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
        MacAddress sourceMacAddress = MacAddress.getByName(networkDevice.getHardwareAddress(),":");
        handle.setFilter(
            "icmp and ether dst " + Pcaps.toBpfString(sourceMacAddress), BpfProgram.BpfCompileMode.OPTIMIZE);

        PacketListener listener=(packet -> {
           log.info(packet.toString());
        });

        Task t = new Task(handle, listener);
        ExecutorService pool = Executors.newSingleThreadExecutor();
        pool.execute(t);


        byte[] echoData = new byte[TU - 28];
        for (int i = 0; i < echoData.length; i++) {
            echoData[i] = (byte) i;
        }
        IcmpV4EchoPacket.Builder echoBuilder = new IcmpV4EchoPacket.Builder();
        echoBuilder
            .identifier((short) 1)
            .payloadBuilder(new UnknownPacket.Builder().rawData(echoData));


        IcmpV4CommonPacket.Builder icmpV4CommonBuilder = new IcmpV4CommonPacket.Builder();
        icmpV4CommonBuilder
            .type(IcmpV4Type.ECHO)
            .code(IcmpV4Code.NO_CODE)
            .payloadBuilder(echoBuilder)
            .correctChecksumAtBuild(true);


        IpV4Packet.Builder ipV4Builder = new IpV4Packet.Builder();

        ipV4Builder
            .version(IpVersion.IPV4)
            .tos(IpV4Rfc791Tos.newInstance((byte) 0))
            .ttl((byte) 100)
            .protocol(IpNumber.ICMPV4)
            .srcAddr((Inet4Address) InetAddress.getByName(networkDevice.getIpV4Address()))
            .dstAddr((Inet4Address) InetAddress.getByName(destinationIpAddress))
            .payloadBuilder(icmpV4CommonBuilder)
            .correctChecksumAtBuild(true)
            .correctLengthAtBuild(true);

        EthernetPacket.Builder etherBuilder = new EthernetPacket.Builder();
        etherBuilder
            .dstAddr(MacAddress.ETHER_BROADCAST_ADDRESS)
            .srcAddr(sourceMacAddress)
            .type(EtherType.IPV4)
            .paddingAtBuild(true);



        for (int i = 0; i < 100; i++) {
            echoBuilder.sequenceNumber((short) i);
            ipV4Builder.identification((short) i);

            for (final Packet ipV4Packet : IpV4Helper.fragment(ipV4Builder.build(), MTU)) {
                etherBuilder.payloadBuilder(
                    new AbstractPacket.AbstractBuilder() {
                        @Override
                        public Packet build() {
                            return ipV4Packet;
                        }
                    });

                Packet p = etherBuilder.build();
                sendHandle.sendPacket(p);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }


    public void sendArpRequest(String sourceIpAddress, String destinationIpAddress) throws Exception, PcapNativeException, NotOpenException{


        PcapNetworkInterface nif = detectNetworkDevices().get(0);
        PcapHandle handle = nif.openLive(SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
        PcapHandle sendHandle = nif.openLive(SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
        ExecutorService pool = Executors.newSingleThreadExecutor();
        NetworkDevice networkDevice = new NetworkDevice(nif.getLinkLayerAddresses().get(0).toString(), nif.getAddresses());
        MacAddress sourceMacAddress = MacAddress.getByAddress( NetworkInterface.getNetworkInterfaces().nextElement().getHardwareAddress());
        try{

        handle.setFilter(
                "arp and src host "
                    + destinationIpAddress
                    + " and dst host "
                    + sourceIpAddress
                    + " and ether dst "
                    + Pcaps.toBpfString( sourceMacAddress),
                BpfProgram.BpfCompileMode.OPTIMIZE);

            PacketListener listener =
                new PacketListener() {
                    @Override
                    public void gotPacket(Packet packet) {
                        if (packet.contains(ArpPacket.class)) {
                            log.info("Found arp packet*********************************");
                            log.info(packet.toString());

                            ArpPacket arp = packet.get(ArpPacket.class);
                            if (arp.getHeader().getOperation().equals(ArpOperation.REPLY)) {
                                TCPCensorshipDetectorService.resolvedAddr = arp.getHeader().getSrcHardwareAddr();
                            }
                        }
                    }
                };

            Task t = new Task(handle, listener);
            pool.execute(t);

        ArpPacket.Builder arpBuilder = new ArpPacket.Builder();
        try {
            arpBuilder
                .hardwareType(ArpHardwareType.ETHERNET)
                .protocolType(EtherType.IPV4)
                .hardwareAddrLength((byte) MacAddress.SIZE_IN_BYTES)
                .protocolAddrLength((byte) ByteArrays.INET4_ADDRESS_SIZE_IN_BYTES)
                .operation(ArpOperation.REQUEST)
                .srcHardwareAddr(MacAddress.getByName(networkDevice.getHardwareAddress(), ":"))
                .srcProtocolAddr(InetAddress.getByName(networkDevice.getIpV4Address()))
                .dstHardwareAddr(MacAddress.ETHER_BROADCAST_ADDRESS)
                .dstProtocolAddr(InetAddress.getByName(destinationIpAddress));
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(e);
        }

        EthernetPacket.Builder etherBuilder = new EthernetPacket.Builder();
        etherBuilder
            .dstAddr(MacAddress.ETHER_BROADCAST_ADDRESS)
            .srcAddr(MacAddress.getByName( networkDevice.getHardwareAddress(),":"))
            .type(EtherType.ARP)
            .payloadBuilder(arpBuilder)
            .paddingAtBuild(true);

        for (int i = 0; i <=1000; i++) {
            Packet p = etherBuilder.build();
            System.out.println(p);
            log.info("Sending packet----->");
            sendHandle.sendPacket(p);
            Thread.sleep(1000);
        }
    } finally {
       /* if (handle != null && handle.isOpen()) {
            handle.close();
        }
        if (sendHandle != null && sendHandle.isOpen()) {
            sendHandle.close();
        }
        if (pool != null && !pool.isShutdown()) {
            pool.shutdown();
        }
*/
        System.out.println(destinationIpAddress + " was resolved to " + resolvedAddr);
    }



    }


    public void sendIpV4Request(String webAddress) {
        throw new NotImplementedException();
    }



    private static class Task implements Runnable {

        private PcapHandle handle;
        private PacketListener listener;

        public Task(PcapHandle handle, PacketListener listener) {
            this.handle = handle;
            this.listener = listener;
        }

        @Override
        public void run() {
            try {
                handle.loop(COUNT, listener);
            } catch (PcapNativeException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (NotOpenException e) {
                e.printStackTrace();
            }
        }
    }

}
