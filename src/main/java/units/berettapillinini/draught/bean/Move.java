package units.berettapillinini.draught.bean;

import units.berettapillinini.draught.Chessboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

public class Move {

    ArrayList<Position> cellVisited;
    ArrayList<Position> positionCapturedPieces;
    ArrayList<PIECE> capturedPieces;
    Chessboard chessboard;
    PIECE piece;

    public Move(Position move, Position positionCapturedPiece, Chessboard chessboard, PIECE piece){
        cellVisited = new ArrayList<>();
        positionCapturedPieces = new ArrayList<>();
        capturedPieces = new ArrayList<>();

        cellVisited.add(move);
        positionCapturedPieces.add(positionCapturedPiece);
        this.piece = piece;
        this.chessboard = chessboard;
        if(positionCapturedPiece!=null) {
            capturedPieces.add(this.chessboard.getCell(positionCapturedPiece));
        }
    }

    public Move(ArrayList<Position> cellVisited, ArrayList<Position> positionCapturedPieces, ArrayList<PIECE> capturedPieces,Chessboard chessboard, PIECE piece) {
        this.cellVisited = cellVisited;
        this.chessboard = chessboard;
        this.positionCapturedPieces = positionCapturedPieces;
        this.capturedPieces = capturedPieces;
        this.piece = piece;
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

    public Chessboard getChessboard() {
        return chessboard;
    }

    public Position getLastVisited() {
        return cellVisited.get(cellVisited.size()-1);
    }

    public Position getFirstPlace() {
        return  cellVisited.get(0);
    }

    public Move makeCopy() {
        return new Move((ArrayList<Position>) getCellVisited().clone(), (ArrayList<Position>) getPositionCapturedPieces().clone(),
        (ArrayList<PIECE>) getCapturedPieces().clone(),getChessboard().makeCopy(),getPiece());
    }

    public void addJump(Position newCellVisited, Position positionCapturedPiece) {
        chessboard.setSquare(getLastVisited(),PIECE.EMPTY);
        cellVisited.add(newCellVisited);
        if(positionCapturedPiece!=null) {
            positionCapturedPieces.add(positionCapturedPiece);
            capturedPieces.add(chessboard.getCell(positionCapturedPiece));
            chessboard.setSquare(positionCapturedPiece, PIECE.EMPTY);
        }
    }

    public  int getNumberOfKing() {
        return Math.toIntExact(getCapturedPieces().stream()
                .filter(piece -> piece.getGrade() == GRADE.KING).count());
    }


    public int getFirstKingEncounter() {
        return IntStream.range(0,getCapturedPieces().size()).filter(i -> getCapturedPieces().get(i).getGrade()==GRADE.KING)
                .findFirst().orElse(-1);
    }
}
