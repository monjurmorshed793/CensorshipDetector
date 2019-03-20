package org.censorship.repository;

import org.censorship.domain.PacketInformation;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the PacketInformation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PacketInformationRepository extends JpaRepository<PacketInformation, Long> {

}
