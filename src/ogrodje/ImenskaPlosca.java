
package ogrodje;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.Timer;

//
// Razred za predstavitev plo"s"ce, ki prikazuje ime igralca. V primeru zmage
// ali remija se izvede preprosta animacija imena.
//
public class ImenskaPlosca extends JPanel {
    private static final Color[] BARVA_OZADJA = {new Color(64, 0, 0), new Color(0, 64, 0)};
    private static final Color BARVA_BESEDILA = Color.LIGHT_GRAY;

    // interval pro"zenja animacijskega "casovnika (v milisekundah)
    private static int INTERVAL_ANIMACIJSKEGA_CASOVNIKA = 50;

    // razmerje med vi"sino plo"s"ce <this> in maksimalnim odmikom animacije v
    // smeri y
    private static double ANIMACIJA_MAX_REL_Y_ODMIK = 4.0;

    // koeficient k pri Math.sin(k * x) je Math.PI / ANIMACIJA_KOEFICIENT_SINUSA
    private static double ANIMACIJA_KOEFICIENT_SINUSA = 8.0;

    // indeks igralca, na katerega se nana"sa imenska plo"s"ca <this>
    private int ixIgralec;

    // stanje igre (">lastnik"< je objekt razreda GUI)
    private Igra igra;

    // slika, ki jo prikazuje plo"s"ca <this> (predvsem pri animaciji je
    // pomembno, da ri"semo na sliko, ki obstaja samo v pomnilniku, ne
    // neposredno na zaslon)
    private Image slika;

    // "casovnik za izvedbo animacije
    private Timer casovnik;

    // true natanko tedaj, ko izvajamo animacijo
    private boolean animacija;

    // "stevec, ki se uporablja pri animaciji
    private int stevec;

    public ImenskaPlosca(Igra igra, int ixIgralec) {
        this.igra = igra;
        this.ixIgralec = ixIgralec;

        this.setBackground(BARVA_OZADJA[ixIgralec]);
        this.animacija = false;

        // "casovnik, v katerem se bo izvajala animacija
        this.casovnik = new Timer(INTERVAL_ANIMACIJSKEGA_CASOVNIKA, (e) -> {
            ImenskaPlosca pl = ImenskaPlosca.this;
            pl.narisiSliko();
            pl.stevec++;
            pl.repaint();
        });

        // ob vsaki spremembi velikosti se slika nari"se na novo
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                ImenskaPlosca.this.narisiSliko();
                ImenskaPlosca.this.repaint();
            }
        });
    }

    //
    // Nari"se sliko, ki prikazuje ime (bodisi mirujo"ce bodisi v fazi
    // animacije).
    //
    private void narisiSliko() {
        int wPlosca = this.getWidth();
        int hPlosca = this.getHeight();
        this.slika = new BufferedImage(wPlosca, hPlosca, BufferedImage.TYPE_INT_RGB);

        Graphics g = this.slika.getGraphics();
        Razno.nastaviAntialiasing(g);

        g.setColor(BARVA_OZADJA[ixIgralec]);
        g.fillRect(0, 0, wPlosca, hPlosca);

        g.setColor(BARVA_BESEDILA);
        String niz = this.igra.vrniIgralca(ixIgralec).ime();
        Razno.nastaviPisavo(g, Razno.PISAVA_OSNOVA_SANS, hPlosca / 2);
        if (!this.animacija) {
            Razno.narisiNapisVPravokotnik(g, niz, new Rectangle(0, 0, wPlosca, hPlosca));
            return;
        }

        FontMetrics fm = g.getFontMetrics();
        int wNiz = fm.stringWidth(niz);
        int hNiz = fm.getHeight();
        int xNiz = (wPlosca - wNiz) / 2;
        int yNiz = (hPlosca + hNiz) / 2;

        int x = xNiz;
        int dolzinaNiza = niz.length();
        double maxOdmik = hPlosca / ANIMACIJA_MAX_REL_Y_ODMIK;

        for (int i = 0; i < dolzinaNiza; i++) {
            String znak = Character.toString(niz.charAt(i));
            double yOdmik = maxOdmik * Math.sin((this.stevec + i) * Math.PI / ANIMACIJA_KOEFICIENT_SINUSA);
            g.drawString(znak, x, (int) Math.round(yNiz + yOdmik));
            x += fm.stringWidth(znak);
        }
    }

    //
    // Spro"zi animacijo napisa.
    //
    public void sproziAnimacijo() { 
        this.animacija = true;
        this.stevec = 0;
        this.casovnik.start();
    }

    //
    // Ustavi animacijo napisa.
    //
    public void ustaviAnimacijo() { 
        this.animacija = false;
        this.casovnik.stop();
        this.narisiSliko();
        this.repaint();
    }

    //
    // Sliko prika"ze na zaslonu.
    //
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Razno.nastaviAntialiasing(g);
        g.drawImage(this.slika, 0, 0, null);
    }
}
