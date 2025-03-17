package units.berettapillinini.draught;

public interface DraughtView {

    void on_chessboard_update(PIECE[][] grid);

    void on_next_turn(String m);

}
