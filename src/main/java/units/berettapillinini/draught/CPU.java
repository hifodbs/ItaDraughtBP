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

       /* MoveNode tree = new MoveNode(new Move(null,null,null,chessboard,null));
        createTree(tree,depth,color);
        ArrayList<Move> moves = new ArrayList<>();
        for(Position pos : chessboard.getPositionColorPieces(color)) {
            PIECE p =  chessboard.getCell(pos);
            moves.addAll(checkPossiblePath(new Move(pos, null,chessboard,p ), getCatchablePiece(p)));
        }
        moves = getLegalMove(moves);

        ArrayList<Move> bestPath = getBestPath(depth,move,color);
*/
        return "";
    }

    private MoveNode createTree(MoveNode moveNode, int depth, COLOR color) {
        ArrayList<Move> moves = new ArrayList<>();
        /*
        for(Position pos : chessboard.getPositionColorPieces(color)) {
            PIECE p =  chessboard.getCell(pos);
            moves.addAll(checkPossiblePath(new Move(pos, null,chessboard,p ), getCatchablePiece(p)));
        }

         */
        moves = getLegalMove(moves);
        return moveNode;
    }

    private ArrayList<Move> getBestPath(int depth, ArrayList<Move> moves, COLOR color) {
/*
        ArrayList<Move> furtherMoves = new ArrayList<>();
        for(Position pos : moves.getPositionColorPieces(color)) {
            PIECE p =  chessboard.getCell(pos);
            moves.addAll(checkPossiblePath(new Move(pos, null,chessboard,p ), getCatchablePiece(p)));
        }
        moves = getLegalMove(moves);

        int bestVal = moves.stream().mapToInt(move -> getVal(move.getChessboard())).max().orElse(0);
        ArrayList<Move> bestMove = moves.stream().filter(move -> getVal(move.getChessboard())==bestVal)
                .collect(Collectors.toCollection(ArrayList::new));*/
        return moves;
    }

    private int getVal(Chessboard chessboard) {
        int val = 0;
        val = Arrays.stream(chessboard.getGrid()).flatMap(Arrays::stream).mapToInt(PIECE::getValue).sum();
        return val;
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

}
