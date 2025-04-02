package units.berettapillinini.draught;

import units.berettapillinini.draught.bean.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.*;

public class DraughtGame {

    private final Chessboard chessboard;

    private final DraughtView draughtView;

    private COLOR turn;

    public DraughtGame(DraughtView draughtView){
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
        ArrayList<Position> positionOfPiece = new ArrayList<>();
        String message =checkMoveCorrect(positionOfPiece,player,move);
        if(!message.isEmpty()){
            draughtView.on_next_turn(message);
            return;
        }

        ArrayList<Move> paths = new ArrayList<>();
        for(Position pos : chessboard.getPositionColorPieces(player)) {
            PIECE p =  chessboard.getCell(pos);
            paths.addAll(checkPossiblePath(new Move(pos,p), getCatchablePiece(p),chessboard));
        }
        paths = getLegalMove(paths);

        Move path = paths.stream().filter(p->p.getCellVisited().equals(positionOfPiece))
                .findFirst().orElse(null);


        if(path == null){
            draughtView.on_next_turn("Can't move here");
            return;
        }

        chessboard.applyMove(path);
        draughtView.on_chessboard_update(chessboard.getGrid());

        turn = (turn==COLOR.WHITE)?COLOR.BLACK:COLOR.WHITE;

        message = getMessageEndTurn(player);
        draughtView.on_next_turn(message);
    }

    private String getMessageEndTurn(COLOR player) {
        boolean game_end = chessboard.getPositionColorPieces((player ==COLOR.WHITE)?COLOR.BLACK:COLOR.WHITE).isEmpty();
        String m;
        if(player == COLOR.WHITE)
            m = (game_end) ? "White win" : "Black turn";
        else
            m = (game_end) ? "Black win" : "White turn";
        return m;
    }

    private String checkMoveCorrect(ArrayList<Position> positions, COLOR player, String move) {
        if(player!=turn){
            return"ERROR: It's black turn";
        }
        Arrays.stream(move.split(";")).forEach(str->positions
                .add(new Position(Integer.parseInt(str.split(",")[0]),Integer.parseInt(str.split(",")[1]))));

        if(chessboard.getCell(positions.get(0)) == PIECE.EMPTY) {
            return "No existing piece";
        }

        if(player != chessboard.getCell(positions.get(0)).getColor()){
            return "Can't move opponent piece";
        }

        return "";
    }

    private ArrayList<Move> getLegalMove(ArrayList<Move> paths) {
        int depth = paths.stream().mapToInt(move -> move.getCapturedPieces().size()).max().orElse(0);
        if(depth != 0)
        {
            paths = paths.stream().filter(move -> move.getCapturedPieces().size()==depth)
                    .collect(Collectors.toCollection(ArrayList::new));

            if(paths.size()==1)
                return paths;

            if(paths.stream().noneMatch(move -> move.getPiece().getGrade()==GRADE.KING)){
                return paths;
            }

            paths = paths.stream().filter(move -> move.getPiece().getGrade()==GRADE.KING)
                    .collect(Collectors.toCollection(ArrayList::new));

            if(paths.size()==1)
                return paths;

            int nKing = paths.stream().mapToInt(Move::getNumberOfKing).max().orElse(0);
            paths = paths.stream().filter(move -> move.getNumberOfKing()==nKing)
                    .collect(Collectors.toCollection(ArrayList::new));

            if(paths.size()==1)
                return paths;

            int firstEncoutner = paths.stream().mapToInt(Move::getFirstKingEncounter
                    ).filter(i -> i>=0).min().orElse(-1);
            paths = paths.stream().filter(move ->(move.getFirstKingEncounter()==firstEncoutner)||firstEncoutner==-1)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        return paths;
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

    private ArrayList<Move> checkPossiblePath(Move possibleMove, EnumSet<PIECE> catchablePieces, Chessboard currentChessboard) {
        ArrayList<Move> moves = new ArrayList<>();

        Set<Position> possiblePosition = possibleMove.getPiece().getMoveList().stream()
                .filter(move->checkBound(Position.add(move,possibleMove.getLastVisited()))
                        && checkBound(Position.add(move,Position.add(move,possibleMove.getLastVisited()))))
                .filter(move -> catchablePieces.contains(currentChessboard.getCell(Position.add(move,possibleMove.getLastVisited())))
                        && currentChessboard.getCell(Position.add(move,Position.add(move,possibleMove.getLastVisited()))) == PIECE.EMPTY).collect(toSet());

        if(!possiblePosition.isEmpty()){
            for(Position pp : possiblePosition){
                Chessboard furtherChessboard = currentChessboard.makeCopy();
                Move furtherMove = possibleMove.makeCopy();
                furtherMove.addJump(Position.add(possibleMove.getLastVisited(),Position.add(pp,pp)),Position.add(pp,possibleMove.getLastVisited())
                    ,furtherChessboard.getCell(Position.add(pp,possibleMove.getLastVisited())));
                furtherChessboard.setSquare(possibleMove.getLastVisited(),PIECE.EMPTY);
                furtherChessboard.setSquare(Position.add(pp,possibleMove.getLastVisited()),PIECE.EMPTY);
                furtherChessboard.setSquare(furtherMove.getLastVisited(),furtherMove.getPiece());
                ArrayList<Move> bar = checkPossiblePath(furtherMove, catchablePieces,furtherChessboard);
                moves.addAll(bar);
            }
            return moves;
        }

        if(!possibleMove.getCapturedPieces().isEmpty()){
            moves.add(possibleMove);
            return moves;
        }

        possiblePosition =  possibleMove.getPiece().getMoveList().stream().filter(move->checkBound(Position.add(move,possibleMove.getLastVisited())))
                .filter(move->currentChessboard.getCell(Position.add(move,possibleMove.getLastVisited()))==PIECE.EMPTY)
                .collect(toSet());
        for(Position pp: possiblePosition)
        {
            Move furtherMove = possibleMove.makeCopy();
            furtherMove.addJump(Position.add(possibleMove.getLastVisited(),pp));
            moves.add(furtherMove);
        }
        return moves;
    }

}
