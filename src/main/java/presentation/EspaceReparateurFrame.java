package presentation;

import javax.swing.*;
import java.awt.*;

import dao.Reparateur;
import presentation.panels.reparateur.AccueilReparateurPanel;
import presentation.panels.reparateur.ProfilReparateurPanel;
import presentation.panels.reparateur.TableauDeBordReparateurPanel; // ✅ Import Added
import presentation.panels.reparateur.TachesReparateurPanel;
import presentation.panels.reparation.AjouterReparationPanel;
import presentation.panels.reparation.GestionReparationsPanel;
import presentation.panels.transaction.CreerTransactionPanel;
import presentation.panels.transaction.ListerTransactionsPanel;

public class EspaceReparateurFrame extends JFrame {

    private Reparateur reparateur;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private ProfilReparateurPanel profilReparateurPanel; // ✅ Field
    private TableauDeBordReparateurPanel tableauDeBordPanel; // ✅ Field
    private ListerTransactionsPanel listerTransactionsPanel; // Panel transactions
    private CreerTransactionPanel creerTransactionPanel; // Panel créer transaction

    public EspaceReparateurFrame(Reparateur reparateur) {
        this.reparateur = reparateur;

        setTitle("Espace Réparateur - " + reparateur.getNom());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initPanels();
        initMenu();
    }

    // =========================
    // PANELS
    // =========================
    private void initPanels() {

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Accueil
        mainPanel.add(
                new AccueilReparateurPanel(reparateur),
                "ACCUEIL");

        // 📊 TABLEAU DE BORD
        tableauDeBordPanel = new TableauDeBordReparateurPanel(reparateur);
        mainPanel.add(tableauDeBordPanel, "TABLEAU_BORD");

        // Profil
        profilReparateurPanel = new ProfilReparateurPanel(reparateur);
        mainPanel.add(profilReparateurPanel, "PROFIL");

        mainPanel.add(
                new TachesReparateurPanel(reparateur),
                "TACHES");

        // 🛠️ Réparations
        GestionReparationsPanel panelRep = new GestionReparationsPanel(null, reparateur);
        mainPanel.add(panelRep, "REPARATIONS");

        AjouterReparationPanel panelAjout = new AjouterReparationPanel(null, reparateur, panelRep);
        mainPanel.add(panelAjout, "AJOUTER_REPARATION");

        // Link bouton
        panelRep.getBtnAjouter().addActionListener(e -> showPanel("AJOUTER_REPARATION"));

        // 💰 Transactions financières
        listerTransactionsPanel = new ListerTransactionsPanel(reparateur);
        creerTransactionPanel = new CreerTransactionPanel(reparateur, listerTransactionsPanel);
        
        // Link bouton "Créer" dans le panel de liste
        mainPanel.add(listerTransactionsPanel, "LISTER_TRANSACTIONS");
        mainPanel.add(creerTransactionPanel, "CREER_TRANSACTION");

        add(mainPanel);

        // Panel par défaut
        cardLayout.show(mainPanel, "ACCUEIL");
    }

    // =========================
    // MENU
    // =========================
    private void initMenu() {

        JMenuBar menuBar = new JMenuBar();

        JMenu menuTableau = new JMenu("Tableau de Bord");
        JMenuItem itemVoirTableau = new JMenuItem("Ma Caisse");

        JMenu menuAccueil = new JMenu("Accueil");
        JMenuItem itemAccueil = new JMenuItem("Accueil");

        JMenu menuProfil = new JMenu("Profil");
        JMenuItem itemProfil = new JMenuItem("Mon profil");

        JMenu menuTaches = new JMenu("Travail");
        JMenuItem itemTaches = new JMenuItem("Mes tâches");

        JMenu menuReparations = new JMenu("Réparations");
        JMenuItem itemReparations = new JMenuItem("Gérer Réparations");

        JMenu menuTransactions = new JMenu("Transactions");
        JMenuItem itemListerTransactions = new JMenuItem("Mes Transactions");
        JMenuItem itemCreerTransaction = new JMenuItem("Nouvelle Transaction");

        JMenu menuSession = new JMenu("Session");
        JMenuItem itemLogout = new JMenuItem("Déconnexion");

        // Listeners
        itemVoirTableau.addActionListener(e -> {
            tableauDeBordPanel.refresh();
            showPanel("TABLEAU_BORD");
        });

        itemAccueil.addActionListener(e -> showPanel("ACCUEIL"));
        itemProfil.addActionListener(e -> {
            profilReparateurPanel.refresh(); // ✅ Refresh !
            showPanel("PROFIL");
        });
        itemTaches.addActionListener(e -> showPanel("TACHES"));

        itemReparations.addActionListener(e -> showPanel("REPARATIONS"));

        itemListerTransactions.addActionListener(e -> {
            listerTransactionsPanel.refresh();
            showPanel("LISTER_TRANSACTIONS");
        });
        itemCreerTransaction.addActionListener(e -> {
            creerTransactionPanel.refresh();
            showPanel("CREER_TRANSACTION");
        });

        itemLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        menuAccueil.add(itemAccueil);
        menuProfil.add(itemProfil);
        menuTaches.add(itemTaches);

        menuReparations.add(itemReparations);

        menuTransactions.add(itemListerTransactions);
        menuTransactions.add(itemCreerTransaction);

        menuSession.add(itemLogout);

        menuTableau.add(itemVoirTableau);

        menuBar.add(menuTableau); // Premier item
        menuBar.add(menuAccueil);
        menuBar.add(menuProfil);
        menuBar.add(menuTaches);
        menuBar.add(menuReparations);
        menuBar.add(menuTransactions);
        menuBar.add(menuSession);

        setJMenuBar(menuBar);
    }

    // =========================
    // UTILITAIRE
    // =========================
    private void showPanel(String name) {
        cardLayout.show(mainPanel, name);
    }
}
