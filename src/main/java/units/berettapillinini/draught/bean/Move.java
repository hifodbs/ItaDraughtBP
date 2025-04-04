package units.berettapillinini.draught.bean;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class Move {

    ArrayList<Position> cellVisited;
    ArrayList<Position> positionCapturedPieces;
    ArrayList<PIECE> capturedPieces;
    PIECE piece;

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

    public Move makeCopy() {
        return new Move(new ArrayList<>(getCellVisited()),new ArrayList<>(getPositionCapturedPieces()),
        new ArrayList<>(getCapturedPieces()),getPiece());
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

    public String encode() {
        StringBuilder move = new StringBuilder();
        for(Position p : cellVisited)
            move.append(";").append(p.getX()).append(",").append(p.getY());
        move = new StringBuilder(move.substring(1));
        return move.toString();
    }
}
