package birkafarkas;

import java.awt.*;
import javax.swing.*;

public class Window {
    private JFrame frame;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Window window = new Window();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the application.
     */
    public Window() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {

        frame = new JFrame();
        frame.setBounds(560, 200, 800, 900);
        frame.setExtendedState(JFrame.MAXIMIZED_VERT);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initWindow();

    }

    private void initWindow() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BorderLayout());

        GamePanel gamePanel = new GamePanel();
        frame.addKeyListener(gamePanel);
        wrapper.add(gamePanel, BorderLayout.CENTER);

        frame.getContentPane().add(wrapper);
    }
}
