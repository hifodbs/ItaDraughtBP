package units.berettapillinini.draught.game;

import org.junit.jupiter.api.Test;
import units.berettapillinini.draught.Chessboard;
import units.berettapillinini.draught.DraughtGame;
import units.berettapillinini.draught.DraughtView;
import units.berettapillinini.draught.bean.COLOR;
import units.berettapillinini.draught.bean.PIECE;
import units.berettapillinini.draught.bean.Position;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DraughtGameTest {

    FakeView view = new FakeView();

    String whiteMakeTwoMoves = "5,3,WP;0,0,BP";

    private static class FakeView implements DraughtView {

        PIECE[][] grid;
        String message;
        boolean close = false;

        @Override
        public void on_chessboard_update(PIECE[][] grid) {
            this.grid = grid;
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

    public PIECE[][] createGrid(String setup){
        Chessboard chessboard = new Chessboard();
        int size = chessboard.getGridSize();
        boolean skip = setup.isEmpty();
        Position p = new Position(0,0);
        String[] allPosition = setup.split(";");
        for(int a = 0; a < size; a++)
            for(int b = 0; b < size; b++) {
                p.setPosition(b,a);
                chessboard.setSquare(p, PIECE.EMPTY);
                if(!skip)
                    for(String position : allPosition){
                        String[] positionSetup = position.split(",");
                        if(a==Integer.parseInt(positionSetup[0]) && b==Integer.parseInt(positionSetup[1]))
                            chessboard.setSquare(p, PIECE.getPiece(positionSetup[2]));
                    }
            }
        return chessboard.getGrid();
    }

    @Test
    void testMoveNonExistingPiece(){
        DraughtGame draughtGame = new DraughtGame(view,"p1","p2");
        draughtGame.getChessboard().setGrid(createGrid(whiteMakeTwoMoves));
        draughtGame.movePiece("0,0;1,1",COLOR.WHITE);
        assertEquals("Can't move opponent piece",view.message);
    }

    @Test
    void testMoveOpponentPiece(){
        DraughtGame draughtGame = new DraughtGame(view,"p1","p2");
        draughtGame.getChessboard().setGrid(createGrid(whiteMakeTwoMoves));
        draughtGame.movePiece("5,1;4,2", COLOR.WHITE);
        assertEquals("No existing piece",view.message);
    }

    @Test
    void testAnomalousMove(){
        DraughtGame draughtGame = new DraughtGame(view,"p1","p2");
        draughtGame.getChessboard().setGrid(createGrid(whiteMakeTwoMoves));
        draughtGame.movePiece("5,3;4,3",COLOR.WHITE);
        assertEquals("Can't move here",view.message);
    }

    @Test
    void testWhiteMakeDoubleMoves(){
        DraughtGame draughtGame = new DraughtGame(view,"p1","p2");
        draughtGame.getChessboard().setGrid(createGrid(whiteMakeTwoMoves));
        //TODO
    }

}
