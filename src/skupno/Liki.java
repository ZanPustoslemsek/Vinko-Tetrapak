
package skupno;

import java.util.*;

//
// Ta razred deluje kot shramba vseh razpolo"zljivih likov.
//
public class Liki {

    private static List<Matrika> LIKI = List.of(
        Matrika.izNiza("++++"),     // [0]
        Matrika.izNiza("++|++"),    // [1]
        Matrika.izNiza("-+-|+++"),  // [2]
        Matrika.izNiza("+++|-+-"),  // [3]
        Matrika.izNiza("+|+|+|+"),  // [4]
        Matrika.izNiza("+--|+++"),  // [5]
        Matrika.izNiza("+++|--+"),  // [6]
        Matrika.izNiza("--+|+++"),  // [7]
        Matrika.izNiza("+++|+--"),  // [8]
        Matrika.izNiza("+-|++|+-"), // [9]
        Matrika.izNiza("-+|++|-+"), // [10]
        Matrika.izNiza("+-|+-|++"), // [11]
        Matrika.izNiza("++|-+|-+"), // [12]
        Matrika.izNiza("-+|-+|++"), // [13]
        Matrika.izNiza("++|+-|+-"), // [14]
        Matrika.izNiza("-+|++|+-"), // [15]
        Matrika.izNiza("+-|++|-+"), // [16]
        Matrika.izNiza("++-|-++"),  // [17]
        Matrika.izNiza("-++|++-")   // [18]
    );

    //
    // Vrne lik s podanim indeksom.
    //
    public static Matrika naIndeksu(int indeks) {
        return LIKI.get(indeks);
    }

    //
    // Vrne "stevilo vseh likov.
    //
    public static int stevilo() {
        return LIKI.size();
    }
}
