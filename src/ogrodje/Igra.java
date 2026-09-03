
package ogrodje;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.*;

import skupno.*;

//
// Objekt tega razreda hrani stanje igre.
//
public class Igra {

    private static final String LOCILNA_CRTA = String.join("", Collections.nCopies(42, "-"));
    public static final String[] PRVI_DRUGI = {"Prvi", "Drugi"};
    public static final String[] PRVI_DRUGI_MALO = {"prvi", "drugi"};

    // oba igralca (tabela dveh elementov)
    private Igralec[] igralca;

    // generator naklju"cnih "stevil, ki se uporablja pri tvorbi ciljne
    // razporeditve likov
    private Random generatorRazporeditve;

    // ciljna razporeditev likov na igralnem polju
    private Razporeditev razporeditev;

    // igralno polje (POZOR: enica predstavlja prosto, ni"cla pa zasedeno celico!)
    private Matrika polje;

    // nabor likov, ki so "se na voljo
    private Nabor nabor;

    // igralec na potezi (0: prvi; 1: drugi)
    private int naPotezi;

    // true, "ce je igre konec; false, "ce "se poteka
    private boolean konecIgre;

    // true: pravkar odigrana poteza je bila neveljavna
    private boolean neveljavnaPoteza;

    // zaporedna "stevilka igre (uporabljamo jo za to, da po prekinitvi igre
    // ne odigramo poteze, ki jo je stroj pravkar izbral)
    private int stevilkaIgre;

    // niz, ki podaja razlog za izid partije (npr. neveljavna poteza,
    // prekora"citev "casa ...)
    private String obrazlozitevIzida;

    // true, "ce "stoparica, s katero se od"steva preostali "cas igralca na
    // potezi, trenutno te"ce
    private boolean stoparicaTece;

    // trenutek, ko se je partija pri"cela
    private LocalDateTime trenutekZacetka;

    // kumulativni rezultat
    private Statistika statistika;

    // razred za predstavitev posamezne poteze (za zapis v dnevnik)
    private static class Poteza {
        int stevilka;    // zaporedna "stevilka poteze (1, 1, 2, 2, 3, 3, ...)
        int izvajalec;   // kdo je odigral potezo (0 ali 1)
        LocalDateTime trenutekIzvedbe;    // kdaj je bila poteza odigrana
        Postavitev postavitev;    // poteza v o"zjem smislu (indeks lika -> ciljno polje)

        Poteza(int stevilka, int izvajalec, LocalDateTime trenutekIzvedbe, Postavitev postavitev) {
            this.stevilka = stevilka;
            this.izvajalec = izvajalec;
            this.trenutekIzvedbe = trenutekIzvedbe;
            this.postavitev = postavitev;
        }

        @Override
        public String toString() {
            return String.format("%d | %d | %s | %s", 
                    this.stevilka, this.izvajalec, this.trenutekIzvedbe, this.postavitev);
        }
    }

    // seznam odigranih potez
    private List<Poteza> poteze;

    //
    // Prebere parametre in inicializira stanje igre. "Ce je vse v redu, vrne
    // prebrane parametre (kot objekt tipa Parametri), sicer pa vrne <null>.
    //
    public Parametri inicializiraj(String[] args) {
        // preberi parametre na podlagi argumentov ukazne vrstice
        Parametri parametri = null;

        try {
            parametri = Parametri.izUkazneVrstice(args);

        } catch (Parametri.ParametriException ex) {
            System.err.println(ex.getMessage());
            System.err.println();
            System.err.println("Za prikaz pomoči uporabite stikalo -?.");
            return null;

        } catch (RuntimeException ex) {
            ex.printStackTrace(System.err);
            System.err.println();
            System.err.println("Za prikaz pomoči uporabite stikalo -?.");
            return null;
        }

        if (parametri == null) {
            return null;
        }

        // generator razporeditve ("ce je ne preberemo iz datoteke)
        this.generatorRazporeditve = parametri.jeSemePodano() ?
            new Random(parametri.vrniSeme()) : new Random();

        // razporeditev preberemo iz vhodne datoteke, "ce je podana
        // (sicer jo bomo v metodi novaIgra tvorili naklju"cno)
        String vdat = parametri.vrniVhodnoDatoteko();
        if (vdat != null) {
            this.razporeditev = Razporeditev.izDatoteke(vdat);
            if (this.razporeditev == null) {
                return null;
            }
            parametri.nastaviDimenzije(this.razporeditev.vrniVisino(), this.razporeditev.vrniSirino());
        }

        // kumulativen rezultat (za celotno ">seanso"<)
        this.statistika = new Statistika();

        // seznam potez za dnevnik
        this.poteze = new ArrayList<>();

        this.igralca = new Igralec[2];
        this.stevilkaIgre = 0;

        return parametri;
    }

