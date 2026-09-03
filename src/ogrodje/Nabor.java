
package ogrodje;

import java.util.*;
import java.util.stream.IntStream;

import skupno.*;

//
// Objekt tega razreda predstavlja trenutni nabor razpolo"zljivih likov.
//
public class Nabor {

    // frekvence[i] = "stevilo primerkov lika z indeksom i, ki je "se na voljo
    // za postavljanje na igralno polje
    private int[] frekvence;

    //
    // Izdela nabor s podanimi frekvencami posameznih likov.
    //
    public Nabor(int[] frekvence) {
        this.frekvence = frekvence;
    }

    //
    // Vrne "stevilo primerkov lika s podanim indeksom.
    //
    public int koliko(int ixLik) {
        return this.frekvence[ixLik];
    }

    //
    // "Stevilo primerkov lika s podanim indeksom zmanj"sa za 1.
    //
    public void odvzemi(int ixLik) {
        this.frekvence[ixLik]--;
    }

    //
    // Vrne tabelo frekvenc posameznih likov (rezultat[i] = "stevilo primerkov
    // lika z indeksom i). Tabele ne kopira.
    //
    public int[] vrniFrekvence() {
        return this.frekvence;
    }

    //
    // Vrne skupno "stevilo likov v naboru.
    //
    public int skupnoStevilo() {
        return Arrays.stream(this.frekvence).sum();
    }

    //
    // Vrne <true> natanko v primeru, "ce je nabor <this> prazen ("ce ni na
    // voljo ve"c nobenega lika).
    //
    public boolean jePrazen() {
        for (int ixLik = 0; ixLik < this.frekvence.length; ixLik++) {
            if (this.frekvence[ixLik] > 0) {
                return false;
            }
        }
        return true;
    }

    //
    // Vrne predstavitev nabora <this> v obliki niza 0->f[0], 1->f[1], ...,
    // kjer je f[i] "stevilo primerkov lika z indeksom i.
    //
    @Override
    public String toString() {
        return String.join(", ", IntStream.range(0, Liki.stevilo()).boxed().
                map(i -> String.format("%d->%d", i, this.frekvence[i])).toList());
    }

    //
    // Vrne ">grafi"cni"< izpis nabora <this>.
    //
    public String izpis() {
        char[][] platno = new char[20][80];
        for (int i = 0; i < platno.length; i++) {
            Arrays.fill(platno[i], ' ');
        }

        int vr = 1;
        int st = 0;
        int stPrikazanih = 0;
        int[] oNizu = new int[2];

        int visinaPlatna = 0;
        int sirinaPlatna = 0;

        for (int i = 0; i < this.frekvence.length; i++) {
            if (this.frekvence[i] > 0) {
                stPrikazanih++;
                naPlatno(String.format("%dx", this.frekvence[i]), platno, vr, st, oNizu);
                st += oNizu[1] + 1;
                naPlatno(String.format("[%d]", i), platno, vr, st, oNizu);
                int s = oNizu[1];
                naPlatno(Liki.naIndeksu(i).toString('+', ' '), platno, vr + 1, st, oNizu);
                visinaPlatna = Math.max(visinaPlatna, vr + 1 + oNizu[0]);
                st += Math.max(oNizu[1], s) + 3;
                sirinaPlatna = Math.max(sirinaPlatna, st);

                if (stPrikazanih % 7 == 0) {
                    vr = visinaPlatna + 1;
                    visinaPlatna = vr;
                    st = 0;
                }
            }
        }
        return platno2str(platno, visinaPlatna, sirinaPlatna);
    }

    //
    // Pomo"zna metoda za izdelavo ">grafi"cne"< predstavitve nabora <this>.
    // Metoda polo"zi podani niz na podano platno, in sicer na koordinati (vr,
    // st). V celici oNizu[0] in oNizu[1] vpi"se "stevilo vrstic niza in
    // dol"zino najdalj"se vrstice niza.
    //
    private static void naPlatno(String niz, char[][] platno, int vr, int st, int[] oNizu) {
        String[] vrstice = niz.split(String.format("%n"));
        oNizu[0] = vrstice.length;
        oNizu[1] = 0;
        int j = 0;
        for (String vrstica: vrstice) {
            int n = vrstica.length();
            oNizu[1] = Math.max(oNizu[1], n);
            for (int i = 0; i < n; i++) {
                platno[vr + j][st + i] = vrstica.charAt(i);
            }
            j++;
        }
    }

    //
    // Vrne niz, ki ga izdela na podlagi prvih <h> vrstic in prvih <w>
    // stolpcev podanega platna
    //
    private static String platno2str(char[][] platno, int h, int w) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                b.append(platno[i][j]);
            }
            b.append(String.format("%n"));
        }
        return b.toString();
    }
}
