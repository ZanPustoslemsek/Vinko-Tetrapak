
package ogrodje;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import skupno.*;

//
// Razred za predstavitev plo"s"ce za prikaz igralnega polja in izbirnika
// likov.
//
public class SpodnjaPlosca extends JPanel {

    // "sirina in vi"sina izbirnika (v celicah)
    public static final int SIRINA_IZBIRNIKA_LEVO = 8;   // levi del
    public static final int SIRINA_IZBIRNIKA_DESNO = 9;  // desni del
    public static final int VISINA_IZBIRNIKA = 18;

    private static final Color BARVA_MREZA = new Color(80, 80, 80);
    private static final Color[] BARVA_LIK_POLNILO = {new Color(180, 60, 60), new Color(60, 160, 60)};
    private static final Color[] BARVA_OMEJEVALNI_OKVIR_LIKA = {Color.RED, Color.GREEN};

    private static final Color BARVA_LIK_POLNILO_IZBIRNIK = new Color(96, 96, 96);
    private static final Color BARVA_LIK_POLNILO_OSVETLJEN = new Color(136, 136, 104);
    private static final Color BARVA_LIK_OBROBA_OSVETLJEN = BARVA_LIK_POLNILO_OSVETLJEN.brighter();
    private static final Color BARVA_LIK_ZATEMNJEN = new Color(48, 48, 48);

    private static final Color BARVA_LIK_OBROBA = BARVA_LIK_POLNILO_IZBIRNIK.brighter();
    private static final Color BARVA_LIK_OBROBA_IZBRAN = BARVA_LIK_OBROBA_OSVETLJEN.brighter();
    private static final Color BARVA_PISAVE_IZBIRNIK = BARVA_LIK_OBROBA;

    private static final Color BARVA_OZADJA_POLJE = new Color(0, 0, 40);

    // debelina in slog obrobe izbranega lika v izbirniku
    private static final int ROB_LIKA_IZBIRNIK = 3;
    private static final Stroke OBROBA_IZBRANEGA_LIKA = new BasicStroke(ROB_LIKA_IZBIRNIK);

    private static final Color BARVA_OBROBA_ZADNJE_POTEZE = new Color(224, 224, 200);

