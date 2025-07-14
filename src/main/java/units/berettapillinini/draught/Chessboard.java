package units.berettapillinini.draught;


import units.berettapillinini.draught.bean.COLOR;
import units.berettapillinini.draught.bean.Move;
import units.berettapillinini.draught.bean.PIECE;
import units.berettapillinini.draught.bean.Position;
import java.util.ArrayList;
import java.util.Arrays;

public class Chessboard{

    private final int SIZE_GRID = 8;
    private PIECE[][] gridSquare;

    public Chessboard(){
        gridSquare = new PIECE[SIZE_GRID][SIZE_GRID];
        initGrid();
    }

    private void initGrid() {
        for(int a = 0; a < SIZE_GRID;a++)
            for(int b = 0; b < SIZE_GRID;b++) {
                if(a<3 && (a+b)%2==1)
                    gridSquare[a][b] = PIECE.BLACK_PAWN;
                else if (a>4 && (a+b)%2==1)
                    gridSquare[a][b] = PIECE.WHITE_PAWN;
                else
                    gridSquare[a][b] = PIECE.EMPTY;
            }
    }

    public PIECE[][] getGrid() {
        return gridSquare;
    }

    public int getGridSize() {
        return SIZE_GRID;
    }

    public void setSquare(Position p, PIECE piece){
        gridSquare[p.getY()][p.getX()] = piece;
    }

    public void setGrid(PIECE[][] grid) {
        this.gridSquare = grid;
    }

    public PIECE getCell(Position p) {
        return gridSquare[p.getY()][p.getX()];
    }

    public Chessboard makeCopy() {
        Chessboard cloned = new Chessboard();
        for(int a = 0; a < 8;a++)
            for (int b = 0; b < 8;b++)
                cloned.setSquare(new Position(b,a),getCell(new Position(b,a)));
        return cloned;
    }

    public ArrayList<Position> getPositionColorPieces(COLOR color){
        ArrayList<Position> colorPieces = new ArrayList<>();
        for(int a = 0; a < 8;a++)
            for (int b = 0; b < 8;b++)
                if(gridSquare[a][b].getColor()==color)
                    colorPieces.add(new Position(b,a));
        return colorPieces;
    }

    public void applyMove(Move move) {
        Position pos = move.getLastVisited();
        PIECE piece = move.getPiece();
        setSquare(move.getLastVisited(),move.getPiece());
        move.getCellVisited().forEach(position -> setSquare(position,PIECE.EMPTY));
        move.getPositionCapturedPieces().forEach(position -> setSquare(position,PIECE.EMPTY));
        if(pos.getY()==0 && piece == PIECE.WHITE_PAWN){
            setSquare(pos,PIECE.WHITE_KING);
        } else if (pos.getY() == 7 && piece == PIECE.BLACK_PAWN){

            setSquare(pos,PIECE.BLACK_KING);
        }else {
            setSquare(pos,piece);
        }
    }

    public int getVal(){
        if(getPositionColorPieces(COLOR.WHITE).isEmpty())
            return Integer.MIN_VALUE;
        if(getPositionColorPieces(COLOR.BLACK).isEmpty())
            return Integer.MAX_VALUE;
        return Arrays.stream(gridSquare).flatMap(Arrays::stream).
                mapToInt(PIECE::getValue).sum();
    }
}
