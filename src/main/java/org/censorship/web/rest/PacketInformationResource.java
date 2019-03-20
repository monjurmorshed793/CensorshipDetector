package org.censorship.web.rest;
import org.censorship.domain.PacketInformation;
import org.censorship.repository.PacketInformationRepository;
import org.censorship.repository.search.PacketInformationSearchRepository;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing PacketInformation.
 */
@RestController
@RequestMapping("/api")
public class PacketInformationResource {

    private final Logger log = LoggerFactory.getLogger(PacketInformationResource.class);

    private static final String ENTITY_NAME = "packetInformation";

    private final PacketInformationRepository packetInformationRepository;

    private final PacketInformationSearchRepository packetInformationSearchRepository;

    public PacketInformationResource(PacketInformationRepository packetInformationRepository, PacketInformationSearchRepository packetInformationSearchRepository) {
        this.packetInformationRepository = packetInformationRepository;
        this.packetInformationSearchRepository = packetInformationSearchRepository;
    }

    /**
     * POST  /packet-informations : Create a new packetInformation.
     *
     * @param packetInformation the packetInformation to create
     * @return the ResponseEntity with status 201 (Created) and with body the new packetInformation, or with status 400 (Bad Request) if the packetInformation has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/packet-informations")
    public ResponseEntity<PacketInformation> createPacketInformation(@RequestBody PacketInformation packetInformation) throws URISyntaxException {
        log.debug("REST request to save PacketInformation : {}", packetInformation);
        if (packetInformation.getId() != null) {
            throw new BadRequestAlertException("A new packetInformation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PacketInformation result = packetInformationRepository.save(packetInformation);
        packetInformationSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/packet-informations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /packet-informations : Updates an existing packetInformation.
     *
     * @param packetInformation the packetInformation to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated packetInformation,
     * or with status 400 (Bad Request) if the packetInformation is not valid,
     * or with status 500 (Internal Server Error) if the packetInformation couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/packet-informations")
    public ResponseEntity<PacketInformation> updatePacketInformation(@RequestBody PacketInformation packetInformation) throws URISyntaxException {
        log.debug("REST request to update PacketInformation : {}", packetInformation);
        if (packetInformation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        PacketInformation result = packetInformationRepository.save(packetInformation);
        packetInformationSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, packetInformation.getId().toString()))
            .body(result);
    }

    /**
     * GET  /packet-informations : get all the packetInformations.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of packetInformations in body
     */
    @GetMapping("/packet-informations")
    public ResponseEntity<List<PacketInformation>> getAllPacketInformations(Pageable pageable) {
        log.debug("REST request to get a page of PacketInformations");
        Page<PacketInformation> page = packetInformationRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/packet-informations");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /packet-informations/:id : get the "id" packetInformation.
     *
     * @param id the id of the packetInformation to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the packetInformation, or with status 404 (Not Found)
     */
    @GetMapping("/packet-informations/{id}")
    public ResponseEntity<PacketInformation> getPacketInformation(@PathVariable Long id) {
        log.debug("REST request to get PacketInformation : {}", id);
        Optional<PacketInformation> packetInformation = packetInformationRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(packetInformation);
    }

    /**
     * DELETE  /packet-informations/:id : delete the "id" packetInformation.
     *
     * @param id the id of the packetInformation to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/packet-informations/{id}")
    public ResponseEntity<Void> deletePacketInformation(@PathVariable Long id) {
        log.debug("REST request to delete PacketInformation : {}", id);
        packetInformationRepository.deleteById(id);
        packetInformationSearchRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/packet-informations?query=:query : search for the packetInformation corresponding
     * to the query.
     *
     * @param query the query of the packetInformation search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/packet-informations")
    public ResponseEntity<List<PacketInformation>> searchPacketInformations(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of PacketInformations for query {}", query);
        Page<PacketInformation> page = packetInformationSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/packet-informations");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
