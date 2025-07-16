package units.berettapillinini.draught;



import javax.swing.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose game mode: GUI or console");

        while (true) {
            String mode = scanner.nextLine().trim().toLowerCase();
            if ("gui".equals(mode)) {
                SwingUtilities.invokeLater(DraughtGUI::new);
                return;
            } else if ("console".equals(mode)) {
                DraughtConsoleUI ui = new DraughtConsoleUI();
                boolean vsCPU = DraughtConsoleUI.askGameMode();
                ui.startGame(vsCPU);
                ui.run();
                return;
            } else {
                System.out.println("Unknown command: choose GUI or console");
            }
        }
    }
}