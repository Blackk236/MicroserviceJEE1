package sn.isi.m2gl.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.isi.m2gl.web.rest.TestUtil;

class CommandeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Commande.class);
        Commande commande1 = new Commande();
        commande1.setId(1L);
        Commande commande2 = new Commande();
        commande2.setId(commande1.getId());
        assertThat(commande1).isEqualTo(commande2);
        commande2.setId(2L);
        assertThat(commande1).isNotEqualTo(commande2);
        commande1.setId(null);
        assertThat(commande1).isNotEqualTo(commande2);
    }
}
