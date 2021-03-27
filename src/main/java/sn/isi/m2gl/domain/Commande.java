package sn.isi.m2gl.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Commande.
 */
@Entity
@Table(name = "commande")
public class Commande implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "numero", nullable = false, unique = true)
    private String numero;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @ManyToMany
    @JoinTable(
        name = "rel_commande__plat",
        joinColumns = @JoinColumn(name = "commande_id"),
        inverseJoinColumns = @JoinColumn(name = "plat_id")
    )
    @JsonIgnoreProperties(value = { "boissons", "commandes" }, allowSetters = true)
    private Set<Plat> plats = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Commande id(Long id) {
        this.id = id;
        return this;
    }

    public String getNumero() {
        return this.numero;
    }

    public Commande numero(String numero) {
        this.numero = numero;
        return this;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public Commande date(LocalDate date) {
        this.date = date;
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Set<Plat> getPlats() {
        return this.plats;
    }

    public Commande plats(Set<Plat> plats) {
        this.setPlats(plats);
        return this;
    }

    public Commande addPlat(Plat plat) {
        this.plats.add(plat);
        plat.getCommandes().add(this);
        return this;
    }

    public Commande removePlat(Plat plat) {
        this.plats.remove(plat);
        plat.getCommandes().remove(this);
        return this;
    }

    public void setPlats(Set<Plat> plats) {
        this.plats = plats;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Commande)) {
            return false;
        }
        return id != null && id.equals(((Commande) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Commande{" +
            "id=" + getId() +
            ", numero='" + getNumero() + "'" +
            ", date='" + getDate() + "'" +
            "}";
    }
}
