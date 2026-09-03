
package ogrodje;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

//
// Razred za predstavitev plo"s"ce za prikaz razpolo"zljivega "casa
//
public class CasovnaPlosca extends JPanel {

    private static final Color[] BARVA_OZADJA = {new Color(40, 0, 0), new Color(0, 40, 0)};
    private static final Color BARVA_BESEDILA = Color.YELLOW.darker();

    // stanje igre (lastnik je objekt tipa GUI)
    private Igra igra;

    // indeks igralca, na katerega se nana"sa "casovna plo"s"ca <this>
    private int ixIgralec;

    // nazadnje shranjeni preostali "cas (da ni treba ob vsaki spro"zitvi
    // "casovnika posodobiti grafi"cni prikaz)
    private long shranjeniCas;

    // slika, ki prikazuje trenutni čas (ri"se se v ozadju, saj je risanje
    // neposredno na zaslon razmeroma po"casno)
    private Image slika;

    public CasovnaPlosca(Igra igra, int ixIgralec) {
        this.igra = igra;
        this.ixIgralec = ixIgralec;
        this.shranjeniCas = this.igra.vrniPreostaliCas(this.ixIgralec);
        this.setBackground(BARVA_OZADJA[this.ixIgralec]);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // ob vsaki spremembi velikosti nari"semo sliko na novo
                CasovnaPlosca pl = CasovnaPlosca.this;
                int wPlosca = pl.getWidth();
                int hPlosca = pl.getHeight();
                pl.slika = new BufferedImage(wPlosca, hPlosca, BufferedImage.TYPE_INT_RGB);
                pl.posodobiPrikazCasa(true);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Razno.nastaviAntialiasing(g);
        g.drawImage(this.slika, 0, 0, null);
    }

    //
    // Po potrebi posodobi prikaz "casa. "Ce velja vVsakemPrimeru, potem
    // to stori v vsakem primeru, ne le takrat, ko je trenutni preostali "cas
    // razli"cen od shranjenega.
    //
    public void posodobiPrikazCasa(boolean vVsakemPrimeru) {
        if (this.slika == null) {
            return;
        }

        long noviCas = this.igra.vrniPreostaliCas(this.ixIgralec);

        if (vVsakemPrimeru || noviCas != this.shranjeniCas) {
            this.shranjeniCas = noviCas;
            String napis = "-";
            if (Tetrapak.s_parametri.vrniCasovnoOmejitev() > 0 && !this.igra.vrniIgralca(this.ixIgralec).jeClovek()) {
                napis = (noviCas < 0) ? String.format("%.1f", 0.0) : String.format("%.1f", ((double) noviCas) / 1e9);
            }

            Graphics g = this.slika.getGraphics();
            Razno.nastaviAntialiasing(g);
            int wSlika = this.slika.getWidth(null);
            int hSlika = this.slika.getHeight(null);
            g.setColor(BARVA_OZADJA[this.ixIgralec]);
            g.fillRect(0, 0, wSlika, hSlika);

            g.setColor(BARVA_BESEDILA);
            Razno.nastaviPisavo(g, Razno.PISAVA_OSNOVA_SANS, hSlika / 2);
            Razno.narisiNapisVPravokotnik(g, napis, new Rectangle(0, 0, wSlika, hSlika));
            this.repaint();
        }
    }
}
