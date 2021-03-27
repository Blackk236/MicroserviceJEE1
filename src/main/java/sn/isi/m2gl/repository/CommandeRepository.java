package sn.isi.m2gl.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.isi.m2gl.domain.Commande;

/**
 * Spring Data SQL repository for the Commande entity.
 */
@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {
    @Query(
        value = "select distinct commande from Commande commande left join fetch commande.plats",
        countQuery = "select count(distinct commande) from Commande commande"
    )
    Page<Commande> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct commande from Commande commande left join fetch commande.plats")
    List<Commande> findAllWithEagerRelationships();

    @Query("select commande from Commande commande left join fetch commande.plats where commande.id =:id")
    Optional<Commande> findOneWithEagerRelationships(@Param("id") Long id);
}
