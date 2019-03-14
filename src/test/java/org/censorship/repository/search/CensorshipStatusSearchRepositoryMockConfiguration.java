package org.censorship.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of CensorshipStatusSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class CensorshipStatusSearchRepositoryMockConfiguration {

    @MockBean
    private CensorshipStatusSearchRepository mockCensorshipStatusSearchRepository;

}
