package units.berettapillinini.draught;

import units.berettapillinini.draught.bean.*;

import java.util.ArrayList;

public class DraughtController {

    private final DraughtGame game;
    private final DraughtView view;
    private final ArrayList<Position> selectedPositions = new ArrayList<>();
    private final boolean vsCPU;
    private CPU cpu;

    public DraughtController(DraughtView view, boolean vsCPU) {
        this.view = view;
        this.vsCPU = vsCPU;
        this.game = new DraughtGame(view, vsCPU);

        if (vsCPU) {
            cpu = new CPU(COLOR.BLACK, 5, game.getChessboard());
        }
    }

    public void startGame() {
        game.start();
    }

    public void handleCellClick(int x, int y) {
        if (vsCPU && game.getTurn() == COLOR.BLACK) {
            view.on_next_turn("Wait for the CPU");
            return;
        }

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


            selectedPositions.clear();

            if (vsCPU && game.getTurn() == COLOR.BLACK) {
                doCPUMove();
                return;
            }

            ArrayList<Move> newMoves = game.getMoves(game.getTurn());
            boolean hasFurtherJumps = newMoves.stream().anyMatch(m ->
                    m.getCellVisited().getFirst().equals(clicked) && m.getCellVisited().size() > 1
            );

            if (hasFurtherJumps) {
                selectedPositions.add(clicked);
                view.on_next_turn("Keep jumping with: " + clicked.getX() + "," + clicked.getY());
            } else {
                view.on_next_turn(game.getTurn() == COLOR.WHITE ? "White turn" : "Black turn");
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

    private void doCPUMove() {
        String cpuMove = cpu.getBestMove();
        if (cpuMove != null && !cpuMove.isEmpty()) {
            game.movePiece(cpuMove, COLOR.BLACK);
            view.on_next_turn("CPU has moved: " + cpuMove);
            if (game.getTurn() == COLOR.BLACK) {
                doCPUMove();
            }
        }
    }

    public DraughtGame getGame() {
        return game;
    }
}
