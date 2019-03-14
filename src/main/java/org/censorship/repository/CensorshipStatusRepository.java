package org.censorship.repository;

import org.censorship.domain.CensorshipStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the CensorshipStatus entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CensorshipStatusRepository extends JpaRepository<CensorshipStatus, Long> {

}
