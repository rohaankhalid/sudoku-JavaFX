package sudoku;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

public class Board {
    private int[][] board;
    private Stack<Move> moveStack;

    public Board() {
        board = new int[9][9];
        moveStack = new Stack<>();
    }

    public static Board loadBoard(InputStream in) throws InvalidBoardException {
        Board board = new Board();
        Scanner scanner = new Scanner(in);
        try {
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    if (!scanner.hasNextInt()) {
                        throw new InvalidBoardException("The file does not contain enough integers.");
                    }
                    int value = scanner.nextInt();
                    if (value < 0 || value > 9) {
                        throw new InvalidBoardException("Invalid value " + value + " at row " + row + ", column " + col + ". Values must be between 0 and 9.");
                    }
                    board.setCell(row, col, value, false); // Use false to avoid tracking initial setup
                }
            }
            if (scanner.hasNext()) {
                throw new InvalidBoardException("The file contains extra data.");
            }
            if (!board.isValidSudokuBoard()) {
                throw new InvalidBoardException("The board contains invalid Sudoku configuration.");
            }
        } finally {
            scanner.close();
        }
        return board;
    }

    public boolean isLegal(int row, int col, int value) {
        return value >= 1 && value <= 9 && getPossibleValues(row, col).contains(value);
    }

    public void setCell(int row, int col, int value) {
        setCell(row, col, value, true);
    }

    private void setCell(int row, int col, int value, boolean trackMove) {
        if (value < 0 || value > 9) {
            throw new IllegalArgumentException("Value must be between 1 and 9 (or 0 to reset a value)");
        }
        if (trackMove) {
            int previousValue = board[row][col];
            moveStack.push(Move.valueOf(row, col, previousValue, value));
        }
        board[row][col] = value;
    }

    public int getCell(int row, int col) {
        return board[row][col];
    }

    public boolean hasValue(int row, int col) {
        return getCell(row, col) > 0;
    }

    public Set<Integer> getPossibleValues(int row, int col) {
        Set<Integer> possibleValues = new HashSet<>();
        for (int i = 1; i <= 9; i++) {
            possibleValues.add(i);
        }
        // check the row
        for (int c = 0; c < 9; c++) {
            possibleValues.remove(getCell(row, c));
        }
        // check the column
        for (int r = 0; r < 9; r++) {
            possibleValues.remove(getCell(r, col));
        }
        // check the 3x3 square
        int startRow = row / 3 * 3;
        int startCol = col / 3 * 3;
        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                possibleValues.remove(getCell(r, c));
            }
        }
        return possibleValues;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                sb.append(getCell(row, col));
                if (col < 8) {
                    sb.append(" ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private boolean isValidSudokuBoard() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int value = getCell(row, col);
                if (value != 0) {
                    board[row][col] = 0;  // Temporarily remove the value to check legality
                    if (!isLegal(row, col, value)) {
                        return false;
                    }
                    board[row][col] = value;  // Restore the value
                }
            }
        }
        return true;
    }

    public void undo() {
        if (!moveStack.isEmpty()) {
            Move lastMove = moveStack.pop();
            board[lastMove.row][lastMove.col] = lastMove.previousValue;
        }
    }

    public List<int[]> getSinglePossibleValueCells() {
        List<int[]> singlePossibleValueCells = new Stack<>();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == 0) {
                    Set<Integer> possibleValues = getPossibleValues(row, col);
                    if (possibleValues.size() == 1) {
                        singlePossibleValueCells.add(new int[]{row, col});
                    }
                }
            }
        }
        return singlePossibleValueCells;
    }

    public List<Move> getMoves() {
        return moveStack.stream().collect(Collectors.toList());
    }

    // extra feature: solve the board using backtracking
    public boolean solve() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == 0) {
                    for (int value : getPossibleValues(row, col)) {
                        board[row][col] = value;
                        if (solve()) {
                            return true;
                        }
                        board[row][col] = 0;
                    }
                    return false;
                }
            }
        }
        return true;
    }
}

class InvalidBoardException extends Exception {
    public InvalidBoardException(String message) {
        super(message);
    }
}
