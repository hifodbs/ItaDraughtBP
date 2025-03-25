package units.berettapillinini.draught.bean;

import java.util.ArrayList;

public class MoveNode {

    Position move;
    Position capturedPiece;
    ArrayList<MoveNode> furtherMoves;

    public MoveNode(Position move, Position capturedPiece){
        this.move = move;
        this.capturedPiece = capturedPiece;
        furtherMoves = new ArrayList<>();
    }

    public void addChild(MoveNode moveNode) {
        furtherMoves.add(moveNode);
    }

    public ArrayList<MoveNode> getChildren() {
        return  furtherMoves;
    }

    public Position getMove() {
        return move;
    }

    public Position getCapturedPiece() {
        return capturedPiece;
    }
}
