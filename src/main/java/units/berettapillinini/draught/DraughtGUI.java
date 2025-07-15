package units.berettapillinini.draught;

import units.berettapillinini.draught.bean.*;

import javax.swing.*;
import java.awt.*;

public class DraughtGUI extends JFrame implements DraughtView {

    private final JButton[][] buttons = new JButton[8][8];
    private final DraughtController controller;
    private final JLabel statusLabel = new JLabel("Turno:");
    private boolean vsCPU = false;

    private final ImageIcon whitePawnIcon;
    private final ImageIcon blackPawnIcon;
    private final ImageIcon whiteKingIcon;
    private final ImageIcon blackKingIcon;

    public DraughtGUI() {
        super("Dama");

        showGameModeDialog();

        whitePawnIcon = loadIcon("/images/white_pawn.png");
        blackPawnIcon = loadIcon("/images/black_pawn.png");
        whiteKingIcon = loadIcon("/images/white_king.png");
        blackKingIcon = loadIcon("/images/black_king.png");

        setDefaultCloseOperation(EXIT_ON_CLOSE);


        JPanel board = new JPanel(new GridLayout(8, 8));
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(60, 60));
                btn.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 28));
                btn.setBackground((x + y) % 2 == 0 ? Color.LIGHT_GRAY : Color.DARK_GRAY);
                final int fx = x;
                final int fy = y;
                btn.addActionListener(e -> cellClicked(fx, fy));
                buttons[y][x] = btn;
                board.add(btn);
            }
        }

        add(board, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setResizable(true);
        setVisible(true);
        toFront();
        requestFocus();

        controller = new DraughtController(this, vsCPU);
        controller.startGame();
    }

    private void showGameModeDialog() {
        String[] options = {"1 vs 1", "vs CPU"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Select the game mode:",
                "Modality",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        vsCPU = (choice == 1);
    }

    private void cellClicked(int x, int y) {
        controller.handleCellClick(x, y);
    }

    @Override
    public void on_chessboard_update(PIECE[][] grid) {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                PIECE p = grid[y][x];
                buttons[y][x].setText("");
                buttons[y][x].setIcon(null);

                switch (p) {
                    case WHITE_PAWN -> buttons[y][x].setIcon(whitePawnIcon);
                    case BLACK_PAWN -> buttons[y][x].setIcon(blackPawnIcon);
                    case WHITE_KING -> buttons[y][x].setIcon(whiteKingIcon);
                    case BLACK_KING -> buttons[y][x].setIcon(blackKingIcon);
                    default -> buttons[y][x].setIcon(null);
                }
            }
        }
    }

    @Override
    public void on_next_turn(String message) {
        statusLabel.setText(message);
    }

    @Override
    public void on_close() {
        dispose();
    }

    private ImageIcon resizeIcon(ImageIcon icon) {
        Image img = icon.getImage();
        Image resized = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        return new ImageIcon(resized);
    }

    private ImageIcon loadIcon(String path) {
        java.net.URL url = getClass().getResource(path);
        assert url != null;
        return resizeIcon(new ImageIcon(url));
    }

    @Override
    public void run() {

    }


}
