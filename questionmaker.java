import java.util.Random;

public class questionmaker {
    private static final int SIZE = 9;
    private static final int SUBGRID = 3;
    private int[][] board;
    private Random random;

    public questionmaker() {
        board = new int[SIZE][SIZE];
        random = new Random();
    }

    public int[][] generatePuzzle() {
        fillDiagonal();
        fillRemaining(0, SUBGRID);
        removeNumbers();
        return board;
    }

    private void fillDiagonal() {
        for (int i = 0; i < SIZE; i += SUBGRID) {
            fillBox(i, i);
        }
    }

    private void fillBox(int row, int col) {
        boolean[] used = new boolean[SIZE + 1];
        for (int i = 0; i < SUBGRID; i++) {
            for (int j = 0; j < SUBGRID; j++) {
                int num;
                do {
                    num = random.nextInt(SIZE) + 1;
                } while (used[num]);
                used[num] = true;
                board[row + i][col + j] = num;
            }
        }
    }

    private boolean fillRemaining(int row, int col) {
        if (col >= SIZE && row < SIZE - 1) {
            row++;
            col = 0;
        }
        if (row >= SIZE && col >= SIZE) {
            return true;
        }
        if (row < SUBGRID) {
            if (col < SUBGRID) {
                col = SUBGRID;
            }
        } else if (row < SIZE - SUBGRID) {
            if (col == (row / SUBGRID) * SUBGRID) {
                col += SUBGRID;
            }
        } else {
            if (col == SIZE - SUBGRID) {
                row++;
                col = 0;
                if (row >= SIZE) {
                    return true;
                }
            }
        }
        for (int num = 1; num <= SIZE; num++) {
            if (isSafe(row, col, num)) {
                board[row][col] = num;
                if (fillRemaining(row, col + 1)) {
                    return true;
                }
                board[row][col] = 0;
            }
        }
        return false;
    }

    private boolean isSafe(int row, int col, int num) {
        for (int x = 0; x < SIZE; x++) {
            if (board[row][x] == num || board[x][col] == num) {
                return false;
            }
        }
        int startRow = row - row % SUBGRID, startCol = col - col % SUBGRID;
        for (int i = 0; i < SUBGRID; i++) {
            for (int j = 0; j < SUBGRID; j++) {
                if (board[startRow + i][startCol + j] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    private void removeNumbers() {
        int count = 40; // Adjust for medium difficulty
        while (count > 0) {
            int i = random.nextInt(SIZE);
            int j = random.nextInt(SIZE);
            if (board[i][j] != 0) {
                board[i][j] = 0;
                count--;
            }
        }
    }

    public static void printBoard(int[][] board) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static int[][] main() {
        questionmaker generator = new questionmaker();
        int[][] puzzle = generator.generatePuzzle();
        // printBoard(puzzle);
        return puzzle; 
    }
}