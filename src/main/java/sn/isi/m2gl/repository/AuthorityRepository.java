package sn.isi.m2gl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.isi.m2gl.domain.Authority;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {}
