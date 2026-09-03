
package ogrodje;

import java.util.*;

import skupno.*;

//
// Vstopni razred za igranje v besedilnem na"cinu
//
public class Besedilno {

    // stanje igre
    private Igra igra;

    public Besedilno(Igra igra) {
        this.igra = igra;
    }

    //
    // V tekstovnem na"cinu odigra celotno igro.
    //
    public void odigrajEnoIgro() {
        Scanner sc = new Scanner(System.in);
        boolean konec = false;
        Izid izid = null;

        if (!this.igra.novaIgra()) {
            return;
        }

        while (!konec) {
            System.out.println(this.igra.izpisStanja(true));

            if (this.igra.jeClovekNaPotezi()) {
                Postavitev poteza = null;

                do {
                    // bodimo prijazni do "clove"skega igralca; "ce odigra
                    // neveljavno potezo, mu to sporo"cimo in omogo"cimo
                    // ponovni vnos poteze
                    String razlaga = this.igra.vrniObrazlozitevIzida();
                    if (razlaga != null) {
                        System.out.println(razlaga);
                        System.out.println();
                    }
                    System.out.print("Vnesite potezo v obliki <lik vr st> (npr. 3 2 1): ");
                    poteza = Postavitev.preberi(sc);
                } while (!this.igra.preveriPotezo(poteza));

                izid = this.igra.uveljaviPotezo(poteza);

            } else {
                // stroj na potezi
                this.igra.sproziStoparico();
                Postavitev poteza = this.igra.postavi();
                this.igra.posodobiCas();
                if (this.igra.preveriPrekoracitevCasa()) {
                    izid = this.igra.prekoracitevCasa();
                    break;
                }
                this.igra.ustaviStoparico();
                izid = this.igra.uveljaviPotezo(poteza);
                System.out.printf("Odigrana poteza: %s%n", poteza);
            }
            konec = (izid != Izid.NI_SE_KONEC);
            System.out.println();
        }

        System.out.println(this.igra.izpisStanja(false));
        String razlaga = this.igra.vrniObrazlozitevIzida();
        if (razlaga != null) {
            System.out.println(razlaga);
        }
        String opis = izid.vrniOpis();
        if (!opis.equals(razlaga)) {
            System.out.println(opis);
        }
        System.out.println(izid);
    }

    //
    // Odigra podano "stevilo iger (samo v na"cinu stroj proti stroju).
    //
    public void odigrajNizIger(int stIger) {
        for (int ixIgra = 0; ixIgra < stIger; ixIgra++) {
            System.out.printf("Igra %d | ", ixIgra + 1);
            boolean konec = false;
            Izid izid = Izid.NI_SE_KONEC;

            if (!this.igra.novaIgra()) {
                return;
            }

            while (izid == Izid.NI_SE_KONEC) {
                this.igra.sproziStoparico();
                Postavitev poteza = this.igra.postavi();
                this.igra.posodobiCas();
                if (this.igra.preveriPrekoracitevCasa()) {
                    izid = this.igra.prekoracitevCasa();
                    break;
                }
                this.igra.ustaviStoparico();
                izid = this.igra.uveljaviPotezo(poteza);
            }

            String razlaga = this.igra.vrniObrazlozitevIzida();
            if (razlaga != null) {
                System.out.print(razlaga);
            }
            System.out.printf(" | %s | %s%n", izid, this.igra.vrniStatistiko());
        }
    }
}
