package units.berettapillinini.draught;

import units.berettapillinini.draught.bean.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toSet;

public class CPU{

    COLOR color;
    int depth;
    Chessboard chessboard;

    public CPU(COLOR color, int depth, Chessboard chessboard){
        this.color = color;
        this.depth = depth;
        this.chessboard = chessboard;
    }

    public String getBestMove() {

        MoveNode tree = new MoveNode(null,chessboard);
        createTree(tree,depth,color);

        ArrayList<Move> moves = tree.getChild().stream().filter(moveNode -> moveNode.getValue()==tree.getValue()).map(MoveNode::getMove)
                .collect(Collectors.toCollection(ArrayList::new));

        Move move = moves.get(0);
        if(moves.size()>1) {
            System.out.println("Più soluzioni trovate:");
            move = moves.stream().findAny().orElse(null);
            for(Move test : moves)
                System.out.println(test.encode());
            System.out.println("Fine soluzioni trovate");
        }

        return move.encode();
    }


    private void createTree(MoveNode moveNode, int depth, COLOR color) {
        if(depth==0)
            return ;

        ArrayList<Move> moves = new ArrayList<>();
        for(Position pos : moveNode.getChessboard().getPositionColorPieces(color)) {
            PIECE p =  moveNode.getChessboard().getCell(pos);
            moves.addAll(checkPossiblePath(new Move(pos,p ), getCatchablePiece(p),moveNode.getChessboard()));
        }
        moves = getLegalMove(moves);

        for(Move move : moves){
            Chessboard furtherChessboard = moveNode.getChessboard().makeCopy();
            furtherChessboard.applyMove(move);
            moveNode.addChild(new MoveNode(move,furtherChessboard));
        }

        for(MoveNode child : moveNode.getChild()) {
            if(!child.getChessboard().getPositionColorPieces((color == COLOR.WHITE) ? COLOR.BLACK : COLOR.WHITE).isEmpty())
                createTree(child, depth - 1, (color == COLOR.WHITE) ? COLOR.BLACK : COLOR.WHITE);
        }

        if (moveNode.getChild().isEmpty()){
            int ver = 1;
            if(color==COLOR.BLACK)
                ver*=-1;
            moveNode.setValue(ver*1000);
            return;
        }
        int valu = 0;
        int best_value = moveNode.getChild().get(0).getValue();
        for(MoveNode child : moveNode.getChild()){
            valu = child.getValue();
            if(color == COLOR.WHITE && valu > best_value){
                best_value = valu;
            }
            if(color == COLOR.BLACK && valu < best_value){
                best_value = valu;
            }
        }
        moveNode.setValue(best_value);

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
