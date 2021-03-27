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
import sn.isi.m2gl.domain.Boisson;
import sn.isi.m2gl.repository.BoissonRepository;
import sn.isi.m2gl.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.isi.m2gl.domain.Boisson}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class BoissonResource {

    private final Logger log = LoggerFactory.getLogger(BoissonResource.class);

    private static final String ENTITY_NAME = "jeeProjectApi1Boisson";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BoissonRepository boissonRepository;

    public BoissonResource(BoissonRepository boissonRepository) {
        this.boissonRepository = boissonRepository;
    }

    /**
     * {@code POST  /boissons} : Create a new boisson.
     *
     * @param boisson the boisson to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new boisson, or with status {@code 400 (Bad Request)} if the boisson has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/boissons")
    public ResponseEntity<Boisson> createBoisson(@Valid @RequestBody Boisson boisson) throws URISyntaxException {
        log.debug("REST request to save Boisson : {}", boisson);
        if (boisson.getId() != null) {
            throw new BadRequestAlertException("A new boisson cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Boisson result = boissonRepository.save(boisson);
        return ResponseEntity
            .created(new URI("/api/boissons/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /boissons/:id} : Updates an existing boisson.
     *
     * @param id the id of the boisson to save.
     * @param boisson the boisson to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated boisson,
     * or with status {@code 400 (Bad Request)} if the boisson is not valid,
     * or with status {@code 500 (Internal Server Error)} if the boisson couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/boissons/{id}")
    public ResponseEntity<Boisson> updateBoisson(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Boisson boisson
    ) throws URISyntaxException {
        log.debug("REST request to update Boisson : {}, {}", id, boisson);
        if (boisson.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, boisson.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!boissonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Boisson result = boissonRepository.save(boisson);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, boisson.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /boissons/:id} : Partial updates given fields of an existing boisson, field will ignore if it is null
     *
     * @param id the id of the boisson to save.
     * @param boisson the boisson to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated boisson,
     * or with status {@code 400 (Bad Request)} if the boisson is not valid,
     * or with status {@code 404 (Not Found)} if the boisson is not found,
     * or with status {@code 500 (Internal Server Error)} if the boisson couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/boissons/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Boisson> partialUpdateBoisson(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Boisson boisson
    ) throws URISyntaxException {
        log.debug("REST request to partial update Boisson partially : {}, {}", id, boisson);
        if (boisson.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, boisson.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!boissonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Boisson> result = boissonRepository
            .findById(boisson.getId())
            .map(
                existingBoisson -> {
                    if (boisson.getLibelle() != null) {
                        existingBoisson.setLibelle(boisson.getLibelle());
                    }

                    return existingBoisson;
                }
            )
            .map(boissonRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, boisson.getId().toString())
        );
    }

    /**
     * {@code GET  /boissons} : get all the boissons.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of boissons in body.
     */
    @GetMapping("/boissons")
    public ResponseEntity<List<Boisson>> getAllBoissons(Pageable pageable) {
        log.debug("REST request to get a page of Boissons");
        Page<Boisson> page = boissonRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /boissons/:id} : get the "id" boisson.
     *
     * @param id the id of the boisson to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the boisson, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/boissons/{id}")
    public ResponseEntity<Boisson> getBoisson(@PathVariable Long id) {
        log.debug("REST request to get Boisson : {}", id);
        Optional<Boisson> boisson = boissonRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(boisson);
    }

    /**
     * {@code DELETE  /boissons/:id} : delete the "id" boisson.
     *
     * @param id the id of the boisson to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/boissons/{id}")
    public ResponseEntity<Void> deleteBoisson(@PathVariable Long id) {
        log.debug("REST request to delete Boisson : {}", id);
        boissonRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
