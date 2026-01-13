package presentation.panels;

import javax.swing.*;
import java.awt.*;

import dao.Proprietaire;

public class AcceuilPanel extends JPanel {

        public AcceuilPanel(Proprietaire proprietaire) {

                setLayout(new BorderLayout());
                setBackground(Color.WHITE);

                // Conteneur central
                JPanel centerPanel = new JPanel();
                centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
                centerPanel.setBackground(Color.WHITE);
                centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

                // 1. Titre PRINCIPAL
                JLabel lblWelcome = new JLabel("Bienvenue, " + proprietaire.getPrenom() + " " + proprietaire.getNom(),
                                SwingConstants.CENTER);
                lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 32));
                lblWelcome.setForeground(new Color(70, 130, 180)); // Steel Blue
                lblWelcome.setAlignmentX(Component.CENTER_ALIGNMENT);

                // 2. Sous-titre ROLE
                JLabel lblRole = new JLabel("Espace Propriétaire", SwingConstants.CENTER);
                lblRole.setFont(new Font("Segoe UI", Font.ITALIC, 20));
                lblRole.setForeground(Color.GRAY);
                lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);

                // 3. Info Block
                JLabel lblInfo = new JLabel(
                                "<html><div style='text-align: center; color: #555555;'>" +
                                                "Gérez vos boutiques et vos réparateurs en toute simplicité.<br>" +
                                                "Utilisez le menu ci-dessus pour naviguer." +
                                                "</div></html>",
                                SwingConstants.CENTER);
                lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
                lblInfo.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

                // Ajout au panel central
                centerPanel.add(lblWelcome);
                centerPanel.add(Box.createVerticalStrut(10)); // Espace
                centerPanel.add(lblRole);
                centerPanel.add(Box.createVerticalStrut(40)); // Grand espace
                centerPanel.add(lblInfo);

                add(centerPanel, BorderLayout.CENTER);
        }
}
