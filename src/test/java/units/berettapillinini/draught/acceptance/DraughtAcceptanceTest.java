package units.berettapillinini.draught.acceptance;

import org.junit.jupiter.api.Test;
import units.berettapillinini.draught.Chessboard;
import units.berettapillinini.draught.DraughtGame;
import units.berettapillinini.draught.DraughtView;
import units.berettapillinini.draught.PIECE;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;


public class DraughtAcceptanceTest {

    private FakeView view;
    private DraughtGame game;

    String whiteWinThreeTurns = "2,6,BP;5,3,WP";

    @Test
    void personalTest(){
        String str = "";
        String[] sub_str = str.split(";",0);
        for(String s : sub_str)
            System.out.println("Daje");
    }

    @Test
    void testWhiteWin(){
        //TODO
        startGame().verifyChessboard(new Chessboard().getGrid()).verifyMessage("White turn");
        game.getChessboard().setGrid(createGrid(whiteWinThreeTurns));
        whiteTurn("5,3;4,4").verifyChessboard(createGrid("2,6,BP;4,4,WP")).verifyMessage("Black turn");
        blackTurn("2,6;3,5").verifyChessboard(createGrid("3,5,BP;4,4,WP")).verifyMessage("White turn");
        whiteTurn("4,4;2,6").verifyChessboard(createGrid("2,6,WP")).verifyMessage("White win");

    }

    private DraughtAcceptanceTest blackTurn(String move) {
        game.movePiece(move,"black");
        return this;
    }

    private DraughtAcceptanceTest whiteTurn(String move) {
        game.movePiece(move,"white");
        return this;
    }

    private void verifyMessage(String m) {
        assertEquals(view.message,m,"Il messaggio deve essere : "+m);
    }

    private DraughtAcceptanceTest verifyChessboard(PIECE[][] setup) {
        assertEquals(Arrays.deepToString(view.grid), Arrays.deepToString(setup),"La griglia non è come dovrebbe essere");
        return this;
    }

    private DraughtAcceptanceTest startGame() {
        view = new FakeView();
        game = new DraughtGame(view,"p1","p2");
        game.start();
        return this;
    }

    public PIECE[][] createGrid(String setup){
        Chessboard chessboard = new Chessboard();
        int size = chessboard.getGridSize();
        boolean skip = setup.isEmpty();

        String[] allPosition = setup.split(";");
        for(int a = 0; a < size; a++)
            for(int b = 0; b < size; b++) {
                chessboard.setSquare(a,b, PIECE.EMPTY);
                if(!skip)
                    for(String position : allPosition){
                        String[] positionSetup = position.split(",");
                        if(a==Integer.parseInt(positionSetup[0]) && b==Integer.parseInt(positionSetup[1]))
                            chessboard.setSquare(a,b, PIECE.getPiece(positionSetup[2]));
                    }
            }
        return chessboard.getGrid();
    }

    private static class FakeView implements DraughtView{

        PIECE[][] grid;
        String message;
        boolean close = false;

        @Override
        public void on_chessboard_update(PIECE[][] grid) {
            this.grid = grid;
            int size = 8;
            int a;
            System.out.println("-----------------inizio scacchiera----------------");
            for(int c = 0; c < size*2; c++) {
                a = (c-c%2)/2;
                for (int b = 0; b < size; b++) {
                    if(c%2==0) {
                        System.out.print("---");
                    }
                    else {
                        System.out.print("|");
                        switch (grid[a][b]) {
                            case WHITE_PAWN:
                                System.out.print("WP");
                                break;
                            case WHITE_KING:
                                System.out.print("WK");
                                break;
                            case BLACK_PAWN:
                                System.out.print("BP");
                                break;
                            case BLACK_KING:
                                System.out.print("BK");
                                break;
                            default:
                                System.out.print("  ");
                        }
                    }
                }
                System.out.println();
            }

            System.out.println("--------------------fine scacchiera----------------");
        }

        @Override
        public void on_next_turn(String m) {
            message = m;
        }

        @Override
        public  void on_close(){
            close = true;
        }
    }

}