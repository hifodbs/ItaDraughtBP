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
        minMax(depth, chessboard,color,Integer.MIN_VALUE,Integer.MAX_VALUE);
        return move.encode();
    }

    private int minMax(int depth, Chessboard chessboard, COLOR color, int alpha, int beta) {
        if(depth==0)
            return chessboard.getVal();

        ArrayList<Move> moves = getMoves(chessboard, color);

        if(moves.isEmpty())
            return (color==COLOR.WHITE)?Integer.MIN_VALUE:Integer.MAX_VALUE;

        int minMaxEval = (color==COLOR.WHITE)?Integer.MIN_VALUE:Integer.MAX_VALUE;
        int eval;
        Chessboard furtherChessboard;
        for(Move move : moves){
            furtherChessboard = chessboard.makeCopy();
            furtherChessboard.applyMove(move);
            eval = minMax(depth-1,furtherChessboard,(color==COLOR.WHITE)?COLOR.BLACK:COLOR.WHITE,alpha,beta);
            if(color==COLOR.WHITE){
                if(eval>minMaxEval){
                    minMaxEval = eval;
                    if(depth==this.depth)
                        this.move = move;
                }
                alpha = Math.max(alpha,eval);
            }else{
                if(eval<minMaxEval){
                    minMaxEval = eval;
                    if(depth==this.depth)
                        this.move = move;
                }
                beta = Math.min(beta,eval);
            }
            if (beta <= alpha)
                break;
        }
        return minMaxEval;

    }

    private ArrayList<Move> getMoves(Chessboard chessboard, COLOR color) {
        ArrayList<Move> moves = new ArrayList<>();
        for(Position pos : chessboard.getPositionColorPieces(color)) {
            PIECE p =  chessboard.getCell(pos);
            moves.addAll(getAllPossibleMoves(new Move(pos,p ), getCatchablePiece(p), chessboard));
        }
        moves = getLegalMove(moves);
        return moves;
    }

    private ArrayList<Move> getLegalMove(ArrayList<Move> moves) {
        int depth = moves.stream().mapToInt(move -> move.getCapturedPieces().size()).max().orElse(0);
        if(depth != 0)
        {
            moves = moves.stream().filter(move -> move.getCapturedPieces().size()==depth)
                    .collect(Collectors.toCollection(ArrayList::new));

            if(moves.size()==1)
                return moves;

            if(moves.stream().noneMatch(move -> move.getPiece().getGrade()==GRADE.KING)){
                return moves;
            }

            moves = moves.stream().filter(move -> move.getPiece().getGrade()==GRADE.KING)
                    .collect(Collectors.toCollection(ArrayList::new));

            if(moves.size()==1)
                return moves;

            int nKing = moves.stream().mapToInt(Move::getNumberOfKing).max().orElse(0);
            moves = moves.stream().filter(move -> move.getNumberOfKing()==nKing)
                    .collect(Collectors.toCollection(ArrayList::new));

            if(moves.size()==1)
                return moves;

            int firstEncoutner = moves.stream().mapToInt(Move::getFirstKingEncounter
            ).filter(i -> i>=0).min().orElse(-1);
            moves = moves.stream().filter(move ->(move.getFirstKingEncounter()==firstEncoutner)||firstEncoutner==-1)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        return moves;
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

    private ArrayList<Move> getAllPossibleMoves(Move possibleMove, EnumSet<PIECE> catchablePieces, Chessboard currentChessboard) {
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
                ArrayList<Move> bar = getAllPossibleMoves(furtherMove, catchablePieces,furtherChessboard);
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
