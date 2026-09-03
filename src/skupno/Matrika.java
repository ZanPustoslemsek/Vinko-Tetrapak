
package skupno;

import java.util.*;

//
// Objekt tega razreda predstavlja matriko z elementi tipa boolean.
//
public class Matrika {

    // "stevilo vrstic
    private int visina;

    // "stevilo stolpcev
    private int sirina;

    // elementi
    private boolean[][] elementi;

    //
    // Izdela matriko s podanimi elementi. Tabela se ne kopira.
    //
    public Matrika(boolean[][] elementi) {
        this.visina = elementi.length;
        this.sirina = elementi[0].length;
        this.elementi = elementi;
    }

    //
    // Izdela matriko, ki ima enako vi"sino, enako "sirino in iste elemente
    // kot matrika <m>. Tabela z elementi se ne kopira.
    //
    public Matrika(Matrika m) {
        this.visina = m.visina;
        this.sirina = m.sirina;
        this.elementi = m.elementi;
    }

    //
    // Ustvari in vrne matriko z <visina> x <sirina> ni"clami.
    //
    public static Matrika nicle(int visina, int sirina) {
        return new Matrika(new boolean[visina][sirina]);
    }

    //
    // Ustvari in vrne matriko z <visina> x <sirina> enicami.
    //
    public static Matrika enice(int visina, int sirina) {
        boolean[][] b = new boolean[visina][sirina];
        for (int i = 0; i < visina; i++) {
            Arrays.fill(b[i], true);
        }
        return new Matrika(b);
    }

    //
    // Ustvari in vrne kopijo matrike <this>.
    //
    public Matrika kopija() {
        boolean[][] b = new boolean[this.visina][this.sirina];
        for (int i = 0; i < this.visina; i++) {
            for (int j = 0; j < this.sirina; j++) {
                b[i][j] = this.elementi[i][j];
            }
        }
        return new Matrika(b);
    }

    //
    // Ustvari in vrne matriko na podlagi podanega niza. Znaki '+' v nizu
    // predstavljajo enice, znaki '-' ni"cle, znak '|' pa lo"cilo med
    // posameznimi vrsticami. Vsi deli niza med zaporednimi znaki '|' morajo
    // imeti enako dol"zino. Na primer, niz "+--+|-+-+|+++-" predstavlja
    // matriko [[1, 0, 0, 1], [0, 1, 0, 1], [1, 1, 1, 0]].
    //
    public static Matrika izNiza(String niz) {
        String[] vrstice = niz.split("\\|");
        int visina = vrstice.length;
        int sirina = vrstice[0].length();

        Matrika m = nicle(visina, sirina);
        for (int i = 0; i < visina; i++) {
            for (int j = 0; j < sirina; j++) {
                m.elementi[i][j] = (vrstice[i].charAt(j) == '+');
            }
        }
        return m;
    }

    //
    // Vrne "stevilo vrstic matrike <this>.
    //
    public int vrniVisino() {
        return this.visina;
    }

    //
    // Vrne "stevilo stolpcev matrike <this>.
    //
    public int vrniSirino() {
        return this.sirina;
    }

    //
    // Vrne tabelo elementov matrike <this>. Tabela se ne kopira.
    //
    public boolean[][] vrniVrednosti() {
        return this.elementi;
    }

    //
    // Vrne element v vrstici z indeksom <vr> in stolpcu z indeksom <st>.
    //
    public boolean vrni(int vr, int st) {
        return this.elementi[vr][st];
    }

    //
    // Element v vrstici z indeksom <vr> in stolpcu z indeksom <st> nastavi na
    // vrednost <kaj>.
    //
    public void nastavi(int vr, int st, boolean kaj) {
        this.elementi[vr][st] = kaj;
    }

