import java.io.*;
import java.util.Random;
import javax.microedition.io.*;
import javax.microedition.rms.*;

public class Puzzle {
  private RecordStore rs = null;
  private static final int LENGTH = 3;
  private static final String REC_STORE = "Moves";

  public String description;
  public boolean[] pencilMarks;
  public int[] gameBoard;
  public boolean[] puzzleData;
  public static final String[] levels = {"Simple", "Easy", "Intermediate", "Expert"};

  public Puzzle() {
    this(-1, -1);
  }

  public Puzzle(int level, int puzzle) {
    pencilMarks = new boolean[81 * 9];
    gameBoard = new int[81];
    puzzleData = new boolean[81];
    Random random = new Random();
    if ((level < 0) || (level > Puzzle.levels.length)) level = 1;
    if ((puzzle < 0) || (puzzle > 18)) puzzle = random.nextInt(19);
    StringBuffer sb = new StringBuffer(24);
    sb.append(levels[level]);
    sb.append(": ");
    sb.append(String.valueOf(puzzle + 1));
    description = sb.toString();
    sb.delete(0, sb.length());
    sb.append("puzzles_");
    sb.append(Puzzle.levels[level].toLowerCase());
    sb.append(".txt");
    InputStream is = getClass().getResourceAsStream(sb.toString());
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

  public void pencilValue(int cell, int value) {
    int tickIndex = cell * 9 + value - 1;
    pencilMarks[tickIndex] = pencilMarks[tickIndex] ? false : true;
  }

  public void penValue(int cell, int value) {
    gameBoard[cell] = value;
    pencilCleanup(cell, value);
  }

  public void pencilCleanup(int cell, int value) {
    if (value > 0) {
      int myX = cell % 9;
      int myY = cell / 9;
      int houseX = (myX / 3) * 3;
      int houseY = (myY / 3) * 3;
      for (int i = 0; i < 81; i++) {
        if (i == cell) {
          for (int t = 1; t < 10; t++) {
            pencilMarks[i * 9 + t - 1] = false;
          }
        } else if (((i >= myY * 9) && (i < myY * 9 + 9)) || (i % 9 == myX) ||
            ((i % 9 >= houseX) && (i % 9 < houseX + 3) &&
            (i / 9 >= houseY) && (i / 9 < houseY + 3))) {
          pencilMarks[i * 9 + value - 1] = false;
        }
      }
    }
  }

  public void autoPencil() {
    for (int i = 0; i < pencilMarks.length; i++) {
      pencilMarks[i] = true;
    }
    for (int i = 0; i < gameBoard.length; i++) {
      if (gameBoard[i] != 0) pencilCleanup(i, gameBoard[i]);
    }
  }

  // (puzzle, level, moves) 0-19, 0-3  0-255
  //   (cell, value, pen)     0-80, 1-9, 1
  //   (cell, value, pencil)  0-80, 1-9, 0

  //openRecStore();
  //loadMoves();
  //closeRecStore();



  public byte[] toByteArray(int value) {
    return new byte[] { (byte)(value >> 24), (byte)(value >> 16), (byte)(value >> 8), (byte)value };
  }

  public int fromByteArray(byte[] bytes) {
    return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
  }

  public void saveMoves(int[] values) {
    for (int i = 0; i < values.length; i++) {
      byte[] rec = toByteArray(values[i]);
      try {
        rs.addRecord(rec, 0, rec.length);
      } catch (Exception e) {}
    }
  }

  public void loadMoves(){
    try{
      byte[] recData = new byte[LENGTH];
      for(int i = 1; i <= rs.getNumRecords(); i++){
        rs.getRecord(i, recData, 0);
        // enter moves = fromByteArray(recData);
      }
    } catch (Exception e){}
  }

  public void openRecStore() {
    try {
      rs = RecordStore.openRecordStore(REC_STORE, true );
    } catch (Exception e) {}
  }

  public void closeRecStore() {
    try {
      rs.closeRecordStore();
    } catch (Exception e) {}
  }

  public void deleteRecStore() {
    if (RecordStore.listRecordStores() != null) {
      try {
        RecordStore.deleteRecordStore(REC_STORE);
      } catch (Exception e) {}
    }
  }

}
