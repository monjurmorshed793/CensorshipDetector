package org.censorship.repository;

import org.censorship.domain.WebAddress;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the WebAddress entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WebAddressRepository extends JpaRepository<WebAddress, Long> {

}
