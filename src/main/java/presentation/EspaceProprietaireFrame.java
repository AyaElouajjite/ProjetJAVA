package presentation;

import javax.swing.*;
import java.awt.*;

import dao.Proprietaire;
import presentation.panels.AcceuilPanel;
import presentation.panels.boutique.CreerBoutiquePanel;
import presentation.panels.boutique.ListerBoutiquesPanel;
import presentation.panels.reparateur.AjouterReparateurPanel;
import presentation.panels.reparateur.ListerReparateursPanel;
import presentation.panels.client.GestionClientsPanel;
import presentation.panels.appareil.GestionTypeAppareilPanel;
import presentation.panels.reparation.AjouterReparationPanel;
import presentation.panels.reparation.GestionReparationsPanel;
import presentation.panels.transaction.CreerTransactionPanel;
import presentation.panels.transaction.ListerTransactionsPanel;

public class EspaceProprietaireFrame extends JFrame {

    private Proprietaire proprietaire;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // 🔑 PANELS PARTAGÉS
    private ListerReparateursPanel listerReparateursPanel;
    private AjouterReparateurPanel ajouterReparateurPanel;
    private presentation.panels.proprietaire.TableauDeBordPanel tableauDeBordPanel;

    // NOUVEAU : On partage aussi le panel Boutique
    private ListerBoutiquesPanel listerBoutiquesPanel;

    // Réparations
    private GestionReparationsPanel gestionReparationsPanel;
    private AjouterReparationPanel ajouterReparationPanel;

    // Transactions financières
    private ListerTransactionsPanel listerTransactionsPanel;
    private CreerTransactionPanel creerTransactionPanel;

    public EspaceProprietaireFrame(Proprietaire proprietaire) {
        this.proprietaire = proprietaire;

        setTitle("Espace Propriétaire - " + proprietaire.getNom());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initPanels();
        initMenu();
    }

