package units.berettapillinini.draught.bean;

import units.berettapillinini.draught.Chessboard;

import java.util.ArrayList;
import java.util.Arrays;

public class MoveNode {
    Move move;
    ArrayList<MoveNode> furtherMove;
    Chessboard chessboard;
    int value;

    public MoveNode(Move move, Chessboard chessboard){
        this.move= move;
        this.chessboard = chessboard;
        value = chessboard.getVal();
        furtherMove = new ArrayList<>();
    }

    public Chessboard getChessboard() {
        return chessboard;
    }

    public void addChild(MoveNode moveNode) {
        furtherMove.add(moveNode);
    }

    public ArrayList<MoveNode> getChild() {
        return furtherMove;
    }


    private int getVal(Chessboard chessboard) {
        int val = 0;
        val = Arrays.stream(chessboard.getGrid()).flatMap(Arrays::stream).mapToInt(PIECE::getValue).sum();
        return val;
    }

    public Move getMove() {
        return move;
    }

    public void setChildren(ArrayList<MoveNode> proudChild) {
        furtherMove = proudChild;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
