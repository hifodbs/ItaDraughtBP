package units.berettapillinini.draught;

import units.berettapillinini.draught.bean.*;

import java.util.Scanner;

public class DraughtConsoleUI implements DraughtView {

    private DraughtGame game;
    private String lastMessage = "";
    private boolean vsCPU;
    private CPU cpu;

    @Override
    public void on_next_turn(String message) {
        System.out.println(message);
        lastMessage = message;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public DraughtConsoleUI() {
    }

    public void startGame(boolean vsCPU){
        this.vsCPU = vsCPU;
        game = new DraughtGame(this, vsCPU);
        cpu = new CPU(COLOR.BLACK, 5, game.getChessboard());
        game.start();
    }

    public static boolean askGameMode() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose the game mode: ");
        System.out.println("1) Play vs CPU");
        System.out.println("2) Play 1 vs 1");
        System.out.print("Insert 1 o 2: ");

        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equals("1")) {
                return true;
            } else if (input.equals("2")) {
                return false;
            } else {
                System.out.print("Invalid input. Insert 1 o 2: ");
            }
        }
    }



    public void run() {

        Scanner scanner = new Scanner(System.in);

        while (true) {
            if(vsCPU && game.getTurn() == COLOR.BLACK) {
                doCPUMove();
                continue;
            }
            System.out.print("Enter move (format: x1,y1;x2,y2;...): ");
            String move = scanner.nextLine();
            if (move.equals("exit")) {
                return;
            } else if (move.matches("(\\d+,\\d+)(;\\d+,\\d+)*")) {
                game.movePiece(move, game.getTurn());
            } else {
                System.out.println("Invalid command, please retry");
            }
        }
    }


    @Override
    public void on_chessboard_update(PIECE[][] grid) {

        System.out.println("\n    0 1 2 3 4 5 6 7");
        System.out.println("   -----------------");

        for (int i = 0; i < 8; i++) {
            System.out.print(i + " | ");
            for (int j = 0; j < 8; j++) {
                System.out.print(getAsciiFromPiece(grid[i][j]) + " ");
            }
            System.out.println("| " + i);
        }

        System.out.println("   -----------------");
        System.out.println("    0 1 2 3 4 5 6 7\n");
    }


    private String getAsciiFromPiece(PIECE piece) {
        if (piece == null || piece == PIECE.EMPTY) return ".";
        COLOR color = piece.getColor();
        GRADE grade = piece.getGrade();
        if (color == COLOR.WHITE) {
            return (grade == GRADE.KING) ? "W" : "w";
        } else {
            return (grade == GRADE.KING) ? "B" : "b";
        }
    }


    @Override
    public void on_close() {
        System.out.println("Game Finished. See you next time :)");


        if (!isInTestEnvironment()) {
            System.exit(0);
        }

    }

    private void doCPUMove() {
        String cpuMove = cpu.getBestMove();  // prendi la mossa migliore della CPU
        if (cpuMove != null && !cpuMove.isEmpty()) {
            game.movePiece(cpuMove, COLOR.BLACK);  // fai muovere la CPU (supponendo che giochi nero)
            on_next_turn("CPU has moved: " + cpuMove);

            // Se dopo la mossa è ancora il turno della CPU, fai un'altra mossa (es. multi salto)
            if (game.getTurn() == COLOR.BLACK) {
                doCPUMove();
            }
        }
    }

    private boolean isInTestEnvironment() {
        return System.getProperty("TEST_ENV") != null;
    }



    protected void exit() {
        System.exit(0);
    }


}