
package ogrodje;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import skupno.*;

//
// Objekt tega razreda predstavlja razporeditev likov na igralnem polju.
//
public class Razporeditev {

    // "stevilo iteracij postopka za izdelavo naklju"cne razporeditve
    private static final int NAKLJUCNA_RAZPOREDITEV_ST_ITERACIJ = 10;

    // vi"sina in "sirina igralnega polja
    private int visina;
    private int sirina;

    // postavitve posameznih likov
    private List<Postavitev> postavitve;

    //
    // Izdela razporeditev likov na polju velikosti <visina> x <sirina>, ki
    // jo dolo"ca podani seznam postavitev.
    //
    public Razporeditev(int visina, int sirina, List<Postavitev> postavitve) {
        this.visina = visina;
        this.sirina = sirina;
        this.postavitve = postavitve;
    }

    //
    // Vrne vi"sino igralnega polja, na katero se nana"sa razporeditev <this>.
    //
    public int vrniVisino() {
        return this.visina;
    }

    //
    // Vrne "sirino igralnega polja, na katero se nana"sa razporeditev <this>.
    //
    public int vrniSirino() {
        return this.sirina;
    }

    //
    // Vrne seznam postavitev likov, na katerega se nana"sa razporeditev <this>.
    //
    public List<Postavitev> vrniPostavitve() {
        return this.postavitve;
    }

    //
    // Vrne razporeditev, ki jo prebere iz podane datoteke. Datoteka se mora
    // skladati s slede"cim formatom:
    //
    // <visina>x<sirina>
    // <ixLik1> <vr1> <st1>
    // <ixLik2> <vr2> <st2>
    // ...
    //
    public static Razporeditev izDatoteke(String datoteka) {
        int stevilkaVrstice = 0;

        try (Scanner sc = new Scanner(new File(datoteka))) {
            boolean prva = true;
            Matrika polje = null;
            int visina = 0;
            int sirina = 0;
            List<Postavitev> postavitve = new ArrayList<>();

            while (sc.hasNextLine()) {
                stevilkaVrstice++;
                String vrstica = sc.nextLine().trim();
                if (vrstica.length() == 0 || vrstica.startsWith("#")) {
                    continue;
                }

                if (prva) {
                    String[] hw = vrstica.split("x");
                    visina = Integer.parseInt(hw[0].trim());
                    sirina = Integer.parseInt(hw[1].trim());
                    if (visina < Parametri.MIN_STRANICA || sirina < Parametri.MIN_STRANICA || 
                            visina > Parametri.MAX_STRANICA || sirina > Parametri.MAX_STRANICA) {
                        throw new RuntimeException(
                                String.format("Višina in širina morata biti med %d in %d.",
                                    Parametri.MIN_STRANICA, Parametri.MAX_STRANICA));
                    }
                    prva = false;
                    polje = Matrika.enice(visina, sirina);

                } else {
                    String[] lvs = vrstica.split("\\s+");

                    int ixLik = Integer.parseInt(lvs[0]);
                    if (ixLik < 0 || ixLik >= Liki.stevilo()) {
                        throw new RuntimeException(
                                String.format("Neveljaven indeks lika: %d", ixLik));
                    }

                    int vr = Integer.parseInt(lvs[1]);
                    int st = Integer.parseInt(lvs[2]);
                    if (vr < 0 || st < 0 || vr >= visina || st >= sirina) {
                        throw new RuntimeException(
                                String.format("Neveljavni koordinati: (%d, %d)", vr, st));
                    }

                    // "ce se trenutni lik prekriva s katerim od obstoje"cih,
                    // je postavitev neveljavna
                    Matrika lik = Liki.naIndeksu(ixLik);
                    if (polje.lahkoPolozimo(lik, vr, st)) {
                        polje.inNe(lik, vr, st);
                        postavitve.add(new Postavitev(ixLik, vr, st));
                    } else {
                        throw new RuntimeException(
                                String.format("Lika %d ni mogoče postaviti na točko (%d, %d).", ixLik, vr, st));
                    }
                }
            }

            if (postavitve.isEmpty()) {
                throw new RuntimeException("Razporeditev nima nobenega lika.");
            }

            return new Razporeditev(visina, sirina, postavitve);

        } catch (FileNotFoundException ex) {
            System.err.printf("Ne najdem datoteke %s%n", datoteka);
        } catch (RuntimeException ex) {
            System.err.printf("Napaka pri branju datoteke %s (vrstica %d):%n", datoteka, stevilkaVrstice);
            System.err.println(ex.getMessage());
        }
        return null;
    }

