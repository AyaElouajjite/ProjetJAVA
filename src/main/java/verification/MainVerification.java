package verification;

import dao.Boutique;
import dao.Proprietaire;
import dao.Reparateur;
import metier.GestionBoutique;
import metier.GestionReparateur;
import metier.GestionUtilisateurs;
import metier.IGestionBoutique;
import metier.IGestionReparateur;
import metier.IGestionUtilisateurs;

import java.util.List;
import java.util.Random;

public class MainVerification {

    public static void main(String[] args) {
        System.out.println(">>> STARTING VERIFICATION >>>");

        try {
            // 1. Initialize Services
            IGestionUtilisateurs gestionUtilisateurs = new GestionUtilisateurs();
            IGestionBoutique gestionBoutique = new GestionBoutique();
            IGestionReparateur gestionReparateur = new GestionReparateur();

            // 2. Create Unique Data
            long timestamp = System.currentTimeMillis();
            String cin = "C" + (timestamp % 10000);
            String email = "test" + timestamp + "@mail.com";

            System.out.println("[INFO] Creating Proprietaire with CIN: " + cin);

            Proprietaire p = Proprietaire.builder()
                    .nom("NomTest")
                    .prenom("PrenomTest")
                    .CIN(cin)
                    .email(email)
                    .motDePasse("1234")
                    .numTel("0600000000")
                    .actif(true)
                    .estReparateur(false)
                    .build();

            // 3. Register
            gestionUtilisateurs.inscriptionProprietaire(p);
            System.out.println("[SUCCESS] Proprietaire registered.");

            // 4. Create Boutique
            System.out.println("[INFO] Creating Boutique...");
            Boutique b = Boutique.builder()
                    .nom("Boutique " + timestamp)
                    .adresse("123 Test St")
                    .patente("PAT" + timestamp)
                    .numTel("0500000000")
                    .proprietaire(p)
                    .build();

            gestionBoutique.ajouterBoutique(b);
            System.out.println("[SUCCESS] Boutique created.");

            // 5. Verify Boutique List
            List<Boutique> boutiques = gestionBoutique.listerBoutiquesParProprietaire(p);
            if (boutiques.isEmpty()) {
                throw new RuntimeException("Boutique list is empty after creation!");
            }
            Boutique createdBoutique = boutiques.get(0);
            System.out.println("[SUCCESS] Boutique listed: " + createdBoutique.getNom());

            // 6. Add Reparateur
            System.out.println("[INFO] Adding Reparateur...");
            Reparateur r = Reparateur.builder()
                    .nom("Rep")
                    .prenom("Repairman")
                    .CIN("R" + (timestamp % 10000))
                    .email("rep" + timestamp + "@mail.com")
                    .motDePasse("pass")
                    .numTel("0700000000")
                    .actif(true)
                    .build();

            // Note: In IGestionReparateur, method signature is ajouterReparateur(Reparateur
            // reparateur, int idBoutique)
            gestionReparateur.ajouterReparateur(r, createdBoutique.getId());
            System.out.println("[SUCCESS] Reparateur added to Boutique ID: " + createdBoutique.getId());

            // 7. Verify Reparateur List
            List<Reparateur> reparateurs = gestionReparateur.listerReparateursParBoutique(createdBoutique.getId());
            if (reparateurs.isEmpty()) {
                throw new RuntimeException("Reparateur list is empty!");
            }
            System.out.println("[SUCCESS] Reparateur found: " + reparateurs.get(0).getNom());

            System.out.println("<<< VERIFICATION COMPLETE - ALL SYSTEMS FUNCTIONAL >>>");

        } catch (Exception e) {
            System.err.println("[ERROR] Verification Failed:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
