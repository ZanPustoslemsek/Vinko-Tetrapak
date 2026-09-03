package s_vinko;

import java.util.*;
import skupno.*;

public class Vinko implements Stroj {
    private Matrika polje;
    private int[] frekvenceLikov;
    private Random random;

    private int stVsehLikov = 0;
    private long normTime = 0;

    private boolean startingRandom = true;
    private int stPotezDaIgramRandom = 500;

    public Vinko() {
        this.random = new Random(27);
    }

    @Override
    public void novaIgra(boolean prviNaPotezi, int stVrstic, int stStolpcev, int[] frekvenceLikov) {
        this.polje = Matrika.enice(stVrstic, stStolpcev);
        this.frekvenceLikov = Arrays.copyOf(frekvenceLikov, frekvenceLikov.length);
        
        this.stVsehLikov = this.preveriStLikov();
        this.normTime = -1;
        startingRandom = true;
    }

    private int preveriStLikov() {
        int stLikov = 0;
        for(int f : this.frekvenceLikov) 
            stLikov += f;
        return (stLikov+1)/2;
    }

    private double gauss(double x) {
        double std = 0.3, mean = 0.5;
        double razteg_y = 1.0, razteg_x = 0.7;
        double g = (1.0 / (razteg_y * std * Math.sqrt(2*Math.PI)) ) * 
            Math.pow(Math.E,
                (-0.5)*(razteg_x * (x - mean) * (x - mean)) / (std*std)
            );
        return g;
    }

    private double odstotekCasa(double x) {
        return gauss(x);
    }

    private void setNormTime(long celCas, int stLikov) {
        celCas = Math.min(celCas, 10000000000L) - 100L;
        this.stVsehLikov = stLikov;
        this.normTime = celCas / (long)this.stVsehLikov;
    }

    @Override
    public Postavitev postavi(long preostaliCas) {  
        VrhnoStanje vrhnoStanje = new VrhnoStanje();
        int stLikov = preveriStLikov();    
        double odstotekLikov = (double)(stLikov) / (double)this.stVsehLikov;

        if(startingRandom) {
            int stPotez = vrhnoStanje.vrniSteviloPotez(polje, frekvenceLikov);
            //System.out.printf("Imam %d moznih potez: ", stPotez);
            if(stPotez < this.stPotezDaIgramRandom) { startingRandom = false;}
            else { 
                //System.out.println("Igram random!");
                return postaviNakljucno(vrhnoStanje); 
            }            
        }

        if(this.normTime == -1)
            setNormTime(preostaliCas, stLikov);

        return postaviSCasom(vrhnoStanje, odstotekLikov, preostaliCas);
    }

    private Postavitev postaviNakljucno(VrhnoStanje vrhnoStanje) {
        Stanje nalkjucnoStanje = vrhnoStanje.vrniNakljucnoStanje(this.polje, this.frekvenceLikov, this.random);
        
        this.frekvenceLikov[nalkjucnoStanje.getIxLik()]--;
        this.polje.inNe(Liki.naIndeksu(nalkjucnoStanje.getIxLik()), nalkjucnoStanje.getVr(), nalkjucnoStanje.getSt());
        return new Postavitev(nalkjucnoStanje.getIxLik(), nalkjucnoStanje.getVr(), nalkjucnoStanje.getSt());
    }

    private Postavitev postaviSCasom(VrhnoStanje vrhnoStanje, double odstotekLikov, long preostaliCas) {
        long zacetniCas = System.nanoTime();
        double odstotekCasa = this.odstotekCasa(1 - odstotekLikov);
        long casZaPorabit = Math.min((long)(this.normTime * odstotekCasa), preostaliCas);

        int cnt = 0;
        long prejsniCas = zacetniCas;
        while(true) {
            Matrika p = polje.kopija();
            int[] f =  Arrays.copyOf(frekvenceLikov, frekvenceLikov.length);
            vrhnoStanje.pozeniStanje(p, f, this.random);

            cnt++;

            long zdajsniCas = System.nanoTime();
            long deltaCelotenCas = zdajsniCas - zacetniCas, deltaCas = zdajsniCas - prejsniCas;
            if(deltaCelotenCas + deltaCas >= casZaPorabit) {
                break;
            }
            prejsniCas = zdajsniCas;
        }
        //System.out.printf("Preiskal sem %d stanj\n", cnt);

        Stanje najbolseStanje = vrhnoStanje.vrniNajboljseStanje();
        
        this.frekvenceLikov[najbolseStanje.getIxLik()]--;
        this.polje.inNe(Liki.naIndeksu(najbolseStanje.getIxLik()), najbolseStanje.getVr(), najbolseStanje.getSt());
        return new Postavitev(najbolseStanje.getIxLik(), najbolseStanje.getVr(), najbolseStanje.getSt());
    }

    @Override
    public void sprejmi(Postavitev postavitev) {
        int ixLik = postavitev.vrniIxLik();
        int vr = postavitev.vrniVr();
        int st = postavitev.vrniSt();
        this.frekvenceLikov[ixLik]--;
        this.polje.inNe(Liki.naIndeksu(ixLik), vr, st);
    }

    @Override
    public void konec(int izid, String obrazlozitev) {
    }

/////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////

    public class Stanje {
        
        private static final double c = 0.4;

        private int ixLik, vr, st;
        private ArrayList<Stanje> stanja;

