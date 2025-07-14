package units.berettapillinini.draught.game.util;

import units.berettapillinini.draught.DraughtView;
import units.berettapillinini.draught.bean.PIECE;

public class FakeView implements DraughtView {

    public PIECE[][] grid;
    public String message;
    boolean close = false;

    @Override
    public void on_chessboard_update(PIECE[][] grid) {
        this.grid = grid;
            /*int size = 8;
            int a;
            System.out.println("-----------------inizio scacchiera----------------");
            for(int c = 0; c < size*2; c++) {
                a = (c-c%2)/2;
                for (int b = 0; b < size; b++) {
                    if(c%2==0) {
                        System.out.print("---");
                    }
                    else {
                        System.out.print("|");
                        switch (grid[a][b]) {
                            case WHITE_PAWN:
                                System.out.print("WP");
                                break;
                            case WHITE_KING:
                                System.out.print("WK");
                                break;
                            case BLACK_PAWN:
                                System.out.print("BP");
                                break;
                            case BLACK_KING:
                                System.out.print("BK");
                                break;
                            default:
                                System.out.print("  ");
                        }
                    }
                }
                System.out.println();
            }

            System.out.println("--------------------fine scacchiera----------------");*/
    }

    @Override
    public  void on_close(){
        close = true;
    }

    @Override
    public void run() {

    }

    @Override
    public void on_next_turn(String m) {
        message = m;
    }
}
