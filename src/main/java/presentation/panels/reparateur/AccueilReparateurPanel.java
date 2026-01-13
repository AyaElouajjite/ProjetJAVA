package presentation.panels.reparateur;

import javax.swing.*;
import java.awt.*;
import dao.Reparateur;

public class AccueilReparateurPanel extends JPanel {

    private Reparateur reparateur;

    public AccueilReparateurPanel(Reparateur reparateur) {
        this.reparateur = reparateur;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Conteneur central
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // 1. Titre PRINCIPAL
        JLabel lblWelcome = new JLabel("Bienvenue, " + reparateur.getPrenom() + " " + reparateur.getNom(),
                SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblWelcome.setForeground(new Color(0, 100, 0)); // Dark Green for distinction
        lblWelcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 2. Sous-titre ROLE
        JLabel lblRole = new JLabel("Espace Réparateur", SwingConstants.CENTER);
        lblRole.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        lblRole.setForeground(Color.GRAY);
        lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 3. Info Block
        JLabel lblInfo = new JLabel(
                "<html><div style='text-align: center; color: #555555;'>" +
                        "Consultez vos tâches assignées et gérez votre profil.<br>" +
                        "Bon travail !" +
                        "</div></html>",
                SwingConstants.CENTER);
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblInfo.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        // Ajout au panel central
        centerPanel.add(lblWelcome);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(lblRole);
        centerPanel.add(Box.createVerticalStrut(40));
        centerPanel.add(lblInfo);

        add(centerPanel, BorderLayout.CENTER);
    }
}
