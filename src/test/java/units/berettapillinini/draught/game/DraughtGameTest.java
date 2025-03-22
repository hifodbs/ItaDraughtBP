package units.berettapillinini.draught.game;

import org.junit.jupiter.api.Test;
import units.berettapillinini.draught.Chessboard;
import units.berettapillinini.draught.DraughtGame;
import units.berettapillinini.draught.DraughtView;
import units.berettapillinini.draught.bean.COLOR;
import units.berettapillinini.draught.bean.PIECE;
import units.berettapillinini.draught.bean.Position;
import units.berettapillinini.draught.game.util.FakeView;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static units.berettapillinini.draught.game.util.TestUtil.createGrid;

public class DraughtGameTest {

    FakeView view = new FakeView();

    String whiteMakeTwoMoves = "3,5,WP;0,0,BP";

    @Test
    void testMoveNonExistingPiece(){
        DraughtGame draughtGame = new DraughtGame(view,"p1","p2");
        draughtGame.getChessboard().setGrid(createGrid(whiteMakeTwoMoves));
        draughtGame.movePiece("7,7;6,6",COLOR.WHITE);
        assertEquals("No existing piece",view.message);
    }

    @Test
    void testMoveOpponentPiece(){
        DraughtGame draughtGame = new DraughtGame(view,"p1","p2");
        draughtGame.getChessboard().setGrid(createGrid(whiteMakeTwoMoves));
        draughtGame.movePiece("0,0;1,1", COLOR.WHITE);
        assertEquals("Can't move opponent piece",view.message);
    }

    @Test
    void testAnomalousMove(){
        DraughtGame draughtGame = new DraughtGame(view,"p1","p2");
        draughtGame.getChessboard().setGrid(createGrid(whiteMakeTwoMoves));
        draughtGame.movePiece("3,5;3,4",COLOR.WHITE);
        assertEquals("Can't move here",view.message);
    }

    @Test
    void testWhiteMakeDoubleMoves(){
        DraughtGame draughtGame = new DraughtGame(view,"p1","p2");
        draughtGame.getChessboard().setGrid(createGrid(whiteMakeTwoMoves));
        //TODO
    }

}
