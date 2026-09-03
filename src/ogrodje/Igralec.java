
package ogrodje;

import java.util.*;

import skupno.*;

//
// Objekt tega razreda predstavlja enega od igralcev ("clove"skega ali
// strojnega).
//
public class Igralec {

    // <null>, "ce <this> predstavlja "clove"skega, oziroma objekt tipa Stroj,
    // "ce <this> predstavlja strojnega igralca
    private Stroj stroj; 

    // "cas v nanosekundah, ki ga ima igralec na voljo do konca igre
    // (pri "clove"skih igralcih in "casovno neomejenih igrah je ta atribut
    // brezpredmeten)
    private long preostaliCas;

    // trenutek, ko smo spro"zili "stoparico
    private long casSprozitveStoparice;

    // preostali "cas ob spro"zitvi "stoparice
    // (preostaliCas = referencniPreostaliCas - (System.nanoTime() - casSprozitveStoparice))
    private long referencniPreostaliCas;

    //
    // Izdela objekt, ki predstavlja "clove"skega igralca.
    //
    public Igralec() {
        this(null, -1);
    }

    //
    // Izdela objekt, ki predstavlja strojnega igralca s pripadajo"cim
    // objektom tipa Stroj.
    //
    public Igralec(Stroj stroj, long preostaliCas) {
        this.stroj = stroj;
        this.preostaliCas = preostaliCas;
    }

    //
    // Vrne <true> natanko v primeru, "ce <this> predstavlja "clove"skega
    // igralca.
    //
    public boolean jeClovek() {
        return this.stroj == null;
    }

    //
    // "Ce <this> predstavlja "clove"skega igralca, vrne niz <"Clovek>, sicer
    // pa vrne ime razreda, ki mu pripada stroj.
    //
    public String ime() {
        return (this.jeClovek()) ? ("Človek") : (this.stroj.getClass().getSimpleName());
    }

    //
    // Vrne preostali "cas (v nanosekundah) za igralca <this>.
    //
    public long vrniPreostaliCas() {
        return this.preostaliCas;
    }

    //
    // "Ce <this> predstavlja strojnega igralca, pokli"ce metodo <novaIgra>
    // pripadajo"cega objekta tipa Stroj, sicer pa ne naredi ni"cesar.
    //
    public void novaIgra(boolean prviNaPotezi, int stVrstic, int stStolpcev, int[] frekvenceLikov) {
        if (this.stroj != null) {
            this.stroj.novaIgra(prviNaPotezi, stVrstic, stStolpcev, frekvenceLikov);
        }
    }

    //
    // "Ce <this> predstavlja strojnega igralca, pokli"ce metodo <sprejmi>
    // pripadajo"cega objekta tipa Stroj, sicer pa ne naredi ni"cesar.
    //
    public void sprejmi(Postavitev postavitev) {
        if (this.stroj != null) {
            this.stroj.sprejmi(postavitev);
        }
    }

    //
    // "Ce <this> predstavlja strojnega igralca, pokli"ce metodo <odigraj>
    // pripadajo"cega objekta tipa Stroj, sicer pa ne naredi ni"cesar. Vrne
    // rezultat klicane metode (oziroma <null>, "ce metode ne pokli"ce).
    //
    public Postavitev postavi() {
        if (this.stroj == null) {
            return null;
        }
        long omejitev = Tetrapak.s_parametri.vrniCasovnoOmejitev();
        return this.stroj.postavi(omejitev <= 0 ? Long.MAX_VALUE : this.preostaliCas);
    }

    //
    // "Ce <this> predstavlja strojnega igralca, pokli"ce metodo <konec>
    // pripadajo"cega objekta tipa Stroj, sicer pa ne naredi ni"cesar.
    //
    public void konec(int izid, String obrazlozitev) {
        if (this.stroj != null) {
            this.stroj.konec(izid, obrazlozitev);
        }
    }

    //
    // Zapomni si spro"zitve "stoparice nastavi na trenutni "cas.
    //
    public void sproziStoparico() {
        if (Tetrapak.s_parametri.vrniCasovnoOmejitev() > 0) {
            this.casSprozitveStoparice = System.nanoTime();
            this.referencniPreostaliCas = this.preostaliCas;
        }
    }

    //
    // Posodobi "cas, ki ga ima igralec <this> na voljo do konca igre.
    //
    public void posodobiCas() {
        if (Tetrapak.s_parametri.vrniCasovnoOmejitev() > 0) {
            this.preostaliCas = this.referencniPreostaliCas - (System.nanoTime() - this.casSprozitveStoparice);
        }
    }

}
