package sn.isi.m2gl.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.isi.m2gl.web.rest.TestUtil;

class PlatTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Plat.class);
        Plat plat1 = new Plat();
        plat1.setId(1L);
        Plat plat2 = new Plat();
        plat2.setId(plat1.getId());
        assertThat(plat1).isEqualTo(plat2);
        plat2.setId(2L);
        assertThat(plat1).isNotEqualTo(plat2);
        plat1.setId(null);
        assertThat(plat1).isNotEqualTo(plat2);
    }
}
