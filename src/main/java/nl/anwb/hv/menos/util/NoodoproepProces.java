package nl.anwb.hv.menos.util;

import nl.anwb.hv.menos.models.NoodoproepTelefoon;
import org.anwb.hv.ict.oxi.disweg.GPS;
import org.anwb.hv.ict.oxi.disweg.Noodoproep;
import org.anwb.hv.ict.oxi.disweg.TypeNoodoproep;
import org.anwb.hv.ict.oxi.wam.SendRequest;
import org.anwb.hv.oxi3.util.Oxi3JaxbContext;
import org.anwb.hvii.lib.util.LatLongXY;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.IOException;


/**
 * Deze class wordt geen instantie van aangemaakt maar gebruikt om code omtrent het
 * Noodoproep proces te abstraheren van de andere componenten.
 */
public class NoodoproepProces {

    private static final Logger logger = LoggerFactory.getLogger(NoodoproepProces.class);

    private NoodoproepProces() {
        logger.info("Initialiseren...");
    }

    /**
     * Maakt met behulp van een noodoproep en een mannummer het send request wat naar de WAM gaat.
     *
     * @param mannr mannummer van de wegenwacht
     * @param n     de noodoproep data
     * @return SendRequest voor de WAM
     */
    public static SendRequest makeSendRequest(int mannr, Noodoproep n) throws JAXBException, IOException {
        logger.info("makeSendRequest()");
        SendRequest request = new SendRequest();
        request.setCountOpen(false);
        request.setIsTask(true);
        request.setValidateUniqueSenderAndType(true);
        request.setSender("" + mannr);
        request.setType("noodoproep");
        // Convert to string and put it in body
        String theBody = Oxi3JaxbContext.marshall(n, true);
        request.setBody(theBody);
        logger.info(theBody);
        return request;
    }

    /**
     * Deze methode convert NoodoproepTelefoon naat Noodoproep
     *
     * @param noodoproepTelefoon data afkomstig van de telefoon
     * @return noodoproep met de data uit de telefoon.
     */
    public static Noodoproep noodoproepTelefoonToNoodoproep(NoodoproepTelefoon noodoproepTelefoon) {
        logger.info("noodoproepTelefoonToNoodoproep()");
        GPS gps = makeGPS(noodoproepTelefoon.getlat(), noodoproepTelefoon.getlng(), noodoproepTelefoon.getAdres(), noodoproepTelefoon.getHvnr());
        return makeNoodoproep(noodoproepTelefoon.getHvnr(), gps);
    }

    /**
     * Maakt een GPS object
     *
     * @param lat   latitude
     * @param lng   longtitude
     * @param adres adres string
     * @param hvnr  hulpverlenernummer
     * @return GPS object
     */
    private static GPS makeGPS(Float lat, Float lng, String adres, int hvnr) {
        logger.info("makeGPS()");
        GPS gps = new GPS();
        gps.setLat(lat);
        gps.setLong(lng);
        gps.setOmschr(adres);
        gps.setHvnr(hvnr);
        LatLongXY latLongXY = new LatLongXY(lat, lng);
        gps.setX(latLongXY.getPosX());
        gps.setY(latLongXY.getPosY());
        logger.info(gps.toString());
        return gps;
    }

    /**
     * Maakt een Noodoproep object
     *
     * @param hvnr hulpverlenernummer
     * @param gps  GPS object
     * @return Noodoproep object
     */
    private static Noodoproep makeNoodoproep(int hvnr, GPS gps) {
        logger.info("makeNoodoproep()");
        Noodoproep noodoproep = new Noodoproep();
        noodoproep.setGPS(gps);
        noodoproep.setHvnr(hvnr);
        noodoproep.setType(TypeNoodoproep.GEWOON);
        logger.info(noodoproep.toString());
        return noodoproep;
    }
}