    //
    // Vrne vi"sino igralnega polja.
    //
    public int vrniVisino() {
        return this.razporeditev.vrniVisino();
    }

    //
    // Vrne "sirino igralnega polja.
    //
    public int vrniSirino() {
        return this.razporeditev.vrniSirino();
    }

    //
    // Vrne indeks igralca na potezi.
    //
    public int kdoNaPotezi() {
        return this.naPotezi;
    }

    //
    // Vrne preostalo "stevilo primerkov lika s podanim indeksom.
    //
    public int kolikoPrimerkovLika(int ixLik) {
        return this.nabor.koliko(ixLik);
    }

    //
    // Vrne <true> natanko v primeru, "ce je igre "ze konec.
    //
    public boolean jeKonec() {
        return this.konecIgre;
    }

    //
    // Vrne <true> natanko v primeru, ko lahko na celico (vr, st) igralnega
    // polja postavimo podani lik.
    //
    public boolean lahkoPostavimoLik(Matrika lik, int vr, int st) {
        return this.polje.lahkoPolozimo(lik, vr, st);
    }

    //
    // Vrne igralca s podanim indeksom.
    //
    public Igralec vrniIgralca(int ixIgralec) {
        return this.igralca[ixIgralec];
    }

    //
    // Vrne true natanko v primeru, "ce je pravkar odigrana poteza neveljavna.
    //
    public boolean neveljavnaPoteza() {
        return this.neveljavnaPoteza;
    }

    //
    // Vrne obrazlo"zitev izida (npr. zakaj je bila pravkar odigrana poteza
    // neveljavna).
    //
    public String vrniObrazlozitevIzida() {
        return this.obrazlozitevIzida;
    }

    //
    // Vrne objekt, ki hrani kumulativni izid ">seanse"<.
    //
    public Statistika vrniStatistiko() {
        return this.statistika;
    }

    //
    // Vrne zaporedno "stevilko trenutne igre.
    //
    public int vrniStevilkoIgre() {
        return this.stevilkaIgre;
    }

    //
    // Inicializira podatkovne strukture, vezane na posamezno partijo, in
    // obvesti oba igralca, da se je pri"cela nova partija. Vrne <false>,
    // "ce je pri tem pri"slo do kak"snih napak.
    // 
    //
    public boolean novaIgra() {
        // vsakokrat na za"cetku ustvarimo objekta strojev (na ta na"cin
        // ">pozabimo"< morebitno zgodovino, npr. potezo, ki se ni odigrala
        // zaradi prekora"citve "casovne omejitve) in nastavimo preostali "cas
        if (!this.inicializirajIgralca()) {
            return false;
        }

        this.stevilkaIgre++;

        Parametri parametri = Tetrapak.s_parametri;
        if (parametri.vrniVhodnoDatoteko() == null) {
            // razporeditev izdelamo naklju"cno
            int h = parametri.vrniVisino();
            int w = parametri.vrniSirino();
            this.razporeditev = Razporeditev.nakljucna(
                    h, w, parametri.vrniDovoljeneLike(), this.generatorRazporeditve);

            // razporeditev shranimo v izhodno datoteko, "ce je podana
            String idat = parametri.vrniIzhodnoDatoteko();
            if (idat != null) {
                this.razporeditev.vDatoteko(idat);
            }
        }

        int hPolje = this.razporeditev.vrniVisino();
        int wPolje = this.razporeditev.vrniSirino();

        // inicializiramo igralno polje in nabor razpolo"zljivih likov
        this.polje = Matrika.enice(hPolje, wPolje);
        this.nabor = this.razporeditev.nabor();

        // obvestimo igralca, da se je pri"cela nova igra
        int[] frek = this.nabor.vrniFrekvence();
        this.igralca[0].novaIgra(true, hPolje, wPolje, Arrays.copyOf(frek, frek.length));
        this.igralca[1].novaIgra(false, hPolje, wPolje, Arrays.copyOf(frek, frek.length));

        this.naPotezi = 0;
        this.konecIgre = false;
        this.stoparicaTece = false;
        this.obrazlozitevIzida = null;
        this.neveljavnaPoteza = false;

        this.poteze.clear();
        this.trenutekZacetka = LocalDateTime.now();

        return true;
    }

