
package ogrodje;

//
// Vstopna to"cka programa
//
public class Tetrapak {

    public static final String IME_PROGRAMA = "Tetrapak";

    // edini objekt tipa Parametri v celotnem ">sistemu"<
    public static Parametri s_parametri;

    public static void main(String[] args) {
        // preberemo argumente ukazne vrstice in inicializiramo stanje igre
        Igra igra = new Igra();
        s_parametri = igra.inicializiraj(args);

        if (s_parametri == null) {  // neveljavni argumenti ukazne vrstice
            System.exit(1);
        }

        if (s_parametri.besedilniNacin()) {
            // igra v besedilnem na"cinu
            Besedilno besedilno = new Besedilno(igra);
            int stIger = s_parametri.vrniSteviloIger();
            if (stIger <= 1) {
                besedilno.odigrajEnoIgro();
            } else {
                besedilno.odigrajNizIger(stIger);
            }

        } else {
            // igra v grafi"cnem na"cinu
            GUI gui = new GUI(igra);
            gui.pricni();
        }
    }
}
