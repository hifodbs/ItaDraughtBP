package units.berettapillinini.draught;


import units.berettapillinini.draught.bean.PIECE;
import units.berettapillinini.draught.bean.Position;

public class Chessboard {

    private final int SIZE_GRID = 8;
    private PIECE[][] gridSquare;

    public Chessboard(){
        gridSquare = new PIECE[SIZE_GRID][SIZE_GRID];
        initGrid();
    }

    private void initGrid() {
        for(int a = 0; a < SIZE_GRID;a++)
            for(int b = 0; b < SIZE_GRID;b++) {
                if(a<3 && (a+b)%2==0)
                    gridSquare[a][b] = PIECE.BLACK_PAWN;
                else if (a>4 && (a+b)%2==0)
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
}
