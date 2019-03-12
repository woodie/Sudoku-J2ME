import java.io.*;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * A simple utility to provide larger fonts to MIDP.
 */
public class SpecialFont {

  public Image numbersImage = null;
  public Image numbersK = null;
  public Image numbersG = null;
  public static final int numbersBaseline = 19;

  public SpecialFont() {
    try {
      numbersK = Image.createImage ("/numbers14x21.png");
      numbersG = Image.createImage ("/numbers14x21g.png");
      numbersImage = numbersK;
    } catch (Exception ex) {
    }
  }

  public int numbersWidth(String phrase) {
    int length = 0;
    for (int i = 0; i < phrase.length(); i++) {
      int intValue = ((int) phrase.charAt(i)) - 48;
      if (intValue >= 0 && intValue <= 9) {
        length += 14;
      } else if (intValue == 10) {
        length += 7;
      }
    }
    return length;
  }

  public void numbers(Graphics g, String phrase, int fx, int fy) {
    int width = g.getClipWidth();
    int height = g.getClipHeight();
    for (int i = 0; i < phrase.length(); i++) {
      int cw = 14;
      int ch = 21;
      int intValue = ((int) phrase.charAt(i)) - 48;
      if (intValue >= 0 && intValue <= 10) {
        int cx = intValue * cw;
        if (intValue == 10) { cw = cw / 2; }
        g.setClip(fx, fy, cw, ch);
        g.fillRect(fx, fy, cw, ch);
        g.drawImage(numbersImage, fx - cx, fy, Graphics.LEFT | Graphics.TOP);
        fx += cw;
        g.setClip(0 ,0, width, height);
      }
    }
  }

}
