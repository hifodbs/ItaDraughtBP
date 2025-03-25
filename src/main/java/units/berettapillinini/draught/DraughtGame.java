package units.berettapillinini.draught;

import units.berettapillinini.draught.bean.*;

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

    private COLOR turn;

    public DraughtGame(DraughtView draughtView, String name1, String name2){
        whitePlayer = name1;
        blackPlayer = name2;
        chessboard = new Chessboard();
        this.draughtView = draughtView;
    }

    public void start(){
        draughtView.on_chessboard_update(chessboard.getGrid());
        draughtView.on_next_turn("White turn");
        turn = COLOR.WHITE;
    }

    public Chessboard getChessboard() {
        return chessboard;
    }

    public void movePiece(String move, COLOR player) {
        if(player!=turn){
            draughtView.on_next_turn("ERROR: It's black turn");
            return;
        }
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

        MoveNode possiblePaths = new MoveNode(positionOfPiece.get(0),null);
        checkPossiblePath(possiblePaths,oldCell, positionOfPiece.get(0),getCatchablePiece(oldCell), chessboard);


        ArrayList<Position> path = new ArrayList<>();
        path.add(positionOfPiece.remove(0));
        for(Position position: positionOfPiece){
            possiblePaths = possiblePaths.getChildren().stream()
                    .filter(moveNode -> moveNode.getMove().equals(position)).findFirst().orElse(null);
            if(possiblePaths != null){
                if(possiblePaths.getCapturedPiece()!=null)
                    path.add(possiblePaths.getCapturedPiece());
                path.add(possiblePaths.getMove());
            }
            else {
                path = null;
                break;
            }
        }

        if(path == null || !possiblePaths.getChildren().isEmpty()){
            draughtView.on_next_turn("Can't move here");
            return;
        }

        Position var = path.remove(path.size()-1);
        if(var.getY()==0 && oldCell == PIECE.WHITE_PAWN){
            chessboard.setSquare(var,PIECE.WHITE_KING);
        } else if (var.getY() == 8 && oldCell == PIECE.BLACK_PAWN){

            chessboard.setSquare(var,PIECE.BLACK_KING);
        }else {
            chessboard.setSquare(var,chessboard.getCell(path.get(0)));
        }
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
        turn = (turn==COLOR.WHITE)?COLOR.BLACK:COLOR.WHITE;
        draughtView.on_next_turn(m);
    }

    public EnumSet<PIECE> getCatchablePiece(PIECE oldCell) {
        return EnumSet.allOf(PIECE.class).stream().filter(piece -> piece.getColor()!=oldCell.getColor() && piece!=PIECE.EMPTY)
                .filter(piece -> (oldCell.getGrade()== GRADE.PAWN && piece.getGrade()==GRADE.PAWN) || oldCell.getGrade() == GRADE.KING)
                .collect(collectingAndThen(toSet(), EnumSet::copyOf));
    }

    private boolean checkBound(Position p){
        if(p.getX()<0||p.getX()>=8)
            return false;
        return p.getY() >= 0 && p.getY() < 8;
    }

    private void checkPossiblePath(MoveNode possiblePath, PIECE oldCell, Position old_p, EnumSet<PIECE> catchablePieces,Chessboard currentChessboard) {
        Set<Position> possiblePosition = oldCell.getMoveList().stream()
                .filter(move->checkBound(Position.add(move,old_p))&&checkBound(Position.add(move,Position.add(move,old_p))))
                .filter(move -> catchablePieces.contains(currentChessboard.getCell(Position.add(move,old_p)))
                        && currentChessboard.getCell(Position.add(move,Position.add(move,old_p))) == PIECE.EMPTY).collect(Collectors.toSet());

        if(!possiblePosition.isEmpty()){
            for(Position pp : possiblePosition){
                MoveNode moveNode = new MoveNode(Position.add(old_p,Position.add(pp,pp)),Position.add(pp,old_p));
                Chessboard furtherChessboard = currentChessboard.makeCopy();
                furtherChessboard.setSquare(old_p,PIECE.EMPTY);
                furtherChessboard.setSquare(moveNode.getCapturedPiece(),PIECE.EMPTY);
                furtherChessboard.setSquare(moveNode.getMove(),oldCell);
                possiblePath.addChild(moveNode);
                checkPossiblePath(moveNode,oldCell,moveNode.getMove(),catchablePieces,furtherChessboard);
            }

            return;
        }
        if(possiblePath.getCapturedPiece()!=null)
            return;
        possiblePosition =  oldCell.getMoveList().stream().filter(move->checkBound(Position.add(move,old_p)))
                .filter(move->currentChessboard.getCell(Position.add(move,old_p))==PIECE.EMPTY)
                .collect(Collectors.toSet());
        for(Position pp: possiblePosition)
        {
            possiblePath.addChild(new MoveNode(Position.add(pp,old_p),null));
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
