package units.berettapillinini.draught;

import java.util.Arrays;
import java.util.EnumSet;

public class DraughtGame {

    private String whitePlayer;
    private String blackPlayer;

    private Chessboard chessboard;

    private DraughtView draughtView;

    public DraughtGame(DraughtView draughtView, String name1, String name2){
        whitePlayer = name1;
        blackPlayer = name2;
        chessboard = new Chessboard();
        this.draughtView = draughtView;
    }

    public void start(){
        draughtView.on_chessboard_update(chessboard.getGrid());
        draughtView.on_next_turn("White turn");
    }

    public Chessboard getChessboard() {
        return chessboard;
    }

    public void movePiece(String move, String player) {
        int old_y = Integer.parseInt(move.split(";")[0].split(",")[0]);
        int old_x = Integer.parseInt(move.split(";")[0].split(",")[1]);
        int new_y = Integer.parseInt(move.split(";")[1].split(",")[0]);
        int new_x = Integer.parseInt(move.split(";")[1].split(",")[1]);

        if (Math.abs(old_y-new_y)==2 && Math.abs(old_x-new_x)==2) {
            chessboard.setSquare(new_y, new_x, chessboard.getCell(old_y, old_x));
            chessboard.setSquare(old_y, old_x, PIECE.EMPTY);
            chessboard.setSquare(old_y-Integer.signum(old_y-new_y), old_x-Integer.signum(old_x-new_x), PIECE.EMPTY);
        }
        else {
            chessboard.setSquare(new_y, new_x, chessboard.getCell(old_y, old_x));
            chessboard.setSquare(old_y, old_x, PIECE.EMPTY);
        }

        draughtView.on_chessboard_update(chessboard.getGrid());
        boolean game_end = checkGameEnd(player);
        String m;
        if(player.equals("white"))
            m = (game_end) ? "White win" : "Black turn";
        else
            m = (game_end) ? "Black win" : "White turn";
        draughtView.on_next_turn(m);
    }

    private boolean checkGameEnd(String player) {
        EnumSet<PIECE> set;
        if(player.equals("white"))
            set = EnumSet.of(PIECE.BLACK_KING,PIECE.BLACK_PAWN);
        else
            set = EnumSet.of(PIECE.WHITE_KING,PIECE.WHITE_PAWN);

        PIECE[][] grid = chessboard.getGrid();

        return Arrays.stream(grid).flatMap(Arrays::stream)
                .noneMatch(set::contains);
    }

}
