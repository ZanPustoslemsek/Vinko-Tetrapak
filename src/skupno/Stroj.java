
package skupno;

//
// Objekt tega vmesnika predstavlja strojnega igralca.
//
public interface Stroj {

    //
    // Ta metoda se pokli"ce ob pri"cetku igre.
    //
    // prviNaPotezi: <true> natanko v primeru, "ce stroj <this> povle"ce prvo potezo
    // stVrstic, stStolpcev: vi"sina in "sirina igralnega polja (ciljnega
    //    pravokotnika)
    // frekvenceLikov[i]: "stevilo primerkov lika z indeksom <i>, ki nastopajo
    //    v razbitju igralnega polja na like
    //
    public abstract void novaIgra(boolean prviNaPotezi, int stVrstic, int stStolpcev, int[] frekvenceLikov);

    //
    // Ta metoda se pokli"ce, ko je stroj <this> na potezi. Metoda mora vrniti
    // veljavno postavitev nekega lika.
    //
    // preostaliCas: "cas (v nanosekundah), ki ga ima igralec <this> na voljo
    // do konca partije (Long.MAX_VALUE, "ce ni "casovne omejitve)
    //
    public abstract Postavitev postavi(long preostaliCas);

    //
    // Ta metoda se pokli"ce, ko potezo odigra nasprotnik stroja <this>.
    //
    // postavitev: pravkar odigrana nasprotnikova poteza
    //
    public abstract void sprejmi(Postavitev postavitev);

    //
    // Ta metoda se pokli"ce ob koncu igre.
    //
    // izid:  1, "ce je zmagal stroj <this>;
    //       -1, "ce je zmagal nasprotnik stroja <this>;
    //        0, "ce se je igra kon"cala neodlo"ceno.
    //
    public abstract void konec(int izid, String obrazlozitev);
}
