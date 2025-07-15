package units.berettapillinini.draught.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import units.berettapillinini.draught.Chessboard;
import units.berettapillinini.draught.DraughtController;
import units.berettapillinini.draught.bean.COLOR;
import units.berettapillinini.draught.bean.PIECE;
import units.berettapillinini.draught.game.util.FakeView;

import static org.junit.jupiter.api.Assertions.*;

class DraughtControllerTest {
    private DraughtController controller;

    @BeforeEach
    void setup() {
        FakeView testView = new FakeView();
        controller = new DraughtController(testView, false);
        controller.startGame();
    }

    @Test
    void testValidMoveChangesTurn() {
        FakeView view = new FakeView();
        DraughtController controller = new DraughtController(view, false);
        controller.startGame();

        controller.handleCellClick(2, 5);
        controller.handleCellClick(3, 4);


        assertNotNull(view.grid);
        assertEquals(COLOR.BLACK, controller.getGame().getTurn());

    }

    @Test
    void testBoardChangesAfterMove() {
        PIECE[][] boardBefore = copyBoard(controller.getGame().getChessboard().getGrid());


        controller.handleCellClick(2, 3);
        controller.handleCellClick(3, 4);


        Chessboard boardAfter = controller.getGame().getChessboard();


        assertTrue(boardsAreEqual(boardBefore, boardAfter.getGrid()), "Chessboard should change after the move");


        assertNotSame(PIECE.EMPTY, boardAfter.getGrid());

    }

    private PIECE[][] copyBoard(PIECE[][] original) {
        PIECE[][] copy = new PIECE[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, 8);
        }
        return copy;
    }

    private boolean boardsAreEqual(PIECE[][] b1, PIECE[][] b2) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (b1[x][y] != b2[x][y]) return false;
            }
        }
        return true;
    }

}
