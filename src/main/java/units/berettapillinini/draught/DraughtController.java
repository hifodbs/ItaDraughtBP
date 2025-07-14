package units.berettapillinini.draught;

import units.berettapillinini.draught.bean.*;

import java.util.ArrayList;

public class DraughtController {

    private final DraughtGame game;
    private final DraughtView view;
    private final ArrayList<Position> selectedPositions = new ArrayList<>();
    private final boolean vsCPU;

    public DraughtController(DraughtView view, boolean vsCPU) {
        this.view = view;
        this.vsCPU = vsCPU;
        this.game = new DraughtGame(view, vsCPU);
    }

    public void startGame() {
        game.start();
    }

    public void handleCellClick(int x, int y) {
        Position clicked = new Position(x, y);

        if (selectedPositions.isEmpty()) {
            PIECE clickedPiece = game.getChessboard().getCell(clicked);
            if (clickedPiece != PIECE.EMPTY && clickedPiece.getColor() == game.getTurn()) {
                selectedPositions.add(clicked);
                view.on_next_turn("Selected:  " + clicked.getX() + "," + clicked.getY());
            } else {
                view.on_next_turn("Select one of your pieces");
            }
            return;
        }

        selectedPositions.add(clicked);
        ArrayList<Move> moves = game.getMoves(game.getTurn());

        boolean partialMatch = moves.stream().anyMatch(m -> {
            ArrayList<Position> path = m.getCellVisited();
            if (path.size() < selectedPositions.size()) return false;
            for (int i = 0; i < selectedPositions.size(); i++) {
                if (!path.get(i).equals(selectedPositions.get(i))) return false;
            }
            return true;
        });

        if (!partialMatch) {
            view.on_next_turn("Invalid move, select a piece");
            selectedPositions.clear();
            return;
        }

        boolean exactMatch = moves.stream().anyMatch(m -> m.getCellVisited().equals(selectedPositions));

        if (exactMatch) {
            String moveStr = buildMoveString(selectedPositions);
            game.movePiece(moveStr, game.getTurn());

            PIECE startPiece = game.getChessboard().getCell(selectedPositions.getFirst());
            if (game.getTurn() != startPiece.getColor()) {
                selectedPositions.clear();
            } else {
                selectedPositions.clear();
                selectedPositions.add(clicked);
                view.on_next_turn("Keep jumping with: " + clicked.getX() + "," + clicked.getY());
            }
        } else {
            view.on_next_turn("Select another square to complete the move");
        }
    }

    private String buildMoveString(ArrayList<Position> positions) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < positions.size(); i++) {
            Position pos = positions.get(i);
            builder.append(pos.getX()).append(",").append(pos.getY());
            if (i < positions.size() - 1) builder.append(";");
        }
        return builder.toString();
    }

    public DraughtGame getGame(){return game;}
}
