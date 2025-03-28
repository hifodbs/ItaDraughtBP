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

        ArrayList<ArrayList<MoveNode>> paths = new ArrayList<>();
        for(MoveNode moveNode : chessboard.getColorPieces(player)) {
            PIECE p =  chessboard.getCell(moveNode.getNewPosition());
            paths.addAll(checkPossiblePath(moveNode, p, getCatchablePiece(p)));
        }
        paths = getLegalMove(paths);

        ArrayList<MoveNode> path = paths.stream().filter(p->p.stream().
                        map(MoveNode::getNewPosition).toList().equals(positionOfPiece))
                .findFirst().orElse(null);


        if(path == null){
            draughtView.on_next_turn("Can't move here");
            return;
        }

        updateChessboard(path);
        draughtView.on_chessboard_update(chessboard.getGrid());

        turn = (turn==COLOR.WHITE)?COLOR.BLACK:COLOR.WHITE;

        String m = getMessageEndTurn(player);
        draughtView.on_next_turn(m);
    }

    private String getMessageEndTurn(COLOR player) {
        boolean game_end = chessboard.getColorPieces((player ==COLOR.WHITE)?COLOR.BLACK:COLOR.WHITE).isEmpty();
        String m;
        if(player == COLOR.WHITE)
            m = (game_end) ? "White win" : "Black turn";
        else
            m = (game_end) ? "Black win" : "White turn";
        return m;
    }

    private void updateChessboard(ArrayList<MoveNode> path) {
        Position var = path.get(path.size()-1).getNewPosition();
        PIECE movedPiece = chessboard.getCell(path.get(0).getNewPosition());
        for(MoveNode moveNode : path){
            chessboard.setSquare(moveNode.getNewPosition(),PIECE.EMPTY);
            if(moveNode.getPositionCapturedPiece()!=null)
                chessboard.setSquare(moveNode.getPositionCapturedPiece(),PIECE.EMPTY);
        }
        if(var.getY()==0 && movedPiece == PIECE.WHITE_PAWN){
            chessboard.setSquare(var,PIECE.WHITE_KING);
        } else if (var.getY() == 8 && movedPiece == PIECE.BLACK_PAWN){

            chessboard.setSquare(var,PIECE.BLACK_KING);
        }else {
            chessboard.setSquare(var,movedPiece);
        }
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

    private ArrayList<ArrayList<MoveNode>> getLegalMove(ArrayList<ArrayList<MoveNode>> paths) {
        if(paths.stream().anyMatch(moveNodes -> moveNodes.stream().anyMatch(moveNode -> moveNode.getCapturedPiece()!=null)))
        {
            int depth = paths.stream().max(Comparator.comparing(ArrayList::size)).get().size();
            paths = paths.stream().filter(moveNodes -> moveNodes.size()==depth).collect(Collectors.toCollection(ArrayList::new));

            if(paths.size()==1)
                return paths;

            if (paths.stream().noneMatch(moveNodes -> chessboard.getCell(moveNodes.get(0).getNewPosition()).getGrade()==GRADE.KING)){
                return paths;
            }
            paths = paths.stream().filter(moveNodes -> chessboard.getCell(moveNodes.get(0).getNewPosition()).getGrade()==GRADE.KING)
                    .collect(Collectors.toCollection(ArrayList::new));

            if(paths.size()==1)
                return paths;

            int nKing = paths.stream().map(moveNodes -> moveNodes.stream().filter(moveNode -> moveNode.getCapturedPiece()!=null)
                    .mapToInt(moveNode -> (moveNode.getCapturedPiece().getGrade()==GRADE.KING)?1:0).sum()).max(Comparator.naturalOrder()).orElse(0);

            paths = paths.stream().filter(moveNodes -> moveNodes.stream().filter(moveNode -> moveNode.getCapturedPiece()!=null)
                            .mapToInt(moveNode -> (moveNode.getCapturedPiece().getGrade()==GRADE.KING)?1:0).sum()==nKing)
                    .collect(Collectors.toCollection(ArrayList::new));

            if(paths.size()==1)
                return paths;

            int firstEncounter = paths.stream().mapToInt(moveNodes ->
                            IntStream.range(0,moveNodes.size()).filter(i -> moveNodes.get(i).getCapturedPiece()!=null)
                                    .filter(i -> moveNodes.get(i).getCapturedPiece().getGrade()==GRADE.KING).findFirst().orElse(-1)).filter(i -> i>0)
                    .min().orElse(-1);
            paths = paths.stream().filter(moveNodes ->
                            IntStream.range(0,moveNodes.size()).filter(i -> moveNodes.get(i).getCapturedPiece()!=null)
                                    .anyMatch(i -> (moveNodes.get(i).getCapturedPiece().getGrade()==GRADE.KING && i == firstEncounter)|| firstEncounter==-1))
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

    private ArrayList<ArrayList<MoveNode>> checkPossiblePath(MoveNode possiblePath, PIECE piece, EnumSet<PIECE> catchablePieces) {
        ArrayList<ArrayList<MoveNode>> paths = new ArrayList<>();
        Chessboard currentChessboard = possiblePath.getChessboard();
        Set<Position> possiblePosition = piece.getMoveList().stream()
                .filter(move->checkBound(Position.add(move,possiblePath.getNewPosition()))
                        && checkBound(Position.add(move,Position.add(move,possiblePath.getNewPosition()))))
                .filter(move -> catchablePieces.contains(currentChessboard.getCell(Position.add(move,possiblePath.getNewPosition())))
                        && currentChessboard.getCell(Position.add(move,Position.add(move,possiblePath.getNewPosition()))) == PIECE.EMPTY).collect(toSet());

        if(!possiblePosition.isEmpty()){
            for(Position pp : possiblePosition){
                Chessboard furtherChessboard = currentChessboard.makeCopy();
                MoveNode moveNode = new MoveNode(Position.add(possiblePath.getNewPosition(),Position.add(pp,pp)),Position.add(pp,possiblePath.getNewPosition()),furtherChessboard);
                furtherChessboard.setSquare(possiblePath.getNewPosition(),PIECE.EMPTY);
                furtherChessboard.setSquare(moveNode.getPositionCapturedPiece(),PIECE.EMPTY);
                furtherChessboard.setSquare(moveNode.getNewPosition(),piece);
                ArrayList<ArrayList<MoveNode>> bar = checkPossiblePath(moveNode, piece, catchablePieces);
                bar.forEach(path->path.add(0,possiblePath));
                paths.addAll(bar);
            }
            return paths;
        }

        if(possiblePath.getCapturedPiece()!=null){
            ArrayList<MoveNode> test = new ArrayList<>();
            test.add(possiblePath);
            paths.add(test);
            return paths;
        }

        possiblePosition =  piece.getMoveList().stream().filter(move->checkBound(Position.add(move,possiblePath.getNewPosition())))
                .filter(move->currentChessboard.getCell(Position.add(move,possiblePath.getNewPosition()))==PIECE.EMPTY)
                .collect(toSet());
        for(Position pp: possiblePosition)
        {
            ArrayList<MoveNode> test = new ArrayList<>();
            test.add(possiblePath);
            test.add(new MoveNode(Position.add(possiblePath.getNewPosition(),pp),null,null));
            paths.add(test);
        }
        return paths;
    }

}
