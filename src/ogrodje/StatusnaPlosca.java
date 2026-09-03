
package ogrodje;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

//
// Razred za predstavitev plo"s"ce, ki prikazuje izid pravkar odigrane igre in
// njegovo obrazlo"zitev ter skupni izid ">seanse"<.
//
public class StatusnaPlosca extends JPanel {

    private static final Color BARVA_LEVO_DESNO = new Color(64, 0, 96);
    private static final Color BARVA_SREDINA = new Color(48, 0, 64);
    private static final Color BARVA_BESEDILA = Color.LIGHT_GRAY;
    private static final Font OSNOVNA_PISAVA = Razno.PISAVA_OSNOVA_SANS;
    private static final int VELIKOST_PISAVE = 16;

    // napis na levem, sredinskem in desnem delu statusne plo"s"ce
    private String napisLevo;
    private String napisSredina;
    private String napisDesno;

    public StatusnaPlosca() {
        this.setBackground(BARVA_LEVO_DESNO);
        this.ponastavi("0 : 0");
    }

    //
    // Osve"zi plo"s"co, pri "cemer levi in sredinski napis nastavi na prazen
    // niz, desni napis pa na podani niz.
    //
    public void ponastavi(String napisDesno) {
        this.napisLevo = "";
        this.napisSredina = "";
        this.napisDesno = napisDesno;
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Razno.nastaviAntialiasing(g);
        Graphics2D g2 = (Graphics2D) g;

        Razno.nastaviPisavo(g, OSNOVNA_PISAVA, VELIKOST_PISAVE);
        FontMetrics fm = g.getFontMetrics();

        int wPlosca = this.getWidth();
        int hPlosca = this.getHeight();

        int wLevoDesno = fm.stringWidth("999 : 999");
        Rectangle rLevo = new Rectangle(0, 0, wLevoDesno, hPlosca);
        Rectangle rDesno = new Rectangle(wPlosca - wLevoDesno, 0, wLevoDesno, hPlosca);
        Rectangle rSredina = new Rectangle(wLevoDesno, 0, wPlosca - 2 * wLevoDesno, hPlosca);

        g.setColor(BARVA_LEVO_DESNO);
        g2.fill(rLevo);
        g2.fill(rDesno);
        g.setColor(BARVA_SREDINA);
        g2.fill(rSredina);

        g.setColor(BARVA_BESEDILA);
        Razno.narisiNapisVPravokotnik(g, this.napisLevo, rLevo);
        Razno.narisiNapisVPravokotnik(g, this.napisSredina, rSredina);
        Razno.narisiNapisVPravokotnik(g, this.napisDesno, rDesno);
    }

    //
    // Vrne privzeto velikost plo"s"ce. Pomembna je samo vi"sina, ker je
    // plo"s"ca na star"sevsko plo"s"co razporejena z dolo"cilom
    // BorderLayout.SOUTH.
    //
    @Override
    public Dimension getPreferredSize() {
        Graphics g = this.getGraphics();
        Razno.nastaviPisavo(g, OSNOVNA_PISAVA, VELIKOST_PISAVE);
        return new Dimension(0, 3 * g.getFontMetrics().getHeight() / 2);
    }

    //
    // Nastavi vse tri napise in osve"zi plo"s"co. Vrednost <null> pomeni, da
    // se napis ne osve"zi (ostane tak, kot je bil).
    //
    public void nastavi(String napisLevo, String napisSredina, String napisDesno) {
        if (napisLevo != null) {
            this.napisLevo = napisLevo;
        }
        if (napisSredina != null) {
            this.napisSredina = napisSredina;
        }
        if (napisDesno != null) {
            this.napisDesno = napisDesno;
        }
        this.repaint();
    }
}
