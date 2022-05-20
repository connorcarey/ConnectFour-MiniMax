public class MiniAiyazMaxAgent extends Agent {
    private final int rows, columns, steps;
    private int turns;
    private int[] blankSpots;

    public MiniAiyazMaxAgent(Connect4Game game, boolean iAmRed, int steps) {
        super(game, iAmRed);
        rows = game.getRowCount();
        columns = game.getColumnCount();
        this.steps = steps;
    }

    @Override
    public void move() {
        Connect4Game simulation = new Connect4Game(myGame);
        blankSpots = new int[columns];
        turns = 0;
        for (int i = 0; i < columns; i++) {
            for (int j = rows - 1; j >= -1; j--) {
                if (j != -1 && simulation.getColumn(i).getSlot(j).getIsFilled()) continue;
                blankSpots[i] = j;
                break;
            }
        }
        if (blankSpots[columns / 2] >= rows - 2) {
            add(myGame, columns / 2, iAmRed);
            return;
        }
        int index = 0;
        float max = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < columns; i++) {
            if (blankSpots[i] == -1) continue;
            add(simulation, i, iAmRed);
            float temp = minimax(simulation, steps, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, false);
            remove(simulation, i);
            if (temp > max || temp == max && Math.random() < 0.5) {
                index = i;
                max = temp;
            }
            if (max == Float.POSITIVE_INFINITY) break;
        }
        add(myGame, index, iAmRed);
    }

    private float minimax(Connect4Game simulation, int limit, float alpha, float beta, boolean maximizing) {
        char winner = simulation.gameWon();
        if (winner != 'N') {
            return winner == (iAmRed ? 'R' : 'Y') ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
        }
        if (limit == 0) {
            if (iAmRed) return staticEval(simulation, 'R') - staticEval(simulation, 'Y');
            return staticEval(simulation, 'Y') - staticEval(simulation, 'R');
        }

        if (maximizing) {
            float max = Float.NEGATIVE_INFINITY;
            for (int i = 0; i < columns; i++) {
                if (blankSpots[i] == -1) continue;
                add(simulation, i, iAmRed);
                float temp = minimax(simulation, limit - 1, alpha, beta, false);
                remove(simulation, i);
                if (temp > max) max = temp;
                alpha = Math.max(alpha, max);
                if (beta <= alpha) break;
            }
            return max;
        } else {
            float min = Float.POSITIVE_INFINITY;
            for (int i = 0; i < columns; i++) {
                if (blankSpots[i] == -1) continue;
                add(simulation, i, !iAmRed);
                float temp = minimax(simulation, limit - 1, alpha, beta, true);
                remove(simulation, i);
                if (temp < min) min = temp;
                beta = Math.min(beta, min);
                if (beta <= alpha) break;
            }
            return min;
        }
    }

    private float staticEval(Connect4Game simulation, char target) {
        int one = 0;
        int two = 0;
        char[][] mat = simulation.getBoardMatrix();

        for (int r = 0; r < rows; r++) {
            int blank = 0;
            int found = 0;
            for (int c = 0; c < columns; c++) {
                if (mat[r][c] == target) {
                    found++;
                    if (found + blank == 4) {
                        if (blank == 1) one++;
                        else two++;
                        if (mat[r][c - 3] == 'B') blank--;
                        else found--;
                    }
                } else {
                    if (mat[r][c] == 'B') {
                        blank++;
                        if (blank == 3) blank--;
                        if (found + blank == 4) {
                            if (blank == 1) one++;
                            else two++;
                            if (mat[r][c - 3] == 'B') blank--;
                            else found--;
                        }
                    } else {
                        blank = 0;
                        found = 0;
                    }
                }
            }
        }

        for (int c = 0; c < columns; c++) {
            int blank = 0;
            int found = 0;
            for (int r = 0; r < rows; r++) {
                if (mat[r][c] == target) {
                    found++;
                    if (found + blank == 4) {
                        if (blank == 1) one++;
                        else two++;
                        if (mat[r - 3][c] == 'B') blank--;
                        else found--;
                    }
                } else {
                    if (mat[r][c] == 'B') {
                        blank++;
                        if (blank == 3) blank--;
                        if (found + blank == 4) {
                            if (blank == 1) one++;
                            else two++;
                            if (mat[r - 3][c] == 'B') blank--;
                            else found--;
                        }
                    } else {
                        blank = 0;
                        found = 0;
                    }
                }
            }
        }
        for (int r = 0; r < rows - 3; r++) {
            int blank = 0;
            int found = 0;
            for (int c = 0; c < columns && r + c < rows; c++) {
                if (mat[r + c][c] == target) {
                    found++;
                    if (found + blank == 4) {
                        if (blank == 1) one++;
                        else two++;
                        if (mat[r + c - 3][c - 3] == 'B') blank--;
                        else found--;
                    }
                } else {
                    if (mat[r + c][c] == 'B') {
                        blank++;
                        if (blank == 3) blank--;
                        if (found + blank == 4) {
                            if (blank == 1) one++;
                            else two++;
                            if (mat[r + c - 3][c - 3] == 'B') blank--;
                            else found--;
                        }
                    } else {
                        blank = 0;
                        found = 0;
                    }
                }
            }
        }
        for (int c = 1; c < columns - 3; c++) {
            int blank = 0;
            int found = 0;
            for (int r = 0; r < rows && r + c < columns; r++) {
                if (mat[r][r + c] == target) {
                    found++;
                    if (found + blank == 4) {
                        if (blank == 1) one++;
                        else two++;
                        if (mat[r - 3][r + c - 3] == 'B') blank--;
                        else found--;
                    }
                } else {
                    if (mat[r][r + c] == 'B') {
                        blank++;
                        if (blank == 3) blank--;
                        if (found + blank == 4) {
                            if (blank == 1) one++;
                            else two++;
                            if (mat[r - 3][r + c - 3] == 'B') blank--;
                            else found--;
                        }
                    } else {
                        blank = 0;
                        found = 0;
                    }
                }
            }
        }
        for (int r = rows - 1; r >= 3; r--) {
            int blank = 0;
            int found = 0;
            for (int c = 0; c < columns && r - c >= 0; c++) {
                if (mat[r - c][c] == target) {
                    found++;
                    if (found + blank == 4) {
                        if (blank == 1) one++;
                        else two++;
                        if (mat[r - c + 3][c - 3] == 'B') blank--;
                        else found--;
                    }
                } else {
                    if (mat[r - c][c] == 'B') {
                        blank++;
                        if (blank == 3) blank--;
                        if (found + blank == 4) {
                            if (blank == 1) one++;
                            else two++;
                            if (mat[r - c + 3][c - 3] == 'B') blank--;
                            else found--;
                        }
                    } else {
                        blank = 0;
                        found = 0;
                    }
                }
            }
        }
        for (int c = 1; c < columns - 3; c++) {
            int blank = 0;
            int found = 0;
            for (int r = rows - 1; r >= 0 && c + rows - 1 - r < columns; r--) {
                if (mat[r][c + rows - 1 - r] == target) {
                    found++;
                    if (found + blank == 4) {
                        if (blank == 1) one++;
                        else two++;
                        if (mat[r + 3][c + (rows - 1 - r) - 3] == 'B') blank--;
                        else found--;
                    }
                } else {
                    if (mat[r][c + rows - 1 - r] == 'B') {
                        blank++;
                        if (blank == 3) blank--;
                        if (found + blank == 4) {
                            if (blank == 1) one++;
                            else two++;
                            if (mat[r + 3][c + (rows - 1 - r) - 3] == 'B') blank--;
                            else found--;
                        }
                    } else {
                        blank = 0;
                        found = 0;
                    }
                }
            }
        }
        return one * one + (float)Math.pow(two, 1.5f);
    }

    private void add(Connect4Game game, int column, boolean red) {
        while (blankSpots[column] == -1) column++;
        if (red) game.getColumn(column).getSlot(blankSpots[column]).addRed();
        else game.getColumn(column).getSlot(blankSpots[column]).addYellow();
        blankSpots[column]--;
    }

    private void remove(Connect4Game game, int column) {
        blankSpots[column]++;
        game.getColumn(column).getSlot(blankSpots[column]).clear();
    }

    @Override
    public String getName() {
        return "Aiyaz Agent";
    }
}
