import java.io.*;
import java.util.Random;
import java.util.Vector;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

/*
 * SudokuJ2ME
 */
public class SudokuJ2ME extends MIDlet {
  private Display display = null;
  private MainCanvas mainCanvas = null;
  private int width;
  private int height;
  private SpecialFont specialFont = new SpecialFont();
  private Font smallFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
  private Font largeFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_LARGE);
  private final int cellSize = 26;
  private final int padding = 4;
  private final int margin = 2;
  private final int boardSize = cellSize * 9;
  private final int BLACK = 0x000000;
  private final int WHITE = 0xFFFFFF;
  private final int DARK = 0x333333;
  private final int GRAY = 0x888888;
  private final int CYAN = 0x00AAFF;
  private final int BLUE = 0x000088;
  private final int GREEN = 0x88FF00;
  private final int YELLOW = 0xFFFF00;
  private final int SALMON = 0xFF8888;
  private final int CRIMSON = 0x88000;
  private int selectX = 4;
  private int selectY = 4;
  private String modeLabel;
  private int[] gameBoard = new int[81];
  private boolean[] puzzleData = new boolean[81];
  private boolean[] ticks = new boolean[81 * 9];
  private int highlight = -1;
  private int selected = -1;
  private boolean usingPen = true;

  public SudokuJ2ME() {
    display = Display.getDisplay(this);
    mainCanvas = new MainCanvas(this);
    loadData();
  }

  public void startApp() throws MIDletStateChangeException {
    display.setCurrent(mainCanvas);
  }

  public void pauseApp() {}

  protected void destroyApp(boolean unconditional)
      throws MIDletStateChangeException {}

  private void loadData() {
    Random random = new Random();
    int puzzle = random.nextInt(19);
    InputStream is = getClass().getResourceAsStream("puzzles_easy.txt");
    int n = 0;
    int i = 0;
    int chars = 0;
    try {
      while ((chars = is.read()) != -1) {
        char c = (char) chars;
        if (c == '\n') {
          n++;
        } else {
          if (n == puzzle) {
            if (c == '.') {
              gameBoard[i] = 0;
            } else {
              gameBoard[i] = c - 48;
              puzzleData[i] = true;
            }
            i++;
          }
        }
      }
    } catch (Exception e) {}
  }

 /*
  * Main Canvas
  */
  class MainCanvas extends Canvas {
    private final long SECOND = 1000;
    private SudokuJ2ME parent = null;
    boolean selection;

    public MainCanvas(SudokuJ2ME parent) {
      this.parent = parent;
      this.setFullScreenMode(true);
      width = getWidth();
      height = getHeight();
    }

    public void keyRepeated(int keyCode){
      String key = getKeyName(keyCode).toUpperCase();
      if (key.equals("UP")) {
        selectY = (selectY <= 0) ? 8 : --selectY;
      } else if (key.equals("DOWN")) {
        selectY = (selectY >= 8) ? 0 : ++selectY;
      } else if (key.equals("LEFT")) {
        selectX = (selectX <= 0) ? 8 : --selectX;
      } else if (key.equals("RIGHT")) {
        selectX = (selectX >= 8) ? 0 : ++selectX;
      } else if (key.equals("*")) {
        // undo
      } else if (key.equals("#")) {
        // redo
      }
      this.repaint();
    }

    public void keyPressed(int keyCode){
      int value = keyCode - 48;
      String key = getKeyName(keyCode).toUpperCase();
      if (key.equals("SELECT")) {
        if ((value > -1) && (value < 10)) {
          highlight = (highlight == selected) ? -1 : selected;
        } else {
          selection = (selection) ? false : true;
        }
      } else if (key.equals("SOFT1")) {
        // menu
      } else if (key.equals("SOFT2")) {
        usingPen = usingPen ? false : true;
      } else if (key.equals("UP")) {
        selectY = (selectY <= 0) ? 8 : --selectY;
      } else if (key.equals("DOWN")) {
        selectY = (selectY >= 8) ? 0 : ++selectY;
      } else if (key.equals("LEFT")) {
        selectX = (selectX <= 0) ? 8 : --selectX;
      } else if (key.equals("RIGHT")) {
        selectX = (selectX >= 8) ? 0 : ++selectX;
      } else if (key.equals("*")) {
        // undo
      } else if (key.equals("#")) {
        // redo
      } else if ((value > -1) && (value < 10)) {
        if (selection) {
          int index = selectY * 9 + selectX;
          enterValue(index, true, value);
        } else {
          highlight = (highlight == value) ? -1 : value;
        }
      }
      if (!(key.equals("SELECT"))) selection = false;
      this.repaint();
    }

    public void enterValue(int cell, boolean pen, int value) {
      if (usingPen) {
        gameBoard[cell] = value;
      } else {
        int tickIndex = cell * 9 + value - 1;
        ticks[tickIndex] = ticks[tickIndex] ? false : true;
      }
    }

    public void drawTick(Graphics g, int cx, int cy, int value) {
      g.fillRect(cx + 5 + ((value - 1) % 3 * 7), cy + 3 + ((value - 1) / 3 * 8), 3, 5);
    }

    public void paint(Graphics g) {
      g.setColor(BLACK);
      g.fillRect(0, 0, width, height);
      selected = gameBoard[selectY * 9 + selectX];

      g.setFont(largeFont);
      modeLabel = (usingPen ? "Pen" : "Pencil");
      g.setColor(usingPen ? GREEN : SALMON);
      g.drawString(modeLabel, width - padding, height - padding, Graphics.RIGHT | Graphics.BOTTOM);

      for (int i = 0; i < 10; i++) {
        int offset = i * cellSize;
        g.setColor(((i == 3) || (i == 6)) ? (selection ? GRAY : CYAN) : (selection ? DARK : BLUE));
        g.drawLine(margin + 1, offset + margin, boardSize + margin - 1, offset + margin);
        g.drawLine(offset + margin, margin + 1, offset + margin, boardSize + margin - 1);
      }

      for (int i = 0; i < gameBoard.length; i++) { 
        int thisValue = gameBoard[i];
        int thisX = i % 9;
        int thisY = i / 9;
        if ((thisX == selectX) && (thisY == selectY)) {
          g.setColor(selection ? (usingPen ? GREEN : SALMON) : WHITE);
          g.drawRect(cellSize * thisX + margin, cellSize * thisY + margin, cellSize, cellSize);
          g.drawRect(cellSize * thisX + margin - 1, cellSize * thisY + margin - 1, cellSize + 2, cellSize + 2);
          g.setColor(WHITE);
          for (int t = 1; t < 10; t++) {
            if (ticks[i * 9 + t - 1]) {
              drawTick(g, cellSize * thisX + margin, cellSize * thisY + margin, t);
            }
          }
        } else if (thisValue == highlight) {
          g.setColor(YELLOW);
          g.drawRect(cellSize * thisX + margin, cellSize * thisY + margin, cellSize, cellSize);
        } else if ((highlight > -1) && (ticks[i * 9 + highlight - 1])) {
          g.setColor(SALMON);
          for (int t = 1; t < 10; t++) {
            if (ticks[i * 9 + t - 1]) {
              drawTick(g, cellSize * thisX + margin, cellSize * thisY + margin, t);
            }
          }
          g.drawRect(cellSize * thisX + margin, cellSize * thisY + margin, cellSize, cellSize);
        } else {
          g.setColor(WHITE);
          for (int t = 1; t < 10; t++) {
            if (ticks[i * 9 + t - 1]) {
              drawTick(g, cellSize * thisX + margin, cellSize * thisY + margin, t);
            }
          }
        }
        if (thisValue != 0) {
          g.setColor((thisValue == highlight) ? YELLOW : (puzzleData[i] ? (selection ? GRAY : CYAN) : WHITE));
          specialFont.numbers(g, String.valueOf(thisValue), cellSize * thisX + margin + 7, cellSize * thisY + margin + 3);
        }
      }
    }
  }

}
