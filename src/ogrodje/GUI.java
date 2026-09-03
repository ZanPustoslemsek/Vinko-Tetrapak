
package ogrodje;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;

import skupno.*;

//
// Vstopni razred za igranje v grafi"cnem na"cinu
//
public class GUI {
    // dol"zina stranica celice, na podlagi katere se izra"cuna za"cetna
    // velikost okna
    private static final int PRIVZETA_STRANICA_CELICE = 36;

    // predvidena skupna vi"sina naslovne vrstice, imenske plo"s"ce in
    // statusne plo"s"ce (to uporabimo za izra"cun za"cetne vi"sine okna)
    private static final int H_DODATEK = 80;

    private static final int SIRINA_IKONE = 64;
    private static final int VISINA_IKONE = SIRINA_IKONE;

    // interval "casovnika (v milisekundah), v okviru katerega se preverja
    // prekora"citev "casovne omejitve
    private static final int INTERVAL_CASOVNIKA_STOPARICE = 50;

    // stanje igre (<this> je ">lastnik"< tega objekta)
    private Igra igra;

    // plo"s"ce
    private JPanel glavnaPlosca;            // vse skupaj
    private ZgornjaPlosca zgornjaPlosca;    // ime + "cas
    private SpodnjaPlosca spodnjaPlosca;    // igralno polje + izbirnik
    private StatusnaPlosca statusnaPlosca;  // prikaz rezultata

    public GUI(Igra igra) {
        this.igra = igra;
    }

    public JPanel vrniGlavnoPlosco() {
        return this.glavnaPlosca;
    }

    public ZgornjaPlosca vrniZgornjoPlosco() {
        return this.zgornjaPlosca;
    }

    public StatusnaPlosca vrniStatusnoPlosco() {
        return this.statusnaPlosca;
    }

    //
    // Inicializira igro ter izdela in prika"ze grafi"cni vmesnik.
    //
    public void pricni() {
        if (!this.igra.novaIgra()) {
            return;
        }

        SwingUtilities.invokeLater(() -> {  // po"cakaj na dogodkovno nit
            // izdelaj okno
            JFrame okno = new JFrame(Tetrapak.IME_PROGRAMA);
            okno.setBackground(Razno.BARVA_OZADJA_SPLOSNA);
            okno.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            Image ikona = new BufferedImage(SIRINA_IKONE, VISINA_IKONE, BufferedImage.TYPE_INT_RGB);
            Razno.narisiIkono(ikona.getGraphics(), Razno.BARVA_IKONA_OZADJE, SIRINA_IKONE, VISINA_IKONE);
            okno.setIconImage(ikona);

            // velikost okna izra"cunamo na podlagi privzete dol"zine stranice
            // celice, a upo"stevamo, da okno ne more biti ve"cje od zaslona
            Dimension velikostZaslona = Toolkit.getDefaultToolkit().getScreenSize();
            Parametri parametri = Tetrapak.s_parametri;
            int w = SpodnjaPlosca.SIRINA_IZBIRNIKA_LEVO + SpodnjaPlosca.SIRINA_IZBIRNIKA_DESNO + parametri.vrniSirino() + 4;
            int h = Math.max(SpodnjaPlosca.VISINA_IZBIRNIKA, parametri.vrniVisino()) + 2;
            int wOkno = Math.min(velikostZaslona.width, w * PRIVZETA_STRANICA_CELICE);
            int hOkno = Math.min(velikostZaslona.height, h * PRIVZETA_STRANICA_CELICE + H_DODATEK);
            int xOkno = (velikostZaslona.width - wOkno) / 2;
            int yOkno = (velikostZaslona.height - hOkno) / 2;
            okno.setBounds(xOkno, yOkno, wOkno, hOkno);

            // glavna plo"s"ca
            this.glavnaPlosca = new JPanel();
            okno.add(this.glavnaPlosca);
            this.glavnaPlosca.setLayout(new BorderLayout());

            // plo"s"ca, ki prikazuje imeni igralcev in preostali "cas
            GUI.this.zgornjaPlosca = new ZgornjaPlosca(GUI.this, GUI.this.igra);

            // plo"s"ca, ki prikazuje igralno polje in izbirnik likov
            GUI.this.spodnjaPlosca = new SpodnjaPlosca(GUI.this, GUI.this.igra);

            // plo"s"ca, ki prikazuje izid partije in kumulativni izid
            // ">seanse"<
            GUI.this.statusnaPlosca = new StatusnaPlosca();

            this.glavnaPlosca.add(GUI.this.zgornjaPlosca, BorderLayout.NORTH);
            this.glavnaPlosca.add(GUI.this.spodnjaPlosca, BorderLayout.CENTER);
            this.glavnaPlosca.add(GUI.this.statusnaPlosca, BorderLayout.SOUTH);

            // spro"zimo "casovnik (posebno nit), v katerem preverjamo
            // spo"stovanje "casovnih omejitev in posodabljamo prikaz
            // preostalega "casa
            new Timer(INTERVAL_CASOVNIKA_STOPARICE, (e) -> {
                Igra igra = GUI.this.igra;
                long preostaliCas = igra.posodobiCas();
                GUI.this.zgornjaPlosca.posodobiPrikazCasa(false);
                if (igra.preveriPrekoracitevCasa()) {
                    GUI.this.prekoracitevCasa();
                }
            }).start();

            okno.setVisible(true);
        });
    }

    //
    // Ta metoda se pokli"ce ob pritisku na gumb Nova igra.
    //
    public void novaIgra() {
        this.igra.novaIgra();
        this.zgornjaPlosca.ustaviAnimacijo();
        this.statusnaPlosca.ponastavi(this.igra.vrniStatistiko().toString());
        this.spodnjaPlosca.ponastavi();
    }

    //
    // Ta metoda se pokli"ce, "ce igralec na potezi prekora"ci "casovno
    // omejitev.
    //
    private void prekoracitevCasa() {
        if (!this.igra.jeKonec()) {
            Izid izid = this.igra.prekoracitevCasa();
            String razlaga = this.igra.vrniObrazlozitevIzida();
            this.statusnaPlosca.nastavi(izid.toString(), razlaga, this.igra.vrniStatistiko().toString());
            this.zgornjaPlosca.sproziAnimacijo(izid.zmagovalec());
        }
    }
}
