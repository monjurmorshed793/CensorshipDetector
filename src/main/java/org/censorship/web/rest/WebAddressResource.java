package org.censorship.web.rest;
import com.google.gson.JsonObject;
import org.censorship.domain.WebAddress;
import org.censorship.repository.WebAddressRepository;
import org.censorship.repository.search.WebAddressSearchRepository;
import org.censorship.web.rest.errors.BadRequestAlertException;
import org.censorship.web.rest.util.HeaderUtil;
import org.censorship.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing WebAddress.
 */
@RestController
@RequestMapping("/api")
public class WebAddressResource {

    private final Logger log = LoggerFactory.getLogger(WebAddressResource.class);

    private static final String ENTITY_NAME = "webAddress";

    private final WebAddressRepository webAddressRepository;

    private final WebAddressSearchRepository webAddressSearchRepository;

    public WebAddressResource(WebAddressRepository webAddressRepository, WebAddressSearchRepository webAddressSearchRepository) {
        this.webAddressRepository = webAddressRepository;
        this.webAddressSearchRepository = webAddressSearchRepository;
    }

    /**
     * POST  /web-addresses : Create a new webAddress.
     *
     * @param webAddress the webAddress to create
     * @return the ResponseEntity with status 201 (Created) and with body the new webAddress, or with status 400 (Bad Request) if the webAddress has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/web-addresses")
    public ResponseEntity<WebAddress> createWebAddress(@RequestBody WebAddress webAddress) throws URISyntaxException {
        log.debug("REST request to save WebAddress : {}", webAddress);
        if (webAddress.getId() != null) {
            throw new BadRequestAlertException("A new webAddress cannot already have an ID", ENTITY_NAME, "idexists");
        }
        WebAddress result = webAddressRepository.save(webAddress);
        webAddressSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/web-addresses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @RequestMapping(value = "/web-address-file-upload",
        produces ={"application/json"},
        consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
        method = RequestMethod.POST
    )
    public ResponseEntity<String> uploadExcelFile(@RequestParam("file") File file) throws URISyntaxException{
        log.debug("REST request to upload excel file");
        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert("File Successfully Uploaded",""))
            .body("success");
    }

    /**
     * PUT  /web-addresses : Updates an existing webAddress.
     *
     * @param webAddress the webAddress to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated webAddress,
     * or with status 400 (Bad Request) if the webAddress is not valid,
     * or with status 500 (Internal Server Error) if the webAddress couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/web-addresses")
    public ResponseEntity<WebAddress> updateWebAddress(@RequestBody WebAddress webAddress) throws URISyntaxException {
        log.debug("REST request to update WebAddress : {}", webAddress);
        if (webAddress.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        WebAddress result = webAddressRepository.save(webAddress);
        webAddressSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, webAddress.getId().toString()))
            .body(result);
    }

    /**
     * GET  /web-addresses : get all the webAddresses.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of webAddresses in body
     */
    @GetMapping("/web-addresses")
    public ResponseEntity<List<WebAddress>> getAllWebAddresses(Pageable pageable) {
        log.debug("REST request to get a page of WebAddresses");
        Page<WebAddress> page = webAddressRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/web-addresses");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /web-addresses/:id : get the "id" webAddress.
     *
     * @param id the id of the webAddress to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the webAddress, or with status 404 (Not Found)
     */
    @GetMapping("/web-addresses/{id}")
    public ResponseEntity<WebAddress> getWebAddress(@PathVariable Long id) {
        log.debug("REST request to get WebAddress : {}", id);
        Optional<WebAddress> webAddress = webAddressRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(webAddress);
    }

    /**
     * DELETE  /web-addresses/:id : delete the "id" webAddress.
     *
     * @param id the id of the webAddress to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/web-addresses/{id}")
    public ResponseEntity<Void> deleteWebAddress(@PathVariable Long id) {
        log.debug("REST request to delete WebAddress : {}", id);
        webAddressRepository.deleteById(id);
        webAddressSearchRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/web-addresses?query=:query : search for the webAddress corresponding
     * to the query.
     *
     * @param query the query of the webAddress search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/web-addresses")
    public ResponseEntity<List<WebAddress>> searchWebAddresses(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of WebAddresses for query {}", query);
        Page<WebAddress> page = webAddressSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/web-addresses");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