    //
    // Za vsakega strojnega igralca izdela pripadajo"ci objekt in inicializira
    // preostali "cas.
    //
    private boolean inicializirajIgralca() {
        Parametri parametri = Tetrapak.s_parametri;

        for (int i = 0; i < 2; i++) {
            if (parametri.vrniNazivStroja(i) == null) {
                this.igralca[i] = new Igralec();
            } else {
                String imeRazreda = "";
                try {
                    imeRazreda = parametri.vrniNazivStroja(i);
                    Class<?> c = Class.forName(imeRazreda);
                    this.igralca[i] = new Igralec(
                        (Stroj) c.getDeclaredConstructor().newInstance(),
                        parametri.vrniCasovnoOmejitev() * 1_000_000
                    );
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
                    System.err.printf("Ne morem ustvariti primerka razreda %s.%n", imeRazreda);
                    return false;
                }
            }
        }
        return true;
    }


    //
    // Vrne niz, ki na ">uporabniku prijazen"< na"cin podaja trenutno stanje
    // igre (nabor razpolo"zljivih likov + vsebina polja).
    //
    public String izpisStanja(boolean izpisPoteze) {
        StringBuilder sb = new StringBuilder(String.format("%s%n", LOCILNA_CRTA));
        if (izpisPoteze) {
            sb.append(String.format("Na potezi je %s igralec (%s).%n",
                        PRVI_DRUGI_MALO[this.naPotezi],
                        this.igralca[this.naPotezi].ime()));
            if (Tetrapak.s_parametri.vrniCasovnoOmejitev() > 0 &&
                    !this.igralca[this.naPotezi].jeClovek()) {
                double cas = ((double) this.vrniPreostaliCas(this.naPotezi)) / 1e9;
                sb.append(String.format("Preostali čas: %.3f s%n", cas));
            }
            sb.append(String.format("%s%n", LOCILNA_CRTA));
        }
        sb.append(String.format("%s%n%s%n", this.nabor.izpis(), this.polje.toStringZRobom()));
        return sb.toString();
    }

    //
    // Vrne true natanko v primeru, "ce je na potezi "clove"ski igralec.
    //
    public boolean jeClovekNaPotezi() {
        return this.igralca[naPotezi].jeClovek();
    }

    //
    // Vrne true natanko v primeru, ko igrata stroja drug proti drugemu.
    //
    public boolean strojProtiStroju() {
        return !this.igralca[0].jeClovek() && !this.igralca[1].jeClovek();
    }

    //
    // Vrne preostali "cas (v nanosekundah) za podanega igralca.
    //
    public long vrniPreostaliCas(int ixIgralec) {
        return this.igralca[ixIgralec].vrniPreostaliCas();
    }

    //
    // Naro"ci stroju, naj odigra potezo, in opravi vse potrebne nadaljnje aktivnosti.
    //
    public Postavitev postavi() {
        Postavitev postavitev = this.igralca[this.naPotezi].postavi();
        if (postavitev == null) {
            throw new RuntimeException("Igra::postavi: nekaj močno smrdi!");
        }
        return postavitev;
    }

