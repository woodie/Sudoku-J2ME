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
  private Puzzle puzzle;
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
  private final int THIN = 0x555555;
  private final int GRAY = 0x888888;
  private final int CYAN = 0x00AAFF;
  private final int BLUE = 0x000088;
  private final int GREEN = 0x88FF00;
  private final int FOREST = 0x009900;
  private final int YELLOW = 0xFFFF00;
  private final int SALMON = 0xFF6666;
  private final int CRIMSON = 0x770000;
  private final int MUSTARD = 0x555500;
  private int selectX = 4;
  private int selectY = 4;
  private String modeLabel;
  private int[] gameBoard;
  private boolean[] puzzleData;
  private boolean[] ticks;
  private int highlight = -1;
  private int selected = -1;
  private boolean usingPen = true;
  private boolean locked = true;

  public SudokuJ2ME() {
    display = Display.getDisplay(this);
    mainCanvas = new MainCanvas(this);
    puzzle = new Puzzle();
    ticks = puzzle.ticks;
    gameBoard = puzzle.gameBoard;
    puzzleData = puzzle.puzzleData;
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
    private final long SECOND = 1000;
    private SudokuJ2ME parent = null;

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
        if (locked) {
          highlight = (highlight == selected) ? -1 : selected;
        } else {
          if (highlight > 0) {
            int index = selectY * 9 + selectX;
            int entry = (highlight == selected) ? 0 : highlight;
            if (usingPen) {
              puzzle.penValue(index, entry);
            } else if (selected == 0) {
              puzzle.pencilValue(index, entry);
            }
          }
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
      } else if ((value > 0) && (value < 10)) {
        highlight = (highlight == value) ? -1 : value;
      }
      this.repaint();
    }

    public void drawTick(Graphics g, int cx, int cy, int value) {
      g.fillRect(cx + 4 + ((value - 1) % 3 * 7), cy + 4 + ((value - 1) / 3 * 7), 5, 5);
    }

    public void paint(Graphics g) {
      g.setColor(BLACK);
      g.fillRect(0, 0, width, height);
      selected = gameBoard[selectY * 9 + selectX];
      locked = puzzleData[selectY * 9 + selectX];

      g.setFont(smallFont);
      g.setColor(WHITE);
      modeLabel = (usingPen ? "Pen" : "Pencil");
      g.drawString(modeLabel, width - padding, height - padding, Graphics.RIGHT | Graphics.BOTTOM);
      g.drawString("Menu", padding, height - padding, Graphics.LEFT | Graphics.BOTTOM);

      // Draw lines on board
      for (int i = 0; i < 10; i++) {
        int offset = i * cellSize;
        g.setColor(((i == 3) || (i == 6)) ? WHITE : THIN);
        g.drawLine(margin + 1, offset + margin, boardSize + margin - 1, offset + margin);
        g.drawLine(offset + margin, margin + 1, offset + margin, boardSize + margin - 1);
      }

      for (int i = 0; i < gameBoard.length; i++) {
        int thisValue = gameBoard[i];
        int thisX = i % 9;
        int thisY = i / 9;
        // Highlight border
        if ((thisValue == highlight) || ((highlight > -1) && (ticks[i * 9 + highlight - 1]))) {
          g.setColor((thisValue == highlight) ? FOREST : MUSTARD);
          g.fillRect(cellSize * thisX + margin + 1, cellSize * thisY + margin + 1, cellSize - 1, cellSize - 1);
        }
        // Cell content
        if (thisValue != 0) {
          g.setColor((thisValue == highlight) ? WHITE : (puzzleData[i] ? CYAN : WHITE));
          specialFont.numbersImage = (thisValue == highlight) ? specialFont.numbersG : specialFont.numbersK;
          specialFont.numbers(g, String.valueOf(thisValue), cellSize * thisX + margin + 7, cellSize * thisY + margin + 3);
        } else {
          for (int t = 1; t < 10; t++) {
            if (ticks[i * 9 + t - 1]) {
              g.setColor((t == highlight) ? WHITE : GRAY);
              drawTick(g, cellSize * thisX + margin, cellSize * thisY + margin, t);
            }
          }
        }
      }
      // Selection border
      g.setColor(highlight > 0 ? (usingPen ? GREEN : YELLOW) : GRAY);
      g.drawRect(cellSize * selectX + margin, cellSize * selectY + margin, cellSize, cellSize);
      g.drawRect(cellSize * selectX + margin - 1, cellSize * selectY + margin - 1, cellSize + 2, cellSize + 2);

      g.setFont(smallFont);
      specialFont.numbersImage = specialFont.numbersK;
      for (int n = 1; n < 10; n++) {
        g.setColor(((highlight == n) && (!usingPen)) ? YELLOW : GRAY);
        g.drawString(String.valueOf(n), 20 * n + 21, height - 72, Graphics.HCENTER | Graphics.TOP);
        g.drawString(String.valueOf(n), 20 * n + 22, height - 72, Graphics.HCENTER | Graphics.TOP);
        g.setColor(((highlight == n) && (usingPen)) ? GREEN : GRAY);
        specialFont.numbers(g, String.valueOf(n), 20 * n + 13, height - 52);
      }
    }

  }
}
