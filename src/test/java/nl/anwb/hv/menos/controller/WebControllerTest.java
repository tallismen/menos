package nl.anwb.hv.menos.controller;

import nl.anwb.hv.menos.models.NoodoproepTelefoon;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WebControllerTest {

    private WebController webController;
    private NoodoproepTelefoon noodoproepTelefoon;

    //testdata
    private final int mannr = 15;
    private final int hvnr = 285143;
    private final double lat = 53.0156;
    private final double lng = 3.13616;
    private final String adres = "Den Haag\n2244SN";

    @Before
    public void setUp() throws Exception {
        webController = new WebController();
        noodoproepTelefoon = new NoodoproepTelefoon(mannr, hvnr, lat, lng, adres);
    }

    @Test
    public void testConnectieTest() throws Exception {
        assertEquals("Testconnectie niet gelukt!", "gelukt", webController.testConnectie());
    }

    @Test
    public void noodmeldingOntvangen() throws Exception {
        assertEquals("noodmeldingOntvangen niet gelukt!", noodoproepTelefoon, webController.noodmeldingOntvangen(noodoproepTelefoon));
    }

}