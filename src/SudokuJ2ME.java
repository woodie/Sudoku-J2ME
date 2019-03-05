import java.io.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
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
  private final int DARK = 0x333333;
  private final int CYAN = 0x00AAFF;
  private final int BLUE = 0x000088;
  private final int YELLOW = 0xFFFF00;
  private final String nums = "0123456789";
  private int selectX = 4;
  private int selectY = 4;
  private Vector puzzleMoves = new Vector();
  private String puzzleData;
  private String highlight = "";
  private String selected = "";

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

/*
 * Main Canvas
 */
  class MainCanvas extends Canvas {
    private final long SECOND = 1000;
    private SudokuJ2ME parent = null;
    private TimerTask updateTask;
    private Timer timer;
    boolean selection;
    boolean toggle = true;;

    public MainCanvas(SudokuJ2ME parent) {
      this.parent = parent;
      this.setFullScreenMode(true);
      width = getWidth();
      height = getHeight();
      puzzleData = puzzleData();
      puzzleMoves.addElement(puzzleData);
    }

    protected void showNotify() {
      startFrameTimer();
    }

    protected void hideNotify() {
      stopFrameTimer();
    }

    protected void startFrameTimer() {
      timer = new Timer();
      updateTask = new TimerTask() {
        public void run() {
        toggle = (toggle) ? false : true;
          if (selection) {
            repaint();
          } else {
            // paint the clock
            repaint(width - largeFont.stringWidth(timeOfday) - padding,
                padding, largeFont.stringWidth(timeOfday), largeFont.getHeight());
          }
        }
      };
      timer.schedule(updateTask, SECOND / 2, SECOND / 2);
    }

    protected void stopFrameTimer() {
      timer.cancel();
    }

    public void keyPressed(int keyCode){
      String key = getKeyName(keyCode).toUpperCase();
      if (key.equals("SELECT")) {
        if (nums.indexOf(selected) != -1) {
          highlight = highlight.equals(selected) ? "" : selected;
        } else {
          selection = (selection) ? false : true;
        }
      } else if (key.equals("SOFT1")) {
        // menu
      } else if (key.equals("SOFT2")) {
        // back
      } else if (key.equals("UP")) {
        selectY = (selectY <= 0) ? 8 : --selectY;
      } else if (key.equals("DOWN")) {
        selectY = (selectY >= 8) ? 0 : ++selectY;
      } else if (key.equals("LEFT")) {
        selectX = (selectX <= 0) ? 8 : --selectX;
      } else if (key.equals("RIGHT")) {
        selectX = (selectX >= 8) ? 0 : ++selectX;
      } else if (nums.indexOf(key) != -1) {
        if (selection) {
          int index = selectX * 9 + selectY;
          String newMove = lastMove().substring(0, index) + key + lastMove().substring(index + 1);
          puzzleMoves.addElement(newMove);
        } else {
          highlight = highlight.equals(key) ? "" : key;
        }
      }
      if (!(key.equals("SELECT"))) selection = false;
      this.repaint();
    }

    public String lastMove() {
      return (String) puzzleMoves.lastElement();
    }

    public void paint(Graphics g) {
      String thisData = lastMove();
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
      selected = thisData.substring(selectX * 9 + selectY, selectX * 9 + selectY + 1);

      for (int i = 0; i < 10; i++) {
        int offset = i * cellSize;
        g.setColor(((i == 3) || (i == 6)) ? CYAN : BLUE);
        g.drawLine(margin + 1, offset + cbarHeight, boardSize + margin - 1, offset + cbarHeight);
        g.drawLine(offset + margin, cbarHeight + 1, offset + margin, boardSize + cbarHeight - 1);
      }

      for (int i = 0; i < thisData.length(); i++) {
        String s = thisData.substring(i, i + 1);
        boolean userMove = puzzleData.substring(i, i + 1).equals(".");
        int thisX = i / 9;
        int thisY = i % 9;
        if ((thisX == selectX) && (thisY == selectY)) {
          if (!(selection && toggle)) {
            g.setColor(WHITE);
            g.drawRect(cellSize * thisX + margin, cellSize * thisY + cbarHeight, cellSize, cellSize);
            g.drawRect(cellSize * thisX + margin - 1, cellSize * thisY + cbarHeight - 1, cellSize + 2, cellSize + 2);
          }
        } else if (s.equals(highlight) && !s.equals(".")) {
          g.setColor(YELLOW);
          g.drawRect(cellSize * thisX + margin, cellSize * thisY + cbarHeight, cellSize, cellSize);
        }
        if (!s.equals(".")) {
          g.setColor(s.equals(highlight) ? YELLOW : (userMove ? WHITE : CYAN));
          specialFont.numbers(g, s, cellSize * thisX + margin + 7, cellSize * thisY + cbarHeight + 3);
        }
      }
    }
  }

}
