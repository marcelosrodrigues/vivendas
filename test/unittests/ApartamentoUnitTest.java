package unittests;

import models.Apartamento;
import org.junit.Test;
import play.test.UnitTest;

import java.util.List;

/**
 * Created by Marceloo on 19/08/2014.
 */
public class ApartamentoUnitTest extends UnitTest {

    @Test
    public void pesquisarApartamento() {

        List<Apartamento> apartamentos = Apartamento.findBy(null, null).list();
        assertFalse(apartamentos.isEmpty());
        assertEquals(50,apartamentos.size());

    }

}
