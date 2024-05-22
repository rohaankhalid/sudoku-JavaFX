package sudoku;

import java.util.Map;
import java.util.HashMap;

public class Move {
    public final int row;
    public final int col;
    public final int previousValue;
    public final int newValue;

    private static Map<String, Move> moveCache = new HashMap<>();

    private static String key(int row, int col, int newValue) {
        return row + "," + col + "," + newValue;
    }

    public static Move valueOf(int row, int col, int previousValue, int newValue) {
        String key = key(row, col, newValue); // Changed to use newValue
        Move move = moveCache.get(key);
        if (move == null) {
            move = new Move(row, col, previousValue, newValue);
            moveCache.put(key, move);
        }
        return move;
    }

    private Move(int row, int col, int previousValue, int newValue) {
        this.row = row;
        this.col = col;
        this.previousValue = previousValue;
        this.newValue = newValue;
    }
}
