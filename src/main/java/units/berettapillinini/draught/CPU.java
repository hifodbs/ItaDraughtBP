package units.berettapillinini.draught;

import units.berettapillinini.draught.bean.*;

import java.util.*;

public class CPU{

    COLOR color;
    int depth;
    Chessboard chessboard;

    public CPU(COLOR color, int depth, Chessboard chessboard){
        this.color = color;
        this.depth = depth;
        this.chessboard = chessboard;
    }

    public String getBestMove() {
        ArrayList<ArrayList<Move>> paths = new ArrayList<>();
        /*for(Move moveNode : chessboard.getPositionColorPieces(color)) {
            PIECE p =  chessboard.getCell(moveNode.getNewPosition());
            paths.addAll(checkPossiblePath(moveNode, p, getCatchablePiece(p)));
        }
        paths = getLegalMove(paths);
        int bestVal = paths.stream().mapToInt(moveNodes -> getVal(moveNodes.get(moveNodes.size()-1).getChessboard())).max().orElse(0);
        ArrayList<Move> bestPath = paths.stream().filter(mo).findAny().orElse(null);
*/
        return "";
    }

    private int getVal(Chessboard chessboard) {
        int val = 0;
        val = Arrays.stream(chessboard.getGrid()).flatMap(Arrays::stream).mapToInt(PIECE::getValue).sum();
        return val;
    }
}
