package org.censorship.web.rest;

import org.censorship.CensorshipDetectorApp;

import org.censorship.domain.CensorshipStatus;
import org.censorship.domain.WebAddress;
import org.censorship.domain.Isp;
import org.censorship.repository.CensorshipStatusRepository;
import org.censorship.repository.WebAddressRepository;
import org.censorship.repository.search.CensorshipStatusSearchRepository;
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
import java.util.Collections;
import java.util.List;


import static org.censorship.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.censorship.domain.enumeration.Status;
/**
 * Test class for the CensorshipStatusResource REST controller.
 *
 * @see CensorshipStatusResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CensorshipDetectorApp.class)
public class CensorshipStatusResourceIntTest {

    private static final Status DEFAULT_STATUS = Status.CENSORED;
    private static final Status UPDATED_STATUS = Status.NOT_CENSORED;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_OONI_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_OONI_STATUS = "BBBBBBBBBB";

    @Autowired
    private CensorshipStatusRepository censorshipStatusRepository;
    @Autowired
    private WebAddressRepository webAddressRepository;

    /**
     * This repository is mocked in the org.censorship.repository.search test package.
     *
     * @see org.censorship.repository.search.CensorshipStatusSearchRepositoryMockConfiguration
     */
    @Autowired
    private CensorshipStatusSearchRepository mockCensorshipStatusSearchRepository;

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

    private MockMvc restCensorshipStatusMockMvc;

    private CensorshipStatus censorshipStatus;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CensorshipStatusResource censorshipStatusResource = new CensorshipStatusResource(censorshipStatusRepository, mockCensorshipStatusSearchRepository, webAddressRepository);
        this.restCensorshipStatusMockMvc = MockMvcBuilders.standaloneSetup(censorshipStatusResource)
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
    public static CensorshipStatus createEntity(EntityManager em) {
        CensorshipStatus censorshipStatus = new CensorshipStatus()
            .status(DEFAULT_STATUS)
            .description(DEFAULT_DESCRIPTION)
            .ooniStatus(DEFAULT_OONI_STATUS);
        // Add required entity
        WebAddress webAddress = WebAddressResourceIntTest.createEntity(em);
        em.persist(webAddress);
        em.flush();
        censorshipStatus.setWebAddress(webAddress);
        // Add required entity
        Isp isp = IspResourceIntTest.createEntity(em);
        em.persist(isp);
        em.flush();
        censorshipStatus.setIsp(isp);
        return censorshipStatus;
    }

    @Before
    public void initTest() {
        censorshipStatus = createEntity(em);
    }

    @Test
    @Transactional
    public void createCensorshipStatus() throws Exception {
        int databaseSizeBeforeCreate = censorshipStatusRepository.findAll().size();

        // Create the CensorshipStatus
        restCensorshipStatusMockMvc.perform(post("/api/censorship-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(censorshipStatus)))
            .andExpect(status().isCreated());

        // Validate the CensorshipStatus in the database
        List<CensorshipStatus> censorshipStatusList = censorshipStatusRepository.findAll();
        assertThat(censorshipStatusList).hasSize(databaseSizeBeforeCreate + 1);
        CensorshipStatus testCensorshipStatus = censorshipStatusList.get(censorshipStatusList.size() - 1);
        assertThat(testCensorshipStatus.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testCensorshipStatus.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCensorshipStatus.getOoniStatus()).isEqualTo(DEFAULT_OONI_STATUS);

        // Validate the id for MapsId, the ids must be same
        assertThat(testCensorshipStatus.getId()).isEqualTo(testCensorshipStatus.getWebAddress().getId());

        // Validate the CensorshipStatus in Elasticsearch
        verify(mockCensorshipStatusSearchRepository, times(1)).save(testCensorshipStatus);
    }

    @Test
    @Transactional
    public void createCensorshipStatusWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = censorshipStatusRepository.findAll().size();

        // Create the CensorshipStatus with an existing ID
        censorshipStatus.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCensorshipStatusMockMvc.perform(post("/api/censorship-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(censorshipStatus)))
            .andExpect(status().isBadRequest());

        // Validate the CensorshipStatus in the database
        List<CensorshipStatus> censorshipStatusList = censorshipStatusRepository.findAll();
        assertThat(censorshipStatusList).hasSize(databaseSizeBeforeCreate);

        // Validate the CensorshipStatus in Elasticsearch
        verify(mockCensorshipStatusSearchRepository, times(0)).save(censorshipStatus);
    }

    @Test
    @Transactional
    public void getAllCensorshipStatuses() throws Exception {
        // Initialize the database
        censorshipStatusRepository.saveAndFlush(censorshipStatus);

        // Get all the censorshipStatusList
        restCensorshipStatusMockMvc.perform(get("/api/censorship-statuses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(censorshipStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].ooniStatus").value(hasItem(DEFAULT_OONI_STATUS.toString())));
    }
    
    @Test
    @Transactional
    public void getCensorshipStatus() throws Exception {
        // Initialize the database
        censorshipStatusRepository.saveAndFlush(censorshipStatus);

        // Get the censorshipStatus
        restCensorshipStatusMockMvc.perform(get("/api/censorship-statuses/{id}", censorshipStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(censorshipStatus.getId().intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.ooniStatus").value(DEFAULT_OONI_STATUS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCensorshipStatus() throws Exception {
        // Get the censorshipStatus
        restCensorshipStatusMockMvc.perform(get("/api/censorship-statuses/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCensorshipStatus() throws Exception {
        // Initialize the database
        censorshipStatusRepository.saveAndFlush(censorshipStatus);

        int databaseSizeBeforeUpdate = censorshipStatusRepository.findAll().size();

        // Update the censorshipStatus
        CensorshipStatus updatedCensorshipStatus = censorshipStatusRepository.findById(censorshipStatus.getId()).get();
        // Disconnect from session so that the updates on updatedCensorshipStatus are not directly saved in db
        em.detach(updatedCensorshipStatus);
        updatedCensorshipStatus
            .status(UPDATED_STATUS)
            .description(UPDATED_DESCRIPTION)
            .ooniStatus(UPDATED_OONI_STATUS);

        restCensorshipStatusMockMvc.perform(put("/api/censorship-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCensorshipStatus)))
            .andExpect(status().isOk());

        // Validate the CensorshipStatus in the database
        List<CensorshipStatus> censorshipStatusList = censorshipStatusRepository.findAll();
        assertThat(censorshipStatusList).hasSize(databaseSizeBeforeUpdate);
        CensorshipStatus testCensorshipStatus = censorshipStatusList.get(censorshipStatusList.size() - 1);
        assertThat(testCensorshipStatus.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testCensorshipStatus.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCensorshipStatus.getOoniStatus()).isEqualTo(UPDATED_OONI_STATUS);

        // Validate the CensorshipStatus in Elasticsearch
        verify(mockCensorshipStatusSearchRepository, times(1)).save(testCensorshipStatus);
    }

    @Test
    @Transactional
    public void updateNonExistingCensorshipStatus() throws Exception {
        int databaseSizeBeforeUpdate = censorshipStatusRepository.findAll().size();

        // Create the CensorshipStatus

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCensorshipStatusMockMvc.perform(put("/api/censorship-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(censorshipStatus)))
            .andExpect(status().isBadRequest());

        // Validate the CensorshipStatus in the database
        List<CensorshipStatus> censorshipStatusList = censorshipStatusRepository.findAll();
        assertThat(censorshipStatusList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CensorshipStatus in Elasticsearch
        verify(mockCensorshipStatusSearchRepository, times(0)).save(censorshipStatus);
    }

    @Test
    @Transactional
    public void deleteCensorshipStatus() throws Exception {
        // Initialize the database
        censorshipStatusRepository.saveAndFlush(censorshipStatus);

        int databaseSizeBeforeDelete = censorshipStatusRepository.findAll().size();

        // Delete the censorshipStatus
        restCensorshipStatusMockMvc.perform(delete("/api/censorship-statuses/{id}", censorshipStatus.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<CensorshipStatus> censorshipStatusList = censorshipStatusRepository.findAll();
        assertThat(censorshipStatusList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the CensorshipStatus in Elasticsearch
        verify(mockCensorshipStatusSearchRepository, times(1)).deleteById(censorshipStatus.getId());
    }

    @Test
    @Transactional
    public void searchCensorshipStatus() throws Exception {
        // Initialize the database
        censorshipStatusRepository.saveAndFlush(censorshipStatus);
        when(mockCensorshipStatusSearchRepository.search(queryStringQuery("id:" + censorshipStatus.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(censorshipStatus), PageRequest.of(0, 1), 1));
        // Search the censorshipStatus
        restCensorshipStatusMockMvc.perform(get("/api/_search/censorship-statuses?query=id:" + censorshipStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(censorshipStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].ooniStatus").value(hasItem(DEFAULT_OONI_STATUS)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CensorshipStatus.class);
        CensorshipStatus censorshipStatus1 = new CensorshipStatus();
        censorshipStatus1.setId(1L);
        CensorshipStatus censorshipStatus2 = new CensorshipStatus();
        censorshipStatus2.setId(censorshipStatus1.getId());
        assertThat(censorshipStatus1).isEqualTo(censorshipStatus2);
        censorshipStatus2.setId(2L);
        assertThat(censorshipStatus1).isNotEqualTo(censorshipStatus2);
        censorshipStatus1.setId(null);
        assertThat(censorshipStatus1).isNotEqualTo(censorshipStatus2);
    }
}
