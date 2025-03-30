package units.berettapillinini.draught.bean;

import units.berettapillinini.draught.Chessboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

public class Move {

    ArrayList<Position> cellVisited;
    ArrayList<Position> positionCapturedPieces;
    ArrayList<PIECE> capturedPieces;
    PIECE piece;

    public Move(Position move, Position positionCapturedPiece, PIECE capturedPiece, PIECE piece){
        cellVisited = new ArrayList<>();
        positionCapturedPieces = new ArrayList<>();
        capturedPieces = new ArrayList<>();

        cellVisited.add(move);
        positionCapturedPieces.add(positionCapturedPiece);
        this.piece = piece;
        capturedPieces.add(capturedPiece);
    }
    

    public Move(ArrayList<Position> cellVisited, ArrayList<Position> positionCapturedPieces, ArrayList<PIECE> capturedPieces, PIECE piece) {
        this.cellVisited = cellVisited;
        this.positionCapturedPieces = positionCapturedPieces;
        this.capturedPieces = capturedPieces;
        this.piece = piece;
    }

    public Move(Position pos, PIECE p) {
        cellVisited = new ArrayList<>();
        positionCapturedPieces = new ArrayList<>();
        capturedPieces = new ArrayList<>();
        cellVisited.add(pos);
        piece = p;
    }

    public ArrayList<Position> getCellVisited() {
        return cellVisited;
    }

    public ArrayList<PIECE> getCapturedPieces() {
        return capturedPieces;
    }

    public ArrayList<Position> getPositionCapturedPieces() {
        return positionCapturedPieces;
    }

    public PIECE getPiece() {
        return piece;
    }


    public Position getLastVisited() {
        return cellVisited.get(cellVisited.size()-1);
    }

    public Position getFirstPlace() {
        return  cellVisited.get(0);
    }

    public Move makeCopy() {
        return new Move((ArrayList<Position>) getCellVisited().clone(), (ArrayList<Position>) getPositionCapturedPieces().clone(),
        (ArrayList<PIECE>) getCapturedPieces().clone(),getPiece());
    }

    public void addJump(Position newCellVisited, Position positionCapturedPiece, PIECE capturedPiece) {
        cellVisited.add(newCellVisited);
        positionCapturedPieces.add(positionCapturedPiece);
        capturedPieces.add(capturedPiece);
    }

    public  int getNumberOfKing() {
        return Math.toIntExact(getCapturedPieces().stream()
                .filter(piece -> piece.getGrade() == GRADE.KING).count());
    }


    public int getFirstKingEncounter() {
        return IntStream.range(0,getCapturedPieces().size()).filter(i -> getCapturedPieces().get(i).getGrade()==GRADE.KING)
                .findFirst().orElse(-1);
    }

    public void addJump(Position pos) {
        cellVisited.add(pos);
    }
}
