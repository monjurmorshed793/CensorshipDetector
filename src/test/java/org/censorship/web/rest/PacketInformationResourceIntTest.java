package org.censorship.web.rest;

import org.censorship.CensorshipDetectorApp;

import org.censorship.domain.PacketInformation;
import org.censorship.repository.PacketInformationRepository;
import org.censorship.repository.search.PacketInformationSearchRepository;
import org.censorship.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;


import static org.censorship.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the PacketInformationResource REST controller.
 *
 * @see PacketInformationResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CensorshipDetectorApp.class)
public class PacketInformationResourceIntTest {

    private static final String DEFAULT_SOURCE_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_SOURCE_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_DESTINATION_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_DESTINATION_ADDRESS = "BBBBBBBBBB";

    private static final Integer DEFAULT_WINDOW = 1;
    private static final Integer UPDATED_WINDOW = 2;

    private static final Integer DEFAULT_IDENTIFICATION_NUMBER = 1;
    private static final Integer UPDATED_IDENTIFICATION_NUMBER = 2;

    private static final Integer DEFAULT_SEQUENCE_NUMBER = 1;
    private static final Integer UPDATED_SEQUENCE_NUMBER = 2;

    private static final Integer DEFAULT_SOURCE_PORT = 1;
    private static final Integer UPDATED_SOURCE_PORT = 2;

    private static final Integer DEFAULT_DESTINATION_PORT = 1;
    private static final Integer UPDATED_DESTINATION_PORT = 2;

    private static final Integer DEFAULT_ACKNOWLEDGE_NUMBER = 1;
    private static final Integer UPDATED_ACKNOWLEDGE_NUMBER = 2;

    private static final Integer DEFAULT_TTL = 1;
    private static final Integer UPDATED_TTL = 2;

    private static final String DEFAULT_SYN = "AAAAAAAAAA";
    private static final String UPDATED_SYN = "BBBBBBBBBB";

    private static final String DEFAULT_FIN = "AAAAAAAAAA";
    private static final String UPDATED_FIN = "BBBBBBBBBB";

    private static final String DEFAULT_ACK = "AAAAAAAAAA";
    private static final String UPDATED_ACK = "BBBBBBBBBB";

    private static final Integer DEFAULT_PROTOCOL = 1;
    private static final Integer UPDATED_PROTOCOL = 2;

    private static final Instant DEFAULT_LAST_MODIFIED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private PacketInformationRepository packetInformationRepository;

    /**
     * This repository is mocked in the org.censorship.repository.search test package.
     *
     * @see org.censorship.repository.search.PacketInformationSearchRepositoryMockConfiguration
     */
    @Autowired
    private PacketInformationSearchRepository mockPacketInformationSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restPacketInformationMockMvc;

