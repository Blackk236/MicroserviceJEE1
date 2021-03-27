package sn.isi.m2gl.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Plat.
 */
@Entity
@Table(name = "plat")
public class Plat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "libelle", nullable = false, unique = true)
    private String libelle;

    @NotNull
    @Column(name = "prix_unitaire", nullable = false)
    private Long prixUnitaire;

    @NotNull
    @Column(name = "quantite", nullable = false)
    private Long quantite;

    @ManyToMany
    @JoinTable(
        name = "rel_plat__boisson",
        joinColumns = @JoinColumn(name = "plat_id"),
        inverseJoinColumns = @JoinColumn(name = "boisson_id")
    )
    @JsonIgnoreProperties(value = { "plats" }, allowSetters = true)
    private Set<Boisson> boissons = new HashSet<>();

    @ManyToMany(mappedBy = "plats")
    @JsonIgnoreProperties(value = { "plats" }, allowSetters = true)
    private Set<Commande> commandes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Plat id(Long id) {
        this.id = id;
        return this;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public Plat libelle(String libelle) {
        this.libelle = libelle;
        return this;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Long getPrixUnitaire() {
        return this.prixUnitaire;
    }

    public Plat prixUnitaire(Long prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
        return this;
    }

    public void setPrixUnitaire(Long prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public Long getQuantite() {
        return this.quantite;
    }

    public Plat quantite(Long quantite) {
        this.quantite = quantite;
        return this;
    }

    public void setQuantite(Long quantite) {
        this.quantite = quantite;
    }

    public Set<Boisson> getBoissons() {
        return this.boissons;
    }

    public Plat boissons(Set<Boisson> boissons) {
        this.setBoissons(boissons);
        return this;
    }

    public Plat addBoisson(Boisson boisson) {
        this.boissons.add(boisson);
        boisson.getPlats().add(this);
        return this;
    }

    public Plat removeBoisson(Boisson boisson) {
        this.boissons.remove(boisson);
        boisson.getPlats().remove(this);
        return this;
    }

    public void setBoissons(Set<Boisson> boissons) {
        this.boissons = boissons;
    }

    public Set<Commande> getCommandes() {
        return this.commandes;
    }

    public Plat commandes(Set<Commande> commandes) {
        this.setCommandes(commandes);
        return this;
    }

    public Plat addCommande(Commande commande) {
        this.commandes.add(commande);
        commande.getPlats().add(this);
        return this;
    }

    public Plat removeCommande(Commande commande) {
        this.commandes.remove(commande);
        commande.getPlats().remove(this);
        return this;
    }

    public void setCommandes(Set<Commande> commandes) {
        if (this.commandes != null) {
            this.commandes.forEach(i -> i.removePlat(this));
        }
        if (commandes != null) {
            commandes.forEach(i -> i.addPlat(this));
        }
        this.commandes = commandes;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Plat)) {
            return false;
        }
        return id != null && id.equals(((Plat) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Plat{" +
            "id=" + getId() +
            ", libelle='" + getLibelle() + "'" +
            ", prixUnitaire=" + getPrixUnitaire() +
            ", quantite=" + getQuantite() +
            "}";
    }
}
