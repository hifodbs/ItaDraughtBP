package units.berettapillinini.draught.game.util;

import units.berettapillinini.draught.Chessboard;
import units.berettapillinini.draught.bean.PIECE;
import units.berettapillinini.draught.bean.Position;

public class TestUtil {


    public static PIECE[][] createGrid(String setup){
        Chessboard chessboard = new Chessboard();
        int size = chessboard.getGridSize();
        boolean skip = setup.isEmpty();
        Position p = new Position(0,0);
        String[] allPosition = setup.split(";");
        for(int a = 0; a < size; a++)
            for(int b = 0; b < size; b++) {
                p.setPosition(b,a);
                chessboard.setSquare(p, PIECE.EMPTY);
                if(!skip)
                    for(String position : allPosition){
                        String[] positionSetup = position.split(",");
                        if(a==Integer.parseInt(positionSetup[1]) && b==Integer.parseInt(positionSetup[0]))
                            chessboard.setSquare(p, PIECE.getPiece(positionSetup[2]));
                    }
            }
        return chessboard.getGrid();
    }

}