    //
    // Odigra podano ("clovekovo ali ra"cunalnikovo) potezo. "Ce je partija s
    // tem zaklju"cena, vrne njen izid, sicer pa vrne Izid.NI_SE_KONEC.
    //
    public Izid uveljaviPotezo(Postavitev postavitev) {
        this.poteze.add(new Poteza(
                    this.poteze.size() / 2 + 1, this.naPotezi,
                    LocalDateTime.now(), postavitev));

        if (!this.preveriPotezo(postavitev)) {
            // neveljavna poteza --- nasprotnik takoj zmaga!
            Izid izid = Izid.zmaga(1 - this.naPotezi);
            this.konec(izid);
            return izid;
        }

        // nasprotnik bo sprejel potezo
        this.igralca[1 - this.naPotezi].sprejmi(postavitev);

        int ixLik = postavitev.vrniIxLik();
        int vr = postavitev.vrniVr();
        int st = postavitev.vrniSt();

        // posodobi igralno polje in nabor likov
        this.polje.inNe(Liki.naIndeksu(ixLik), vr, st);
        this.nabor.odvzemi(ixLik);

        // Preveri, ali je igra zaklju"cena. To se zgodi, ko na igralno polje
        // ni mogo"ce postaviti ve"c nobenega lika. V tem primeru se igra
        // takoj zaklju"ci. "Ce sta igralca porabila vse like, je rezultat
        // neodlo"cen, v nasprotnem primeru pa zmaga igralec, ki je pravkar
        // odigral potezo.
        if (this.poljeZasedeno()) {
            Izid izid = (this.nabor.jePrazen()) ? (Izid.REMI) : (Izid.zmaga(this.naPotezi));
            this.obrazlozitevIzida = izid.vrniOpis();
            this.konec(izid);
            return izid;
        }

        // predaj potezo nasprotniku
        this.naPotezi = 1 - this.naPotezi;

        return Izid.NI_SE_KONEC;
    }

    //
    // Ta metoda se pokli"ce, "ce igralec na potezi prekora"ci "cas. Metoda
    // ustrezno zaklju"ci partijo in vrne objekt, ki predstavlja zmago
    // nasprotnika.
    //
    public Izid prekoracitevCasa() {
        Izid izid = Izid.zmaga(1 - this.naPotezi);
        this.obrazlozitevIzida = String.format("%s igralec je prekoračil časovno omejitev.", PRVI_DRUGI[this.naPotezi]);
        this.konec(izid);
        return izid;
    }

    //
    // Preveri veljavnost poteze. "Ce je neveljavna, v this.obrazlozitevIzida
    // vpi"se razlog za neveljavnost poteze. Vrne <true> natanko v primeru,
    // "ce je poteza veljavna.
    //
    public boolean preveriPotezo(Postavitev postavitev) {
        int ixLik = postavitev.vrniIxLik();
        int vr = postavitev.vrniVr();
        int st = postavitev.vrniSt();

        int stLikov = Liki.stevilo();
        int visina = this.polje.vrniVisino();
        int sirina = this.polje.vrniSirino();

        this.neveljavnaPoteza = false;

        if (ixLik < 0 || ixLik >= stLikov) {
            this.neveljavnaPoteza = true;
            this.obrazlozitevIzida = String.format("Indeks lika mora biti med 0 in %d.", stLikov - 1);
            return false;
        }
        if (vr < 0 || vr >= visina) {
            this.neveljavnaPoteza = true;
            this.obrazlozitevIzida = String.format("Indeks vrstice mora biti med 0 in %d.", visina - 1);
            return false;
        }
        if (st < 0 || st >= sirina) {
            this.neveljavnaPoteza = true;
            this.obrazlozitevIzida = String.format("Indeks stolpca mora biti med 0 in %d.", sirina - 1);
            return false;
        }
        if (this.nabor.koliko(ixLik) <= 0) {
            this.neveljavnaPoteza = true;
            this.obrazlozitevIzida = String.format("Igralno polje ne vsebuje (več) nobenega primerka lika %d.", ixLik);
            return false;
        }

        Matrika lik = Liki.naIndeksu(ixLik);
        if (!this.polje.lahkoPolozimo(lik, vr, st)) {
            this.neveljavnaPoteza = true;
            this.obrazlozitevIzida = String.format("Lika %d ni mogoče postaviti na koordinati (%d, %d).", ixLik, vr, st);
            return false;
        }

        this.obrazlozitevIzida = null;
        return true;
    }

