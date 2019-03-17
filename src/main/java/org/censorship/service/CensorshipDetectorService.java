package org.censorship.service;

import org.censorship.domain.Isp;
import org.censorship.domain.WebAddress;
import org.censorship.repository.IspRepository;
import org.censorship.repository.WebAddressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CensorshipDetectorService {

    private final Logger log = LoggerFactory.getLogger(CensorshipDetectorService.class);

    private WebAddressRepository webAddressRepository;
    private IspRepository ispRepository;

    public CensorshipDetectorService(WebAddressRepository webAddressRepository, IspRepository ispRepository) {
        this.webAddressRepository = webAddressRepository;
        this.ispRepository = ispRepository;
    }

    public void DetectCensorship(Isp isp){
        List<WebAddress> webAddressList = webAddressRepository.findAll();



    }
}
