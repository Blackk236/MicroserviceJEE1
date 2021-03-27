package sn.isi.m2gl.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.isi.m2gl.domain.Boisson;

/**
 * Spring Data SQL repository for the Boisson entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BoissonRepository extends JpaRepository<Boisson, Long> {}