    //
    // Vrne <true> natanko v primeru, "ce na igralno polje ni mogo"ce
    // postaviti nobenega od morebitnih preostalih likov.
    //
    private boolean poljeZasedeno() {
        int visina = this.polje.vrniVisino();
        int sirina = this.polje.vrniSirino();

        boolean vsiLikiPorabljeni = true;

        for (int ixLik = 0; ixLik < Liki.stevilo(); ixLik++) {
            if (this.nabor.koliko(ixLik) > 0) {
                vsiLikiPorabljeni = false;
                Matrika lik = Liki.naIndeksu(ixLik);
                for (int vr = 0; vr < visina; vr++) {
                    for (int st = 0; st < sirina; st++) {
                        if (this.polje.lahkoPolozimo(lik, vr, st)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    //
    // Pri"cne z od"stevanjem "casa za igralca na potezi.
    //
    public void sproziStoparico() {
        this.stoparicaTece = true;
        this.igralca[this.naPotezi].sproziStoparico();
    }

    //
    // Posodobi in vrne preostali "cas igralca na potezi.
    //
    public long posodobiCas() {
        if (this.stoparicaTece) {
            this.igralca[this.naPotezi].posodobiCas();
        }
        return this.igralca[this.naPotezi].vrniPreostaliCas();
    }

    //
    // Vrne <true> natanko v primeru, "ce je igralec na potezi prekora"cil
    // "casovno omejitev.
    //
    public boolean preveriPrekoracitevCasa() {
        return
            Tetrapak.s_parametri.vrniCasovnoOmejitev() > 0 &&
            !this.igralca[this.naPotezi].jeClovek() &&
            this.stoparicaTece &&
            this.igralca[this.naPotezi].vrniPreostaliCas() < 0;
    }

    //
    // Ustavi "stoparico, ki od"steva razpolo"zljivi "cas.
    //
    public void ustaviStoparico() {
        this.igralca[this.naPotezi].posodobiCas();
        this.stoparicaTece = false;
    }

    //
    // Zaklju"ci igro s podanim izidom.
    //
    public void konec(Izid izid) {
        this.konecIgre = true;
        this.statistika.dodaj(izid);
        this.zapisiVDnevnik(izid);
        String obrazlozitev = (this.obrazlozitevIzida == null) ? ("") : (this.obrazlozitevIzida);
        this.igralca[0].konec(izid.oznakaZa(0), obrazlozitev);
        this.igralca[1].konec(izid.oznakaZa(1), obrazlozitev);
    }

    //
    // Zapi"se potek partije (s podanim izidom) v dnevnik.
    //
    private void zapisiVDnevnik(Izid izid) {
        String datotekaDnevnik = Tetrapak.s_parametri.vrniDatotekoDnevnik();
        if (datotekaDnevnik == null) {
            return;
        }
        try (Writer pisec = new FileWriter(datotekaDnevnik, true)) {
            pisec.write(String.format("%s%n", LOCILNA_CRTA));
            pisec.write(String.format("Višina: %d%n", this.polje.vrniVisino()));
            pisec.write(String.format("Širina: %d%n", this.polje.vrniSirino()));
            pisec.write(String.format("Ciljna razporeditev: %s%n", this.razporeditev.vrniPostavitve()));
            pisec.write(String.format("Prvi: %s%n", this.igralca[0].ime()));
            pisec.write(String.format("Drugi: %s%n", this.igralca[1].ime()));
            pisec.write(String.format("Začetek ob: %s%n", this.trenutekZacetka));
            for (Poteza poteza: this.poteze) {
                pisec.write(String.format("%s%n", poteza));
            }
            pisec.write(String.format("Izid partije: %s%n", izid));
            if (this.obrazlozitevIzida != null) {
                pisec.write(String.format("Pojasnilo: %s%n", this.obrazlozitevIzida));
            }
            pisec.write(String.format("Skupni izid: %s%n", this.statistika));
            pisec.write(String.format("%s%n", LOCILNA_CRTA));
        } catch (IOException ex) {
            System.err.printf("Napaka pri pisanju v datoteko %s.%n", datotekaDnevnik);
        }
    }
}
