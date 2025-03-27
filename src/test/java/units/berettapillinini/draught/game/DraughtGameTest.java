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
    String fifthScenario = "1,7,WP;2,6,BP;2,4,BP;4,4,BP;2,2,BP;4,2,BP";
    String sixthScenario =  "1,1,WP;3,1,BP";
    String seventhScenario =  "2,4,WK;1,3,BP;3,3,BK";
    String eightScenario =  "2,6,WK;4,6,WP;3,5,BP;3,3,BP";
    String nineScenario =  "6,7,WK;5,6,BK;5,4,BK;0,4,WK;1,3,BP;1,1,BK";
    String tenScenario =  "6,7,WK;5,6,BK;5,4,BP;0,4,WK;1,3,BP;1,1,BK;3,4,WK;4,3,BK;6,1,BP";
    String elevenScenario =  "7,3,WK;6,2,BK;3,7,WP;2,6,BP;2,4,BP";

    @Test
    void testMoveNonExistingPiece(){
        DraughtGame draughtGame = new DraughtGame(view,"p1","p2");
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(firstScenario));
        draughtGame.movePiece("7,7;6,6",COLOR.WHITE);
        assertEquals("No existing piece",view.message);
    }

    @Test
    void testMoveOpponentPiece(){
        DraughtGame draughtGame = new DraughtGame(view,"p1","p2");
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(firstScenario));
        draughtGame.movePiece("0,0;1,1", COLOR.WHITE);
        assertEquals("Can't move opponent piece",view.message);
    }

    @Test
    void testAnomalousMove(){
        DraughtGame draughtGame = new DraughtGame(view,"p1","p2");
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(firstScenario));
        draughtGame.movePiece("3,5;3,4",COLOR.WHITE);
        assertEquals("Can't move here",view.message);
    }

    @Test
    void testAvoidCapture(){
        DraughtGame draughtGame = new DraughtGame(view,"p1","p2");
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(secondScenario));
        draughtGame.movePiece("4,4;3,3",COLOR.WHITE);
        assertEquals("Can't move here",view.message);
    }

    @Test
    void testCapturingKingWithPawn(){
        DraughtGame draughtGame = new DraughtGame(view,"p1","p2");
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(thirdScenario));
        draughtGame.movePiece("4,4;6,2",COLOR.WHITE);
        assertEquals("Can't move here",view.message);
    }

    @Test
    void testGoingOutOfBound(){
        DraughtGame draughtGame = new DraughtGame(view,"p1","p2");
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(secondScenario));
        draughtGame.movePiece("4,4;6,2",COLOR.WHITE);
        assertEquals("Black turn",view.message);
        draughtGame.movePiece("0,0;-1,1",COLOR.BLACK);
        assertEquals("Can't move here",view.message);
    }

    @Test
    void testWhiteMakeDoubleMoves(){
        DraughtGame draughtGame = new DraughtGame(view,"p1","p2");
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(firstScenario));
        draughtGame.movePiece("3,5;2,4",COLOR.WHITE);
        assertEquals("Black turn",view.message);
        draughtGame.movePiece("2,4;3,3",COLOR.WHITE);
        assertEquals("ERROR: It's black turn",view.message);
    }
    @Test
    void testCapturingTwoPieces(){
        DraughtGame draughtGame = new DraughtGame(view,"p1","p2");
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(fourthScenario));
        draughtGame.movePiece("3,5;1,3;3,1",COLOR.WHITE);
        assertEquals("White win",view.message);
    }

    @Test
    void testCapturingTwoPiecesInsteadOfThree(){
        DraughtGame draughtGame = new DraughtGame(view,"p1","p2");
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(fifthScenario));
        draughtGame.movePiece("1,7;3,5;5,3",COLOR.WHITE);
        assertEquals("Can't move here",view.message);
    }

    @Test
    void testPromotingPiece(){
        DraughtGame draughtGame = new DraughtGame(view,"p1","p2");
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(sixthScenario));
        draughtGame.movePiece("1,1;2,0",COLOR.WHITE);
        assertEquals("Black turn",view.message);
        assertEquals(Arrays.deepToString(createGrid("2,0,WK;3,1,BP")),Arrays.deepToString(view.grid) ,"La griglia non è come dovrebbe essere");
    }

    @Test
    void testCapturingPawnInsteadOfKing(){
        DraughtGame draughtGame = new DraughtGame(view,"p1","p2");
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(seventhScenario));
        draughtGame.movePiece("2,4;0,2",COLOR.WHITE);
        assertEquals("Can't move here",view.message);
    }

    @Test
    void testCapturingWithPawnInsteadOfKing(){
        DraughtGame draughtGame = new DraughtGame(view,"p1","p2");
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(eightScenario));
        draughtGame.movePiece("4,6;2,4;4,2",COLOR.WHITE);
        assertEquals("Can't move here",view.message);
        draughtGame.movePiece("2,6;4,4;2,2",COLOR.WHITE);
        assertEquals("White win",view.message);
    }

    @Test
    void testCapturingOneKingInsteadTwoKing(){
        DraughtGame draughtGame = new DraughtGame(view,"p1","p2");
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(nineScenario));
        draughtGame.movePiece("0,4;2,2;0,0",COLOR.WHITE);
        assertEquals("Can't move here",view.message);
        draughtGame.movePiece("6,7;4,5;6,3",COLOR.WHITE);
        assertEquals("Black turn",view.message);
        assertEquals(Arrays.deepToString(createGrid("0,4,WK;1,3,BP;1,1,BK;6,3,WK")),Arrays.deepToString(view.grid) ,"La griglia non è come dovrebbe essere");
    }

    @Test
    void testCapturingSamePieceButWrongOrder(){
        DraughtGame draughtGame = new DraughtGame(view,"p1","p2");
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(tenScenario));
        draughtGame.movePiece("0,4;2,2;0,0",COLOR.WHITE);
        assertEquals("Can't move here",view.message);
        draughtGame.movePiece("3,4;5,2;7,0",COLOR.WHITE);
        assertEquals("Black turn",view.message);
        assertEquals(Arrays.deepToString(createGrid("0,4,WK;1,3,BP;1,1,BK;7,0,WK;6,7,WK;5,6,BK;5,4,BP")),Arrays.deepToString(view.grid) ,"La griglia non è come dovrebbe essere");
    }

    @Test
    void testCapturingKingInsteadOfTwoPawn(){
        DraughtGame draughtGame = new DraughtGame(view,"p1","p2");
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(elevenScenario));
        draughtGame.movePiece("7,3;5,1",COLOR.WHITE);
        assertEquals("Can't move here",view.message);
        draughtGame.movePiece("3,7;1,5;3,3",COLOR.WHITE);
        assertEquals("Black turn",view.message);
    }


}
