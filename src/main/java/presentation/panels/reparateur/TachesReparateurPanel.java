package presentation.panels.reparateur;

import javax.swing.*;
import java.awt.*;

import dao.Reparateur;

public class TachesReparateurPanel extends JPanel {

    public TachesReparateurPanel(Reparateur reparateur) {

        setLayout(new BorderLayout());

        JLabel label = new JLabel(
                "Ici s'afficheront les tâches du réparateur",
                SwingConstants.CENTER
        );

        label.setFont(new Font("Arial", Font.ITALIC, 18));
        add(label, BorderLayout.CENTER);
    }
}
