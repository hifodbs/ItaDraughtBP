package units.berettapillinini.draught.bean;

import units.berettapillinini.draught.Chessboard;

public class MoveNode {

    Position newPosition;
    Position positionCapturedPiece;
    PIECE capturedPiece;
    Chessboard chessboard;

    public MoveNode(Position move, Position positionCapturedPiece, Chessboard chessboard){
        this.newPosition = move;
        this.positionCapturedPiece = positionCapturedPiece;
        this.chessboard = chessboard;
        if(positionCapturedPiece!=null) {
            this.capturedPiece = this.chessboard.getCell(positionCapturedPiece);
        }
    }

    public Position getNewPosition() {
        return newPosition;
    }

    public Position getPositionCapturedPiece() {
        return positionCapturedPiece;
    }

    public PIECE getCapturedPiece() {
        return capturedPiece;
    }

    public Chessboard getChessboard() {
        return chessboard;
    }
}
