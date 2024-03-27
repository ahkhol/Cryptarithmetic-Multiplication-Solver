import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public void initialize() {
        JLabel tLabel = new JLabel();
        

        JPanel maiPanel = new JPanel();
        maiPanel.setLayout(new BorderLayout());
        maiPanel.setBackground(new Color(128, 128, 255));

        setTitle("test");
        setSize(500, 700);
        setVisible(true);
    }
}
