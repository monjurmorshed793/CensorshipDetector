package org.censorship.repository.search;

import org.censorship.domain.PacketInformation;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the PacketInformation entity.
 */
public interface PacketInformationSearchRepository extends ElasticsearchRepository<PacketInformation, Long> {
}
