package units.berettapillinini.draught.game;

import org.junit.jupiter.api.*;
import units.berettapillinini.draught.DraughtConsoleUI;
import units.berettapillinini.draught.bean.PIECE;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class DraughtConsoleUITest {

    private DraughtConsoleUI ui;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {

        System.setProperty("TEST_ENV", "true");

        ui = new DraughtConsoleUI();
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {

        System.clearProperty("TEST_ENV");
        System.setOut(originalOut);
    }

    @Test
    void testOnNextTurnStoresAndPrintsMessage() {
        String message = "White turn";
        ui.on_next_turn(message);

        assertEquals(message, ui.getLastMessage(), "Saved message not correct");
        assertTrue(outContent.toString().contains(message), "The message was not printed on console");
    }

    @Test
    void testChessboardRendering() {
        PIECE[][] grid = new PIECE[8][8];
        grid[0][0] = PIECE.WHITE_PAWN;
        grid[0][1] = PIECE.BLACK_PAWN;
        grid[0][2] = PIECE.WHITE_KING;
        grid[0][3] = PIECE.BLACK_KING;
        grid[0][4] = PIECE.EMPTY;

        ui.on_chessboard_update(grid);

        String output = outContent.toString();
        assertTrue(output.contains("w"), "The white pawn (w) was not printed");
        assertTrue(output.contains("b"), "The black pawn (b) was not printed");
        assertTrue(output.contains("W"), "The white king (W) was not printed");
        assertTrue(output.contains("B"), "The black king (B) was not printed");
        assertTrue(output.contains("."), "The empty square (.) was not printed");
    }

    @Test
    void testOnClosePrintsMessageWithoutExitInTest() {
        ui.on_close();

        String output = outContent.toString();
        assertTrue(output.contains("Game Finished. See you next time :)"), "The final message was not printed");
    }

}
