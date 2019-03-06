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
  private final String nums = "0123456789";
  private int selectX = 4;
  private int selectY = 4;
  private int moveIndex = 0;
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
    String puzzles[] = new String[20];
    StringBuffer sb = new StringBuffer(81);
    InputStream is = getClass().getResourceAsStream("puzzles_easy.txt");
    try {
      int n = 0;
      int chars, i = 0;
      while ((chars = is.read()) != -1) {
        sb.append((char) chars);
        if (((char) chars) == '\n') {
          puzzles[n] = sb.toString();
          sb.delete(0, sb.length());
          n++;
        }
      }
      Random random = new Random();
      int index = random.nextInt(puzzles.length + 1);
      return puzzles[index];
    } catch (Exception e) {}
    return null;
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
      puzzleData = puzzleData();
      puzzleMoves.addElement(puzzleData);
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
      } else if (key.equals("*")) {
        if (moveIndex > 0) --moveIndex;
      } else if (key.equals("#")) {
        if (moveIndex + 1 < puzzleMoves.size()) ++moveIndex;
      } else if (nums.indexOf(key) != -1) {
        if (selection) {
          int index = selectY * 9 + selectX;
          String newMove = lastMove().substring(0, index) + key + lastMove().substring(index + 1);
          if (puzzleMoves.size() > moveIndex + 1) puzzleMoves.setSize(moveIndex + 1);
          puzzleMoves.addElement(newMove);
          moveIndex++;
        } else {
          highlight = highlight.equals(key) ? "" : key;
        }
      }
      if (!(key.equals("SELECT"))) selection = false;
      this.repaint();
    }

    public String lastMove() {
      return (String) puzzleMoves.elementAt(moveIndex);
    }

    public void paint(Graphics g) {
      String thisData = lastMove();
      g.setColor(BLACK);
      g.fillRect(0, 0, width, height);
      selected = puzzleData.substring(selectY * 9 + selectX, selectY * 9 + selectX + 1);

      for (int i = 0; i < 10; i++) {
        int offset = i * cellSize;
        g.setColor(((i == 3) || (i == 6)) ? (selection ? GRAY : CYAN) : (selection ? DARK : BLUE));
        g.drawLine(margin + 1, offset + margin, boardSize + margin - 1, offset + margin);
        g.drawLine(offset + margin, margin + 1, offset + margin, boardSize + margin - 1);
      }

      for (int i = 0; i < thisData.length(); i++) {
        String s = thisData.substring(i, i + 1);
        boolean userMove = puzzleData.substring(i, i + 1).equals(".");
        int thisX = i % 9;
        int thisY = i / 9;
        if ((thisX == selectX) && (thisY == selectY)) {
          g.setColor(selection ? GREEN : WHITE);
          g.drawRect(cellSize * thisX + margin, cellSize * thisY + margin, cellSize, cellSize);
          g.drawRect(cellSize * thisX + margin - 1, cellSize * thisY + margin - 1, cellSize + 2, cellSize + 2);
        } else if (s.equals(highlight) && !s.equals(".")) {
          g.setColor(YELLOW);
          g.drawRect(cellSize * thisX + margin, cellSize * thisY + margin, cellSize, cellSize);
        }
        if (!s.equals(".")) {
          g.setColor(s.equals(highlight) ? YELLOW : (userMove ? WHITE : (selection ? GRAY : CYAN)));
          specialFont.numbers(g, s, cellSize * thisX + margin + 7, cellSize * thisY + margin + 3);
        }
      }
    }
  }

}