    // =========================
    // PANELS (CardLayout)
    // =========================
    private void initPanels() {

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Accueil
        mainPanel.add(new AcceuilPanel(proprietaire), "ACCUEIL");

        // 📊 TABLEAU DE BORD (FINANCE)
        tableauDeBordPanel = new presentation.panels.proprietaire.TableauDeBordPanel(proprietaire);
        mainPanel.add(tableauDeBordPanel, "TABLEAU_BORD");

        // Boutique (Instanciation dans l'ordre pour dépendance)
        listerBoutiquesPanel = new ListerBoutiquesPanel(proprietaire);
        // On passe le listerPanel au creatorPanel
        mainPanel.add(new CreerBoutiquePanel(proprietaire, listerBoutiquesPanel), "CREER_BOUTIQUE");
        mainPanel.add(listerBoutiquesPanel, "LISTER_BOUTIQUES");

        // Réparateurs (INSTANCES PARTAGÉES)
        listerReparateursPanel = new ListerReparateursPanel(proprietaire);
        ajouterReparateurPanel = new AjouterReparateurPanel(proprietaire, listerReparateursPanel);

        mainPanel.add(ajouterReparateurPanel, "AJOUTER_REPARATEUR");
        mainPanel.add(ajouterReparateurPanel, "AJOUTER_REPARATEUR");
        mainPanel.add(listerReparateursPanel, "LISTER_REPARATEURS");

        // 👥 Clients
        mainPanel.add(new GestionClientsPanel(), "GESTION_CLIENTS");

        // 📱 Appareils (Types)
        mainPanel.add(new GestionTypeAppareilPanel(), "GESTION_TYPES_APPAREIL");

        // 🛠️ Réparations
        gestionReparationsPanel = new GestionReparationsPanel(proprietaire, null);
        ajouterReparationPanel = new AjouterReparationPanel(proprietaire, null,
                gestionReparationsPanel);

        // Link "Ajouter" button
        // Link "Ajouter" button
        gestionReparationsPanel.getBtnAjouter().addActionListener(e -> {
            ajouterReparationPanel.refresh();
            showPanel("AJOUTER_REPARATION");
        });

        mainPanel.add(gestionReparationsPanel, "LISTER_REPARATIONS");
        mainPanel.add(ajouterReparationPanel, "AJOUTER_REPARATION");

        // 💰 Transactions financières
        listerTransactionsPanel = new ListerTransactionsPanel(proprietaire);
        creerTransactionPanel = new CreerTransactionPanel(proprietaire, listerTransactionsPanel);
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
        JMenuItem itemVoirTableau = new JMenuItem("Vue Financière");

        JMenu menuBoutique = new JMenu("Boutique");
        JMenuItem itemCreerBoutique = new JMenuItem("Créer");
        JMenuItem itemListerBoutiques = new JMenuItem("Lister");

        JMenu menuReparateur = new JMenu("Réparateurs");
        JMenuItem itemAjouterReparateur = new JMenuItem("Ajouter");
        JMenuItem itemListerReparateurs = new JMenuItem("Lister");

        JMenu menuSession = new JMenu("Session");
        JMenuItem itemLogout = new JMenuItem("Déconnexion");

        JMenu menuClients = new JMenu("Clients");
        JMenuItem itemGererClients = new JMenuItem("Gérer les clients");

        JMenu menuAppareils = new JMenu("Appareils");
        JMenuItem itemGererTypes = new JMenuItem("Gérer Types Appareil");

        JMenu menuReparations = new JMenu("Réparations");
        JMenuItem itemListerReparations = new JMenuItem("Toutes les réparations");
        JMenuItem itemNouvelleReparation = new JMenuItem("Nouvelle réparation");

        JMenu menuTransactions = new JMenu("Transactions");
        JMenuItem itemListerTransactions = new JMenuItem("Mes Transactions");
        JMenuItem itemCreerTransaction = new JMenuItem("Nouvelle Transaction");

        // Navigation
        itemVoirTableau.addActionListener(e -> {
            tableauDeBordPanel.refresh();
            showPanel("TABLEAU_BORD");
        });

        itemGererTypes.addActionListener(e -> showPanel("GESTION_TYPES_APPAREIL"));

        itemCreerBoutique.addActionListener(e -> showPanel("CREER_BOUTIQUE"));
        itemListerBoutiques.addActionListener(e -> {
            listerBoutiquesPanel.refresh(); // 🔁 Refresh Ajouté
            showPanel("LISTER_BOUTIQUES");
        });

        itemAjouterReparateur.addActionListener(e -> {
            ajouterReparateurPanel.updateBoutiqueList(); // 🔁 Refresh
            showPanel("AJOUTER_REPARATEUR");
        });
        itemListerReparateurs.addActionListener(e -> {
            listerReparateursPanel.refresh(); // 🔁 sécurité
            listerReparateursPanel.updateBoutiqueList(); // 🔁 Refresh les filtres aussi
            showPanel("LISTER_REPARATEURS");
        });

        itemGererClients.addActionListener(e -> showPanel("GESTION_CLIENTS"));

        itemListerReparations.addActionListener(e -> showPanel("LISTER_REPARATIONS"));
        itemNouvelleReparation.addActionListener(e -> {
            ajouterReparationPanel.refresh();
            showPanel("AJOUTER_REPARATION");
        });

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

        menuTableau.add(itemVoirTableau);

        menuBoutique.add(itemCreerBoutique);
        menuBoutique.add(itemListerBoutiques);

        menuReparateur.add(itemAjouterReparateur);
        menuReparateur.add(itemListerReparateurs);

        menuClients.add(itemGererClients);

        menuReparations.add(itemListerReparations);
        menuReparations.add(itemNouvelleReparation);

        menuTransactions.add(itemListerTransactions);
        menuTransactions.add(itemCreerTransaction);

        menuSession.add(itemLogout);

        menuAppareils.add(itemGererTypes);

        menuBar.add(menuTableau);
        menuBar.add(menuBoutique);
        menuBar.add(menuReparateur);
        menuBar.add(menuClients);
        menuBar.add(menuAppareils); // Ajout
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
