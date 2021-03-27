package sn.isi.m2gl.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.isi.m2gl.web.rest.TestUtil;

class BoissonTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Boisson.class);
        Boisson boisson1 = new Boisson();
        boisson1.setId(1L);
        Boisson boisson2 = new Boisson();
        boisson2.setId(boisson1.getId());
        assertThat(boisson1).isEqualTo(boisson2);
        boisson2.setId(2L);
        assertThat(boisson1).isNotEqualTo(boisson2);
        boisson1.setId(null);
        assertThat(boisson1).isNotEqualTo(boisson2);
    }
}
