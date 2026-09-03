
package ogrodje;

//
// Na"stevni razred, ki predstavlja izid partije.
//
public enum Izid {
    ZMAGA_PRVEGA(1, Statistika.ZMAGA_PRVEGA, "Zmagal je prvi igralec."),
    ZMAGA_DRUGEGA(-1, Statistika.ZMAGA_DRUGEGA, "Zmagal je drugi igralec."),
    REMI(0, Statistika.REMI, "Igra se je končala neodločeno."),
    NI_SE_KONEC(0, null, "Igre še ni konec.");

    // oznaka izida, ki se uporablja pri strojevi metodi <konec>
    private int oznaka;

    // "stevilo to"ck, ki jih prvi in drugi igralec pridobita pri izidu <this>
    private Statistika statistika;

    // opis izida
    private String opis;

    private Izid(int oznaka, Statistika statistika, String opis) {
        this.oznaka = oznaka;
        this.statistika = statistika;
        this.opis = opis;
    }

    //
    // Vrne oznako izida <this> z vidika podanega igralca.
    //
    public int oznakaZa(int ixIgralec) {
        return (ixIgralec == 0) ? (this.oznaka) : (-this.oznaka);
    }

    //
    // Vrne "stevilo to"ck, ki jih podani igralec pridobi v primeru izida
    // <this>.
    //
    public int tockeZa(int ixIgralec) {
        return this.statistika.vrniTocke(ixIgralec);
    }

    //
    // Vrne opis izida <this>.
    //
    public String vrniOpis() {
        return this.opis;
    }

    //
    // Vrne zmagovalca (0 ali 1) partije, ki ga predstavlja izid <this>. V
    // primeru remija vrne -1.
    //
    public int zmagovalec() {
        return switch (this.oznaka) {
            case 1 -> 0;     //:
            case -1 -> 1;    //:
            default -> -1;
        };
    }

    //
    // Vrne izid, ki predstavlja zmago podanega igralca.
    //
    public static Izid zmaga(int ixIgralec) {
        return (ixIgralec == 0) ? (ZMAGA_PRVEGA) : (ZMAGA_DRUGEGA);
    }

    //
    // Vrne niz oblike <tockePrvega> : <tockeDrugega>.
    //
    @Override
    public String toString() {
        return this.statistika.toString();
    }
}
