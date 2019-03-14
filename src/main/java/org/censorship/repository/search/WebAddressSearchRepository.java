package org.censorship.repository.search;

import org.censorship.domain.WebAddress;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the WebAddress entity.
 */
public interface WebAddressSearchRepository extends ElasticsearchRepository<WebAddress, Long> {
}
