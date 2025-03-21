package units.berettapillinini.draught;

import units.berettapillinini.draught.bean.COLOR;
import units.berettapillinini.draught.bean.GRADE;
import units.berettapillinini.draught.bean.PIECE;
import units.berettapillinini.draught.bean.Position;

import java.util.ArrayList;
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

    public void movePiece(String move, COLOR player) {
        int old_y = Integer.parseInt(move.split(";")[0].split(",")[0]);
        int old_x = Integer.parseInt(move.split(";")[0].split(",")[1]);
        Position old_p = new Position(old_x,old_y);
        int new_y = Integer.parseInt(move.split(";")[1].split(",")[0]);
        int new_x = Integer.parseInt(move.split(";")[1].split(",")[1]);
        Position new_p = new Position(new_x,new_y);

        PIECE oldCell = chessboard.getCell(old_p);

        if(oldCell == PIECE.EMPTY) {
            draughtView.on_next_turn("No existing piece");
            return;
        }

        if(player == COLOR.WHITE && oldCell.getColor() == COLOR.BLACK){
            draughtView.on_next_turn("Can't move opponent piece");
            return;
        }

        if(player == COLOR.BLACK && oldCell.getColor() == COLOR.WHITE){
            draughtView.on_next_turn("Can't move opponent piece");
            return;
        }

        if( oldCell.getMoveList().stream().noneMatch(p -> Position.add(p,old_p).equals(new_p))){
            draughtView.on_next_turn("Can't move here");
            return;
        }



        if (Math.abs(old_y-new_y)==2 && Math.abs(old_x-new_x)==2) {
            chessboard.setSquare(new_p, oldCell);
            chessboard.setSquare(old_p, PIECE.EMPTY);
            chessboard.setSquare(new Position(old_x-Integer.signum(old_x-new_x),old_y-Integer.signum(old_y-new_y)), PIECE.EMPTY);
        }
        else {
            chessboard.setSquare(new_p, oldCell);
            chessboard.setSquare(old_p, PIECE.EMPTY);
        }

        draughtView.on_chessboard_update(chessboard.getGrid());
        boolean game_end = checkGameEnd(player);
        String m;
        if(player == COLOR.WHITE)
            m = (game_end) ? "White win" : "Black turn";
        else
            m = (game_end) ? "Black win" : "White turn";
        draughtView.on_next_turn(m);
    }

    private boolean checkGameEnd(COLOR player) {
        EnumSet<PIECE> set;
        if(player == COLOR.WHITE)
            set = EnumSet.of(PIECE.BLACK_KING,PIECE.BLACK_PAWN);
        else
            set = EnumSet.of(PIECE.WHITE_KING,PIECE.WHITE_PAWN);

        PIECE[][] grid = chessboard.getGrid();

        return Arrays.stream(grid).flatMap(Arrays::stream)
                .noneMatch(set::contains);
    }

}
