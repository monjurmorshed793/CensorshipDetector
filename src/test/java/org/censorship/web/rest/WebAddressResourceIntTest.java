package org.censorship.web.rest;

import org.censorship.CensorshipDetectorApp;

import org.censorship.domain.WebAddress;
import org.censorship.repository.WebAddressRepository;
import org.censorship.repository.search.WebAddressSearchRepository;
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

/**
 * Test class for the WebAddressResource REST controller.
 *
 * @see WebAddressResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CensorshipDetectorApp.class)
public class WebAddressResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private WebAddressRepository webAddressRepository;

    /**
     * This repository is mocked in the org.censorship.repository.search test package.
     *
     * @see org.censorship.repository.search.WebAddressSearchRepositoryMockConfiguration
     */
    @Autowired
    private WebAddressSearchRepository mockWebAddressSearchRepository;

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

    private MockMvc restWebAddressMockMvc;

    private WebAddress webAddress;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final WebAddressResource webAddressResource = new WebAddressResource(webAddressRepository, mockWebAddressSearchRepository);
        this.restWebAddressMockMvc = MockMvcBuilders.standaloneSetup(webAddressResource)
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
    public static WebAddress createEntity(EntityManager em) {
        WebAddress webAddress = new WebAddress()
            .name(DEFAULT_NAME);
        return webAddress;
    }

    @Before
    public void initTest() {
        webAddress = createEntity(em);
    }

    @Test
    @Transactional
    public void createWebAddress() throws Exception {
        int databaseSizeBeforeCreate = webAddressRepository.findAll().size();

        // Create the WebAddress
        restWebAddressMockMvc.perform(post("/api/web-addresses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(webAddress)))
            .andExpect(status().isCreated());

        // Validate the WebAddress in the database
        List<WebAddress> webAddressList = webAddressRepository.findAll();
        assertThat(webAddressList).hasSize(databaseSizeBeforeCreate + 1);
        WebAddress testWebAddress = webAddressList.get(webAddressList.size() - 1);
        assertThat(testWebAddress.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the WebAddress in Elasticsearch
        verify(mockWebAddressSearchRepository, times(1)).save(testWebAddress);
    }

    @Test
    @Transactional
    public void createWebAddressWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = webAddressRepository.findAll().size();

        // Create the WebAddress with an existing ID
        webAddress.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restWebAddressMockMvc.perform(post("/api/web-addresses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(webAddress)))
            .andExpect(status().isBadRequest());

        // Validate the WebAddress in the database
        List<WebAddress> webAddressList = webAddressRepository.findAll();
        assertThat(webAddressList).hasSize(databaseSizeBeforeCreate);

        // Validate the WebAddress in Elasticsearch
        verify(mockWebAddressSearchRepository, times(0)).save(webAddress);
    }

    @Test
    @Transactional
    public void getAllWebAddresses() throws Exception {
        // Initialize the database
        webAddressRepository.saveAndFlush(webAddress);

        // Get all the webAddressList
        restWebAddressMockMvc.perform(get("/api/web-addresses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(webAddress.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
    
    @Test
    @Transactional
    public void getWebAddress() throws Exception {
        // Initialize the database
        webAddressRepository.saveAndFlush(webAddress);

        // Get the webAddress
        restWebAddressMockMvc.perform(get("/api/web-addresses/{id}", webAddress.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(webAddress.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingWebAddress() throws Exception {
        // Get the webAddress
        restWebAddressMockMvc.perform(get("/api/web-addresses/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWebAddress() throws Exception {
        // Initialize the database
        webAddressRepository.saveAndFlush(webAddress);

        int databaseSizeBeforeUpdate = webAddressRepository.findAll().size();

        // Update the webAddress
        WebAddress updatedWebAddress = webAddressRepository.findById(webAddress.getId()).get();
        // Disconnect from session so that the updates on updatedWebAddress are not directly saved in db
        em.detach(updatedWebAddress);
        updatedWebAddress
            .name(UPDATED_NAME);

        restWebAddressMockMvc.perform(put("/api/web-addresses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedWebAddress)))
            .andExpect(status().isOk());

        // Validate the WebAddress in the database
        List<WebAddress> webAddressList = webAddressRepository.findAll();
        assertThat(webAddressList).hasSize(databaseSizeBeforeUpdate);
        WebAddress testWebAddress = webAddressList.get(webAddressList.size() - 1);
        assertThat(testWebAddress.getName()).isEqualTo(UPDATED_NAME);

        // Validate the WebAddress in Elasticsearch
        verify(mockWebAddressSearchRepository, times(1)).save(testWebAddress);
    }

    @Test
    @Transactional
    public void updateNonExistingWebAddress() throws Exception {
        int databaseSizeBeforeUpdate = webAddressRepository.findAll().size();

        // Create the WebAddress

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWebAddressMockMvc.perform(put("/api/web-addresses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(webAddress)))
            .andExpect(status().isBadRequest());

        // Validate the WebAddress in the database
        List<WebAddress> webAddressList = webAddressRepository.findAll();
        assertThat(webAddressList).hasSize(databaseSizeBeforeUpdate);

        // Validate the WebAddress in Elasticsearch
        verify(mockWebAddressSearchRepository, times(0)).save(webAddress);
    }

    @Test
    @Transactional
    public void deleteWebAddress() throws Exception {
        // Initialize the database
        webAddressRepository.saveAndFlush(webAddress);

        int databaseSizeBeforeDelete = webAddressRepository.findAll().size();

        // Delete the webAddress
        restWebAddressMockMvc.perform(delete("/api/web-addresses/{id}", webAddress.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<WebAddress> webAddressList = webAddressRepository.findAll();
        assertThat(webAddressList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the WebAddress in Elasticsearch
        verify(mockWebAddressSearchRepository, times(1)).deleteById(webAddress.getId());
    }

    @Test
    @Transactional
    public void searchWebAddress() throws Exception {
        // Initialize the database
        webAddressRepository.saveAndFlush(webAddress);
        when(mockWebAddressSearchRepository.search(queryStringQuery("id:" + webAddress.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(webAddress), PageRequest.of(0, 1), 1));
        // Search the webAddress
        restWebAddressMockMvc.perform(get("/api/_search/web-addresses?query=id:" + webAddress.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(webAddress.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WebAddress.class);
        WebAddress webAddress1 = new WebAddress();
        webAddress1.setId(1L);
        WebAddress webAddress2 = new WebAddress();
        webAddress2.setId(webAddress1.getId());
        assertThat(webAddress1).isEqualTo(webAddress2);
        webAddress2.setId(2L);
        assertThat(webAddress1).isNotEqualTo(webAddress2);
        webAddress1.setId(null);
        assertThat(webAddress1).isNotEqualTo(webAddress2);
    }
}
