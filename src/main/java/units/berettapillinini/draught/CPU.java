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
    Move move;

    public CPU(COLOR color, int depth, Chessboard chessboard){
        this.color = color;
        this.depth = depth;
        this.chessboard = chessboard;
    }


    public String getBestMove(){
        int bestVal =  minMax(depth, chessboard,color,Integer.MIN_VALUE,Integer.MAX_VALUE);
        System.out.println("Best val found : "+ bestVal);
        return move.encode();
    }

    private int minMax(int depth, Chessboard chessboard, COLOR color, int alpha, int beta) {
        if(depth==0)
            return chessboard.getVal();

        ArrayList<Move> moves = new ArrayList<>();
        for(Position pos : chessboard.getPositionColorPieces(color)) {
            PIECE p =  chessboard.getCell(pos);
            moves.addAll(checkPossiblePath(new Move(pos,p ), getCatchablePiece(p),chessboard));
        }
        moves = getLegalMove(moves);

        if(moves.isEmpty())
            return (color==COLOR.WHITE)?Integer.MIN_VALUE:Integer.MAX_VALUE;

        if(color==COLOR.WHITE){
            int maxEval = Integer.MIN_VALUE;
            int eval;
            Chessboard furtherChessboard;
            for(Move move :moves){
                furtherChessboard = chessboard.makeCopy();
                furtherChessboard.applyMove(move);
                eval = minMax(depth-1,furtherChessboard,COLOR.BLACK,alpha,beta);
                if(eval>maxEval){
                    maxEval = eval;
                    this.move = move;
                }
                alpha = Math.max(alpha,eval);
                if (beta <= alpha)
                    break;
            }
            return maxEval;
        }else {
            int minEval = Integer.MAX_VALUE;
            int eval;
            Chessboard furtherChessboard;
            for(Move move :moves){
                furtherChessboard = chessboard.makeCopy();
                furtherChessboard.applyMove(move);
                eval = minMax(depth-1,furtherChessboard,COLOR.WHITE,alpha,beta);
                if(eval<minEval){
                    minEval = eval;
                    this.move = move;
                }
                beta = Math.min(beta,eval);
                if (beta <= alpha)
                    break;
            }
            return minEval;
        }
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