        private int n, w;

        protected Stanje() {
            this.n = 0;
            this.w = 0;
        }

        public Stanje(int ixLik, int vr, int st) {
            this();
            this.ixLik = ixLik;
            this.vr = vr;
            this.st = st;
        }

        public int getN() {
            return this.n;
        }

        public int getIxLik() {
            return this.ixLik;
        }

        public int getVr() {
            return this.vr;
        }

        public int getSt() {
            return this.st;
        }

        public int vrniSteviloPotez(final Matrika polje, final int[] frekvence) {
            if(this.stanja == null) {
                najdiSosednjaStanja(polje, frekvence);
            }

            return stanja.size();
        }

        private void najdiSosednjaStanja(final Matrika polje, final int[] frekvence) {
            int hPolje = polje.vrniVisino();
            int wPolje = polje.vrniSirino();

            this.stanja = new ArrayList<>();

            for(int k = 0; k < frekvence.length; k++) {
                if(frekvence[k] <= 0)
                    continue;

                Matrika lik = Liki.naIndeksu(k);
                for (int i = 0; i < hPolje; i++) {
                    for (int j = 0; j < wPolje; j++) {
                        if (polje.lahkoPolozimo(lik, i, j)) {
                            this.stanja.add(new Stanje(k, i, j));
                        }
                    }
                }
            }
        }

        protected void spremeniPolje(Matrika polje, int[] frekvence) {
            frekvence[this.ixLik]--;
            polje.inNe(Liki.naIndeksu(this.ixLik), this.vr, this.st);
        }

        public int pozeniStanje(Matrika polje, int[] frekvence, Random random) {
            spremeniPolje(polje, frekvence);

            if(this.stanja == null) {
                najdiSosednjaStanja(polje, frekvence);

                int val = this.pozeniNakljucnoIgro(polje, frekvence, random);
                this.w += val;
                this.n++;

                return val;
            }

            int najboljsaPoteza = -1;
            double najboljsaVrednost = -1;

            for(int i = 0; i<this.stanja.size(); i++) {
                double vrednost = this.stanja.get(i).izracunajVrednost(this.n);
                if(vrednost > najboljsaVrednost) {
                    najboljsaVrednost = vrednost;
                    najboljsaPoteza = i;
                }
            }


            int val = 0;
            if (najboljsaPoteza == -1) {
                val = oceniIgro(frekvence);
            } else {
                val = 2 - this.stanja.get(najboljsaPoteza).pozeniStanje(polje, frekvence, random);
            }

            this.w += val;
            this.n++;
            return val;
        }

        private int pozeniNakljucnoIgro(Matrika polje, int[] frekvence, Random random) {        
            int hPolje = polje.vrniVisino();
            int wPolje = polje.vrniSirino();

            int number_of_turnes = 0;
            while (true) {
                ArrayList<int[]> mozni = new ArrayList<>();

                for (int k = 0; k < frekvence.length; k++) {
                    if(frekvence[k] <= 0)
                    continue;

                    Matrika lik = Liki.naIndeksu(k);
                    for (int i = 0; i < hPolje; i++) {
                        for (int j = 0; j < wPolje; j++) {
                            if (polje.lahkoPolozimo(lik, i, j)) {
                                mozni.add(new int[] {k, i, j});
                            }
                        }
                    }
                }

                if (mozni.isEmpty()) {
                    return ( (number_of_turnes%2 == 0) ? oceniIgro(frekvence) : 2 - oceniIgro(frekvence) );
                }

                int[] kandidat = mozni.get(random.nextInt(mozni.size()));

                frekvence[kandidat[0]]--;
                polje.inNe(Liki.naIndeksu(kandidat[0]), kandidat[1], kandidat[2]);
                number_of_turnes++;
            }
        }

        public double izracunajVrednost(int par_n) {
            return this.razmerjeZmag() + 
                Stanje.c * Math.sqrt( 
                    Math.log((double)par_n + 0.00000000001) / 
                    ((double)this.n + 0.00000000001)
                );
        }

        public double razmerjeZmag() {
            return (double)this.w / ((double)this.n + 0.000001);
        }

        public Stanje vrniNajboljseStanje() {
            int najboljsaPoteza = -1;
            double najboljsaVrednost = -1;
            int najboljsiN = -1;

            for(int i = 0; i<this.stanja.size(); i++) {
                double vrednost = this.stanja.get(i).razmerjeZmag();
                int curN = this.stanja.get(i).getN();

                if(curN > najboljsiN || curN == najboljsiN && vrednost > najboljsaVrednost) {
                    najboljsaVrednost = vrednost;
                    najboljsaPoteza = i;
                    najboljsiN = curN;
                }
            }
            
            return this.stanja.get(najboljsaPoteza);
        }

        public Stanje vrniNakljucnoStanje(Matrika polje, int[] frekvence, Random random) {
            if(this.stanja == null)
                this.najdiSosednjaStanja(polje, frekvence);
            return this.stanja.get(random.nextInt(this.stanja.size()));
        }

        private int oceniIgro(int[] frekvence) {
            for(int k = 0; k<frekvence.length; k++) {
                if(frekvence[k] != 0) {
                    return 2;
                }
            }
            return 1;
        }

    }

    public class VrhnoStanje extends Stanje {
        @Override
        protected void spremeniPolje(Matrika polje, int[] frekvence) {
        }
    }

}