    private PacketInformation packetInformation;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PacketInformationResource packetInformationResource = new PacketInformationResource(packetInformationRepository, mockPacketInformationSearchRepository);
        this.restPacketInformationMockMvc = MockMvcBuilders.standaloneSetup(packetInformationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PacketInformation createEntity(EntityManager em) {
        PacketInformation packetInformation = new PacketInformation()
            .sourceAddress(DEFAULT_SOURCE_ADDRESS)
            .destinationAddress(DEFAULT_DESTINATION_ADDRESS)
            .window(DEFAULT_WINDOW)
            .identificationNumber(DEFAULT_IDENTIFICATION_NUMBER)
            .sequenceNumber(DEFAULT_SEQUENCE_NUMBER)
            .sourcePort(DEFAULT_SOURCE_PORT)
            .destinationPort(DEFAULT_DESTINATION_PORT)
            .acknowledgeNumber(DEFAULT_ACKNOWLEDGE_NUMBER)
            .ttl(DEFAULT_TTL)
            .syn(DEFAULT_SYN)
            .fin(DEFAULT_FIN)
            .ack(DEFAULT_ACK)
            .protocol(DEFAULT_PROTOCOL)
            .lastModified(DEFAULT_LAST_MODIFIED);
        return packetInformation;
    }

    @Before
    public void initTest() {
        packetInformation = createEntity(em);
    }

    @Test
    @Transactional
    public void createPacketInformation() throws Exception {
        int databaseSizeBeforeCreate = packetInformationRepository.findAll().size();

        // Create the PacketInformation
        restPacketInformationMockMvc.perform(post("/api/packet-informations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(packetInformation)))
            .andExpect(status().isCreated());

        // Validate the PacketInformation in the database
        List<PacketInformation> packetInformationList = packetInformationRepository.findAll();
        assertThat(packetInformationList).hasSize(databaseSizeBeforeCreate + 1);
        PacketInformation testPacketInformation = packetInformationList.get(packetInformationList.size() - 1);
        assertThat(testPacketInformation.getSourceAddress()).isEqualTo(DEFAULT_SOURCE_ADDRESS);
        assertThat(testPacketInformation.getDestinationAddress()).isEqualTo(DEFAULT_DESTINATION_ADDRESS);
        assertThat(testPacketInformation.getWindow()).isEqualTo(DEFAULT_WINDOW);
        assertThat(testPacketInformation.getIdentificationNumber()).isEqualTo(DEFAULT_IDENTIFICATION_NUMBER);
        assertThat(testPacketInformation.getSequenceNumber()).isEqualTo(DEFAULT_SEQUENCE_NUMBER);
        assertThat(testPacketInformation.getSourcePort()).isEqualTo(DEFAULT_SOURCE_PORT);
        assertThat(testPacketInformation.getDestinationPort()).isEqualTo(DEFAULT_DESTINATION_PORT);
        assertThat(testPacketInformation.getAcknowledgeNumber()).isEqualTo(DEFAULT_ACKNOWLEDGE_NUMBER);
        assertThat(testPacketInformation.getTtl()).isEqualTo(DEFAULT_TTL);
        assertThat(testPacketInformation.getSyn()).isEqualTo(DEFAULT_SYN);
        assertThat(testPacketInformation.getFin()).isEqualTo(DEFAULT_FIN);
        assertThat(testPacketInformation.getAck()).isEqualTo(DEFAULT_ACK);
        assertThat(testPacketInformation.getProtocol()).isEqualTo(DEFAULT_PROTOCOL);
        assertThat(testPacketInformation.getLastModified()).isEqualTo(DEFAULT_LAST_MODIFIED);

        // Validate the PacketInformation in Elasticsearch
        verify(mockPacketInformationSearchRepository, times(1)).save(testPacketInformation);
    }

    @Test
    @Transactional
    public void createPacketInformationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = packetInformationRepository.findAll().size();

        // Create the PacketInformation with an existing ID
        packetInformation.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPacketInformationMockMvc.perform(post("/api/packet-informations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(packetInformation)))
            .andExpect(status().isBadRequest());

        // Validate the PacketInformation in the database
        List<PacketInformation> packetInformationList = packetInformationRepository.findAll();
        assertThat(packetInformationList).hasSize(databaseSizeBeforeCreate);

        // Validate the PacketInformation in Elasticsearch
        verify(mockPacketInformationSearchRepository, times(0)).save(packetInformation);
    }

    @Test
    @Transactional
    public void getAllPacketInformations() throws Exception {
        // Initialize the database
        packetInformationRepository.saveAndFlush(packetInformation);

        // Get all the packetInformationList
        restPacketInformationMockMvc.perform(get("/api/packet-informations?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(packetInformation.getId().intValue())))
            .andExpect(jsonPath("$.[*].sourceAddress").value(hasItem(DEFAULT_SOURCE_ADDRESS.toString())))
            .andExpect(jsonPath("$.[*].destinationAddress").value(hasItem(DEFAULT_DESTINATION_ADDRESS.toString())))
            .andExpect(jsonPath("$.[*].window").value(hasItem(DEFAULT_WINDOW)))
            .andExpect(jsonPath("$.[*].identificationNumber").value(hasItem(DEFAULT_IDENTIFICATION_NUMBER)))
            .andExpect(jsonPath("$.[*].sequenceNumber").value(hasItem(DEFAULT_SEQUENCE_NUMBER)))
            .andExpect(jsonPath("$.[*].sourcePort").value(hasItem(DEFAULT_SOURCE_PORT)))
            .andExpect(jsonPath("$.[*].destinationPort").value(hasItem(DEFAULT_DESTINATION_PORT)))
            .andExpect(jsonPath("$.[*].acknowledgeNumber").value(hasItem(DEFAULT_ACKNOWLEDGE_NUMBER)))
            .andExpect(jsonPath("$.[*].ttl").value(hasItem(DEFAULT_TTL)))
            .andExpect(jsonPath("$.[*].syn").value(hasItem(DEFAULT_SYN.toString())))
            .andExpect(jsonPath("$.[*].fin").value(hasItem(DEFAULT_FIN.toString())))
            .andExpect(jsonPath("$.[*].ack").value(hasItem(DEFAULT_ACK.toString())))
            .andExpect(jsonPath("$.[*].protocol").value(hasItem(DEFAULT_PROTOCOL)))
            .andExpect(jsonPath("$.[*].lastModified").value(hasItem(DEFAULT_LAST_MODIFIED.toString())));
    }
    
    @Test
    @Transactional
    public void getPacketInformation() throws Exception {
        // Initialize the database
        packetInformationRepository.saveAndFlush(packetInformation);

        // Get the packetInformation
        restPacketInformationMockMvc.perform(get("/api/packet-informations/{id}", packetInformation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(packetInformation.getId().intValue()))
            .andExpect(jsonPath("$.sourceAddress").value(DEFAULT_SOURCE_ADDRESS.toString()))
            .andExpect(jsonPath("$.destinationAddress").value(DEFAULT_DESTINATION_ADDRESS.toString()))
            .andExpect(jsonPath("$.window").value(DEFAULT_WINDOW))
            .andExpect(jsonPath("$.identificationNumber").value(DEFAULT_IDENTIFICATION_NUMBER))
            .andExpect(jsonPath("$.sequenceNumber").value(DEFAULT_SEQUENCE_NUMBER))
            .andExpect(jsonPath("$.sourcePort").value(DEFAULT_SOURCE_PORT))
            .andExpect(jsonPath("$.destinationPort").value(DEFAULT_DESTINATION_PORT))
            .andExpect(jsonPath("$.acknowledgeNumber").value(DEFAULT_ACKNOWLEDGE_NUMBER))
            .andExpect(jsonPath("$.ttl").value(DEFAULT_TTL))
            .andExpect(jsonPath("$.syn").value(DEFAULT_SYN.toString()))
            .andExpect(jsonPath("$.fin").value(DEFAULT_FIN.toString()))
            .andExpect(jsonPath("$.ack").value(DEFAULT_ACK.toString()))
            .andExpect(jsonPath("$.protocol").value(DEFAULT_PROTOCOL))
            .andExpect(jsonPath("$.lastModified").value(DEFAULT_LAST_MODIFIED.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPacketInformation() throws Exception {
        // Get the packetInformation
        restPacketInformationMockMvc.perform(get("/api/packet-informations/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePacketInformation() throws Exception {
        // Initialize the database
        packetInformationRepository.saveAndFlush(packetInformation);

        int databaseSizeBeforeUpdate = packetInformationRepository.findAll().size();

        // Update the packetInformation
        PacketInformation updatedPacketInformation = packetInformationRepository.findById(packetInformation.getId()).get();
        // Disconnect from session so that the updates on updatedPacketInformation are not directly saved in db
        em.detach(updatedPacketInformation);
        updatedPacketInformation
            .sourceAddress(UPDATED_SOURCE_ADDRESS)
            .destinationAddress(UPDATED_DESTINATION_ADDRESS)
            .window(UPDATED_WINDOW)
            .identificationNumber(UPDATED_IDENTIFICATION_NUMBER)
            .sequenceNumber(UPDATED_SEQUENCE_NUMBER)
            .sourcePort(UPDATED_SOURCE_PORT)
            .destinationPort(UPDATED_DESTINATION_PORT)
            .acknowledgeNumber(UPDATED_ACKNOWLEDGE_NUMBER)
            .ttl(UPDATED_TTL)
            .syn(UPDATED_SYN)
            .fin(UPDATED_FIN)
            .ack(UPDATED_ACK)
            .protocol(UPDATED_PROTOCOL)
            .lastModified(UPDATED_LAST_MODIFIED);

        restPacketInformationMockMvc.perform(put("/api/packet-informations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPacketInformation)))
            .andExpect(status().isOk());

        // Validate the PacketInformation in the database
        List<PacketInformation> packetInformationList = packetInformationRepository.findAll();
        assertThat(packetInformationList).hasSize(databaseSizeBeforeUpdate);
        PacketInformation testPacketInformation = packetInformationList.get(packetInformationList.size() - 1);
        assertThat(testPacketInformation.getSourceAddress()).isEqualTo(UPDATED_SOURCE_ADDRESS);
        assertThat(testPacketInformation.getDestinationAddress()).isEqualTo(UPDATED_DESTINATION_ADDRESS);
        assertThat(testPacketInformation.getWindow()).isEqualTo(UPDATED_WINDOW);
        assertThat(testPacketInformation.getIdentificationNumber()).isEqualTo(UPDATED_IDENTIFICATION_NUMBER);
        assertThat(testPacketInformation.getSequenceNumber()).isEqualTo(UPDATED_SEQUENCE_NUMBER);
        assertThat(testPacketInformation.getSourcePort()).isEqualTo(UPDATED_SOURCE_PORT);
        assertThat(testPacketInformation.getDestinationPort()).isEqualTo(UPDATED_DESTINATION_PORT);
        assertThat(testPacketInformation.getAcknowledgeNumber()).isEqualTo(UPDATED_ACKNOWLEDGE_NUMBER);
        assertThat(testPacketInformation.getTtl()).isEqualTo(UPDATED_TTL);
        assertThat(testPacketInformation.getSyn()).isEqualTo(UPDATED_SYN);
        assertThat(testPacketInformation.getFin()).isEqualTo(UPDATED_FIN);
        assertThat(testPacketInformation.getAck()).isEqualTo(UPDATED_ACK);
        assertThat(testPacketInformation.getProtocol()).isEqualTo(UPDATED_PROTOCOL);
        assertThat(testPacketInformation.getLastModified()).isEqualTo(UPDATED_LAST_MODIFIED);

        // Validate the PacketInformation in Elasticsearch
        verify(mockPacketInformationSearchRepository, times(1)).save(testPacketInformation);
    }

    @Test
    @Transactional
    public void updateNonExistingPacketInformation() throws Exception {
        int databaseSizeBeforeUpdate = packetInformationRepository.findAll().size();

        // Create the PacketInformation

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPacketInformationMockMvc.perform(put("/api/packet-informations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(packetInformation)))
            .andExpect(status().isBadRequest());

        // Validate the PacketInformation in the database
        List<PacketInformation> packetInformationList = packetInformationRepository.findAll();
        assertThat(packetInformationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PacketInformation in Elasticsearch
        verify(mockPacketInformationSearchRepository, times(0)).save(packetInformation);
    }

    @Test
    @Transactional
    public void deletePacketInformation() throws Exception {
        // Initialize the database
        packetInformationRepository.saveAndFlush(packetInformation);

        int databaseSizeBeforeDelete = packetInformationRepository.findAll().size();

        // Delete the packetInformation
        restPacketInformationMockMvc.perform(delete("/api/packet-informations/{id}", packetInformation.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<PacketInformation> packetInformationList = packetInformationRepository.findAll();
        assertThat(packetInformationList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the PacketInformation in Elasticsearch
        verify(mockPacketInformationSearchRepository, times(1)).deleteById(packetInformation.getId());
    }

    @Test
    @Transactional
    public void searchPacketInformation() throws Exception {
        // Initialize the database
        packetInformationRepository.saveAndFlush(packetInformation);
        when(mockPacketInformationSearchRepository.search(queryStringQuery("id:" + packetInformation.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(packetInformation), PageRequest.of(0, 1), 1));
        // Search the packetInformation
        restPacketInformationMockMvc.perform(get("/api/_search/packet-informations?query=id:" + packetInformation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(packetInformation.getId().intValue())))
            .andExpect(jsonPath("$.[*].sourceAddress").value(hasItem(DEFAULT_SOURCE_ADDRESS)))
            .andExpect(jsonPath("$.[*].destinationAddress").value(hasItem(DEFAULT_DESTINATION_ADDRESS)))
            .andExpect(jsonPath("$.[*].window").value(hasItem(DEFAULT_WINDOW)))
            .andExpect(jsonPath("$.[*].identificationNumber").value(hasItem(DEFAULT_IDENTIFICATION_NUMBER)))
            .andExpect(jsonPath("$.[*].sequenceNumber").value(hasItem(DEFAULT_SEQUENCE_NUMBER)))
            .andExpect(jsonPath("$.[*].sourcePort").value(hasItem(DEFAULT_SOURCE_PORT)))
            .andExpect(jsonPath("$.[*].destinationPort").value(hasItem(DEFAULT_DESTINATION_PORT)))
            .andExpect(jsonPath("$.[*].acknowledgeNumber").value(hasItem(DEFAULT_ACKNOWLEDGE_NUMBER)))
            .andExpect(jsonPath("$.[*].ttl").value(hasItem(DEFAULT_TTL)))
            .andExpect(jsonPath("$.[*].syn").value(hasItem(DEFAULT_SYN)))
            .andExpect(jsonPath("$.[*].fin").value(hasItem(DEFAULT_FIN)))
            .andExpect(jsonPath("$.[*].ack").value(hasItem(DEFAULT_ACK)))
            .andExpect(jsonPath("$.[*].protocol").value(hasItem(DEFAULT_PROTOCOL)))
            .andExpect(jsonPath("$.[*].lastModified").value(hasItem(DEFAULT_LAST_MODIFIED.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PacketInformation.class);
        PacketInformation packetInformation1 = new PacketInformation();
        packetInformation1.setId(1L);
        PacketInformation packetInformation2 = new PacketInformation();
        packetInformation2.setId(packetInformation1.getId());
        assertThat(packetInformation1).isEqualTo(packetInformation2);
        packetInformation2.setId(2L);
        assertThat(packetInformation1).isNotEqualTo(packetInformation2);
        packetInformation1.setId(null);
        assertThat(packetInformation1).isNotEqualTo(packetInformation2);
    }
}
