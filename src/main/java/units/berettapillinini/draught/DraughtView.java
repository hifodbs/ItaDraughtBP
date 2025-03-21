package units.berettapillinini.draught;

import units.berettapillinini.draught.bean.PIECE;

public interface DraughtView {

    void on_chessboard_update(PIECE[][] grid);

    void on_next_turn(String m);

    void on_close();

}
