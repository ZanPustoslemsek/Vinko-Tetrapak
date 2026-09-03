
package ogrodje;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import skupno.*;

//
// Objekt tega razreda hrani parametre zagona programa.
//
public class Parametri {

    //
    // Razred za izjeme, povezane z branjem parametrov
    //
    public static class ParametriException extends RuntimeException {
        public ParametriException() {
            super();
        }

        public ParametriException(String opis) {
            super(opis);
        }
    }

    // vzorec za razpoznavanje podane velikosti (<h>x<w>)
    private static final Pattern REGEX_VELIKOST = Pattern.compile("([0-9]+)x([0-9]+)");

    // minimalna in maksimalna dol"zina stranice igralnega polja
    public static final int MIN_STRANICA = 4;
    public static final int MAX_STRANICA = 20;

    // privzete vrednosti nekaterih parametrov
    private static final int PRIVZETA_VISINA = 7;  // "sirina igralnega polja
    private static final int PRIVZETA_SIRINA = 8;  // vi"sina igralnega polja
    private static final long PRIVZETI_CAS = -1;   // ni "casovne omejitve
    private static final long PRIVZETO_MEDPOTEZNO_CAKANJE = 500;  // milisekund

    // vi"sina in "sirina igralnega polja
    private int visina;
    private int sirina;

    // mno"zica indeksov likov, ki se lahko uporabijo pri tvorbi razporeditev
    private Set<Integer> dovoljeniLiki;

    // datoteka, iz katere se prebere razporeditev (null, "ce ni podana)
    private String vhodnaDatoteka;

    // datoteka, v katero se shrani razporeditev (null, "ce ni podana)
    private String izhodnaDatoteka;

    // [i] = naziv stroja z indeksom i (null, "ce gre za "clove"skega igralca)
    private String[] nazivaStrojev;

    // "casovna omejitev strojev v milisekundah (-1, "ce je ni)
    private long casovnaOmejitev;

    // datoteka, na konec katere se shrani potek partije
    private String datotekaDnevnik;

    // seme naklju"cnega generatorja za tvorbo razporeditev
    private long seme;

    // true, "ce je seme podano
    private boolean semePodano;

    // true: igra se po"zene v besedilnem na"cinu;
    // false: igra se po"zene v grafi"cnem na"cinu
    private boolean besedilniNacin;

    // "stevilo iger, ki se bodo odigrale (pri besedilnem na"cinu in igri
    // stroja proti stroja)
    private int steviloIger;

    // koliko milisekund po"cakamo po vsaki strojevi potezi (v grafi"cnem
    // na"cinu, pri igri stroja proti stroju)
    private long medpoteznoCakanje;

    //
    // Izdela objekt, ki hrani privzete vrednosti parametrov.
    //
    private Parametri() {
        this.visina = PRIVZETA_VISINA;
        this.sirina = PRIVZETA_SIRINA;
        this.casovnaOmejitev = PRIVZETI_CAS;
        this.steviloIger = 1;
        this.medpoteznoCakanje = PRIVZETO_MEDPOTEZNO_CAKANJE;

        this.nazivaStrojev = new String[2];

        // po privzetih nastavitvah so dovoljeni vsi liki
        this.dovoljeniLiki = new TreeSet<>(
                IntStream.range(0, Liki.stevilo()).boxed().toList());
    }

