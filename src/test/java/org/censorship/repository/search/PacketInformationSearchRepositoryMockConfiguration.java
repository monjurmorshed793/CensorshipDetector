package org.censorship.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of PacketInformationSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class PacketInformationSearchRepositoryMockConfiguration {

    @MockBean
    private PacketInformationSearchRepository mockPacketInformationSearchRepository;

}
