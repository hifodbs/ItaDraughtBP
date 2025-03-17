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
    void testWhiteWin(){
        //TODO
        startGame().verifyBeginChessboard().verifyMessage("White turn");
        setChessboard(whiteWinThreeTurns);
        view.on_chessboard_update(game.getChessboard().getGrid());
    }

    private void setChessboard(String setup) {
        Chessboard chessboard = game.getChessboard();
        int size = chessboard.getGridSize();
        String[] allPosition = setup.split(";");
        for(int a = 0; a < size; a++)
            for(int b = 0; b < size; b++) {
                chessboard.setSquare(a,b, PIECE.EMPTY);
                for(String position : allPosition){
                    String[] positionSetup = position.split(",");
                    if(a==Integer.parseInt(positionSetup[0]) && b==Integer.parseInt(positionSetup[1]))
                        chessboard.setSquare(a,b, PIECE.getPiece(positionSetup[2]));
                }
            }
    }

    private void verifyMessage(String m) {
        assertEquals(view.message,m,"Il messaggio deve essere : "+m);
    }

    private DraughtAcceptanceTest verifyBeginChessboard() {
        assertEquals(Arrays.deepToString(view.grid), Arrays.deepToString(new Chessboard().getGrid()),"La griglia deve essre vuota");
        return this;
    }

    private DraughtAcceptanceTest startGame() {
        view = new FakeView();
        game = new DraughtGame(view,"p1","p2");
        game.start();
        return this;
    }

    private static class FakeView implements DraughtView{

        boolean shown = false;
        PIECE[][] grid;
        String message;

        @Override
        public void on_chessboard_update(PIECE[][] grid) {
            this.grid = grid;
            int size = 8;
            int a = 0;
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
        }

        @Override
        public void on_next_turn(String m) {
            message = m;
        }
    }

}