    //
    // Ustvari nabor, ki pripada razporeditvi <this>. (Nabor je objekt, ki
    // hrani tabelo frekvenc posameznih likov.)
    //
    public Nabor nabor() {
        int stLikov = Liki.stevilo();
        int[] frekvenca = new int[stLikov];
        for (Postavitev p: this.postavitve) {
            frekvenca[p.vrniIxLik()]++;
        }
        return new Nabor(frekvenca);
    }

    //
    // Razporeditev <this> shrani v podano datoteko. V prvo vrstico zapi"se
    // velikost polja (v obliki <visina>x<sirina>), v naslednje vrstice pa
    // postavitve posameznih likov.
    //
    public void vDatoteko(String datoteka) {
        try (Writer wr = new FileWriter(new File(datoteka))) {
            wr.write(String.format("%dx%d%n", this.visina, this.sirina));
            for (Postavitev postavitev: this.postavitve) {
                int ixLik = postavitev.vrniIxLik();
                int vr = postavitev.vrniVr();
                int st = postavitev.vrniSt();
                wr.write(String.format("%d %d %d%n", ixLik, vr, st));
            }
        } catch (IOException ex) {
            System.err.printf("Napaka pri pisanju v datoteko %s", datoteka);
        }
    }

    //
    // Ustvari naklju"cno razporeditev likov z indeksi iz seznama
    // <dovoljeniLiki> za polje velikosti <visina> x <sirina>. Kot vir
    // naklju"cnosti uporabi objekt <generator>.
    //
    public static Razporeditev nakljucna(int visina, int sirina,
            Collection<Integer> dovoljeniLiki, Random generator) {

        Razporeditev najRazporeditev = null;
        int najStevilo = -1;

        // postopek ponovimo NAKLJUCNA_RAZPOREDITEV_ST_ITERACIJ-krat in
        // izberemo razporeditev z najve"cjim "stevilom likov (z najmanj
        // ">luknjami"<)

        for (int iter = 0; iter < NAKLJUCNA_RAZPOREDITEV_ST_ITERACIJ; iter++) {
            Matrika polje = Matrika.enice(visina, sirina);
            List<Postavitev> vsePostavitve = new ArrayList<>();

            for (int vr = 0; vr < visina; vr++) {
                for (int st = 0; st < sirina; st++) {
                    // za vsak lik poi"s"ci prvo mo"zno postavitev v trenutni
                    // vrstici
                    List<Postavitev> moznePostavitve = moznePostavitve(polje, dovoljeniLiki, vr, st);
                    int stPostavitev = moznePostavitve.size();
                    if (stPostavitev > 0) {
                        // simuliramo postavitev lika na celico (vr, st)
                        Postavitev postavitev = moznePostavitve.get(
                                generator.nextInt(stPostavitev));
                        polje.inNe(Liki.naIndeksu(postavitev.vrniIxLik()),
                                postavitev.vrniVr(), postavitev.vrniSt());
                        vsePostavitve.add(postavitev);
                    }
                }
            }

            // Smo v trenutni razporeditvi porabili ve"c likov kot v doslej
            // najbolj"si?
            Razporeditev razporeditev = new Razporeditev(visina, sirina, vsePostavitve);
            int stevilo = razporeditev.nabor().skupnoStevilo();
            if (stevilo > najStevilo) {
                najRazporeditev = razporeditev;
                najStevilo = stevilo;
            }
        }
        return najRazporeditev;
    }

    //
    // Za vsak lik iz zbirke <dovoljeniLiki> poi"s"ce prvo mo"zno postavitev v
    // vrstici <vr> od stolpca <st> naprej. Vrne seznam tak"snih postavitev.
    //
    private static List<Postavitev> moznePostavitve(
            Matrika polje, Collection<Integer> dovoljeniLiki, int vr, int st) {

        List<Postavitev> rezultat = new ArrayList<>();
        int wPolje = polje.vrniSirino();
        for (int ixLik: dovoljeniLiki) {
            Matrika lik = Liki.naIndeksu(ixLik);
            for (int j = st; j < wPolje; j++) {
                if (polje.lahkoPolozimo(lik, vr, j)) {
                    rezultat.add(new Postavitev(ixLik, vr, j));
                    break;
                }
            }
        }
        return rezultat;
    }
}
