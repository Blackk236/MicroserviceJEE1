package sn.isi.m2gl.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Boisson.
 */
@Entity
@Table(name = "boisson")
public class Boisson implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "libelle", nullable = false, unique = true)
    private String libelle;

    @ManyToMany(mappedBy = "boissons")
    @JsonIgnoreProperties(value = { "boissons", "commandes" }, allowSetters = true)
    private Set<Plat> plats = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boisson id(Long id) {
        this.id = id;
        return this;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public Boisson libelle(String libelle) {
        this.libelle = libelle;
        return this;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Set<Plat> getPlats() {
        return this.plats;
    }

    public Boisson plats(Set<Plat> plats) {
        this.setPlats(plats);
        return this;
    }

    public Boisson addPlat(Plat plat) {
        this.plats.add(plat);
        plat.getBoissons().add(this);
        return this;
    }

    public Boisson removePlat(Plat plat) {
        this.plats.remove(plat);
        plat.getBoissons().remove(this);
        return this;
    }

    public void setPlats(Set<Plat> plats) {
        if (this.plats != null) {
            this.plats.forEach(i -> i.removeBoisson(this));
        }
        if (plats != null) {
            plats.forEach(i -> i.addBoisson(this));
        }
        this.plats = plats;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Boisson)) {
            return false;
        }
        return id != null && id.equals(((Boisson) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Boisson{" +
            "id=" + getId() +
            ", libelle='" + getLibelle() + "'" +
            "}";
    }
}
