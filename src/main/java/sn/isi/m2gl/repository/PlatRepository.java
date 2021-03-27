package sn.isi.m2gl.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.isi.m2gl.domain.Plat;

/**
 * Spring Data SQL repository for the Plat entity.
 */
@Repository
public interface PlatRepository extends JpaRepository<Plat, Long> {
    @Query(
        value = "select distinct plat from Plat plat left join fetch plat.boissons",
        countQuery = "select count(distinct plat) from Plat plat"
    )
    Page<Plat> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct plat from Plat plat left join fetch plat.boissons")
    List<Plat> findAllWithEagerRelationships();

    @Query("select plat from Plat plat left join fetch plat.boissons where plat.id =:id")
    Optional<Plat> findOneWithEagerRelationships(@Param("id") Long id);
}