    //
    // Prebere parametre na podlagi argumentov ukazne vrstice.
    //
    public static Parametri izUkazneVrstice(String[] args) {
        int iArg = 0;
        boolean minusR = false;
        boolean minusRI = false;
        boolean minusM = false;
        boolean dimenzije = false;
        boolean bMedpoteznoCakanje = false;
        boolean bStIger = false;

        Parametri parametri = new Parametri();

        int ixStroja = 0;
        while (iArg < args.length) {
            if (args[iArg].equals("-?")) {
                System.out.println(pomoc());
                return null;

            } else if (args[iArg].equals("-m")) {
                minusM = true;
                String niz = args[++iArg];
                parametri.dovoljeniLiki = new TreeSet<>(
                    Arrays.stream(niz.split(",")).
                    map(Integer::parseInt).
                    toList()
                );
                int stLikov = Liki.stevilo();
                if (parametri.dovoljeniLiki.stream().anyMatch(
                            ixLik -> ixLik < 0 || ixLik >= stLikov)) {
                    throw new ParametriException(
                            String.format("Parameter -m: indeksi likov morajo biti med 0 in %d.", stLikov - 1));
                }

            } else if (args[iArg].equals("-r")) {
                minusR = true;
                parametri.vhodnaDatoteka = args[++iArg];

            } else if (args[iArg].equals("-ri")) {
                minusRI = true;
                parametri.izhodnaDatoteka = args[++iArg];

            } else if (args[iArg].equals("-1")) {
                if (parametri.nazivaStrojev[0] != null) {
                    parametri.nazivaStrojev[1] = parametri.nazivaStrojev[0];
                }
                parametri.nazivaStrojev[0] = args[++iArg];
                ixStroja = 1;

            } else if (args[iArg].equals("-2")) {
                if (parametri.nazivaStrojev[1] != null) {
                    parametri.nazivaStrojev[0] = parametri.nazivaStrojev[1];
                }
                parametri.nazivaStrojev[1] = args[++iArg];
                ixStroja = 0;

            } else if (args[iArg].equals("-t")) {
                parametri.casovnaOmejitev = obdelajCasovniParameter(args[++iArg]);

            } else if (args[iArg].equals("-c")) {
                bMedpoteznoCakanje = true;
                parametri.medpoteznoCakanje = obdelajCasovniParameter(args[++iArg]);

            } else if (args[iArg].equals("-d")) {
                parametri.datotekaDnevnik = args[++iArg];

            } else if (args[iArg].equals("-s")) {
                parametri.seme = Long.parseLong(args[++iArg]);
                parametri.semePodano = true;

            } else if (args[iArg].equals("-b")) {
                parametri.besedilniNacin = true;

            } else if (args[iArg].equals("-n")) {
                parametri.steviloIger = Integer.parseInt(args[++iArg]);
                if (parametri.steviloIger <= 0) {
                    throw new ParametriException("Število iger mora biti večje od 0.");
                }
                bStIger = true;

            } else {
                Matcher m = REGEX_VELIKOST.matcher(args[iArg]);
                if (m.matches()) {
                    dimenzije = true;
                    parametri.visina = Integer.parseInt(m.group(1));
                    parametri.sirina = Integer.parseInt(m.group(2));
                } else {
                    parametri.nazivaStrojev[ixStroja++] = args[iArg];
                }
                if (parametri.visina < MIN_STRANICA || parametri.visina > MAX_STRANICA ||
                        parametri.sirina < MIN_STRANICA || parametri.sirina > MAX_STRANICA) {
                    throw new ParametriException(
                                String.format("Višina in širina morata biti med %d in %d.",
                                    Parametri.MIN_STRANICA, Parametri.MAX_STRANICA));
                }
            }

            iArg++;
        }

        if (minusR && (dimenzije || minusM || parametri.semePodano || minusRI)) {
            throw new ParametriException("Stikalo -r se izključuje z navedbo velikosti igralnega polja ter s stikali -m, -s in -ri.");
        }
        if (bStIger && (!parametri.besedilniNacin || parametri.nazivaStrojev[0] == null || parametri.nazivaStrojev[1] == null)) {
            throw new ParametriException("Stikalo -n je smiselno le pri igri stroja proti stroju v besedilnem načinu.");
        }
        if (bMedpoteznoCakanje && (parametri.besedilniNacin || parametri.nazivaStrojev[0] == null || parametri.nazivaStrojev[1] == null)) {
            throw new ParametriException("Stikalo -c je smiselno le pri igri stroja proti stroju v grafičnem načinu.");
        }
        return parametri;
    }

    //
    // Vrne niz, ki podaja pomo"c glede uporabe parametrov.
    //
    public static String pomoc() {
        return String.format("""
        Parametri: [<h>x<w>]
                   [-m <indeksi_likov>]
                   [-s <seme>]
                   [-r <datoteka>]
                   [-ri <datoteka>]
                   [[-1] <stroj>]
                   [[-2] <stroj>]
                   [-t <čas>]
                   [-d <datoteka>]
                   [-b]
                   [-n <število_iger>]
                   [-c <čas>]
                   [-?]
            
            <h>, <w>  višina in širina igralnega polja

            -m        indeksi likov (tetromin), ki lahko nastopajo v naboru

            -r        velikost polja in nabor likov pridobi 
                      iz podane datoteke z razporeditvijo

            -ri       razporeditev se shrani v podano datoteko

            -s        seme naključnega generatorja, ki se uporablja
                      za tvorbo nabora likov

            -1, -2    stroj, ki igra kot prvi oz. drugi igralec

            -t        časovna omejitev za stroj(a)

            -d        potek partije se shrani na konec podane datoteke

            -b        besedilni način

            -n        zaporedno število partij, ki jih bosta odigrala stroja
                      (samo v besedilnem načinu)

            -c        čakalni čas po vsaki odigrani partiji (za lažje spremljanje
                      igre stroja proti stroju v grafičnem načinu)

            -?        prikaži pomoč
        """);
    }

