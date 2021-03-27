package sn.isi.m2gl.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import sn.isi.m2gl.IntegrationTest;
import sn.isi.m2gl.domain.Boisson;
import sn.isi.m2gl.repository.BoissonRepository;

/**
 * Integration tests for the {@link BoissonResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BoissonResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/boissons";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BoissonRepository boissonRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBoissonMockMvc;

    private Boisson boisson;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Boisson createEntity(EntityManager em) {
        Boisson boisson = new Boisson().libelle(DEFAULT_LIBELLE);
        return boisson;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Boisson createUpdatedEntity(EntityManager em) {
        Boisson boisson = new Boisson().libelle(UPDATED_LIBELLE);
        return boisson;
    }

    @BeforeEach
    public void initTest() {
        boisson = createEntity(em);
    }

    @Test
    @Transactional
    void createBoisson() throws Exception {
        int databaseSizeBeforeCreate = boissonRepository.findAll().size();
        // Create the Boisson
        restBoissonMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(boisson))
            )
            .andExpect(status().isCreated());

        // Validate the Boisson in the database
        List<Boisson> boissonList = boissonRepository.findAll();
        assertThat(boissonList).hasSize(databaseSizeBeforeCreate + 1);
        Boisson testBoisson = boissonList.get(boissonList.size() - 1);
        assertThat(testBoisson.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
    }

    @Test
    @Transactional
    void createBoissonWithExistingId() throws Exception {
        // Create the Boisson with an existing ID
        boisson.setId(1L);

        int databaseSizeBeforeCreate = boissonRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBoissonMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(boisson))
            )
            .andExpect(status().isBadRequest());

        // Validate the Boisson in the database
        List<Boisson> boissonList = boissonRepository.findAll();
        assertThat(boissonList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = boissonRepository.findAll().size();
        // set the field null
        boisson.setLibelle(null);

        // Create the Boisson, which fails.

        restBoissonMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(boisson))
            )
            .andExpect(status().isBadRequest());

        List<Boisson> boissonList = boissonRepository.findAll();
        assertThat(boissonList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBoissons() throws Exception {
        // Initialize the database
        boissonRepository.saveAndFlush(boisson);

        // Get all the boissonList
        restBoissonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(boisson.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)));
    }

    @Test
    @Transactional
    void getBoisson() throws Exception {
        // Initialize the database
        boissonRepository.saveAndFlush(boisson);

        // Get the boisson
        restBoissonMockMvc
            .perform(get(ENTITY_API_URL_ID, boisson.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(boisson.getId().intValue()))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE));
    }

    @Test
    @Transactional
    void getNonExistingBoisson() throws Exception {
        // Get the boisson
        restBoissonMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewBoisson() throws Exception {
        // Initialize the database
        boissonRepository.saveAndFlush(boisson);

        int databaseSizeBeforeUpdate = boissonRepository.findAll().size();

        // Update the boisson
        Boisson updatedBoisson = boissonRepository.findById(boisson.getId()).get();
        // Disconnect from session so that the updates on updatedBoisson are not directly saved in db
        em.detach(updatedBoisson);
        updatedBoisson.libelle(UPDATED_LIBELLE);

        restBoissonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBoisson.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBoisson))
            )
            .andExpect(status().isOk());

        // Validate the Boisson in the database
        List<Boisson> boissonList = boissonRepository.findAll();
        assertThat(boissonList).hasSize(databaseSizeBeforeUpdate);
        Boisson testBoisson = boissonList.get(boissonList.size() - 1);
        assertThat(testBoisson.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void putNonExistingBoisson() throws Exception {
        int databaseSizeBeforeUpdate = boissonRepository.findAll().size();
        boisson.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBoissonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, boisson.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(boisson))
            )
            .andExpect(status().isBadRequest());

        // Validate the Boisson in the database
        List<Boisson> boissonList = boissonRepository.findAll();
        assertThat(boissonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBoisson() throws Exception {
        int databaseSizeBeforeUpdate = boissonRepository.findAll().size();
        boisson.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoissonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(boisson))
            )
            .andExpect(status().isBadRequest());

        // Validate the Boisson in the database
        List<Boisson> boissonList = boissonRepository.findAll();
        assertThat(boissonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBoisson() throws Exception {
        int databaseSizeBeforeUpdate = boissonRepository.findAll().size();
        boisson.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoissonMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(boisson))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Boisson in the database
        List<Boisson> boissonList = boissonRepository.findAll();
        assertThat(boissonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBoissonWithPatch() throws Exception {
        // Initialize the database
        boissonRepository.saveAndFlush(boisson);

        int databaseSizeBeforeUpdate = boissonRepository.findAll().size();

        // Update the boisson using partial update
        Boisson partialUpdatedBoisson = new Boisson();
        partialUpdatedBoisson.setId(boisson.getId());

        partialUpdatedBoisson.libelle(UPDATED_LIBELLE);

        restBoissonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBoisson.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBoisson))
            )
            .andExpect(status().isOk());

        // Validate the Boisson in the database
        List<Boisson> boissonList = boissonRepository.findAll();
        assertThat(boissonList).hasSize(databaseSizeBeforeUpdate);
        Boisson testBoisson = boissonList.get(boissonList.size() - 1);
        assertThat(testBoisson.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void fullUpdateBoissonWithPatch() throws Exception {
        // Initialize the database
        boissonRepository.saveAndFlush(boisson);

        int databaseSizeBeforeUpdate = boissonRepository.findAll().size();

        // Update the boisson using partial update
        Boisson partialUpdatedBoisson = new Boisson();
        partialUpdatedBoisson.setId(boisson.getId());

        partialUpdatedBoisson.libelle(UPDATED_LIBELLE);

        restBoissonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBoisson.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBoisson))
            )
            .andExpect(status().isOk());

        // Validate the Boisson in the database
        List<Boisson> boissonList = boissonRepository.findAll();
        assertThat(boissonList).hasSize(databaseSizeBeforeUpdate);
        Boisson testBoisson = boissonList.get(boissonList.size() - 1);
        assertThat(testBoisson.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void patchNonExistingBoisson() throws Exception {
        int databaseSizeBeforeUpdate = boissonRepository.findAll().size();
        boisson.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBoissonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, boisson.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(boisson))
            )
            .andExpect(status().isBadRequest());

        // Validate the Boisson in the database
        List<Boisson> boissonList = boissonRepository.findAll();
        assertThat(boissonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBoisson() throws Exception {
        int databaseSizeBeforeUpdate = boissonRepository.findAll().size();
        boisson.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoissonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(boisson))
            )
            .andExpect(status().isBadRequest());

        // Validate the Boisson in the database
        List<Boisson> boissonList = boissonRepository.findAll();
        assertThat(boissonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBoisson() throws Exception {
        int databaseSizeBeforeUpdate = boissonRepository.findAll().size();
        boisson.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoissonMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(boisson))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Boisson in the database
        List<Boisson> boissonList = boissonRepository.findAll();
        assertThat(boissonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBoisson() throws Exception {
        // Initialize the database
        boissonRepository.saveAndFlush(boisson);

        int databaseSizeBeforeDelete = boissonRepository.findAll().size();

        // Delete the boisson
        restBoissonMockMvc
            .perform(delete(ENTITY_API_URL_ID, boisson.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Boisson> boissonList = boissonRepository.findAll();
        assertThat(boissonList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
