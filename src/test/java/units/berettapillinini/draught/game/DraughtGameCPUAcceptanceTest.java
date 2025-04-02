package units.berettapillinini.draught.game;

import org.junit.jupiter.api.Test;
import units.berettapillinini.draught.CPU;
import units.berettapillinini.draught.Chessboard;
import units.berettapillinini.draught.DraughtGame;
import units.berettapillinini.draught.bean.COLOR;
import units.berettapillinini.draught.bean.PIECE;
import units.berettapillinini.draught.game.util.FakeView;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static units.berettapillinini.draught.game.util.TestUtil.createGrid;


public class DraughtGameCPUAcceptanceTest {

    private FakeView view;
    private DraughtGame game;
    private CPU cpu;

    String whiteWinScenario = "3,1,BK;5,1,WP;3,3,WP;5,3,WK;0,4,BP;2,4,WP;1,5,BK;3,5,BP";
    // MOVES = 15-11, 6x15; 14-11, 21x14 (or15x6); 7-3, 15x6 (or 14x7); 3x26.


    // CPU can't go further than 7 moves

    @Test
    void testWhiteWin() {
        //TODO
        startGame().verifyChessboard(new Chessboard().getGrid());
        game.getChessboard().setGrid(createGrid(whiteWinScenario));
        cpuTurn().verifyMessage("Black turn").verifyChessboard(createGrid("3,1,BK;5,1,WP;3,3,WP;4,2,WK;0,4,BP;2,4,WP;1,5,BK;3,5,BP"));
        blackTurn("3,1;5,3").verifyMessage("White turn").verifyChessboard(createGrid("5,3,BK;5,1,WP;3,3,WP;0,4,BP;2,4,WP;1,5,BK;3,5,BP"));

        cpuTurn().verifyMessage("Black turn").verifyChessboard(createGrid("5,3,BK;5,1,WP;4,2,WP;0,4,BP;2,4,WP;1,5,BK;3,5,BP"));
        blackTurn("1,5;3,3").verifyMessage("White turn").verifyChessboard(createGrid("5,3,BK;5,1,WP;4,2,WP;0,4,BP;3,3,BK;3,5,BP"));

        cpuTurn().verifyMessage("Black turn").verifyChessboard(createGrid("5,3,BK;4,0,WK;4,2,WP;0,4,BP;3,3,BK;3,5,BP"));
        blackTurn("5,3;3,1").verifyMessage("White turn").verifyChessboard(createGrid("3,1,BK;4,0,WK;0,4,BP;3,3,BK;3,5,BP"));

        cpuTurn().verifyMessage("Black turn").verifyChessboard(createGrid("2,6,WK;0,4,BP"));
        blackTurn("0,4;1,5").verifyMessage("White turn").verifyChessboard(createGrid("2,6,WK;1,5,BP"));

        cpuTurn().verifyMessage("White win").verifyChessboard(createGrid("0,4,WK"));


    }

    private DraughtGameCPUAcceptanceTest blackTurn(String move) {
        game.movePiece(move, COLOR.BLACK);
        return this;
    }

    private DraughtGameCPUAcceptanceTest cpuTurn() {
        game.movePiece(cpu.getBestMove(), COLOR.WHITE);
        return this;
    }

    private DraughtGameCPUAcceptanceTest verifyMessage(String m) {
        assertEquals(m, view.message, "Il messaggio deve essere : " + m);
        return this;
    }

    private DraughtGameCPUAcceptanceTest verifyChessboard(PIECE[][] setup) {
        assertEquals(Arrays.deepToString(setup), Arrays.deepToString(view.grid), "La griglia non è come dovrebbe essere");
        return this;
    }

    private DraughtGameCPUAcceptanceTest startGame() {
        view = new FakeView();
        game = new DraughtGame(view);
        cpu = new CPU(COLOR.WHITE, 9, game.getChessboard());
        game.start();
        return this;
    }
}