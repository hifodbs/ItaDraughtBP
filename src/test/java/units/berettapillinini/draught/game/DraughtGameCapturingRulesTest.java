package units.berettapillinini.draught.game;

import org.junit.jupiter.api.Test;
import units.berettapillinini.draught.DraughtGame;
import units.berettapillinini.draught.bean.COLOR;
import units.berettapillinini.draught.game.util.FakeView;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static units.berettapillinini.draught.game.util.TestUtil.createGrid;

public class DraughtGameCapturingRulesTest {

    FakeView view = new FakeView();

    String firstScenario =  "2,4,WK;1,3,BP;3,3,BK";
    String thirdScenario =  "2,6,WK;4,6,WP;3,5,BP;3,3,BP";
    String fourthScenario =  "6,7,WK;5,6,BK;5,4,BK;0,4,WK;1,3,BP;1,1,BK";
    String fifthScenario =  "6,7,WK;5,6,BK;5,4,BP;0,4,WK;1,3,BP;1,1,BK;3,4,WK;4,3,BK;6,1,BP";
    String secondScenario = "1,7,WP;2,6,BP;2,4,BP;4,4,BP;2,2,BP;4,2,BP";
    String sixthScenario =  "7,3,WK;6,2,BK;3,7,WP;2,6,BP;2,4,BP";


    @Test
    void testCapturingPawnInsteadOfKing(){
        DraughtGame draughtGame = new DraughtGame(view);
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(firstScenario));
        draughtGame.movePiece("2,4;0,2", COLOR.WHITE);
        assertEquals("Can't move here",view.message);
    }

    @Test
    void testCapturingTwoPiecesInsteadOfThree(){
        DraughtGame draughtGame = new DraughtGame(view);
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(secondScenario));
        draughtGame.movePiece("1,7;3,5;5,3",COLOR.WHITE);
        assertEquals("Can't move here",view.message);
    }

    @Test
    void testCapturingWithPawnInsteadOfKing(){
        DraughtGame draughtGame = new DraughtGame(view);
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(thirdScenario));
        draughtGame.movePiece("4,6;2,4;4,2",COLOR.WHITE);
        assertEquals("Can't move here",view.message);
        draughtGame.movePiece("2,6;4,4;2,2",COLOR.WHITE);
        assertEquals("White win",view.message);
    }

    @Test
    void testCapturingOneKingInsteadTwoKing(){
        DraughtGame draughtGame = new DraughtGame(view);
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(fourthScenario));
        draughtGame.movePiece("0,4;2,2;0,0",COLOR.WHITE);
        assertEquals("Can't move here",view.message);
        draughtGame.movePiece("6,7;4,5;6,3",COLOR.WHITE);
        assertEquals("Black turn",view.message);
        assertEquals(Arrays.deepToString(createGrid("0,4,WK;1,3,BP;1,1,BK;6,3,WK")),Arrays.deepToString(view.grid) ,"La griglia non è come dovrebbe essere");
    }

    @Test
    void testCapturingSamePieceButWrongOrder(){
        DraughtGame draughtGame = new DraughtGame(view);
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(fifthScenario));
        draughtGame.movePiece("0,4;2,2;0,0",COLOR.WHITE);
        assertEquals("Can't move here",view.message);
        draughtGame.movePiece("3,4;5,2;7,0",COLOR.WHITE);
        assertEquals("Black turn",view.message);
        assertEquals(Arrays.deepToString(createGrid("0,4,WK;1,3,BP;1,1,BK;7,0,WK;6,7,WK;5,6,BK;5,4,BP")),Arrays.deepToString(view.grid) ,"La griglia non è come dovrebbe essere");
    }

    @Test
    void testCapturingKingInsteadOfTwoPawn(){
        DraughtGame draughtGame = new DraughtGame(view);
        draughtGame.start();
        draughtGame.getChessboard().setGrid(createGrid(sixthScenario));
        draughtGame.movePiece("7,3;5,1",COLOR.WHITE);
        assertEquals("Can't move here",view.message);
        draughtGame.movePiece("3,7;1,5;3,3",COLOR.WHITE);
        assertEquals("Black turn",view.message);
    }
}
