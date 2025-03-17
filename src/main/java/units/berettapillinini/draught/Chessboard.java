package units.berettapillinini.draught;

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

    public void setSquare(int a, int b, PIECE p){
        gridSquare[a][b] = p;
    }
}
