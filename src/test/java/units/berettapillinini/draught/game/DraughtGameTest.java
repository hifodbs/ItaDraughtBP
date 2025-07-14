package units.berettapillinini.draught.game;

import org.junit.jupiter.api.Test;
import units.berettapillinini.draught.DraughtGame;
import units.berettapillinini.draught.bean.COLOR;
import units.berettapillinini.draught.game.util.FakeView;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static units.berettapillinini.draught.game.util.TestUtil.createGrid;

public class DraughtGameTest {

    FakeView view = new FakeView();

    String firstScenario = "3,5,WP;0,0,BP";
    String secondScenario = "4,4,WP;5,3,BP;0,0,BP";
    String thirdScenario = "4,4,WP;5,3,BK;0,0,BP";
    String fourthScenario = "3,5,WP;2,4,BP;2,2,BP";
    String fifthScenario =  "1,1,WP;3,1,BP";
    String sixthScenario =  "1,1,WP;2,2,WK;3,3,WK;4,4,BP;5,5,BP;6,6,BP;7,7,BK;0,0,BK;3,1,BK;5,3,BK";
    String seventhScenario =  "7,1,BP;7,3,WK";

    @Test
    void testMoveNonExistingPiece(){
        DraughtGame draughtGame = new DraughtGame(view, false);
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(firstScenario));
        draughtGame.movePiece("7,7;6,6",COLOR.WHITE);
        assertEquals("No existing piece",view.message);
    }

    @Test
    void testMoveOpponentPiece(){
        DraughtGame draughtGame = new DraughtGame(view, false);
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(firstScenario));
        draughtGame.movePiece("0,0;1,1", COLOR.WHITE);
        assertEquals("Can't move opponent piece",view.message);
    }

    @Test
    void testAnomalousMove(){
        DraughtGame draughtGame = new DraughtGame(view, false);
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(firstScenario));
        draughtGame.movePiece("3,5;3,4",COLOR.WHITE);
        assertEquals("Can't move here",view.message);
    }

    @Test
    void testAvoidCapture(){
        DraughtGame draughtGame = new DraughtGame(view, false);
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(secondScenario));
        draughtGame.movePiece("4,4;3,3",COLOR.WHITE);
        assertEquals("Can't move here",view.message);
    }

    @Test
    void testCapturingKingWithPawn(){
        DraughtGame draughtGame = new DraughtGame(view, false);
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(thirdScenario));
        draughtGame.movePiece("4,4;6,2",COLOR.WHITE);
        assertEquals("Can't move here",view.message);
    }

    @Test
    void testGoingOutOfBound(){
        DraughtGame draughtGame = new DraughtGame(view, false);
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(secondScenario));
        draughtGame.movePiece("4,4;6,2",COLOR.WHITE);
        assertEquals("Black turn",view.message);
        draughtGame.movePiece("0,0;-1,1",COLOR.BLACK);
        assertEquals("Can't move here",view.message);
    }

    @Test
    void testWhiteMakeDoubleMoves(){
        DraughtGame draughtGame = new DraughtGame(view, false);
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(firstScenario));
        draughtGame.movePiece("3,5;2,4",COLOR.WHITE);
        assertEquals("Black turn",view.message);
        draughtGame.movePiece("2,4;3,3",COLOR.WHITE);
        assertEquals("ERROR: It's black turn",view.message);
    }

    @Test
    void testCapturingTwoPieces(){
        DraughtGame draughtGame = new DraughtGame(view, false);
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(fourthScenario));
        draughtGame.movePiece("3,5;1,3;3,1",COLOR.WHITE);
        assertEquals("White win",view.message);
    }

    @Test
    void testPromotingPiece(){
        DraughtGame draughtGame = new DraughtGame(view, false);
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(fifthScenario));
        draughtGame.movePiece("1,1;2,0",COLOR.WHITE);
        assertEquals("Black turn",view.message);
        assertEquals(Arrays.deepToString(createGrid("2,0,WK;3,1,BP")),Arrays.deepToString(view.grid) ,"La griglia non è come dovrebbe essere");
    }

    @Test
    void testValuatingChessboard(){
        DraughtGame draughtGame = new DraughtGame(view, false);
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(sixthScenario));
        assertEquals(-8,draughtGame.getChessboard().getVal());
    }

    @Test
    void testWhiteWinForNonLegalMoveForBlack(){
        DraughtGame draughtGame = new DraughtGame(view, false);
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(seventhScenario));
        draughtGame.movePiece("7,3;6,2",COLOR.WHITE);
        assertEquals("White win",view.message);
    }



}
