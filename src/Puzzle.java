import java.io.*;
import java.util.Random;
import javax.microedition.io.*;
import javax.microedition.rms.*;

public class Puzzle {
  private RecordStore rs = null;
  private static final int LENGTH = 4;
  private static final String REC_STORE = "Moves";

  public boolean[] ticks = new boolean[81 * 9];
  public int[] gameBoard = new int[81];
  public boolean[] puzzleData = new boolean[81];
  private final String[] levels = {"simple", "easy", "intermediate", "expert"};

  public Puzzle() {
    //openRecStore();
    //loadMoves();
    //closeRecStore();
    load(0, -1);
  }

  // 1st (game, puzzle, difficuly) 2, 0-19, 0-3
  // 2nd (pen,    cell, value)     1, 0-80, 1-9
  // 3rd (pencil, cell, value)     0, 0-80, 1-9

  private void load(int level, int puzzle) {
    Random random = new Random();
    if (puzzle == -1) puzzle = random.nextInt(19);
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

  public void penValue(int cell, int value) {
    gameBoard[cell] = value;
    // cleanup tick marks
    if (value > 0) {
      int myX = cell % 9;
      int myY = cell / 9;
      int houseX = (myX / 3) * 3;
      int houseY = (myY / 3) * 3;
      for (int i = 0; i < 81; i++) {
        if (i == cell) {
          for (int t = 1; t < 10; t++) {
            ticks[i * 9 + t - 1] = false;
          }
        } else if (((i >= myY * 9) && (i < myY * 9 + 9)) || (i % 9 == myX) ||
            ((i % 9 >= houseX) && (i % 9 < houseX + 3) &&
            (i / 9 >= houseY) && (i / 9 < houseY + 3))) {
          ticks[i * 9 + value - 1] = false;
        }
      }
    }
  }

  public void pencilValue(int cell, int value) {
    int tickIndex = cell * 9 + value - 1;
    ticks[tickIndex] = ticks[tickIndex] ? false : true;
  }


  public byte[] toByteArray(int value) {
    return new byte[] { (byte)(value >> 24), (byte)(value >> 16), (byte)(value >> 8), (byte)value };
  }

  public int fromByteArray(byte[] bytes) {
    return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
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

}
