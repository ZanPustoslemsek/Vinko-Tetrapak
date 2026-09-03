
package ogrodje;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

//
// Konstante in metode, ki jih uporabljamo na raznih mestih v razredih za
// implementacijo grafi"cnega vmesnika
// 
//
public class Razno {

    public static final Font PISAVA_OSNOVA_MONO = new Font("Monospaced", Font.PLAIN, 12);
    public static final Font PISAVA_OSNOVA_SANS = new Font("Sans Serif", Font.PLAIN, 12);
    public static final Color BARVA_OZADJA_SPLOSNA = new Color(0, 0, 64);

    public static Color BARVA_IKONA_OZADJE = Color.GRAY;
    private static Color BARVA_IKONA_POLNILO = new Color(0, 0, 64);
    private static Color BARVA_IKONA_OBROBA = Color.LIGHT_GRAY;

    public static enum Polozaj {
        LEVO, DESNO, SREDINA, ZGORAJ, SPODAJ
    }

    //
    // Nastavi pisavo, ki je enaka podani, le da je njena velikost enaka
    // <ciljnaVelikost>.
    //
    public static void nastaviPisavo(Graphics g, Font osnovnaPisava, int ciljnaVelikost) {
        g.setFont(osnovnaPisava);
        int hPisava = g.getFontMetrics().getAscent();
        g.setFont(osnovnaPisava.deriveFont( ((float) g.getFont().getSize()) * ciljnaVelikost / hPisava));
    }

    //
    // Nastavi glajenje robov.
    //
    public static void nastaviAntialiasing(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    //
    // V podani grafi"cni kontekst nari"se ikono (T-tetromino na enobarvnem
    // ozadju) s podano "sirino in vi"sino ter barvo ozadja.
    //
    public static void narisiIkono(Graphics g, Color barvaOzadja, int sirina, int visina) {
        g.setColor(barvaOzadja);
        g.fillRect(0, 0, sirina, visina);

        int d = Math.min(sirina / 4, visina / 3);
        int x0 = (sirina - 3 * d) / 2;
        int y0 = (visina - 2 * d) / 2;
        int x = x0, y = y0;
        int[] deltaX = {1, 1, -1, 0};
        int[] deltaY = {0, 0, 1, 0};

        for (int i = 0; i < 4; i++) {
            g.setColor(BARVA_IKONA_POLNILO);
            g.fillRect(x, y, d, d);
            g.setColor(BARVA_IKONA_OBROBA);
            g.drawRect(x, y, d, d);
            x += deltaX[i] * d;
            y += deltaY[i] * d;
        }
    }

    //
    // Na to"cko (x, y) postavi podani napis. Parametra poravnavaX (LEVO,
    // DESNO ali SREDINA) in poravnavaY (ZGORAJ, SPODAJ ali SREDINA) podajata
    // polo"zaj to"cke (x, y) glede na napis. Na primer, vrednosti poravnavaX
    // == LEVO in poravnavaY == SPODAJ povzro"cita, da se bo to"cka (x, y)
    // nahajala v spodnjem levem kotu niza (niz se bo prikazal desno in nad
    // omenjeno to"cko).
    //
    public static void narisiNapis(Graphics g, String napis, int x, int y,
            Polozaj poravnavaX, Polozaj poravnavaY) {

        FontMetrics fm = g.getFontMetrics();
        int wNapis = fm.stringWidth(napis);
        int visina = fm.getHeight();
        int ascent = fm.getAscent();

        int xNapis = switch (poravnavaX) {
            case LEVO -> x;              //:
            case DESNO -> x - wNapis;    //:
            default -> x - wNapis / 2;
        };

        int yNapis = switch (poravnavaY) {
            case ZGORAJ -> y + (visina + ascent) / 2;   //:
            case SPODAJ -> y - (visina - ascent) / 2;   //:
            default -> y + visina / 2;
        };

        g.drawString(napis, xNapis, yNapis);
    }

    //
    // Podani napis postavi na sredino podanega pravokotnika.
    //
    public static void narisiNapisVPravokotnik(Graphics g, String napis,
            Rectangle pravokotnik) {

        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(napis);
        int h = fm.getAscent();
        g.drawString(napis, pravokotnik.x + (pravokotnik.width - w) / 2,
                pravokotnik.y + (pravokotnik.height + h) / 2);
    }

    //
    // Vrne povečano različico podanega pravokotnika. Parametra dx in dy
    // predstavljata pove"cavo (tako v pozitivni kot v negativni smeri) v
    // dimenziji x oz. y.
    //
    public static Rectangle povecaniPravokotnik(Rectangle r, int dx, int dy) {
        Rectangle r1 = new Rectangle(r);
        r1.grow(dx, dy);
        return r1;
    }

}
