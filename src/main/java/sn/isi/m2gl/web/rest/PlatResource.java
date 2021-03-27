package sn.isi.m2gl.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sn.isi.m2gl.domain.Plat;
import sn.isi.m2gl.repository.PlatRepository;
import sn.isi.m2gl.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.isi.m2gl.domain.Plat}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PlatResource {

    private final Logger log = LoggerFactory.getLogger(PlatResource.class);

    private static final String ENTITY_NAME = "jeeProjectApi1Plat";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PlatRepository platRepository;

    public PlatResource(PlatRepository platRepository) {
        this.platRepository = platRepository;
    }

    /**
     * {@code POST  /plats} : Create a new plat.
     *
     * @param plat the plat to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new plat, or with status {@code 400 (Bad Request)} if the plat has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/plats")
    public ResponseEntity<Plat> createPlat(@Valid @RequestBody Plat plat) throws URISyntaxException {
        log.debug("REST request to save Plat : {}", plat);
        if (plat.getId() != null) {
            throw new BadRequestAlertException("A new plat cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Plat result = platRepository.save(plat);
        return ResponseEntity
            .created(new URI("/api/plats/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /plats/:id} : Updates an existing plat.
     *
     * @param id the id of the plat to save.
     * @param plat the plat to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated plat,
     * or with status {@code 400 (Bad Request)} if the plat is not valid,
     * or with status {@code 500 (Internal Server Error)} if the plat couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/plats/{id}")
    public ResponseEntity<Plat> updatePlat(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Plat plat)
        throws URISyntaxException {
        log.debug("REST request to update Plat : {}, {}", id, plat);
        if (plat.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, plat.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!platRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Plat result = platRepository.save(plat);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, plat.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /plats/:id} : Partial updates given fields of an existing plat, field will ignore if it is null
     *
     * @param id the id of the plat to save.
     * @param plat the plat to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated plat,
     * or with status {@code 400 (Bad Request)} if the plat is not valid,
     * or with status {@code 404 (Not Found)} if the plat is not found,
     * or with status {@code 500 (Internal Server Error)} if the plat couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/plats/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Plat> partialUpdatePlat(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Plat plat
    ) throws URISyntaxException {
        log.debug("REST request to partial update Plat partially : {}, {}", id, plat);
        if (plat.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, plat.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!platRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Plat> result = platRepository
            .findById(plat.getId())
            .map(
                existingPlat -> {
                    if (plat.getLibelle() != null) {
                        existingPlat.setLibelle(plat.getLibelle());
                    }
                    if (plat.getPrixUnitaire() != null) {
                        existingPlat.setPrixUnitaire(plat.getPrixUnitaire());
                    }
                    if (plat.getQuantite() != null) {
                        existingPlat.setQuantite(plat.getQuantite());
                    }

                    return existingPlat;
                }
            )
            .map(platRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, plat.getId().toString())
        );
    }

    /**
     * {@code GET  /plats} : get all the plats.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of plats in body.
     */
    @GetMapping("/plats")
    public ResponseEntity<List<Plat>> getAllPlats(
        Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Plats");
        Page<Plat> page;
        if (eagerload) {
            page = platRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = platRepository.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /plats/:id} : get the "id" plat.
     *
     * @param id the id of the plat to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the plat, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/plats/{id}")
    public ResponseEntity<Plat> getPlat(@PathVariable Long id) {
        log.debug("REST request to get Plat : {}", id);
        Optional<Plat> plat = platRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(plat);
    }

    /**
     * {@code DELETE  /plats/:id} : delete the "id" plat.
     *
     * @param id the id of the plat to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/plats/{id}")
    public ResponseEntity<Void> deletePlat(@PathVariable Long id) {
        log.debug("REST request to delete Plat : {}", id);
        platRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
