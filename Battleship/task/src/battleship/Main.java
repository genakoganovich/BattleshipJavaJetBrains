package battleship;
import java.util.Scanner;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        game.printField();
        for (Ship ship: Ship.values()) {
            game.askCoordinates(ship);
        }
    }
}


enum Ship {
    AIRCRAFT_CARRIER("Aircraft Carrier", 5),
    BATTLESHIP("Battleship", 4),
    SUBMARINE("Submarine", 3),
    CRUISER("Cruiser", 3),
    DESTROYER("Destroyer", 2);

    String type;
    int size;

    Ship(String type, int size) {
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
    enum Cell {
        NONE(' '), FOG('~'), SHIP('O'), HIT('X'), MISS('M');

        char value;

        Cell(char value) {
            this.value = value;
        }
    }

    private static final int SIZE = 10;
    private char[][] field;

    private void setHeaders() {
        field[0][0] = Cell.NONE.value;
        for (int j = 1; j < field[0].length; j++) {
            field[0][j] = (char) ('0' + j % SIZE);
        }
        for (int i = 1; i < field.length; i++) {
            field[i][0] = (char) ('A' + i - 1);
        }
    }

    private void initializeField() {
        for (int i = 1; i < field.length; i++) {
            Arrays.fill(field[i], 1, field[i].length, Cell.FOG.value);
        }
    }

    public Field() {
        field = new char[SIZE + 1][SIZE + 1];
        setHeaders();
        initializeField();
    }

    public void print() {
        for (char[] row : field) {
            for (char cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    public void setShip(String from, String to) {

    }
}

class Game {
    private Field field;

    public Game() {
        field = new Field();
    }

    public void printField() {
        field.print();
    }

    public void askCoordinates(Ship ship) {
        Scanner scanner = new Scanner(System.in);
        Pattern pattern = Pattern.compile("([A-J])(\\d{1,2})\\s+([A-J])(\\d{1,2})");
        boolean isValidAnswer = false;

        System.out.printf("Enter the coordinates of the %s (%d cells):\n\n",
                ship.type, ship.size);

        while (!isValidAnswer) {
            Matcher matcher = pattern.matcher(scanner.nextLine());

            if (!matcher.matches()) {
                System.out.println("Error! Invalid format");
                continue;
            }

            int fromRow = matcher.group(1).charAt(0) - 'A' + 1;
            int fromColumn = Integer.parseInt(matcher.group(2));
            int toRow = matcher.group(3).charAt(0) - 'A' + 1;
            int toColumn = Integer.parseInt(matcher.group(4));
            int deltaX = toColumn - fromColumn + 1;
            int deltaY = toRow - fromRow + 1;

            if (deltaX != 1 && deltaY != 1) {
                System.out.println("Error! Wrong ship location! Try again:\n");
                continue;
            }

            if (deltaX == 1 && deltaY != ship.size) {
                System.out.printf("Error! Wrong length of the %s! Try again:\n\n",
                        ship.type);
                continue;
            }

            if (deltaX != ship.size && deltaY == 1) {
                continue;
            }
            isValidAnswer = true;
        }
    }
}