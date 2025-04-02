package units.berettapillinini.draught.bean;

import java.util.ArrayList;

import static units.berettapillinini.draught.bean.GRADE.*;
import static units.berettapillinini.draught.bean.COLOR.*;

public enum PIECE {
    WHITE_PAWN("WP",WHITE, PAWN),
    WHITE_KING("WK", WHITE, KING),
    BLACK_PAWN("BP", BLACK, PAWN),
    BLACK_KING("BK", BLACK, KING),
    EMPTY("EM",null,null);

    private String type;
    private COLOR color;
    private GRADE grade;
    private ArrayList<Position> moveList;

    PIECE(String type, COLOR color, GRADE grade) {
        this.type = type;
        this.color = color;
        this.grade = grade;
        moveList = new ArrayList<>();
        fillMoveList();
    }

    private void fillMoveList() {
        if(grade == PAWN) {
            if (color == WHITE){
                moveList.add(new Position(1,-1));
                moveList.add(new Position(-1,-1));
            }
            else {
                moveList.add(new Position(1,1));
                moveList.add(new Position(-1,1));
            }
        }
        else
        {
            moveList.add(new Position(1,1));
            moveList.add(new Position(-1,1));
            moveList.add(new Position(1,-1));
            moveList.add(new Position(-1,-1));
        }
    }

    public String getType() {
        return type;
    }

    public COLOR getColor() {
        return color;
    }

    public GRADE getGrade() {
        return grade;
    }

    public ArrayList<Position> getMoveList() {
        return moveList;
    }

    public static PIECE getPiece(String tp){
        return switch (tp) {
            case "WP" -> WHITE_PAWN;
            case "WK" -> WHITE_KING;
            case "BP" -> BLACK_PAWN;
            case "BK" -> BLACK_KING;
            default -> EMPTY;
        };
    }

    public int getValue() {
        int colorValue = (color == WHITE)?1:-1;
        int gradeValue = (grade == KING)?3:1;
        return (this == EMPTY )?0:colorValue*gradeValue;
    }
}
