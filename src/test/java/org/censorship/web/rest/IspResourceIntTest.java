package org.censorship.web.rest;

import org.censorship.CensorshipDetectorApp;

import org.censorship.domain.Isp;
import org.censorship.repository.IspRepository;
import org.censorship.repository.search.IspSearchRepository;
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

import org.censorship.domain.enumeration.IspType;
/**
 * Test class for the IspResource REST controller.
 *
 * @see IspResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CensorshipDetectorApp.class)
public class IspResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final IspType DEFAULT_TYPE = IspType.BROADBAND;
    private static final IspType UPDATED_TYPE = IspType.MOBILE_NETWORK;

    @Autowired
    private IspRepository ispRepository;

    /**
     * This repository is mocked in the org.censorship.repository.search test package.
     *
     * @see org.censorship.repository.search.IspSearchRepositoryMockConfiguration
     */
    @Autowired
    private IspSearchRepository mockIspSearchRepository;

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

    private MockMvc restIspMockMvc;

    private Isp isp;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final IspResource ispResource = new IspResource(ispRepository, mockIspSearchRepository);
        this.restIspMockMvc = MockMvcBuilders.standaloneSetup(ispResource)
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
    public static Isp createEntity(EntityManager em) {
        Isp isp = new Isp()
            .name(DEFAULT_NAME)
            .type(DEFAULT_TYPE);
        return isp;
    }

    @Before
    public void initTest() {
        isp = createEntity(em);
    }

    @Test
    @Transactional
    public void createIsp() throws Exception {
        int databaseSizeBeforeCreate = ispRepository.findAll().size();

        // Create the Isp
        restIspMockMvc.perform(post("/api/isps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(isp)))
            .andExpect(status().isCreated());

        // Validate the Isp in the database
        List<Isp> ispList = ispRepository.findAll();
        assertThat(ispList).hasSize(databaseSizeBeforeCreate + 1);
        Isp testIsp = ispList.get(ispList.size() - 1);
        assertThat(testIsp.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testIsp.getType()).isEqualTo(DEFAULT_TYPE);

        // Validate the Isp in Elasticsearch
        verify(mockIspSearchRepository, times(1)).save(testIsp);
    }

    @Test
    @Transactional
    public void createIspWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = ispRepository.findAll().size();

        // Create the Isp with an existing ID
        isp.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restIspMockMvc.perform(post("/api/isps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(isp)))
            .andExpect(status().isBadRequest());

        // Validate the Isp in the database
        List<Isp> ispList = ispRepository.findAll();
        assertThat(ispList).hasSize(databaseSizeBeforeCreate);

        // Validate the Isp in Elasticsearch
        verify(mockIspSearchRepository, times(0)).save(isp);
    }

    @Test
    @Transactional
    public void getAllIsps() throws Exception {
        // Initialize the database
        ispRepository.saveAndFlush(isp);

        // Get all the ispList
        restIspMockMvc.perform(get("/api/isps?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(isp.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }
    
    @Test
    @Transactional
    public void getIsp() throws Exception {
        // Initialize the database
        ispRepository.saveAndFlush(isp);

        // Get the isp
        restIspMockMvc.perform(get("/api/isps/{id}", isp.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(isp.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingIsp() throws Exception {
        // Get the isp
        restIspMockMvc.perform(get("/api/isps/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateIsp() throws Exception {
        // Initialize the database
        ispRepository.saveAndFlush(isp);

        int databaseSizeBeforeUpdate = ispRepository.findAll().size();

        // Update the isp
        Isp updatedIsp = ispRepository.findById(isp.getId()).get();
        // Disconnect from session so that the updates on updatedIsp are not directly saved in db
        em.detach(updatedIsp);
        updatedIsp
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE);

        restIspMockMvc.perform(put("/api/isps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedIsp)))
            .andExpect(status().isOk());

        // Validate the Isp in the database
        List<Isp> ispList = ispRepository.findAll();
        assertThat(ispList).hasSize(databaseSizeBeforeUpdate);
        Isp testIsp = ispList.get(ispList.size() - 1);
        assertThat(testIsp.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testIsp.getType()).isEqualTo(UPDATED_TYPE);

        // Validate the Isp in Elasticsearch
        verify(mockIspSearchRepository, times(1)).save(testIsp);
    }

    @Test
    @Transactional
    public void updateNonExistingIsp() throws Exception {
        int databaseSizeBeforeUpdate = ispRepository.findAll().size();

        // Create the Isp

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIspMockMvc.perform(put("/api/isps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(isp)))
            .andExpect(status().isBadRequest());

        // Validate the Isp in the database
        List<Isp> ispList = ispRepository.findAll();
        assertThat(ispList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Isp in Elasticsearch
        verify(mockIspSearchRepository, times(0)).save(isp);
    }

    @Test
    @Transactional
    public void deleteIsp() throws Exception {
        // Initialize the database
        ispRepository.saveAndFlush(isp);

        int databaseSizeBeforeDelete = ispRepository.findAll().size();

        // Delete the isp
        restIspMockMvc.perform(delete("/api/isps/{id}", isp.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Isp> ispList = ispRepository.findAll();
        assertThat(ispList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Isp in Elasticsearch
        verify(mockIspSearchRepository, times(1)).deleteById(isp.getId());
    }

    @Test
    @Transactional
    public void searchIsp() throws Exception {
        // Initialize the database
        ispRepository.saveAndFlush(isp);
        when(mockIspSearchRepository.search(queryStringQuery("id:" + isp.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(isp), PageRequest.of(0, 1), 1));
        // Search the isp
        restIspMockMvc.perform(get("/api/_search/isps?query=id:" + isp.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(isp.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Isp.class);
        Isp isp1 = new Isp();
        isp1.setId(1L);
        Isp isp2 = new Isp();
        isp2.setId(isp1.getId());
        assertThat(isp1).isEqualTo(isp2);
        isp2.setId(2L);
        assertThat(isp1).isNotEqualTo(isp2);
        isp1.setId(null);
        assertThat(isp1).isNotEqualTo(isp2);
    }
}
