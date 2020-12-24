package battleship;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        game.printField();
        for (ShipType shipType : ShipType.values()) {
            game.askCoordinates(shipType);
            game.printField();
        }
    }
}


enum ShipType {
    AIRCRAFT_CARRIER("Aircraft Carrier", 5),
    BATTLESHIP("Battleship", 4),
    SUBMARINE("Submarine", 3),
    CRUISER("Cruiser", 3),
    DESTROYER("Destroyer", 2);

    String type;
    int size;

    ShipType(String type, int size) {
        this.type = type;
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public int getSize() {
        return size;
    }
}

class Field {
    enum Symbol {
        NONE(' '), FOG('~'), SHIP('O'), HIT('X'), MISS('M');

        char value;

        Symbol(char value) {
            this.value = value;
        }
    }

    private static final int SIZE = 10;
    private char[][] field;

    private void setHeaders() {
        field[0][0] = Symbol.NONE.value;
        for (int j = 1; j < field[0].length; j++) {
            field[0][j] = (char) ('0' + j % SIZE);
        }
        for (int i = 1; i < field.length; i++) {
            field[i][0] = (char) ('A' + i - 1);
        }
    }

    private void initializeField() {
        for (int i = 1; i < field.length; i++) {
            Arrays.fill(field[i], 1, field[i].length, Symbol.FOG.value);
        }
    }

    public Field() {
        field = new char[SIZE + 1][SIZE + 1];
        setHeaders();
        initializeField();
    }

    private void printHeader() {
        for (int j = 0; j < field[0].length - 1; j++) {
            System.out.print(field[0][j] + " ");
        }
        System.out.println("10");
    }

    public void print() {
        System.out.println();
        printHeader();

        for (int i = 1; i < field.length; i++) {
            for (int j = 0; j < field[0].length; j++) {
                System.out.print(field[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void setShip(Cell from, Cell to) {
        for (int i = from.row; i < to.row + 1; i++) {
            for (int j = from.column; j < to.column + 1; j++) {
                field[i][j] = Symbol.SHIP.value;
            }
        }
    }

    private ArrayList<Cell> findNeighbors(Cell from, Cell to) {
        ArrayList<Cell> neighbors = new ArrayList<>();
        for (int i = from.row - 1; i < to.row + 2; i++) {
            for (int j = from.column - 1; j < to.column + 2; j++) {
                if (i < 1 || j < 1 || i > SIZE || j > SIZE) {
                    continue;
                }
                if (i >= from.row && i <= to.row && j >= from.column && j <= to.column) {
                    continue;
                }
                neighbors.add(new Cell(i, j));
            }
        }
        return neighbors;
    }

    public boolean isTooClose(Cell from, Cell to) {
        for (Cell cell : findNeighbors(from, to)) {
            if(field[cell.row][cell.column] == Symbol.SHIP.value) {
                return true;
            }
        }
        return false;
    }
}

class Cell {
    public final int row;
    public final int column;

    public Cell(int row, int column) {
        this.row = row;
        this.column = column;
    }
}

class Parser {
    enum Group {
        ROW_FROM(1), COLUMN_FROM(2), ROW_TO(3), COLUMN_TO(4);
        int value;

        Group(int value) {
            this.value = value;
        }
    }

    private static final String COORDINATE_FORMAT = "([A-J])(\\d{1,2})\\s+([A-J])(\\d{1,2})";
    private static final Pattern PATTERN = Pattern.compile(COORDINATE_FORMAT);
    private Matcher matcher;

    public Parser(String text) {
        matcher = PATTERN.matcher(text);
    }

    public boolean matches() {
        return matcher.matches();
    }

    public Cell getFrom() {
        return createCell(Group.ROW_FROM.value, Group.COLUMN_FROM.value);
    }

    public Cell getTo() {
        return createCell(Group.ROW_TO.value, Group.COLUMN_TO.value);
    }

    private int getX(int xGroup) {
        return matcher.group(xGroup).charAt(0) - 'A' + 1;
    }

    private int getY(int yGroup) {
        return Integer.parseInt(matcher.group(yGroup));
    }

    private Cell createCell(int xGroup, int yGroup) {
        return new Cell(getX(xGroup), getY(yGroup));
    }
}

class Game {
    private Field field;
    private static final String ENTER_COORDINATES
            = "\nEnter the coordinates of the %s (%d cells):\n\n";
    private static final String FORMAT_ERROR
            = "\nError! Invalid format\n";
    private static final String LOCATION_ERROR
            = "\nError! Wrong ship location! Try again:\n";
    private static final String LENGTH_ERROR
            = "\nError! Wrong length of the %s! Try again:\n\n";
    private static final String TOO_CLOSE_ERROR
            = "\nError! You placed it too close to another one. Try again:\n";

    public Game() {
        field = new Field();
    }

    public void printField() {
        field.print();
    }


    public void askCoordinates(ShipType shipType) {
        Scanner scanner = new Scanner(System.in);
        boolean isValidAnswer = false;

        System.out.printf(ENTER_COORDINATES, shipType.type, shipType.size);
        Cell fromCell = null;
        Cell toCell = null;
        while (!isValidAnswer) {
            Parser parser = new Parser(scanner.nextLine());

            if (!parser.matches()) {
                System.out.println(FORMAT_ERROR);
                continue;
            }
            fromCell = parser.getFrom();
            toCell = parser.getTo();

            int minRow = Math.min(fromCell.row, toCell.row);
            int minColumn = Math.min(fromCell.column, toCell.column);
            int maxRow = Math.max(fromCell.row, toCell.row);
            int maxColumn = Math.max(fromCell.column, toCell.column);

            fromCell = new Cell(minRow, minColumn);
            toCell = new Cell(maxRow, maxColumn);

            int deltaX = toCell.column - fromCell.column + 1;
            int deltaY = toCell.row - fromCell.row + 1;

            if (deltaX != 1 && deltaY != 1) {
                System.out.println(LOCATION_ERROR);
                continue;
            }

            if (deltaX == 1 && deltaY != shipType.size) {
                System.out.printf(LENGTH_ERROR, shipType.type);
                continue;
            }

            if (deltaX != shipType.size && deltaY == 1) {
                System.out.printf(LENGTH_ERROR, shipType.type);
                continue;
            }
            if (field.isTooClose(fromCell, toCell)) {
                System.out.println(TOO_CLOSE_ERROR);
                continue;
            }
            isValidAnswer = true;
        }
        field.setShip(fromCell, toCell);
    }
}