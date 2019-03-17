import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

/*
 * SudokuJ2ME
 */
public class SudokuJ2ME extends MIDlet {
  private Display display = null;
  private InfoCanvas infoCanvas = null;
  private MenuCanvas menuCanvas = null;
  private PickCanvas pickCanvas = null;
  private MainCanvas mainCanvas = null;
  private int width;
  private int height;
  private Puzzle puzzle = null;
  private SpecialFont specialFont = new SpecialFont();
  private Font smallFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
  private Font largeFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_LARGE);
  private final int cellSize = 26;
  private final int padding = 4;
  private final int margin = 2;
  private final int boardSize = cellSize * 9;
  private final int BLACK = 0x000000;
  private final int WHITE = 0xFFFFFF;
  private final int GR86 = 0xDDDDDD;
  private final int GR80 = 0xCCCCCC;
  private final int GR40 = 0x666666;
  private final int GR26 = 0x444444;
  private final int GR20 = 0x333333;
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
  private String keyLabel;
  private String modeLabel;
  private int highlight = -1;
  private int selected = -1;
  private boolean usingPen = true;
  private boolean locked = true;
  private final int cbarHeight = 38;

  public SudokuJ2ME() {
    display = Display.getDisplay(this);
    infoCanvas = new InfoCanvas(this);
    menuCanvas = new MenuCanvas(this);
    pickCanvas = new PickCanvas(this);
    mainCanvas = new MainCanvas(this);
    puzzle = new Puzzle();
  }

  public void startApp() throws MIDletStateChangeException {
    display.setCurrent(mainCanvas);
  }

  public void pauseApp() {}

  protected void destroyApp(boolean unconditional)
      throws MIDletStateChangeException {}

 /*
  * Info Canvas
  */
  class InfoCanvas extends Canvas {

    public InfoCanvas(SudokuJ2ME parent) {
      this.setFullScreenMode(true);
    }

    public void keyPressed(int keyCode){
      if (getKeyName(keyCode).equals("SOFT2")) {
        display.setCurrent(mainCanvas);
      }
    }

    public void paint(Graphics g) {
      g.setColor(BLACK);
      g.fillRect(0, 0, width, height);

      g.setFont(smallFont);
      g.setColor(WHITE);
      g.drawString("Back", width - padding, height, Graphics.RIGHT | Graphics.BOTTOM);

      g.setColor(WHITE);
      g.setFont(largeFont);
      int position = 90;
      StringBuffer sb = new StringBuffer();
      sb.append("Version: ");
      sb.append(getAppProperty("MIDlet-Version"));
      String[] lines = {"Sudoku J2ME", "", "(c) 2019 John Woodell", "", sb.toString()};
      for (int i = 0; i < lines.length; i++) {
        position += 20;
        g.drawString(lines[i], width / 2, position, Graphics.HCENTER | Graphics.TOP);
      }
    }
  }

 /**
  * Menu Canvas
  */
  class MenuCanvas extends Canvas {
    private SudokuJ2ME parent = null;
    private int menuSelection = 0;
    private String[] menuItems = {"New Game", "Undo Move", "Redo Move", "Auto Pencil", "About", "Exit"};

    public MenuCanvas(SudokuJ2ME parent) {
      this.parent = parent;
      this.setFullScreenMode(true);
      width = getWidth();
      height = getHeight();
    }

    public void bailout() {
      try {
        destroyApp(true);
        notifyDestroyed();
      } catch (MIDletStateChangeException e) {
        e.printStackTrace();
      }
    }

    public void keyRepeated(int keyCode){
      keyLabel = getKeyName(keyCode).toUpperCase();
      if (keyLabel.equals("UP")) {
        menuSelection = (menuSelection == 0) ? menuItems.length - 1 : --menuSelection;
      } else if (keyLabel.equals("DOWN")) {
        menuSelection = (menuSelection == menuItems.length - 1) ? 0 : ++menuSelection;
      }
      this.repaint();
    }

    public void keyPressed(int keyCode){
      keyLabel = getKeyName(keyCode).toUpperCase();
      if (keyLabel.equals("SELECT")) {
        if (menuItems[menuSelection].equals("New Game")) {
          display.setCurrent(pickCanvas);
        } else if (menuItems[menuSelection].equals("Auto Pencil")) {
          puzzle.autoPencil();
          display.setCurrent(mainCanvas);
        } else if (menuItems[menuSelection].equals("About")) {
          display.setCurrent(infoCanvas);
        } else if (menuItems[menuSelection].equals("Exit")) {
          bailout();
        } else {
          display.setCurrent(mainCanvas);
        }
      } else if (keyLabel.equals("SOFT2")) {
        display.setCurrent(mainCanvas);
      } else if (keyLabel.equals("UP")) {
        menuSelection = (menuSelection == 0) ? menuItems.length - 1 : --menuSelection;
      } else if (keyLabel.equals("DOWN")) {
        menuSelection = (menuSelection == menuItems.length - 1) ? 0 : ++menuSelection;
      }
      this.repaint();
    }

    public void paint(Graphics g) {
      g.setColor(BLACK);
      g.fillRect(0, 0, width, height);

      g.setFont(smallFont);
      g.setColor(WHITE);
      g.drawString("Back", width - padding, height, Graphics.RIGHT | Graphics.BOTTOM);

      int menuPadding = 6;
      int menuLeading = 20;
      int menuWidth = (menuPadding * 2) + largeFont.stringWidth(menuItems[3]);
      int menuHeight = (menuPadding * 2) + (menuLeading * (menuItems.length)) - 2;
      int menuTop = (height - menuHeight) / 2;
      int menuLeft = (width - menuWidth) / 2;
      int selectionWidth = menuWidth - 4;

      g.setColor(GR20);
      g.fillRect(menuLeft, menuTop, menuWidth, menuHeight);
      g.setColor(WHITE);
      g.drawRect(menuLeft, menuTop, menuWidth - 1, menuHeight - 1);
      int topLine = menuTop + menuPadding;
      for (int i = 0; i < menuItems.length; i++) {
        if (menuSelection == i) {
          g.setColor(BLACK);
          g.fillRect(menuLeft + 2, topLine - 2, selectionWidth, menuLeading);
        }
        g.setFont(largeFont);
        g.setColor((menuSelection == i) ? YELLOW : WHITE);
        g.drawString(menuItems[i], width / 2, topLine, Graphics.HCENTER | Graphics.TOP);
        g.setFont(largeFont);

        topLine += menuLeading;
      }
    }
  }

 /**
  * Pick Canvas
  */
  class PickCanvas extends Canvas {
    private SudokuJ2ME parent = null;
    private int menuSelection = 0;
    private String[] menuItems = Puzzle.levels;

    public PickCanvas(SudokuJ2ME parent) {
      this.parent = parent;
      this.setFullScreenMode(true);
      width = getWidth();
      height = getHeight();
    }

    public void bailout() {
      try {
        destroyApp(true);
        notifyDestroyed();
      } catch (MIDletStateChangeException e) {
        e.printStackTrace();
      }
    }

    public void keyRepeated(int keyCode){
      keyLabel = getKeyName(keyCode).toUpperCase();
      if (keyLabel.equals("UP")) {
        menuSelection = (menuSelection == 0) ? menuItems.length - 1 : --menuSelection;
      } else if (keyLabel.equals("DOWN")) {
        menuSelection = (menuSelection == menuItems.length - 1) ? 0 : ++menuSelection;
      }
      this.repaint();
    }

    public void keyPressed(int keyCode){
      keyLabel = getKeyName(keyCode).toUpperCase();
      if (keyLabel.equals("SELECT")) {
        puzzle = new Puzzle(menuSelection, -1);
        display.setCurrent(mainCanvas);
      } else if (keyLabel.equals("UP")) {
        menuSelection = (menuSelection == 0) ? menuItems.length - 1 : --menuSelection;
      } else if (keyLabel.equals("DOWN")) {
        menuSelection = (menuSelection == menuItems.length - 1) ? 0 : ++menuSelection;
      }
      this.repaint();
    }

    public void paint(Graphics g) {
      g.setColor(BLACK);
      g.fillRect(0, 0, width, height);
      int menuPadding = 6;
      int menuLeading = 20;
      int menuWidth = (menuPadding * 2) + largeFont.stringWidth(menuItems[2]);
      int menuHeight = (menuPadding * 2) + (menuLeading * (menuItems.length)) - 2;
      int menuTop = (height - menuHeight) / 2;
      int menuLeft = (width - menuWidth) / 2;
      int selectionWidth = menuWidth - 4;

      g.setColor(GR20);
      g.fillRect(menuLeft, menuTop, menuWidth, menuHeight);
      g.setColor(WHITE);
      g.drawRect(menuLeft, menuTop, menuWidth - 1, menuHeight - 1);
      int topLine = menuTop + menuPadding;
      for (int i = 0; i < menuItems.length; i++) {
        if (menuSelection == i) {
          g.setColor(BLACK);
          g.fillRect(menuLeft + 2, topLine - 2, selectionWidth, menuLeading);
        }
        g.setFont(largeFont);
        g.setColor((menuSelection == i) ? YELLOW : WHITE);
        g.drawString(menuItems[i], width / 2, topLine, Graphics.HCENTER | Graphics.TOP);
        g.setFont(largeFont);

        topLine += menuLeading;
      }
    }
  }


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
      keyLabel = getKeyName(keyCode).toUpperCase();
      if (keyLabel.equals("UP")) {
        selectY = (selectY <= 0) ? 8 : --selectY;
      } else if (keyLabel.equals("DOWN")) {
        selectY = (selectY >= 8) ? 0 : ++selectY;
      } else if (keyLabel.equals("LEFT")) {
        selectX = (selectX <= 0) ? 8 : --selectX;
      } else if (keyLabel.equals("RIGHT")) {
        selectX = (selectX >= 8) ? 0 : ++selectX;
      } else if (keyLabel.equals("*")) {
        // undo
      } else if (keyLabel.equals("#")) {
        // redo
      }
      this.repaint();
    }

    public void keyPressed(int keyCode){
      int value = keyCode - 48;
      keyLabel = getKeyName(keyCode).toUpperCase();
      if (keyLabel.equals("SELECT")) {
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
      } else if (keyLabel.equals("SOFT1")) {
        display.setCurrent(menuCanvas);
      } else if (keyLabel.equals("SOFT2")) {
        usingPen = usingPen ? false : true;
      } else if (keyLabel.equals("UP")) {
        selectY = (selectY <= 0) ? 8 : --selectY;
      } else if (keyLabel.equals("DOWN")) {
        selectY = (selectY >= 8) ? 0 : ++selectY;
      } else if (keyLabel.equals("LEFT")) {
        selectX = (selectX <= 0) ? 8 : --selectX;
      } else if (keyLabel.equals("RIGHT")) {
        selectX = (selectX >= 8) ? 0 : ++selectX;
      } else if (keyLabel.equals("*")) {
        // undo
      } else if (keyLabel.equals("#")) {
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
      selected = puzzle.gameBoard[selectY * 9 + selectX];
      locked = puzzle.puzzleData[selectY * 9 + selectX];

      g.setFont(smallFont);
      g.setColor(WHITE);
      modeLabel = (usingPen ? "Pen" : "Pencil");
      g.drawString(modeLabel, width - padding, height, Graphics.RIGHT | Graphics.BOTTOM);
      g.drawString("Menu", padding, height, Graphics.LEFT | Graphics.BOTTOM);
      g.drawString(puzzle.description, width / 2, height, Graphics.HCENTER | Graphics.BOTTOM);

      // Draw lines on board
      for (int i = 0; i < 10; i++) {
        int offset = i * cellSize;
        g.setColor(((i == 3) || (i == 6)) ? WHITE : THIN);
        g.drawLine(margin + 1, offset + margin, boardSize + margin - 1, offset + margin);
        g.drawLine(offset + margin, margin + 1, offset + margin, boardSize + margin - 1);
      }

      for (int i = 0; i < puzzle.gameBoard.length; i++) {
        int thisValue = puzzle.gameBoard[i];
        int thisX = i % 9;
        int thisY = i / 9;
        // Highlight border
        if ((thisValue == highlight) || ((highlight > -1) && (puzzle.pencilMarks[i * 9 + highlight - 1]))) {
          g.setColor((thisValue == highlight) ? FOREST : MUSTARD);
          g.fillRect(cellSize * thisX + margin + 1, cellSize * thisY + margin + 1, cellSize - 1, cellSize - 1);
        }
        // Cell content
        if (thisValue != 0) {
          g.setColor((thisValue == highlight) ? (puzzle.puzzleData[i] ? BLACK : WHITE) : (puzzle.puzzleData[i] ? CYAN : WHITE));
          specialFont.numbersImage = (thisValue == highlight) ? specialFont.numbersG : specialFont.numbersK;
          specialFont.numbers(g, String.valueOf(thisValue), cellSize * thisX + margin + 7, cellSize * thisY + margin + 3);
        } else {
          for (int t = 1; t < 10; t++) {
            if (puzzle.pencilMarks[i * 9 + t - 1]) {
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
