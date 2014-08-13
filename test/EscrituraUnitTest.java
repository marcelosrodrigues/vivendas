import models.Escritura;
import org.junit.Test;
import play.test.UnitTest;

import java.util.List;

/**
 * Created by Marceloo on 09/07/2014.
 */
public class EscrituraUnitTest extends UnitTest {


    @Test
    public void devePesquisarEscrituras() {

        List<Escritura> escrituras = Escritura.findBy(null, null , null).list();
        assertFalse(escrituras.isEmpty());
    }

    @Test
    public void devePesquisarPorNome() {
        List<Escritura> escrituras = Escritura.findBy(null, null , "MAURO").list();
        assertFalse(escrituras.isEmpty());
    }
}
