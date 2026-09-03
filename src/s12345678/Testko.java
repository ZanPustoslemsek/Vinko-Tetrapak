
package s12345678;

import java.util.*;

import skupno.*;

//
// Testna implementacija vmesnika Stroj
//
public class Testko implements Stroj {

    // igralno polje (element matrike z vrednostjo 1 pove, da je pripadajo"ca
    // celica prosta, ni"cle pa predstavljajo zasedene celice)
    private Matrika polje;

    // frekvenceLikov[i]: "stevilo primerkov lika z indeksom i
    private int[] frekvenceLikov;

    public Testko() {
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
    // Ko sem na potezi, izberem ">prvo"< mo"znost: na prvo celico (od zgoraj
    // navzdol in od leve proti desni), na katero lahko postavim vsaj en lik,
    // postavim lik z najmanj"sim indeksom.
    //
    @Override
    public Postavitev postavi(long preostaliCas) {
        int hPolje = this.polje.vrniVisino();
        int wPolje = this.polje.vrniSirino();

        for (int vr = 0; vr < hPolje; vr++) {
            for (int st = 0; st < wPolje; st++) {
                for (int ixLik = 0; ixLik < this.frekvenceLikov.length; ixLik++) {

                    // Lahko postavimo lik z indeksom ixLik na celico (vr, st)?
                    Matrika lik = Liki.naIndeksu(ixLik);
                    if (this.frekvenceLikov[ixLik] > 0 && polje.lahkoPolozimo(lik, vr, st)) {
                        // uveljavi potezo
                        this.frekvenceLikov[ixLik]--;
                        this.polje.inNe(Liki.naIndeksu(ixLik), vr, st);
                        return new Postavitev(ixLik, vr, st);
                    }
                }
            }
        }

        throw new RuntimeException("""
            Do tod ne bi smel nikoli priti. Če nimam na voljo nobene poteze,
            se ta metoda sploh ne bi smela poklicati.
        """);
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
