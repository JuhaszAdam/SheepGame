package birkafarkas;

import java.awt.*;
import javax.swing.*;

public class Window {
    private JFrame frame;
    static JLabel score = new JLabel("You shouldn't see this!", SwingConstants.CENTER);

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
        frame.setBounds(560, 200, 700, 900);
        frame.setExtendedState(JFrame.MAXIMIZED_VERT);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initWindow();
    }

    /**
     * Initializes Window
     */
    private void initWindow() {
        JPanel wrapper = new JPanel();

        GamePanel gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(700, 800));
        frame.addKeyListener(gamePanel);
        wrapper.add(gamePanel);

        score.setFont(new Font("Courier New", Font.BOLD, 30));
        score.setOpaque(true);
        score.setPreferredSize(new Dimension(700, 200));
        wrapper.add(score);

        frame.getContentPane().add(wrapper);
    }
}
