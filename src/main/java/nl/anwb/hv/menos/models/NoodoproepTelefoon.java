package nl.anwb.hv.menos.models;

/**
 * In deze class staat de data die van de telefoon
 * afkomstig is.
 */
public class NoodoproepTelefoon {

    private int mannr;          //Man nummer
    private int hvnr;           //Hulpverlener nummer
    private float lat;         //Latitude
    private float lng;        //Longitude
    private String adres;       //Adres van de noodmelding

    /**
     * Nodig om de noodoproeptTelefoon aan te maken en later te vullen met JSON data
     * Misschien niet nodig
     * TODO: kijken of deze nog nodig is referentie ServerComm
     */
    public NoodoproepTelefoon() {
    }

    public NoodoproepTelefoon(int mannr, int hvnr, double lat, double lng, String adres) {
        this.mannr = mannr;
        this.hvnr = hvnr;
        this.lat = (float) lat;
        this.lng = (float) lng;
        this.adres = adres;
    }

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }

    public int getHvnr() {
        return hvnr;
    }

    public void setHvnr(int hvnr) {
        this.hvnr = hvnr;
    }

    public int getmannr() {
        return mannr;
    }

    public void setmannr(int mannr) {
        this.mannr = mannr;
    }

    public float getlat() {
        return lat;
    }

    public void setlat(float lat) {
        this.lat = lat;
    }

    public float getlng() {
        return lng;
    }

    public void setlng(float lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "NoodoproepTelefoon{" +
                "mannr=" + mannr +
                "hvnr=" + hvnr +
                ", lat=" + lat +
                ", lng=" + lng +
                ". adres=" + adres +
                "}";
    }
}