package sn.isi.m2gl.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import sn.isi.m2gl.IntegrationTest;
import sn.isi.m2gl.domain.Plat;
import sn.isi.m2gl.repository.PlatRepository;

/**
 * Integration tests for the {@link PlatResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PlatResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final Long DEFAULT_PRIX_UNITAIRE = 1L;
    private static final Long UPDATED_PRIX_UNITAIRE = 2L;

    private static final Long DEFAULT_QUANTITE = 1L;
    private static final Long UPDATED_QUANTITE = 2L;

    private static final String ENTITY_API_URL = "/api/plats";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PlatRepository platRepository;

    @Mock
    private PlatRepository platRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPlatMockMvc;

    private Plat plat;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Plat createEntity(EntityManager em) {
        Plat plat = new Plat().libelle(DEFAULT_LIBELLE).prixUnitaire(DEFAULT_PRIX_UNITAIRE).quantite(DEFAULT_QUANTITE);
        return plat;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Plat createUpdatedEntity(EntityManager em) {
        Plat plat = new Plat().libelle(UPDATED_LIBELLE).prixUnitaire(UPDATED_PRIX_UNITAIRE).quantite(UPDATED_QUANTITE);
        return plat;
    }

    @BeforeEach
    public void initTest() {
        plat = createEntity(em);
    }

    @Test
    @Transactional
    void createPlat() throws Exception {
        int databaseSizeBeforeCreate = platRepository.findAll().size();
        // Create the Plat
        restPlatMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(plat))
            )
            .andExpect(status().isCreated());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeCreate + 1);
        Plat testPlat = platList.get(platList.size() - 1);
        assertThat(testPlat.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
        assertThat(testPlat.getPrixUnitaire()).isEqualTo(DEFAULT_PRIX_UNITAIRE);
        assertThat(testPlat.getQuantite()).isEqualTo(DEFAULT_QUANTITE);
    }

    @Test
    @Transactional
    void createPlatWithExistingId() throws Exception {
        // Create the Plat with an existing ID
        plat.setId(1L);

        int databaseSizeBeforeCreate = platRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlatMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(plat))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = platRepository.findAll().size();
        // set the field null
        plat.setLibelle(null);

        // Create the Plat, which fails.

        restPlatMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(plat))
            )
            .andExpect(status().isBadRequest());

        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrixUnitaireIsRequired() throws Exception {
        int databaseSizeBeforeTest = platRepository.findAll().size();
        // set the field null
        plat.setPrixUnitaire(null);

        // Create the Plat, which fails.

        restPlatMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(plat))
            )
            .andExpect(status().isBadRequest());

        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkQuantiteIsRequired() throws Exception {
        int databaseSizeBeforeTest = platRepository.findAll().size();
        // set the field null
        plat.setQuantite(null);

        // Create the Plat, which fails.

        restPlatMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(plat))
            )
            .andExpect(status().isBadRequest());

        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPlats() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList
        restPlatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(plat.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].prixUnitaire").value(hasItem(DEFAULT_PRIX_UNITAIRE.intValue())))
            .andExpect(jsonPath("$.[*].quantite").value(hasItem(DEFAULT_QUANTITE.intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPlatsWithEagerRelationshipsIsEnabled() throws Exception {
        when(platRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPlatMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(platRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPlatsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(platRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPlatMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(platRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getPlat() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get the plat
        restPlatMockMvc
            .perform(get(ENTITY_API_URL_ID, plat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(plat.getId().intValue()))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE))
            .andExpect(jsonPath("$.prixUnitaire").value(DEFAULT_PRIX_UNITAIRE.intValue()))
            .andExpect(jsonPath("$.quantite").value(DEFAULT_QUANTITE.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingPlat() throws Exception {
        // Get the plat
        restPlatMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPlat() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        int databaseSizeBeforeUpdate = platRepository.findAll().size();

        // Update the plat
        Plat updatedPlat = platRepository.findById(plat.getId()).get();
        // Disconnect from session so that the updates on updatedPlat are not directly saved in db
        em.detach(updatedPlat);
        updatedPlat.libelle(UPDATED_LIBELLE).prixUnitaire(UPDATED_PRIX_UNITAIRE).quantite(UPDATED_QUANTITE);

        restPlatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPlat.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPlat))
            )
            .andExpect(status().isOk());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeUpdate);
        Plat testPlat = platList.get(platList.size() - 1);
        assertThat(testPlat.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testPlat.getPrixUnitaire()).isEqualTo(UPDATED_PRIX_UNITAIRE);
        assertThat(testPlat.getQuantite()).isEqualTo(UPDATED_QUANTITE);
    }

    @Test
    @Transactional
    void putNonExistingPlat() throws Exception {
        int databaseSizeBeforeUpdate = platRepository.findAll().size();
        plat.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, plat.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(plat))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPlat() throws Exception {
        int databaseSizeBeforeUpdate = platRepository.findAll().size();
        plat.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(plat))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPlat() throws Exception {
        int databaseSizeBeforeUpdate = platRepository.findAll().size();
        plat.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlatMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(plat))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePlatWithPatch() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        int databaseSizeBeforeUpdate = platRepository.findAll().size();

        // Update the plat using partial update
        Plat partialUpdatedPlat = new Plat();
        partialUpdatedPlat.setId(plat.getId());

        partialUpdatedPlat.quantite(UPDATED_QUANTITE);

        restPlatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlat.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPlat))
            )
            .andExpect(status().isOk());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeUpdate);
        Plat testPlat = platList.get(platList.size() - 1);
        assertThat(testPlat.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
        assertThat(testPlat.getPrixUnitaire()).isEqualTo(DEFAULT_PRIX_UNITAIRE);
        assertThat(testPlat.getQuantite()).isEqualTo(UPDATED_QUANTITE);
    }

    @Test
    @Transactional
    void fullUpdatePlatWithPatch() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        int databaseSizeBeforeUpdate = platRepository.findAll().size();

        // Update the plat using partial update
        Plat partialUpdatedPlat = new Plat();
        partialUpdatedPlat.setId(plat.getId());

        partialUpdatedPlat.libelle(UPDATED_LIBELLE).prixUnitaire(UPDATED_PRIX_UNITAIRE).quantite(UPDATED_QUANTITE);

        restPlatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlat.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPlat))
            )
            .andExpect(status().isOk());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeUpdate);
        Plat testPlat = platList.get(platList.size() - 1);
        assertThat(testPlat.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testPlat.getPrixUnitaire()).isEqualTo(UPDATED_PRIX_UNITAIRE);
        assertThat(testPlat.getQuantite()).isEqualTo(UPDATED_QUANTITE);
    }

    @Test
    @Transactional
    void patchNonExistingPlat() throws Exception {
        int databaseSizeBeforeUpdate = platRepository.findAll().size();
        plat.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, plat.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(plat))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPlat() throws Exception {
        int databaseSizeBeforeUpdate = platRepository.findAll().size();
        plat.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(plat))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPlat() throws Exception {
        int databaseSizeBeforeUpdate = platRepository.findAll().size();
        plat.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlatMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(plat))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePlat() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        int databaseSizeBeforeDelete = platRepository.findAll().size();

        // Delete the plat
        restPlatMockMvc
            .perform(delete(ENTITY_API_URL_ID, plat.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
