
package ogrodje;

//
// Objekt tega razreda predstavlja kumulativni izid celotne ">seanse"<.
//
public class Statistika {

    // kumulativne to"cke ([0]: za prvega igralca; [1]: za drugega igralca)
    private int[] tocke;
    
    public static final int TOCKE_ZMAGA = 2;
    public static final int TOCKE_REMI = 1;
    public static final int TOCKE_PORAZ = 0;

    public static final Statistika ZMAGA_PRVEGA = new Statistika(TOCKE_ZMAGA, TOCKE_PORAZ);
    public static final Statistika ZMAGA_DRUGEGA = new Statistika(TOCKE_PORAZ, TOCKE_ZMAGA);
    public static final Statistika REMI = new Statistika(TOCKE_REMI, TOCKE_REMI);

    public Statistika() {
        this(0, 0);
    }

    private Statistika(int tockePrvega, int tockeDrugega) {
        this.tocke = new int[]{tockePrvega, tockeDrugega};
    }

    //
    // Vrne "stevilo to"ck podanega igralca.
    //
    public int vrniTocke(int ixIgralec) {
        return this.tocke[ixIgralec];
    }

    //
    // Kumulativnemu izidu <this> pri"steje podani izid.
    //
    public void dodaj(Izid izid) {
        this.tocke[0] += izid.tockeZa(0);
        this.tocke[1] += izid.tockeZa(1);
    }

    //
    // Vrne niz oblike <to"ckePrvega> : <to"ckeDrugega>.
    //
    @Override
    public String toString() {
        return String.format("%d : %d", this.tocke[0], this.tocke[1]);
    }
}