    // slog obrobe omejevalnega okvirja izbranega (a "se ne postavljenega)
    // lika v igralnem polju
    private static final Stroke OBROBA_OMEJEVALNEGA_OKVIRJA =
        new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER,
                10.0f, new float[]{5.0f, 5.0f}, 0.0f);

    // relativni polo"zaji likov na izbirniku;
    // [0] = levi (0) ali desni (1) del izbirnika;
    // [1] = odmik (v "stevilu celic) od levega roba pripadajo"cega dela izbirnika
    // [2] = odmik (v "stevilu celic) od zgornjega roba izbirnika
    private static final int[][] POLOZAJI_LIKOV_IZBIRNIK = {
        {0, 0, 1}, {0, 6, 1}, {1, 0, 1}, {1, 4, 1}, {1, 8, 1},
        {0, 0, 5}, {0, 5, 5}, {1, 0, 5}, {1, 4, 5},
        {0, 0, 9}, {0, 3, 9}, {0, 6, 9}, {1, 0, 9}, {1, 3, 9}, {1, 6, 9},
        {0, 0, 14}, {0, 6, 14}, {1, 0, 14}, {1, 6, 14}
    };

    // to potrebujemo za komunikacijo med objekti, ki predstavljajo plo"s"ce
    private GUI gui;

    // to potrebujemo za dostop do stanja igre
    private Igra igra;

    // true, dokler se paintComponent prvi"c ne pokli"ce; potem postane false
    private boolean prvicPaintComponent;

    // dol"zina stranice celice (v pikah)
    private int stranicaCelice;

    // lega in mere igralnega polja
    private Rectangle rPolje;

    // lega in mere izbirnika
    private Rectangle rIzbirnikLevo;   // levi del
    private Rectangle rIzbirnikDesno;  // desni del

    // lege in mere posameznih likov na izbirniku
    private Rectangle[] rLik;

    // vi"sina in "sirina igralnega polja v celicah
    private int hcPolje, wcPolje;

    // indeks lika na izbirniku, ki se trenutno nahaja pod mi"sko (-1, "ce
    // ga ni)
    private int ixOsvetljeniLik;

    // indeks trenutno izbranega lika oziroma -1, "ce ga ni (lik izberemo
    // tako, da nanj kliknemo na izbirniku)
    private int ixIzbraniLik;

    // trenutni polo"zaj mi"ske
    private Point polozajMiske;

    // polo"zaj celice igralnega polja, na kateri se trenutno nahaja mi"ska
    // (samo pod pogojem, "ce je na potezi "clovek, "ce je nek lik izbran 
    // in "ce lahko izbrani lik postavimo na to celico; v nasprotnem
    // primeru je oboje -1)
    private int vrMiskaNaPolju, stMiskaNaPolju;

    // [i][j] == 0: prazno polje;
    // [i][j] == 1: del lika, ki ga je postavil prvi igralec
    // [i][j] == 2: del lika, ki ga je postavil drugi igralec
    private int[][] vsebinaPolja;

    // nazadnje odigrana strojeva poteza
    private Postavitev zadnjaPoteza;

    public SpodnjaPlosca(GUI gui, Igra igra) {
        this.gui = gui;
        this.igra = igra;

        this.hcPolje = igra.vrniVisino();
        this.wcPolje = igra.vrniSirino();
        this.vsebinaPolja = new int[this.hcPolje][this.wcPolje];

        this.setBackground(Razno.BARVA_OZADJA_SPLOSNA);
        this.prvicPaintComponent = true;
        this.rLik = new Rectangle[Liki.stevilo()];

        this.ponastavi();

        // poslu"salec za premike mi"ske
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                SpodnjaPlosca.this.premikMiske(e);
            }
        });

        // poslu"salec za mi"skine klike
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SpodnjaPlosca.this.klikMiske(e);
            }
        });

        // da lahko pritiskamo tipke za izbiro likov
        this.setFocusable(true);

        // poslu"salec za pritiske tipk
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                SpodnjaPlosca.this.pritiskTipke(e);
            }
        });
    }

    //
    // Ta metoda se pokli"ce ob vsakem za"cetku partije.
    //
    public void ponastavi() {
        for (int i = 0; i < this.hcPolje; i++) {
            Arrays.fill(this.vsebinaPolja[i], 0);
        }
        this.ixOsvetljeniLik = -1;
        this.ixIzbraniLik = -1;
        this.vrMiskaNaPolju = -1;
        this.stMiskaNaPolju = -1;
        this.zadnjaPoteza = null;

        // odigraj potezo stroja, "ce je na potezi
        // (v prvi igri se bo to zgodilo v metodi paintComponent, ko je vse
        // vzpostavljeno in prikazano)
        if (!this.prvicPaintComponent && !this.igra.jeClovekNaPotezi()) {
            this.odigrajPotezoStroja();
        }

        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Razno.nastaviAntialiasing(g);
        Graphics2D g2 = (Graphics2D) g;

        this.izracunajMere();
        g.setColor(BARVA_OZADJA_POLJE);
        g.fillRect(this.rPolje.x - this.stranicaCelice / 2, 0,
                this.rPolje.width + this.stranicaCelice, this.getHeight());
        this.narisiPolje(g2);
        this.narisiIzbirnik(g2);

        // ko se plo"s"ca prika"ze (= prvi"c nari"se), odigraj potezo
        // stroja, "ce je ta na potezi
        if (this.prvicPaintComponent) {
            this.prvicPaintComponent = false;
            if (!this.igra.jeClovekNaPotezi()) {
                this.odigrajPotezoStroja();
            }
        }
    }

    //
    // Izra"cuna vse klju"cne lege in mere. Ta metoda se pokli"ce (vsaj) po
    // vsaki spremembi velikosti plo"s"ce.
    //
    private void izracunajMere() {
        int wPlosca = this.getWidth();
        int hPlosca = this.getHeight();

        this.stranicaCelice = Math.min(
                wPlosca / this.sirinaVCelicah(),
                hPlosca / this.visinaVCelicah());

        int wPolje = this.stranicaCelice * this.igra.vrniSirino();
        int hPolje = this.stranicaCelice * this.igra.vrniVisino();

        int wIzbirnikLevo = SIRINA_IZBIRNIKA_LEVO * this.stranicaCelice;
        int wIzbirnikDesno = SIRINA_IZBIRNIKA_DESNO * this.stranicaCelice;
        int hIzbirnik = VISINA_IZBIRNIKA * this.stranicaCelice;

        int xIzbirnikLevo = (wPlosca - wPolje - wIzbirnikLevo - wIzbirnikDesno - 2 * this.stranicaCelice) / 2;
        int xPolje = xIzbirnikLevo + wIzbirnikLevo + this.stranicaCelice;
        int xIzbirnikDesno = xPolje + wPolje + this.stranicaCelice;

        int yIzbirnik = (hPlosca - hIzbirnik) / 2;
        int yPolje = (hPlosca - hPolje) / 2;

        this.rPolje = new Rectangle(xPolje, yPolje, wPolje, hPolje);
        this.rIzbirnikLevo = new Rectangle(xIzbirnikLevo, yIzbirnik, wIzbirnikLevo, hIzbirnik);
        this.rIzbirnikDesno = new Rectangle(xIzbirnikDesno, yIzbirnik, wIzbirnikDesno, hIzbirnik);
    }

    //
    // Vrne "sirino igralne plo"s"ce v celicah.
    //
    private int sirinaVCelicah() {
        return SIRINA_IZBIRNIKA_LEVO + SIRINA_IZBIRNIKA_DESNO + this.wcPolje + 4;
    }

    //
    // Vrne vi"sino igralne plo"s"ce v celicah.
    //
    private int visinaVCelicah() {
        return Math.max(VISINA_IZBIRNIKA, this.hcPolje) + 2;
    }

    //
    // Nari"se igralno polje.
    //
    private void narisiPolje(Graphics2D g) {
        // vsebina polja
        int y = this.rPolje.y;
        int x = 0;
        for (int i = 0; i < this.hcPolje; i++) {
            x = this.rPolje.x;
            for (int j = 0; j < this.wcPolje; j++) {
                if (this.vsebinaPolja[i][j] > 0) {
                    g.setColor(BARVA_LIK_POLNILO[this.vsebinaPolja[i][j] - 1]);
                    g.fillRect(x, y, this.stranicaCelice, this.stranicaCelice);
                }
                x += this.stranicaCelice;
            }
            y += this.stranicaCelice;
        }

        // mre"za
        g.setColor(BARVA_MREZA);
        y = this.rPolje.y;
        for (int i = 0; i <= this.hcPolje; i++) {
            g.drawLine(this.rPolje.x, y, this.rPolje.x + this.rPolje.width, y);
            y += this.stranicaCelice;
        }

        x = this.rPolje.x;
        for (int i = 0; i <= this.wcPolje; i++) {
            g.drawLine(x, this.rPolje.y, x, this.rPolje.y + this.rPolje.height);
            x += this.stranicaCelice;
        }

        // "ce je na potezi "clovek, "ce je izbran nek lik in "ce lahko ta lik
        // postavimo na trenutno mi"skino to"cko, prika"zemo omejevalni okvir lika
        if (this.vrMiskaNaPolju >= 0 && this.ixIzbraniLik >= 0) {
            this.narisiLik(g, Liki.naIndeksu(this.ixIzbraniLik),
                    this.rPolje.x + this.stMiskaNaPolju * this.stranicaCelice,
                    this.rPolje.y + this.vrMiskaNaPolju * this.stranicaCelice,
                    false, false, false);

            // omejevalni okvir lika
            int naPotezi = this.igra.kdoNaPotezi();
            g.setColor(BARVA_OMEJEVALNI_OKVIR_LIKA[naPotezi]);
            Matrika lik = Liki.naIndeksu(this.ixIzbraniLik);
            Stroke s = g.getStroke();
            g.setStroke(OBROBA_OMEJEVALNEGA_OKVIRJA);
            g.drawRect(this.rPolje.x + this.stMiskaNaPolju * this.stranicaCelice,
                    this.rPolje.y + this.vrMiskaNaPolju * this.stranicaCelice,
                    this.stranicaCelice * lik.vrniSirino(),
                    this.stranicaCelice * lik.vrniVisino());
            g.setStroke(s);
        }

        this.oznaciZadnjoPotezo(g);
    }

    //
    // Nari"se izbirnik.
    //
    private void narisiIzbirnik(Graphics2D g) {
        boolean clovekNaPotezi = this.igra.jeClovekNaPotezi();
        Razno.nastaviPisavo(g, Razno.PISAVA_OSNOVA_MONO, 3 * this.stranicaCelice / 5);

        // nari"se like na izbirniku
        int stLikov = Liki.stevilo();

        for (int ixLik = 0; ixLik < stLikov; ixLik++) {
            Matrika lik = Liki.naIndeksu(ixLik);
            int wLik = lik.vrniSirino();
            int hLik = lik.vrniVisino();
            int[] polozajLika = POLOZAJI_LIKOV_IZBIRNIK[ixLik];
            int x0 = (polozajLika[0] == 0) ? (this.rIzbirnikLevo.x) : (this.rIzbirnikDesno.x);
            int y0 = (polozajLika[0] == 0) ? (this.rIzbirnikLevo.y) : (this.rIzbirnikDesno.y);

            this.rLik[ixLik] = new Rectangle(
                    x0 + polozajLika[1] * this.stranicaCelice,
                    y0 + polozajLika[2] * this.stranicaCelice,
                    wLik * this.stranicaCelice,
                    hLik * this.stranicaCelice);
            int x = this.rLik[ixLik].x;
            int y = this.rLik[ixLik].y;

            boolean zatemnjen = (this.igra.kolikoPrimerkovLika(ixLik) == 0);
            this.narisiLik(g, lik, x, y, zatemnjen,
                    clovekNaPotezi && this.ixOsvetljeniLik == ixLik,
                    clovekNaPotezi && this.ixIzbraniLik == ixLik);
            if (!zatemnjen) {
                g.setColor(BARVA_PISAVE_IZBIRNIK);
                Razno.narisiNapis(g, String.format("%c", 'A' + ixLik),
                        x + wLik * this.stranicaCelice / 2, y,
                        Razno.Polozaj.SREDINA, Razno.Polozaj.SPODAJ);
                Razno.narisiNapis(g, String.format("%d", this.igra.kolikoPrimerkovLika(ixLik)),
                        x + wLik * this.stranicaCelice / 2, y + hLik * this.stranicaCelice,
                        Razno.Polozaj.SREDINA, Razno.Polozaj.ZGORAJ);
            }
        }
    }

    //
    // Podani lik nari"se na koordinatah (x, y).
    // zatemnjen == true ==> lik na izbirniku bo zatemnjen (ne bo ga mogo"ce izbrati)
    // osvetljen == true ==> na liku na izbirniku je mi"ska, zato bo
    //     osvetljen, ni pa "se izbran (nismo "se kliknili nanj)
    // izbran == true ==> lik na izbirniku je izbran (nanj smo kliknili)
    //
    private void narisiLik(Graphics2D g,
            Matrika lik, int xLik, int yLik,
            boolean zatemnjen, boolean osvetljen,
            boolean izbran) {

        int hLik = lik.vrniVisino();
        int wLik = lik.vrniSirino();

        int x = 0;
        int y = yLik;

        for (int i = 0; i < hLik; i++) {
            x = xLik;
            for (int j = 0; j < wLik; j++) {
                if (lik.vrni(i, j)) {
                    g.setColor(zatemnjen ? BARVA_LIK_ZATEMNJEN : (osvetljen ? BARVA_LIK_POLNILO_OSVETLJEN : BARVA_LIK_POLNILO_IZBIRNIK));
                    g.fillRect(x, y, this.stranicaCelice, this.stranicaCelice);
                    g.setColor(zatemnjen ? BARVA_LIK_ZATEMNJEN : (osvetljen ? BARVA_LIK_OBROBA_OSVETLJEN : BARVA_LIK_OBROBA));
                    g.drawRect(x, y, this.stranicaCelice, this.stranicaCelice);

                    if (izbran) {
                        g.setColor(BARVA_LIK_OBROBA_IZBRAN);
                        Stroke s = g.getStroke();
                        g.setStroke(OBROBA_IZBRANEGA_LIKA);
                        if (i == 0 || !lik.vrni(i - 1, j)) {
                            g.drawLine(x, y, x + this.stranicaCelice, y);
                        }
                        if (j == 0 || !lik.vrni(i, j - 1)) {
                            g.drawLine(x, y, x, y + this.stranicaCelice);
                        }
                        if (i == hLik - 1 || !lik.vrni(i + 1, j)) {
                            g.drawLine(x, y + this.stranicaCelice, x + this.stranicaCelice, y + this.stranicaCelice);
                        }
                        if (j == wLik - 1 || !lik.vrni(i, j + 1)) {
                            g.drawLine(x + this.stranicaCelice, y, x + this.stranicaCelice, y + this.stranicaCelice);
                        }
                        g.setStroke(s);
                    }
                }
                x += this.stranicaCelice;
            }
            y += this.stranicaCelice;
        }
    }

    //
    // Nari"se obrobo okrog nazadnje postavljenega lika, "ce so pogoji za to
    // izpolnjeni.
    //
    private void oznaciZadnjoPotezo(Graphics2D g) {
        if (this.zadnjaPoteza == null) {
            return;
        }

        g.setColor(BARVA_OBROBA_ZADNJE_POTEZE);
        Rectangle rcLik = this.pravokotnikLikaNaPolju(this.zadnjaPoteza);
        Matrika lik = Liki.naIndeksu(this.zadnjaPoteza.vrniIxLik());
        int hcLik = lik.vrniVisino();
        int wcLik = lik.vrniSirino();

        int y = rcLik.y;
        for (int i = 0; i < hcLik; i++) {
            int x = rcLik.x;
            for (int j = 0; j < wcLik; j++) {
                if (lik.vrni(i, j)) {
                    if (i == 0 || !lik.vrni(i - 1, j)) {
                        g.drawLine(x, y, x + this.stranicaCelice, y);
                    }
                    if (i == hcLik - 1 || !lik.vrni(i + 1, j)) {
                        g.drawLine(x, y + this.stranicaCelice, x + this.stranicaCelice, y + this.stranicaCelice);
                    }
                    if (j == 0 || !lik.vrni(i, j - 1)) {
                        g.drawLine(x, y, x, y + this.stranicaCelice);
                    }
                    if (j == wcLik - 1 || !lik.vrni(i, j + 1)) {
                        g.drawLine(x + this.stranicaCelice, y, x + this.stranicaCelice, y + this.stranicaCelice);
                    }
                }
                x += this.stranicaCelice;
            }
            y += this.stranicaCelice;
        }
    }

    //
    // To metodo pokli"cemo ob pritisku tipke, da z njo simuliramo premik
    // mi"ske na ustrezni lik na izbirniku (da se lik ozna"ci).
    //
    private void premikMiske(int x, int y) {
        this.premikMiske(new MouseEvent(this, 0, 1, 0, x, y, 0, false));
    }

    //
    // Ta metoda se pokli"ce po premiku mi"ske.
    //
    private void premikMiske(MouseEvent e) {
        this.polozajMiske = new Point(e.getX(), e.getY());

        if (this.igra.jeKonec() || !this.igra.jeClovekNaPotezi()) {
            return;
        }
        int xMiska = e.getX();
        int yMiska = e.getY();

        // Preverimo, ali se mi"ska nahaja na izbirniku. "Ce to dr"zi,
        // ugotovimo, na katerem liku se nahaja.
        int ixStari = this.ixOsvetljeniLik;
        this.ixOsvetljeniLik = this.tockaNaIzbirniku(xMiska, yMiska);

        // "ce smo osvetlili nek drug lik, osve"zimo izbirnik
        this.osveziIzbirnik(ixStari, this.ixOsvetljeniLik);

        if (this.ixIzbraniLik >= 0) {
            // preverimo, ali se mi"ska nahaja na eni od celic igralnega
            // polja in ali lahko "clove"ski igralec ("ce je na potezi) na
            // tisto to"cko postavi lik
            int vrStari = this.vrMiskaNaPolju;
            int stStari = this.stMiskaNaPolju;
            this.vrMiskaNaPolju = -1;
            this.stMiskaNaPolju = -1;

            int[] vrst = this.tockaNaPolju(xMiska, yMiska);
            Matrika lik = null;

            if (vrst != null &&
                    this.igra.jeClovekNaPotezi() && 
                    this.ixIzbraniLik >= 0 &&
                    (lik = Liki.naIndeksu(this.ixIzbraniLik)) != null &&
                    this.igra.lahkoPostavimoLik(lik, vrst[0], vrst[1])) {

                this.vrMiskaNaPolju = vrst[0];
                this.stMiskaNaPolju = vrst[1];
            }

            // osve"zimo prikaz igralnega polja (ne ve"c, kot je nujno
            // potrebno, saj je risanje na zaslon po"casno)
            this.osveziPolje(this.ixIzbraniLik, vrStari, stStari,
                    this.vrMiskaNaPolju, this.stMiskaNaPolju);
        }
    }

    //
    // Ta metoda se pokli"ce po kliku mi"ske.
    //
    private void klikMiske(MouseEvent e) {
        this.odstraniOznakoZadnjePoteze();

        if (this.igra.jeKonec() || !this.igra.jeClovekNaPotezi()) {
            return;
        }

        int ixStari = this.ixIzbraniLik;
        this.ixIzbraniLik = this.ixOsvetljeniLik;
        this.osveziIzbirnik(ixStari, this.ixIzbraniLik);

        if (this.vrMiskaNaPolju >= 0) {
            // klik mi"ske na veljavno celico igralnega polja odigra potezo
            int ixPostavljeniLik = ixStari;

            Izid izid = this.odigrajPotezo(new Postavitev(
                        ixPostavljeniLik, this.vrMiskaNaPolju, this.stMiskaNaPolju));

            this.vrMiskaNaPolju = -1;
            this.stMiskaNaPolju = -1;
            this.poPotezi(izid);
        }
    }

    //
    // Ta metoda se pokli"ce po pritisku tipke.
    //
    private void pritiskTipke(KeyEvent e) {
        this.odstraniOznakoZadnjePoteze();

        if (this.igra.jeKonec() || !this.igra.jeClovekNaPotezi()) {
            return;
        }

        int koda = e.getKeyCode();

        if (koda == KeyEvent.VK_ESCAPE) {
            this.ixOsvetljeniLik = -1;
            this.ixIzbraniLik = -1;
            if (this.polozajMiske != null) {
                // simuliramo premik mi"ske
                this.premikMiske(this.polozajMiske.x, this.polozajMiske.y);
            }
            this.repaint();

        } else if (koda >= 'A' && koda < 'A' + Liki.stevilo()) {
            int ixLik = koda - 'A';
            if (this.igra.kolikoPrimerkovLika(ixLik) > 0) {
                this.ixOsvetljeniLik = -1;
                this.ixIzbraniLik = ixLik;
                if (this.polozajMiske != null) {
                    this.premikMiske(this.polozajMiske.x, this.polozajMiske.y);
                }
                this.repaint();
            }
        }
    }

    private void odstraniOznakoZadnjePoteze() {
        if (this.zadnjaPoteza != null) {
            this.repaint(Razno.povecaniPravokotnik(
                        this.pravokotnikLikaNaPolju(this.zadnjaPoteza), 1, 1));
            this.zadnjaPoteza = null;
        }
    }

    //
    // Odigra podano potezo.
    //
    private Izid odigrajPotezo(Postavitev poteza) {
        // odigramo potezo
        int izvajalec = this.igra.kdoNaPotezi();
        Izid izid = this.igra.uveljaviPotezo(poteza);

        if (this.igra.neveljavnaPoteza()) {
            int naPotezi = this.igra.kdoNaPotezi();
            JOptionPane.showMessageDialog(
                    this.gui.vrniGlavnoPlosco(), 
                    String.format("%s igralec (%s) je odigral neveljavno potezo: %s", 
                        Igra.PRVI_DRUGI[naPotezi], this.igra.vrniIgralca(naPotezi).ime(),
                        this.igra.vrniObrazlozitevIzida()),
                    "Konec igre",
                    JOptionPane.ERROR_MESSAGE);
            this.zadnjaPoteza = null;
            return izid;
        }

        // posodobimo vsebino polja
        int ixLik = poteza.vrniIxLik();
        int vrPoteza = poteza.vrniVr();
        int stPoteza = poteza.vrniSt();

        Matrika lik = Liki.naIndeksu(ixLik);
        int hLik = lik.vrniVisino();
        int wLik = lik.vrniSirino();
        for (int i = 0; i < hLik; i++) {
            for (int j = 0; j < wLik; j++) {
                if (lik.vrni(i, j)) {
                    this.vsebinaPolja[i + vrPoteza][j + stPoteza] = izvajalec + 1;
                }
            }
        }

        // osve"zimo polje
        this.osveziPolje(ixLik, -1, -1, vrPoteza, stPoteza);

        return izid;
    }

    //
    // Ta metoda se pokli"ce po odigrani potezi. "Ce se je igra (pravkar)
    // zaklju"cila, prika"ze sporo"cilno okno. "Ce se "se ni in "ce je na
    // potezi stroj, mu naro"ci, naj odigra potezo.
    //
    private void poPotezi(Izid izid) {
        if (izid == Izid.NI_SE_KONEC) {
            if (!this.igra.jeClovekNaPotezi()) {
                this.odigrajPotezoStroja();
            }
        } else {
            String razlaga = this.igra.vrniObrazlozitevIzida();
            this.gui.vrniStatusnoPlosco().nastavi(izid.toString(), razlaga, this.igra.vrniStatistiko().toString());
            int zmagovalec = izid.zmagovalec();
            ZgornjaPlosca zgornja = this.gui.vrniZgornjoPlosco();
            if (zmagovalec < 0) {  // remi
                zgornja.sproziAnimacijo(0);
                zgornja.sproziAnimacijo(1);
            } else {
                zgornja.sproziAnimacijo(zmagovalec);
            }
        }
    }

    //
    // Ta metoda se pokli"ce, ko je na potezi stroj. Metoda v posebni niti
    // naro"ci stroju, naj odigra potezo.
    //
    private void odigrajPotezoStroja() {
        Thread nit = new Thread() {
            @Override
            public void run() {
                SpodnjaPlosca pl = SpodnjaPlosca.this;

                // med strojevim ">razmi"sljanjem"< od"stevamo razpolo"zjivi "cas
                Igralec stariAkterPoteze = pl.igra.vrniIgralca(pl.igra.kdoNaPotezi());
                pl.igra.sproziStoparico();
                Postavitev poteza = pl.igra.postavi();
                Igralec noviAkterPoteze = pl.igra.vrniIgralca(pl.igra.kdoNaPotezi());
                if (noviAkterPoteze != stariAkterPoteze) {
                    return;
                }
                pl.igra.ustaviStoparico();

                if (pl.igra.jeKonec()) {
                    // konec igre (zaradi prekora"citve "casa)
                    return;
                }
                int stevilkaIgre = pl.igra.vrniStevilkoIgre();

                if (pl.igra.strojProtiStroju()) {
                    // pred dejansko izvedbo poteze po"cakamo, da gledalec
                    // la"zje spremlja igro
                    int cakanje = (int) Tetrapak.s_parametri.vrniMedpoteznoCakanje();

                    Timer casovnik = new Timer(cakanje, (e) -> {
                        if (pl.igra.vrniStevilkoIgre() == stevilkaIgre) {
                            pl.zadnjaPoteza = poteza;
                            Izid izid = pl.odigrajPotezo(poteza);
                            pl.repaint();
                            pl.poPotezi(izid);
                        }
                    });
                    casovnik.setRepeats(false);
                    casovnik.start();

                } else {
                    // pri igri "cloveka proti "cloveku ali "cloveka proti
                    // stroju ni potrebe po "cakanju
                    SwingUtilities.invokeLater(() -> {
                        if (pl.igra.vrniStevilkoIgre() == stevilkaIgre) {
                            pl.zadnjaPoteza = poteza;
                            Izid izid = pl.odigrajPotezo(poteza);
                            pl.repaint();
                            pl.poPotezi(izid);
                        }
                    });
                }
            }
        };
        nit.start();
    }

    //
    // Po potrebi osve"zi obmo"cji izbirnika, ki pripadata liku z indeksom
    // ixLik1 in/ali liku z indeksom ixLik2. Ta metoda se pokli"ce po
    // spremembi osvetljenega ali izbranega lika.
    //
    private void osveziIzbirnik(int ixLik1, int ixLik2) {
        if (ixLik1 != ixLik2) {
            if (ixLik1 >= 0) {
                this.repaint(Razno.povecaniPravokotnik(this.rLik[ixLik1], ROB_LIKA_IZBIRNIK, this.stranicaCelice));
            }
            if (ixLik2 >= 0) {
                this.repaint(Razno.povecaniPravokotnik(this.rLik[ixLik2], ROB_LIKA_IZBIRNIK, this.stranicaCelice));
            }
        }
    }

    //
    // Po potrebi osve"zi obmo"cji igralnega polja, ki pripadata podanemu
    // liku na celici (vr1, st1) in/ali celici (vr2, st2).
    //
    private void osveziPolje(int ixLik, int vr1, int st1, int vr2, int st2) {
        if (ixLik >= 0 && (vr1 != vr2 || st1 != st2)) {
            if (vr1 >= 0) {
                this.repaint(Razno.povecaniPravokotnik(this.pravokotnikLikaNaPolju(ixLik, vr1, st1), ROB_LIKA_IZBIRNIK, ROB_LIKA_IZBIRNIK));
            }
            if (st2 >= 0) {
                this.repaint(Razno.povecaniPravokotnik(this.pravokotnikLikaNaPolju(ixLik, vr2, st2), ROB_LIKA_IZBIRNIK, ROB_LIKA_IZBIRNIK));
            }
        }
    }

    //
    // Vrne pravokotnik, ki pripada podani postavitvi lika.
    //
    private Rectangle pravokotnikLikaNaPolju(Postavitev postavitev) {
        return this.pravokotnikLikaNaPolju(postavitev.vrniIxLik(), postavitev.vrniVr(), postavitev.vrniSt());
    }

    //
    // Vrne pravokotnik, ki pripada liku s podanim indeksom, "ce ga na
    // igralnem polju postavimo na celico (vr, st).
    //
    private Rectangle pravokotnikLikaNaPolju(int ixLik, int vr, int st) {
        int x = this.rPolje.x + st * this.stranicaCelice;
        int y = this.rPolje.y + vr * this.stranicaCelice;
        Matrika lik = Liki.naIndeksu(ixLik);
        int w = lik.vrniSirino() * this.stranicaCelice;
        int h = lik.vrniVisino() * this.stranicaCelice;
        return new Rectangle(x, y, w, h);
    }

    //
    // "Ce se to"cka (x, y) nahajata na enem od likov na izbirniku, vrne
    // indeks tega lika, sicer pa vrne -1.
    //
    private int tockaNaIzbirniku(int x, int y) {
        int i = 0;
        for (Rectangle r: this.rLik) {
            if (r != null && r.contains(x, y) && this.igra.kolikoPrimerkovLika(i) > 0) {
                return i;
            }
            i++;
        }
        return -1;
    }

    //
    // "Ce se to"cka (x, y) nahaja na eni od celic igralnega polja, vrne njeni
    // koordinati (vrstica, stolpec), sicer pa vrne <null>.
    //
    private int[] tockaNaPolju(int x, int y) {
        if (!this.rPolje.contains(x, y)) {
            return null;
        }
        int vr = (y - this.rPolje.y) / this.stranicaCelice;
        int st = (x - this.rPolje.x) / this.stranicaCelice;
        return new int[]{vr, st};
    }
}
