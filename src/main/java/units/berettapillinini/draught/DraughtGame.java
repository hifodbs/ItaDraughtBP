package units.berettapillinini.draught;

import units.berettapillinini.draught.bean.COLOR;
import units.berettapillinini.draught.bean.GRADE;
import units.berettapillinini.draught.bean.PIECE;
import units.berettapillinini.draught.bean.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toSet;

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
        ArrayList<Position> positionOfPiece = new ArrayList<>();
        Arrays.stream(move.split(";")).forEach(str->positionOfPiece
                .add(new Position(Integer.parseInt(str.split(",")[0]),Integer.parseInt(str.split(",")[1]))));

        PIECE oldCell = chessboard.getCell(positionOfPiece.get(0));

        if(oldCell == PIECE.EMPTY) {
            draughtView.on_next_turn("No existing piece");
            return;
        }

        if(player != oldCell.getColor()){
            draughtView.on_next_turn("Can't move opponent piece");
            return;
        }

        ArrayList<ArrayList<Position>> possiblePaths = new ArrayList<>();
        checkPossiblePath(possiblePaths,oldCell, positionOfPiece.get(0),getCatchablePiece(oldCell));


        ArrayList<Position> path = null;
        for(ArrayList<Position> possiblePath : possiblePaths){
            if(possiblePath.containsAll(positionOfPiece)){
                path = possiblePath;
            }
        }

        if(path == null){
            draughtView.on_next_turn("Can't move here");
            return;
        }

        chessboard.setSquare(path.get(path.size()-1),chessboard.getCell(path.get(0)));
        path.remove(path.size()-1);
        for(Position p : path){
            chessboard.setSquare(p,PIECE.EMPTY);
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

    public EnumSet<PIECE> getCatchablePiece(PIECE oldCell) {
        return EnumSet.allOf(PIECE.class).stream().filter(piece -> piece.getColor()!=oldCell.getColor() && piece!=PIECE.EMPTY)
                .filter(piece -> (oldCell.getGrade()== GRADE.PAWN && piece.getGrade()==GRADE.PAWN) || oldCell.getGrade() == GRADE.KING)
                .collect(collectingAndThen(toSet(), EnumSet::copyOf));
    }

    private void checkPossiblePath(ArrayList<ArrayList<Position>> possiblePath, PIECE oldCell, Position old_p, EnumSet<PIECE> catchablePieces) {
        Set<Position> possiblePosition = oldCell.getMoveList().stream()
                .filter(move -> catchablePieces.contains(chessboard.getCell(Position.add(move,old_p)))
                && chessboard.getCell(Position.add(move,Position.add(move,old_p))) == PIECE.EMPTY).collect(Collectors.toSet());

        if(!possiblePosition.isEmpty()){
            for(Position pp : possiblePosition){
                possiblePath.add(new ArrayList<>());
                possiblePath.get(possiblePath.size()-1).add(old_p);
                possiblePath.get(possiblePath.size()-1).add(Position.add(pp,old_p));
                possiblePath.get(possiblePath.size()-1).add(Position.add(old_p,Position.add(pp,pp)));
            }
            return;
        }
        possiblePosition =  oldCell.getMoveList().stream().filter(move->chessboard.getCell(Position.add(move,old_p))==PIECE.EMPTY)
                .collect(Collectors.toSet());
        for(Position pp: possiblePosition)
        {
            possiblePath.add(new ArrayList<>());
            possiblePath.get(possiblePath.size()-1).add(old_p);
            possiblePath.get(possiblePath.size()-1).add(Position.add(pp,old_p));
        }
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