    //
    // Vrne vi"sino igralnega polja.
    //
    public int vrniVisino() {
        return this.visina;
    }

    //
    // Vrne "sirino igralnega polja.
    //
    public int vrniSirino() {
        return this.sirina;
    }

    //
    // S pomo"cjo te metode lahko vi"sino in "sirino igralnega polja nastavimo
    // naknadno (po branju datoteke z razporeditvijo).
    //
    public void nastaviDimenzije(int visina, int sirina) {
        this.visina = visina;
        this.sirina = sirina;
    }

    //
    // Vrne <true> natanko v primeru, "ce je seme naklju"cnega generatorja
    // podano.
    //
    public boolean jeSemePodano() {
        return this.semePodano;
    }

    //
    // Vrne seme naklju"cnega generatorja.
    //
    public long vrniSeme() {
        return this.seme;
    }

    //
    // Vrne ime vhodne datoteke, ki hrani razporeditev.
    //
    public String vrniVhodnoDatoteko() {
        return this.vhodnaDatoteka;
    }

    //
    // Vrne ime datoteke, v katero se shrani razporeditev.
    //
    public String vrniIzhodnoDatoteko() {
        return this.izhodnaDatoteka;
    }

    //
    // Vrne ime datoteke, v katero naj se shrani dnevnik.
    //
    public String vrniDatotekoDnevnik() {
        return this.datotekaDnevnik;
    }

    //
    // Vrne seznam indeksov likov, ki lahko nastopajo v za"cetnem naboru.
    //
    public Set<Integer> vrniDovoljeneLike() {
        return this.dovoljeniLiki;
    }

    //
    // Vrne naziv stroja s podanim indeksom. Naziv je <null>, "ce gre za
    // "clove"skega igralca. Indeks je lahko bodisi 0 (prvi igralec) bodisi 1
    // (drugi igralec).
    //
    public String vrniNazivStroja(int indeks) {
        return this.nazivaStrojev[indeks];
    }

    //
    // Vrne "casovno omejitev.
    //
    public long vrniCasovnoOmejitev() {
        return this.casovnaOmejitev;
    }

    //
    // Vrne "cakalni "cas med potezami pri igri stroja proti stroju v
    // grafi"cnem na"cinu.
    //
    public long vrniMedpoteznoCakanje() {
        return this.medpoteznoCakanje;
    }

    //
    // Vrne <true> natanko v primeru, "ce igramo v besedilnem na"cinu.
    //
    public boolean besedilniNacin() {
        return this.besedilniNacin;
    }

    //
    // Vrne ciljno "stevilo iger.
    //
    public int vrniSteviloIger() {
        return this.steviloIger;
    }

    //
    // Vrne "cas v milisekundah, ki je zapisan v podanem nizu. Na primer, za
    // niz "123ms" vrne 123, za "5" ali "5s" pa 5000.
    //
    private static long obdelajCasovniParameter(String strCas) {
        long faktor = 1000;
        if (strCas.endsWith("ms")) {
            strCas = strCas.substring(0, strCas.length() - 2);
            faktor = 1;
        } else if (strCas.endsWith("s")) {
            strCas = strCas.substring(0, strCas.length() - 1);
        }
        int ixPika = strCas.indexOf(".");
        if (ixPika < 0) {
            ixPika = strCas.indexOf(",");
        }
        if (ixPika < 0) {
            return faktor * Long.parseLong(strCas);
        }
        String celiDel = (ixPika == 0) ? ("0") : (strCas.substring(0, ixPika));
        String decimalniDel = (ixPika == strCas.length() - 1) ? ("0") : (strCas.substring(ixPika + 1));

        int d = decimalniDel.length();
        decimalniDel = decimalniDel.substring(0, Math.min(3, d));
        if (d < 3) {
            decimalniDel += String.format("%0" + (3 - d) + "d", 0);
        }
        return faktor * Long.parseLong(celiDel) +
            faktor * Long.parseLong(decimalniDel.substring(0, Math.min(3, decimalniDel.length()))) / 1000;
    }
}
