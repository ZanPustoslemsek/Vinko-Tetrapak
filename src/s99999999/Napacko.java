
package s99999999;

import java.util.*;

import skupno.*;

//
// Vsako potezo izberem povsem naklju"cno, zato bo veliko mojih potez
// neveljavnih.
//
public class Napacko implements Stroj {

    private Random random;
    private int stVrstic, stStolpcev;

    public Napacko() {
        this.random = new Random();
    }

    @Override
    public void novaIgra(boolean prviNaPotezi, int stVrstic, int stStolpcev, int[] frekvenceLikov) {
        this.stVrstic = stVrstic;
        this.stStolpcev = stStolpcev;
    }

    @Override
    public Postavitev postavi(long preostaliCas) {
        return new Postavitev(
                this.random.nextInt(Liki.stevilo()),
                this.random.nextInt(this.stVrstic),
                this.random.nextInt(this.stStolpcev));
    }

    @Override
    public void sprejmi(Postavitev postavitev) {
    }

    @Override
    public void konec(int izid, String obrazlozitev) {
    }
}
