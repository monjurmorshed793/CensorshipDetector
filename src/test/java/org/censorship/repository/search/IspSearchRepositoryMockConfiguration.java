package org.censorship.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of IspSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class IspSearchRepositoryMockConfiguration {

    @MockBean
    private IspSearchRepository mockIspSearchRepository;

}