    //
    // Vrne <true> natanko v primeru, "ce so vsi elementi matrike <this> enaki 0.
    //
    public boolean jeNicelna() {
        for (int i = 0; i < this.visina; i++) {
            for (int j = 0; j < this.sirina; j++) {
                if (this.elementi[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    //
    // Vrne <true> natanko v primeru, "ce lahko matriko <druga> ">polo"zimo"<
    // na matriko <this> na celici (vr, st), tako da
    //
    // (1) je za matriko <druga> dovolj prostora (da ne sega preko robov matrike <this>);
    // (2) pri tem dejanju vsaka enica matrike <druga> pristane na enici matrike <this>
    //
    public boolean lahkoPolozimo(Matrika druga, int vr, int st) {
        return this.lahkoSprejme(druga, vr, st) &&
            this.presek(druga, vr, st).equals(druga);
    }

    //
    // Vrne <true> natanko v primeru, "ce lahko matriko <druga> ">polo"zimo"<
    // na matriko <this> na celici (vr, st). Rezultat te funkcije je
    // odvisen samo od velikosti obeh matrik. Potrebni pogoj, da lahko matriko
    // h x w (sploh kamorkoli) polo"zimo na matriko h' x w', je h <= h' in w <=
    // w'. V tem primeru lahko matriko polo"zimo na vse celice (y, x), za
    // katere velja 0 <= y <= h' - h in 0 <= x <= w' - w.
    //
    private boolean lahkoSprejme(Matrika druga, int vr, int st) {
        return vr >= 0 && st >= 0 &&
            vr + druga.visina <= this.visina && st + druga.sirina <= this.sirina;
    }

    //
    // Vrne presek (konjunkcijo) matrike <this> in matrike <druga>, "ce
    // matriko <druga> na matriko <this> polo"zimo tako, da je zgornji levi
    // element matrike <druga> na celici (vr, st) matrike <this>.
    //
    private Matrika presek(Matrika druga, int vr, int st) {
        Matrika rezultat = nicle(druga.visina, druga.sirina);
        for (int i = 0; i < druga.visina; i++) {
            for (int j = 0; j < druga.sirina; j++) {
                rezultat.elementi[i][j] = this.elementi[vr + i][st + j] && druga.elementi[i][j];
            }
        }
        return rezultat;
    }

    //
    // Izvede operacijo <this> := <this> & ~druga (spremeni matriko <this>),
    // pri "cemer matriko <druga> polo"zi na celico (vr, st) matrike
    // <this>.
    //
    public void inNe(Matrika druga, int vr, int st) {
        for (int i = 0; i < druga.visina; i++) {
            for (int j = 0; j < druga.sirina; j++) {
                this.elementi[vr + i][st + j] &= !druga.elementi[i][j];
            }
        }
    }

    //
    // Vrne predstavitev matrike v obliki niza, pri "cemer enico predstavi z
    // znakom '+', ni"clo pa z znakom '-'.  Na primer, za matriko [[1, 0, 1],
    // [1, 1, 0]] vrne slede"ci dvovrsti"cni niz:
    // +-+
    // ++-
    //
    @Override
    public String toString() {
        return this.toString('+', '-');
    }

    //
    // Vrne predstavitev matrike v obliki niza, pri "cemer za enico uporabi
    // znak <enica>, za ni"clo pa <nicla>.
    //
    public String toString(char enica, char nicla) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < this.visina; i++) {
            for (int j = 0; j < this.sirina; j++) {
                b.append(this.elementi[i][j] ? enica : nicla);
            }
            b.append(String.format("%n"));
        }
        return b.toString();
    }

    //
    // Vrne predstavitev matrike v obliki niza, pri "cemer ji na levi in
    // zgornji strani doda o"stevi"cen ">rob"<, ki predstavlja indeks vrstice
    // oziroma stolpca. Na primer, za matriko [[1, 0, 1], [1, 1, 0]] vrne niz:
    // 
    //   012
    // 0 +-+
    // 1 ++-
    //
    public String toStringZRobom() {
        StringBuilder b = new StringBuilder();
        b.append("   ");
        if (this.sirina >= 10) {
            for (int j = 0; j < this.sirina; j++) {
                b.append((j > 0 && j % 10 == 0) ? (String.format("%d", j / 10)) : (" "));
            }
        }
        b.append(String.format("%n"));

        b.append("   ");
        for (int j = 0; j < this.sirina; j++) {
            b.append(String.format("%d", j % 10));
        }
        b.append(String.format("%n"));

        for (int i = 0; i < this.visina; i++) {
            b.append(i > 0 && i % 10 == 0 ? String.format("%3d", i) : String.format("%3d", i % 10));
            for (int j = 0; j < this.sirina; j++) {
                b.append(this.elementi[i][j] ? '+' : '-');
            }
            b.append(String.format("%n"));
        }
        return b.toString();
    }

    //
    // Vrne zgo"s"ceno vrednost matrike <this>.
    //
    @Override
    public int hashCode() {
        return 17 * this.visina + 31 * this.sirina + 43 * Arrays.deepHashCode(this.elementi);
    }

    //
    // Vrne <true> natanko v primeru, "ce je matrika <this> enaka matriki, na
    // katero ka"ze kazalec <obj>.
    //
    @Override
    public boolean equals(Object obj) {
        return this == obj || 
            obj instanceof Matrika && this.jeEnakaKot((Matrika) obj);
    }

    //
    // Vrne <true> natanko v primeru, "ce je matrika <this> enaka matriki
    // <druga>.
    //
    private boolean jeEnakaKot(Matrika druga) {
        return this.visina == druga.visina && this.sirina == druga.sirina &&
            Arrays.deepEquals(this.elementi, druga.elementi);
    }
}
