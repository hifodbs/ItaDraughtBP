package units.berettapillinini.draught;

public enum PIECE {
    WHITE_PAWN("WP"),
    WHITE_KING("WK"),
    BLACK_PAWN("BP"),
    BLACK_KING("BK"),
    EMPTY("EM");

    private String type;

    PIECE(String type) {
        this.type = type;
    }

    public static PIECE getPiece(String tp){
        return switch (tp) {
            case "WP" -> WHITE_PAWN;
            case "WK" -> WHITE_KING;
            case "BP" -> BLACK_PAWN;
            case "BK" -> BLACK_KING;
            default -> EMPTY;
        };
    }
}
