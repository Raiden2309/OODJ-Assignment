import resources.LoginView;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Run on the Event Dispatch Thread (EDT) for thread safety
        SwingUtilities.invokeLater(() -> {
            // Create and show the Login View
            new LoginView().setVisible(true);
        });
    }
}