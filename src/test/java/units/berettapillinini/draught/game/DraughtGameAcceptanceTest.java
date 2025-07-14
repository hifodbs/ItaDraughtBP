package units.berettapillinini.draught.game;

import org.junit.jupiter.api.Test;
import units.berettapillinini.draught.Chessboard;
import units.berettapillinini.draught.DraughtGame;
import units.berettapillinini.draught.bean.COLOR;
import units.berettapillinini.draught.bean.PIECE;
import units.berettapillinini.draught.game.util.FakeView;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static units.berettapillinini.draught.game.util.TestUtil.createGrid;


class DraughtGameAcceptanceTest {

    private FakeView view;
    private DraughtGame game;

    String whiteWinThreeTurns = "6,2,BP;3,5,WP";


    @Test
    void testWhiteWin(){
        //TODO
        startGame().verifyChessboard(new Chessboard().getGrid()).verifyMessage("White turn");
        game.getChessboard().setGrid(createGrid(whiteWinThreeTurns));
        whiteTurn("3,5;4,4").verifyMessage("Black turn").verifyChessboard(createGrid("6,2,BP;4,4,WP"));
        blackTurn("6,2;5,3").verifyMessage("White turn").verifyChessboard(createGrid("5,3,BP;4,4,WP"));
        whiteTurn("4,4;6,2").verifyMessage("White win").verifyChessboard(createGrid("6,2,WP"));

    }

    private DraughtGameAcceptanceTest blackTurn(String move) {
        game.movePiece(move, COLOR.BLACK);
        return this;
    }

    private DraughtGameAcceptanceTest whiteTurn(String move) {
        game.movePiece(move,COLOR.WHITE);
        return this;
    }

    private DraughtGameAcceptanceTest verifyMessage(String m) {
        assertEquals(m,view.message,"Il messaggio deve essere : "+m);
        return this;
    }

    private DraughtGameAcceptanceTest verifyChessboard(PIECE[][] setup) {
        assertEquals(Arrays.deepToString(setup),Arrays.deepToString(view.grid) ,"La griglia non è come dovrebbe essere");
        return this;
    }

    private DraughtGameAcceptanceTest startGame() {
        view = new FakeView();
        game = new DraughtGame(view, false);
        game.start();
        return this;
    }


}