package org.censorship.service;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CensorshipStatusService {

    private final Logger log = LoggerFactory.getLogger(CensorshipStatusService.class);

    @Autowired
    private TCPCensorshipDetectorService tcpCensorshipDetectorService;

    public Boolean prepareInitialData() throws Exception{
        return tcpCensorshipDetectorService.sniffStoredWebAddress();
    }

}
