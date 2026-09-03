
package s12345670;

import skupno.*;
import java.util.*;

//
// Testna implementacija vmesnika Stroj
//
public class Nakljucko implements Stroj {

    // igralno polje (element matrike z vrednostjo 1 pove, da je pripadajo"ca
    // celica prosta, ni"cle pa predstavljajo zasedene celice)
    private Matrika polje;

    // frekvenceLikov[i]: "stevilo primerkov lika z indeksom i
    private int[] frekvenceLikov;

    public Nakljucko() {
    }

    //
    // Ko se igra pri"cne, inicializiram obe podatkovni strukturi.
    //
    @Override
    public void novaIgra(boolean prviNaPotezi, int stVrstic, int stStolpcev, int[] frekvenceLikov) {
        this.polje = Matrika.enice(stVrstic, stStolpcev);
        this.frekvenceLikov = frekvenceLikov;
    }

    //
    // Ko sem na potezi, izberem ">nakljucno< mo"znost na katero lahko postavim vsaj en lik,
    //
    @Override
    public Postavitev postavi(long preostaliCas) {
        int hPolje = this.polje.vrniVisino();
        int wPolje = this.polje.vrniSirino();

        ArrayList<int[]> mozni = new ArrayList<int[]>();

        for (int vr = 0; vr < hPolje; vr++) {
            for (int st = 0; st < wPolje; st++) {
                for (int ixLik = 0; ixLik < this.frekvenceLikov.length; ixLik++) {

                    // Lahko postavimo lik z indeksom ixLik na celico (vr, st)?
                    Matrika lik = Liki.naIndeksu(ixLik);
                    if (this.frekvenceLikov[ixLik] > 0 && polje.lahkoPolozimo(lik, vr, st)) {
                        mozni.add(new int[]{ixLik, vr, st});
                    }
                }
            }
        }

        Random random = new Random(27);
        int[] kandidat = mozni.get(random.nextInt(0,mozni.size()));
        // uveljavi potezo
        this.frekvenceLikov[kandidat[0]]--;
        this.polje.inNe(Liki.naIndeksu(kandidat[0]), kandidat[1], kandidat[2]);
        return new Postavitev(kandidat[0], kandidat[1], kandidat[2]);
        
    }

    //
    // Ko je nasprotnik odigral potezo, zgolj posodobim podatkovni strukturi.
    //
    @Override
    public void sprejmi(Postavitev postavitev) {
        int ixLik = postavitev.vrniIxLik();
        int vr = postavitev.vrniVr();
        int st = postavitev.vrniSt();
        this.frekvenceLikov[ixLik]--;
        this.polje.inNe(Liki.naIndeksu(ixLik), vr, st);
    }

    //
    // Ko se igra zaklju"ci, ne naredim ni"cesar.
    //
    @Override
    public void konec(int izid, String obrazlozitev) {
    }
}
