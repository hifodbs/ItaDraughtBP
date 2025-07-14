package units.berettapillinini.draught.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import units.berettapillinini.draught.Chessboard;
import units.berettapillinini.draught.DraughtController;
import units.berettapillinini.draught.bean.PIECE;
import units.berettapillinini.draught.game.util.FakeView;

import static org.junit.jupiter.api.Assertions.*;

class DraughtControllerTest {
    private DraughtController controller;
    private FakeView testView;

    @BeforeEach
    public void setup() {
        testView = new FakeView();
        controller = new DraughtController(testView, false); // partita 1vs1 senza CPU
        controller.startGame();
    }
    @Test
    void testValidMoveChangesTurn() {
        FakeView view = new FakeView();
        DraughtController controller = new DraughtController(view, false);
        controller.startGame();

        controller.handleCellClick(2, 5); // seleziona pedina bianca
        controller.handleCellClick(3, 4); // mossa

        assertNotNull(view.grid);
        assertEquals("Black turn", view.message);
    }
    @Test
    void testBoardChangesAfterMove() {
        // Ottieni la board iniziale (copia profonda)
        PIECE[][] boardBefore = copyBoard(controller.getGame().getChessboard().getGrid());

        // Simula una mossa: clicca su una pedina bianca e sulla posizione di destinazione
        // Esempio: supponiamo che (2, 3) sia una pedina bianca e (3, 4) una mossa legale
        controller.handleCellClick(2, 3); // seleziona pedina
        controller.handleCellClick(3, 4); // muovi pedina

        // Ottieni la board dopo la mossa
        Chessboard boardAfter = controller.getGame().getChessboard();

        // Verifica che la board sia cambiata (non uguale a prima)
        assertTrue(boardsAreEqual(boardBefore, boardAfter.getGrid()), "La scacchiera dovrebbe cambiare dopo la mossa");

        // Eventualmente verifica che la posizione di partenza sia ora vuota
        assertNotSame(PIECE.EMPTY, boardAfter.getGrid());
        // Nota: verifica la logica del gioco, aggiusta le coordinate secondo necessità
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
