package console.module;

import java.util.Random;
import java.util.Scanner;

public class Module {
    private final Dot[][] field;
    private final int SIZE;

    private static final Scanner scanner = new Scanner(System.in);
    private static final Random random = new Random();

    public Module(int size) {
        this.SIZE = size;
        this.field = new Dot[SIZE][SIZE];
    }

    public static void main(String[] args) {
        new Module(3).startGame();
    }

    public void startGame() {
        initField();
        printField();
        while (true) {
            humanMove();
            if (isWin(Dot.X)) {
                System.out.println(StringConst.WIN_X);
                break;
            }
            if (isFieldFull(field)) {
                System.out.println(StringConst.WIN_NO);
                break;
            }
            computerMove();
            if (isWin(Dot.O)) {
                System.out.println(StringConst.WIN_O);
                break;
            }
            if (isFieldFull(field)) {
                System.out.println(StringConst.WIN_NO);
                break;
            }
        }
    }

    private void initField() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                field[i][j] = Dot.E;
            }
        }
    }

    private void humanMove() {
        int x;
        int y;
        do {
            System.out.println(StringConst.ENTER_MOVE + ": ");
            x = scanner.nextInt() - 1;
            y = scanner.nextInt() - 1;
        } while (isNotValid(x, y));
        field[y][x] = Module.Dot.X;
        printField();
    }

    private void computerMove() {
        int[] xy = getBestXY(Dot.O, Dot.X, field);
        int x;
        int y;
        if (xy != null) {
            x = xy[0];
            y = xy[1];
        } else {
            do {
                x = random.nextInt(SIZE);
                y = random.nextInt(SIZE);
            } while (isNotValid(x, y));
        }
        field[y][x] = Dot.O;
        System.out.printf("%s %d, %d%n", StringConst.MOVE_OF_COMP, x + 1, y + 1);
        printField();
    }

    /*
     * Метод, котрый прогнозирует поочередно возможность победить самому, затем противнику.
     * За счет применения рекурсивного алгоритма, вариант победы просчитывается на несколько шагов вперед.
     * По крайней мере, должен просчитывать, вроде))
     */
    private int[] getBestXY(Dot ownSymbol, Dot alienSymbol, Dot[][] field) {
        if (isFieldFull(field)) {
            return null;
        }
        Dot[][] copyField = copyOf(field);
        int[] xy = isSoonWin(Dot.O, field);
        if (xy != null) {
            return xy;
        }
        xy = isSoonWin(Dot.X, field);
        if (xy != null) {
            return xy;
        }
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (copyField[i][j] == Dot.E) {
                    copyField[i][j] = alienSymbol;
                    xy = getBestXY(ownSymbol, alienSymbol, copyField);
                    if (xy == null) {
                        copyField[i][j] = ownSymbol;
                        xy = getBestXY(ownSymbol, alienSymbol, copyField);
                    }
                    if (xy != null) {
                        return xy;
                    }
                }
            }
        }
        return null;
    }

    private int[] isSoonWin(Dot symbol, Dot[][] field) {
        Dot[][] copyField = copyOf(field);
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (copyField[i][j] == Dot.E) {
                    copyField[i][j] = symbol;
                    if (isWin(copyField, symbol)) {
                        return new int[]{j, i};
                    }
                    copyField[i][j] = Dot.E;
                }
            }
        }
        return null;
    }

    private boolean isWin(Dot[][] field, Dot symbol) {
        boolean diagonal1 = true;
        boolean diagonal2 = true;
        boolean horizontal;
        boolean vertical;
        for (int i = 0; i < SIZE; i++) {
            horizontal = true;
            vertical = true;
            for (int j = 0; j < SIZE; j++) {
                horizontal &= field[i][j] == symbol;
                vertical &= field[j][i] == symbol;
                if (!horizontal && !vertical) {
                    break;
                }
            }
            if (horizontal || vertical) {
                return true;
            }
            diagonal1 &= field[i][i] == symbol;
            diagonal2 &= field[i][this.SIZE - i - 1] == symbol;
        }
        return diagonal1 || diagonal2;
    }

    private boolean isWin(Dot symbol) {
        return isWin(field, symbol);
    }

    private boolean isNotValid(int x, int y) {
        return x < 0 || x >= SIZE || y < 0 || y >= SIZE || field[y][x] != Dot.E;
    }

    private boolean isFieldFull(Dot[][] field) {
        for (Dot[] row : field) {
            for (Dot cell : row) {
                if (cell == Dot.E) {
                    return false;
                }
            }
        }
        return true;
    }

    private void printField() {
        for (int i = 0; i <= SIZE; i++) {
            System.out.print(i + " ");
        }
        System.out.println();
        for (int i = 0; i < SIZE; i++) {
            System.out.print(i + 1 + " ");
            for (int j = 0; j < SIZE; j++) {
                System.out.print(field[i][j] + " ");
            }
            System.out.println();
        }
    }

    private Dot[][] copyOf(Dot[][] field) {
        Dot[][] copyField = new Dot[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                copyField[i][j] = field[i][j];
            }
        }
        return copyField;
    }

    private enum Dot {
        X('X'), O('O'), E('•');

        private final char symbol;

        Dot(char symbol) {
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return Character.toString(symbol);
        }
    }

    private static class StringConst {
        public static final String ENTER_MOVE = "Введите координаты X и Y";
        public static final String MOVE_OF_COMP = "Ход компьютера";
        public static final String WIN_X = "Победил X!";
        public static final String WIN_O = "Победил O!";
        public static final String WIN_NO = "Ничья!";
    }
}