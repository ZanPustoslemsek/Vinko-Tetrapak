
package ogrodje;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;

//
// Razred za predstavitev plo"s"ce za prikaz imen in razpolo"zljivega "casa
// igralcev.
//
public class ZgornjaPlosca extends JPanel {

    // razmerje med vi"sino glavne in zgornje plo"s"ce
    private static final int H_GLAVNA_VS_ZGORNJA = 16;

    // minimalna in maksimalna vi"sina zgornje plo"s"ce
    private static final int MIN_VISINA_ZGORNJE_PLOSCE = 25;
    private static final int MAX_VISINA_ZGORNJE_PLOSCE = 2 * MIN_VISINA_ZGORNJE_PLOSCE;

    // razmerje med "sirino celotne zgornje plo"s"ce in posamezne "casovne plo"s"ce
    private static final int W_ZGORNJA_VS_CASOVNA = 12;

    // referenca, ki omogo"ca komunikacijo z drugimi plo"s"cami
    private GUI gui;

    // podplo"s"ci, ki prikazujeta imeni igralcev
    private ImenskaPlosca[] imenskiPlosci;

    // podplo"s"ci, ki prikazujeta razpolo"zljiva "casa igralcev
    private CasovnaPlosca[] casovniPlosci;

    //
    // Razred za gumb ">Nova igra"< 
    //
    private static class Gumb extends JComponent {
        private static Color BARVA_OZADJA = Razno.BARVA_IKONA_OZADJE;
        private static Color BARVA_OZADJA_PODMISKO = BARVA_OZADJA.darker();
        private static Border ROB = BorderFactory.createRaisedBevelBorder();
        private static Border ROB_PODMISKO = BorderFactory.createLoweredBevelBorder();

        private GUI gui;
        private boolean podMisko;

        public Gumb(GUI gui) {
            this.gui = gui;
            this.podMisko = false;
            this.setToolTipText("Nova igra");
            this.setBackground(BARVA_OZADJA);
            this.setBorder(ROB);

            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    Gumb gm = Gumb.this;
                    gm.podMisko = true;
                    gm.setBackground(BARVA_OZADJA_PODMISKO);
                    gm.repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    Gumb gm = Gumb.this;
                    gm.podMisko = false;
                    gm.setBackground(BARVA_OZADJA);
                    gm.repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    Gumb.this.setBorder(ROB_PODMISKO);
                }

                // Spust mi"ske znotraj gumba spro"zi novo igro.
                @Override
                public void mouseReleased(MouseEvent e) {
                    Gumb gm = Gumb.this;
                    gm.setBorder(ROB);
                    int x = e.getX();
                    int y = e.getY();
                    if (x >= 0 && y >= 0 && x < gm.getWidth() && y < gm.getHeight()) {
                        gm.gui.novaIgra();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Color barvaOzadja = this.podMisko ? BARVA_OZADJA_PODMISKO : BARVA_OZADJA;
            Razno.narisiIkono(g, barvaOzadja, this.getWidth(), this.getHeight());
        }
    }

    public ZgornjaPlosca(GUI gui, Igra igra) {
        this.gui = gui;
        this.setLayout(null);

        this.imenskiPlosci = new ImenskaPlosca[2];
        this.casovniPlosci = new CasovnaPlosca[2];

        this.casovniPlosci[0] = new CasovnaPlosca(igra, 0);
        this.imenskiPlosci[0] = new ImenskaPlosca(igra, 0);
        JComponent gumb = new Gumb(this.gui);
        this.imenskiPlosci[1] = new ImenskaPlosca(igra, 1);
        this.casovniPlosci[1] = new CasovnaPlosca(igra, 1);

        this.add(this.casovniPlosci[0]);
        this.add(this.imenskiPlosci[0]);
        this.add(gumb);
        this.add(this.imenskiPlosci[1]);
        this.add(this.casovniPlosci[1]);

        // ko se spremeni velikost plo"s"ce this, ponovno razpostavimo
        // podplo"s"ce
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                ZgornjaPlosca z = ZgornjaPlosca.this;
                int w = z.getWidth();
                int h = z.getHeight();
                int wCas = w / W_ZGORNJA_VS_CASOVNA;
                z.casovniPlosci[0].setBounds(0, 0, wCas, h);
                z.imenskiPlosci[0].setBounds(wCas, 0, w / 2 - wCas - h / 2, h);
                gumb.setBounds(w / 2 - h / 2, 0, h, h);
                z.imenskiPlosci[1].setBounds(w / 2 + h / 2, 0, w - w / 2 - wCas - h / 2, h);
                z.casovniPlosci[1].setBounds(w - wCas, 0, wCas, h);
            }
        });
    }

    //
    // Ta metoda se pokli"ce po vsaki spro"zitvi "casovnika, v katerem
    // od"stevamo "cas.
    //
    public void posodobiPrikazCasa(boolean vVsakemPrimeru) {
        this.casovniPlosci[0].posodobiPrikazCasa(vVsakemPrimeru);
        this.casovniPlosci[1].posodobiPrikazCasa(vVsakemPrimeru);
    }

    //
    // Spro"zi animacijo imena podanega igralca. 
    //
    public void sproziAnimacijo(int ixIgralec) {
        this.imenskiPlosci[ixIgralec].sproziAnimacijo();
    }

    //
    // Ustavi animaciji imen igralcev.
    //
    public void ustaviAnimacijo() {
        this.imenskiPlosci[0].ustaviAnimacijo();
        this.imenskiPlosci[1].ustaviAnimacijo();
    }

    //
    // Vrne privzeto velikost plo"s"ce. Pomembna je samo vi"sina, ker je
    // plo"s"ca na star"sevsko plo"s"co razporejena z dolo"cilom
    // BorderLayout.NORTH.
    //
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(0,
                Math.min(MAX_VISINA_ZGORNJE_PLOSCE,
                    Math.max(MIN_VISINA_ZGORNJE_PLOSCE,
                        this.gui.vrniGlavnoPlosco().getHeight() / H_GLAVNA_VS_ZGORNJA)));
    }
}
