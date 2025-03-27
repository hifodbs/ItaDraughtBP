package units.berettapillinini.draught;

import units.berettapillinini.draught.bean.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.*;

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

        MoveNode pathNode = new MoveNode(positionOfPiece.get(0),null,chessboard);
        ArrayList<ArrayList<MoveNode>> paths = new ArrayList<>();
        for(int a = 0; a < 8; a++)
            for(int b = 0; b < 8;b++)
                if(chessboard.getCell(new Position(b,a)).getColor()==player){
                    pathNode = new MoveNode(new Position(b,a),null,chessboard);
                    paths.addAll(checkPossiblePath(pathNode,chessboard.getCell(pathNode.getNewPosition()),getCatchablePiece(chessboard.getCell(pathNode.getNewPosition()))));
            }

        if(paths.stream().anyMatch(moveNodes -> moveNodes.stream().anyMatch(moveNode -> moveNode.getCapturedPiece()!=null)))
        {
            int depth = Objects.requireNonNull(paths.stream().max(Comparator.comparing(ArrayList::size)).orElse(null)).size();
            paths = paths.stream().filter(moveNodes -> moveNodes.size()==depth).collect(Collectors.toCollection(ArrayList::new));

            if (paths.stream().anyMatch(moveNodes -> chessboard.getCell(moveNodes.get(0).getNewPosition()).getGrade()==GRADE.KING)){
                paths = paths.stream().filter(moveNodes -> chessboard.getCell(moveNodes.get(0).getNewPosition()).getGrade()==GRADE.KING)
                        .collect(Collectors.toCollection(ArrayList::new));
            }

            int nKing = paths.stream().map(moveNodes -> moveNodes.stream().filter(moveNode -> moveNode.getCapturedPiece()!=null)
                    .mapToInt(moveNode -> (moveNode.getCapturedPiece().getGrade()==GRADE.KING)?1:0).sum()).max(Comparator.naturalOrder()).orElse(0);

            paths = paths.stream().filter(moveNodes -> moveNodes.stream().filter(moveNode -> moveNode.getCapturedPiece()!=null)
                            .mapToInt(moveNode -> (moveNode.getCapturedPiece().getGrade()==GRADE.KING)?1:0).sum()==nKing)
                    .collect(Collectors.toCollection(ArrayList::new));

            int firstEncounter = paths.stream().mapToInt(moveNodes ->
                            IntStream.range(0,moveNodes.size()).filter(i -> moveNodes.get(i).getCapturedPiece()!=null)
                                    .filter(i -> moveNodes.get(i).getCapturedPiece().getGrade()==GRADE.KING).findFirst().orElse(-1)).filter(i -> i>0)
                    .min().orElse(-1);
            paths = paths.stream().filter(moveNodes ->
                            IntStream.range(0,moveNodes.size()).filter(i -> moveNodes.get(i).getCapturedPiece()!=null)
                                    .anyMatch(i -> (moveNodes.get(i).getCapturedPiece().getGrade()==GRADE.KING && i == firstEncounter)|| firstEncounter==-1))
                    .collect(Collectors.toCollection(ArrayList::new));
        }


        ArrayList<MoveNode> path = paths.stream().filter(p->p.stream().map(MoveNode::getNewPosition).toList().containsAll(positionOfPiece)
                && positionOfPiece.containsAll(p.stream().map(MoveNode::getNewPosition).toList())).findFirst().orElse(null);


        if(path == null){
            draughtView.on_next_turn("Can't move here");
            return;
        }

        for(MoveNode moveNode : path){
            chessboard.setSquare(moveNode.getNewPosition(),PIECE.EMPTY);
            if(moveNode.getPositionCapturedPiece()!=null)
                chessboard.setSquare(moveNode.getPositionCapturedPiece(),PIECE.EMPTY);
        }
        Position var = path.remove(path.size()-1).getNewPosition();
        if(var.getY()==0 && oldCell == PIECE.WHITE_PAWN){
            chessboard.setSquare(var,PIECE.WHITE_KING);
        } else if (var.getY() == 8 && oldCell == PIECE.BLACK_PAWN){

            chessboard.setSquare(var,PIECE.BLACK_KING);
        }else {
            chessboard.setSquare(var,oldCell);
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

    private ArrayList<ArrayList<MoveNode>> checkPossiblePath(MoveNode possiblePath, PIECE oldCell, EnumSet<PIECE> catchablePieces) {
        ArrayList<ArrayList<MoveNode>> paths = new ArrayList<>();
        Chessboard currentChessboard = possiblePath.getChessboard();
        Set<Position> possiblePosition = oldCell.getMoveList().stream()
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
                furtherChessboard.setSquare(moveNode.getNewPosition(),oldCell);
                ArrayList<ArrayList<MoveNode>> bar = checkPossiblePath(moveNode, oldCell, catchablePieces);
                bar.forEach(path->path.add(0,possiblePath));
                paths.addAll(bar);
            }
            //paths.stream().sorted(Comparator.comparing(ArrayList::size));
            //int depth = Objects.requireNonNull(paths.stream().max(Comparator.comparing(ArrayList::size)).orElse(null)).size();
            //paths = (ArrayList<ArrayList<MoveNode>>) paths.stream().filter(moveNodes -> moveNodes.size()==depth).toList();
            return paths;
        }

        if(possiblePath.getCapturedPiece()!=null){
            ArrayList<MoveNode> test = new ArrayList<>();
            test.add(possiblePath);
            paths.add(test);
            return paths;
        }

        possiblePosition =  oldCell.getMoveList().stream().filter(move->checkBound(Position.add(move,possiblePath.getNewPosition())))
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
