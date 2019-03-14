package org.censorship.repository.search;

import org.censorship.domain.Isp;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Isp entity.
 */
public interface IspSearchRepository extends ElasticsearchRepository<Isp, Long> {
}
