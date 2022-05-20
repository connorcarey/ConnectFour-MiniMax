import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class MiniMaxAgent extends Agent {

    private final int DEPTH; // How many steps/turns the Agent will look into the future.
    private final int[] COLUMN_PRIORITY; // sorry eric but this is too good not to snag
    private int lastCol;

    /**
     * Constructs an agent that utilizes the minimax algorithm with alpha-beta pruning.
     * This agent simulates every possible move within depth amount of steps and determines if the move is good or bad.
     * @param game Connect4Game
     * @param iAmRed boolean
     */
    public MiniMaxAgent(Connect4Game game, boolean iAmRed) {
        this(game, iAmRed, 9);
    }

    /**
     * Constructs an agent that utilizes the minimax algorithm with alpha-beta pruning.
     * Overloaded constructor that takes a depth.
     * @param game Connect4Game
     * @param iAmRed boolean
     * @param depth int
     */
    public MiniMaxAgent(Connect4Game game, boolean iAmRed, int depth) {
        super(game, iAmRed);
        DEPTH = depth;
        // The player represented as a character.
        int colCnt = myGame.getColumnCount();
        COLUMN_PRIORITY = new int[colCnt];
        for(int i = 0; i < myGame.getColumnCount(); i++){ // Flexible for bigger connect 4 games.
            int index = (colCnt/2+((i%2==0)?i/2:colCnt-(i+1)/2))% colCnt;
            COLUMN_PRIORITY[i] = index;
        }
    }

    /**
     * Plays the move and call minimax to determine what the best move is. Iterates through all open columns
     * to determine which one has the highest score.
     */
    public void move() { // I take no credit for this LMAO
        Connect4Game sim = new Connect4Game(myGame);
        int bestColumn = minimax(sim, DEPTH, true, Integer.MIN_VALUE, Integer.MAX_VALUE)[0];
        if(bestColumn == -1){
            for(int i = COLUMN_PRIORITY.length; i >= 0; i--){
                if(!myGame.getColumn(i).getIsFull())
                    bestColumn = i;
            }
        }
        placeSlot(myGame, myGame.getColumn(bestColumn), iAmRed);
    }

    /**
     * Minimax algorithm
     * Minimizes the possible loss for the worst case. Gains are referred to as "maximum" and losses are referred to as
     * "minimum". Maximum value returns the highest value that the player can be sure to get without knowing the
     * actions of the other player. Lowest value returns the lowest value that the opposing player can be sure to get
     * without knowing the actions of the other player.
     * Alpha-beta pruning is implemented to shorten the length of the binary tree.
     * @param game Connect4Game
     * @param depth int
     * @param isMaximizing boolean
     * @param alpha int
     * @param beta int
     * @return score
     */
    private int[] minimax(Connect4Game game, int depth, boolean isMaximizing, int alpha, int beta) {
        // Check for a leaf node.
        if (depth <= 0 || game.boardFull() || game.gameWon() != 'N') {
            return new int[]{lastCol, evaluate(game, depth)};
        }

        // Returns the largest value that the minimizing player chose.
        // Prunes any branches that obviously do not benefit the minimizing player.
        int bestCol = Integer.MIN_VALUE;
        if (isMaximizing) {
            int highestScore = Integer.MIN_VALUE;
            for (int col : COLUMN_PRIORITY) {
                if(game.getColumn(col).getIsFull())
                    continue;
                int topRow = getTopRow(game, game.getColumn(col));
                lastCol = col;
                placeSlot(game, game.getColumn(col), iAmRed);
                int score = minimax(game, depth-1, false, alpha, beta)[1];
                game.getColumn(col).getSlot(topRow).clear();
                alpha = Math.max(score, alpha);
                if(score > highestScore) {
                    bestCol = col;
                    highestScore = score;
                }
                if(beta <= alpha)
                    break;
            }
            return new int[]{bestCol, highestScore};
        }
        // Returns the smallest value that the maximizing player chose.
        // Prunes any branches that obviously do not benefit the maximizing player.
        else {
            int lowestScore = Integer.MAX_VALUE;
            for (int col : COLUMN_PRIORITY) {
                if (game.getColumn(col).getIsFull())
                    continue;
                int topRow = getTopRow(game, game.getColumn(col));
                lastCol = col;
                placeSlot(game, game.getColumn(col), !iAmRed);
                int score = minimax(game, depth - 1, true, alpha, beta)[1];
                game.getColumn(col).getSlot(topRow).clear();
                beta = Math.min(score, beta);
                if (score < lowestScore) {
                    bestCol = col;
                    lowestScore = score;
                }
                if (beta <= alpha)
                    break;
            }
            return new int[]{bestCol, lowestScore};
        }
    }

    /**
     * Places a slot at the top of the desired column.
     * @param game Connect4Game
     * @param column Connect4Column
     * @param isRed boolean
     */
    private void placeSlot(Connect4Game game, Connect4Column column, boolean isRed) {
        if (isRed) {
            Objects.requireNonNull(getTopSlot(game, column)).addRed();
        } else {
            Objects.requireNonNull(getTopSlot(game, column)).addYellow();
        }
    }

    /**
     * Sets the appropriate evaluation for each group of characters which represent either a row, column, or diagonal.
     * @param set char[]
     * @return evaluation
     */
    private int evaluateSet(char[] set) {
        int countEmpty = countEmpty(set);
        // exit quickly
        if (countEmpty == 4)
            return 0;
        int countMe = countGroup(set, iAmRed);
        int countOther = countGroup(set, !iAmRed);
        if(countMe == 4)
            return 1000000;
        if(countOther == 4)
            return -1000000;
        // evaluate group of 3.
        if(countMe == 3 && countEmpty == 1)
            return 5000;
        else if (countOther == 3 && countEmpty == 1)
            return -5000;
        // evaluate groups of 2
        else if (countMe == 2 && countEmpty == 1)
            return 2500;
        else if (countOther == 2 & countEmpty == 1)
            return -2500;
        else if (countMe == 2 && countEmpty == 2)
            return 2000;
        else if (countOther == 2 && countEmpty == 2)
            return -2000;
        return 0;

    }

    /**
     * Returns the total score of the board, negative if it is in the opposing player's favor and positive if it is
     * in the agent's favor.
     * @param game Connect4Game
     * @return score
     */
    public int evaluate(Connect4Game game, int depth) { // basic heuristics
        int score = 0;
        char[][] board = game.getBoardMatrix();
        int[] openColumns = getOpenColumns(game);
        if(game.gameWon() == (iAmRed ? 'R' : 'Y'))
            return 1000000+depth;
        if(game.gameWon() == (iAmRed ? 'Y' : 'R'))
            return -1000000-depth;
        // Evaluates vertical threats
        for (int openColumn : openColumns) {
            int topIndex = getTopRow(game, game.getColumn(openColumn));
            if (topIndex >= game.getRowCount() - 3)
                continue;
            score += evaluateSet(new char[]{board[topIndex][openColumn], board[topIndex + 1][openColumn],
                    board[topIndex + 2][openColumn], board[topIndex + 3][openColumn]});
        }
        // Evaluates horizontal threats
        for (int i = game.getRowCount() - 1; i >= 0; i--) {
            for (int j = 0; j < game.getColumnCount() - 3; j++) {
                score += evaluateSet(Arrays.copyOfRange(board[i], j, j + 4));
            }
        }
        // Evaluates upper upward diagonals and lower downward diagonals
        for (int i = 2; i < game.getRowCount(); i++) {
            for (int j = 0; j < i - 1; j++) {
                score += evaluateSet(new char[]{board[i][j], board[i - 1][j + 1], board[i - 2][j + 2]});
            }
            for (int j = game.getRowCount()-1; j >= game.getRowCount()-i+2; j--){
                score += evaluateSet(new char[]{board[j][i], board[j-1][i-1], board[j-2][i-2]});
            }
        }
        // Evaluates the lower upward diagonals && upper downward diagonals
        for (int i = 1; i < game.getColumnCount(); i++) {
            for (int j = game.getRowCount() - i; j > 1; j--) {
                score += evaluateSet(new char[]{board[j][i], board[j - 1][i + 1], board[j - 2][i + 2]});
            }
            for (int j = 0; j < game.getRowCount() - i - 1; j++) {
                score += evaluateSet(new char[]{board[j][i], board[j + 1][i + 1], board[j + 2][i + 2]});
            }
        }
        return score;
    }

    /**
     * Returns the index of the first empty space above filled slots. Returns -1 if no indices are found.
     * @param board Connect4Game
     * @param column Connect4Column
     * @return int
     */
    private int getTopRow(Connect4Game board, Connect4Column column) {
        int index = -1;
        for (int i = 0; i < board.getRowCount(); i++) {
            if (!(column.getSlot(i).getIsFilled()))
                index = i;
        }
        return index;
    }

    /**
     * Counts the amount of yellows or reds are in a group of chars.
     * @param set char[]
     * @param isRed boolean
     * @return int
     */
    private int countGroup(char[] set, boolean isRed) {
        int count = 0;
        for (char c : set)
            if (c == (isRed ? 'R' : 'Y'))
                count++;
        return count;
    }

    /**
     * Returns the count of the amount of empty spaces in a group of chars.
     * @param set char[]
     * @return int
     */
    private int countEmpty(char[] set) {
        int count = 0;
        for (char c : set)
            if (c == 'B')
                count++;
        return count;
    }

    /**
     * Returns the slot at the lowest empty position in a column. Null if there are none.
     * @param board Connect4Game
     * @param column Connect4Column
     * @return column
     */
    private Connect4Slot getTopSlot(Connect4Game board, Connect4Column column) {
        return getTopRow(board, column) >= 0 ? column.getSlot(getTopRow(board, column)) : null;
    }

    /**
     * Returns an array containing the indices of rows that are not filled.
     * @param game Connect4Game
     * @return int[]
     */
    private int[] getOpenColumns(Connect4Game game) {
        ArrayList<Integer> countList = new ArrayList<>();
        for (int c = 0; c < game.getColumnCount(); c++)
            if (!game.getColumn(c).getIsFull())
                countList.add(c);

        int[] openColumns = new int[countList.size()];
        for (int i = 0; i < openColumns.length; i++)
            openColumns[i] = countList.get(i);

        return openColumns;
    }

    @Override
    public String getName() {
        return "MinMax Basic";
    }
}


