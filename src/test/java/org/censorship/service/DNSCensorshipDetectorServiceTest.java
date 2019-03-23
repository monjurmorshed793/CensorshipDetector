package org.censorship.service;

import org.censorship.CensorshipDetectorApp;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CensorshipDetectorApp.class)
@Transactional
public class DNSCensorshipDetectorServiceTest {
    private final Logger logger = LoggerFactory.getLogger(DNSCensorshipDetectorServiceTest.class);

    @Autowired
    DNSCensorshipDetectorService dnsCensorshipDetectorService;

    @Test
    public void initialTest(){

    }

    @Test
    public void dnsTest() throws Exception{
        CensorshipResponse censorshipResponse = dnsCensorshipDetectorService.detectDNSCensorshipStatus("bdpolitico.com");
        logger.info(censorshipResponse.getMessage());
    }

    @Test
    public void refactorWebAddressTest(){
        String refactoredWebAddress = dnsCensorshipDetectorService.refactorWebAddress("http://bdnews24.com");
        Assert.assertEquals(refactoredWebAddress, "bdnews24.com");

        refactoredWebAddress = dnsCensorshipDetectorService.refactorWebAddress("https://bdnews24.com");
        Assert.assertEquals(refactoredWebAddress, "bdnews24.com");

        refactoredWebAddress = dnsCensorshipDetectorService.refactorWebAddress("bdnews24.com/address");
        Assert.assertEquals(refactoredWebAddress, "bdnews24.com");
    }

}
