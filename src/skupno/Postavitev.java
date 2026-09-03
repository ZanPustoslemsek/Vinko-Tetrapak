
package skupno;

import java.util.*;

//
// Objekt tega razreda predstavlja postavitev dolo"cenega lika na dolo"cen
// polo"zaj na igralnem polju.
//
public class Postavitev {

    // indeks lika
    private int ixLik;

    // koordinati celice polja, na katero postavimo lik
    private int vr;
    private int st;

    //
    // Izdela objekt, ki predstavlja postavitev lika z indeksom <ixLik> na
    // celico (vr, st).
    //
    public Postavitev(int ixLik, int vr, int st) {
        this.ixLik = ixLik;
        this.vr = vr;
        this.st = st;
    }

    //
    // Vrne indeks lika, na katerega se nana"sa postavitev <this>.
    //
    public int vrniIxLik() {
        return this.ixLik;
    }

    //
    // Vrne indeks vrstice celice, na katero se nana"sa postavitev <this>.
    //
    public int vrniVr() {
        return this.vr;
    }

    //
    // Vrne indeks stolpca celice, na katero se nana"sa postavitev <this>.
    //
    public int vrniSt() {
        return this.st;
    }

    //
    // Izdela in vrne postavitev, ki jo prebere s pomo"cjo podanega bralnika.
    // Na vhodu pri"cakujemo zaporedje <ixLik> <vr> <st>.
    //
    public static Postavitev preberi(Scanner sc) {
        int ixLik = sc.nextInt();
        int vr = sc.nextInt();
        int st = sc.nextInt();
        return new Postavitev(ixLik, vr, st);
    }

    //
    // Vrne predstavitev postavitve <this> v obliki niza.
    //
    @Override
    public String toString() {
        return String.format("%d -> (%d, %d)", this.ixLik, this.vr, this.st);
    }
}
