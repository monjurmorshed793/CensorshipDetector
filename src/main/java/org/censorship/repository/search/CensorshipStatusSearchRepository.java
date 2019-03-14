package org.censorship.repository.search;

import org.censorship.domain.CensorshipStatus;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the CensorshipStatus entity.
 */
public interface CensorshipStatusSearchRepository extends ElasticsearchRepository<CensorshipStatus, Long> {
}
