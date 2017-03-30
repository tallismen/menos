package nl.anwb.hv.menos.util;

import nl.anwb.hv.menos.models.NoodoproepTelefoon;
import org.anwb.hv.ict.oxi.disweg.GPS;
import org.anwb.hv.ict.oxi.disweg.Noodoproep;
import org.anwb.hv.ict.oxi.wam.SendRequest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NoodoproepProcesTest {
    private Noodoproep noodoproep;
    private GPS gps;
    private NoodoproepTelefoon noodoproepTelefoon;
    private SendRequest sendRequest;

    //testdata
    private final int mannr = 15;
    private final int hvnr = 285143;
    private final double lat = 53.0156;
    private final double lng = 3.13616;
    private final String adres = "Den Haag\n2244SN";

    @Before
    public void setUp() throws Exception {
        noodoproepTelefoon = new NoodoproepTelefoon(mannr, hvnr, lat, lng, adres);
    }

    @Test
    public void noodoproepTelefoonToNoodoproepTest() throws Exception {
        noodoproep = NoodoproepProces.noodoproepTelefoonToNoodoproep(noodoproepTelefoon);
        assertNotNull("Noodoproep converten niet gelukt!.", noodoproep);
        assertEquals(hvnr, noodoproep.getHvnr());
        assertEquals(true, noodoproep.isSetGPS());
    }

    @Test
    public void makeSendRequestTest() throws Exception {
        noodoproep = NoodoproepProces.noodoproepTelefoonToNoodoproep(noodoproepTelefoon);
        sendRequest = NoodoproepProces.makeSendRequest(mannr, noodoproep);
        assertNotNull("Sendmessage aanmaken niet gelukt!", sendRequest);
        assertEquals("" + mannr, sendRequest.getSender());
        assertEquals("noodoproep", sendRequest.getType());
        assertEquals(true, sendRequest.isSetBody());
    }
}