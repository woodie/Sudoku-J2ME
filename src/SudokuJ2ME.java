import java.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

/*
 * SudokuJ2ME
 */
public class SudokuJ2ME extends MIDlet {
  protected final String appTitle = "Sudoku J2ME";
  private SimplerTimes simplerTimes;
  private String timeOfday;
  private Display display = null;
  private MainCanvas mainCanvas = null;
  private int width;
  private int height;
  private SpecialFont specialFont = new SpecialFont();
  private Font smallFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
  private Font largeFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_LARGE);
  private final int cbarHeight = 38;
  private final int cellSize = 26;
  private final int padding = 4;
  private final int margin = 2;
  private final int boardSize = cellSize * 9;
  private final int BLACK = 0x000000;
  private final int WHITE = 0xFFFFFF;
  private final int CYAN = 0x00AAFF;
  private final int BLUE = 0x000088;
  private String puzzleData;

  public SudokuJ2ME() {
    display = Display.getDisplay(this);
    mainCanvas = new MainCanvas(this);
  }

  public void startApp() throws MIDletStateChangeException {
    display.setCurrent(mainCanvas);
  }

  public void pauseApp() {}

  protected void destroyApp(boolean unconditional)
      throws MIDletStateChangeException {}

/*
 * Main Canvas
 */
  class MainCanvas extends Canvas {
    private SudokuJ2ME parent = null;

    public MainCanvas(SudokuJ2ME parent) {
      this.parent = parent;
      this.setFullScreenMode(true);
      width = getWidth();
      height = getHeight();
      puzzleData = puzzleData();
    }

    public void paint(Graphics g) {
      simplerTimes = new SimplerTimes();
      timeOfday = simplerTimes.timeOfday(true);
      g.setColor(BLACK);
      g.fillRect(0, 0, width, height);
      g.setColor(WHITE);
      g.setFont(largeFont);
      g.drawString(appTitle, padding, padding, Graphics.LEFT | Graphics.TOP);
      g.drawString(timeOfday, width - padding, padding, Graphics.RIGHT | Graphics.TOP);
      Toolbar.drawMenuIcon(g, 18, height - 20);
      // Toolbar.drawBackIcon(g, width - 18, height - 20);

      for (int i = 0; i < 10; i++) {
        int offset = i * cellSize;
        g.setColor(((i == 3) || (i == 6)) ? CYAN : BLUE);
        g.drawLine(margin + 1, offset + cbarHeight, boardSize + margin - 1, offset + cbarHeight);
        g.drawLine(offset + margin, cbarHeight + 1, offset + margin, boardSize + cbarHeight - 1);
      }

      g.setColor(WHITE);
      for (int i = 0; i < puzzleData.length(); i++) {
        String s = puzzleData.substring(i, i + 1);
        if (!s.equals(".")) {
          specialFont.numbers(g, s, cellSize * (i / 9) + margin + 8, cellSize * (i % 9) + cbarHeight + 3);
        }
      }
    }
  }

  private String puzzleData() {
    StringBuffer sb = new StringBuffer();
    InputStream is = getClass().getResourceAsStream("puzzle_easy.txt");
    try {
      int chars, i = 0;
      while ((chars = is.read()) != -1) {
        sb.append((char) chars);
      }
      return sb.toString();
    }catch (Exception e) {}
    return null;
  }

}
