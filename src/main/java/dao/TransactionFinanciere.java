package dao;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class TransactionFinanciere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private Utilisateurs preteur; // Celui qui prête l'argent (peut être null si c'est une personne externe)

    @ManyToOne
    private Utilisateurs emprunteur; // Celui qui emprunte l'argent (peut être null si c'est une personne externe)

    @Column(length = 100)
    private String nomPreteurExterne; // Nom du prêteur si c'est une personne externe au système

    @Column(length = 100)
    private String nomEmprunteurExterne; // Nom de l'emprunteur si c'est une personne externe au système

    @Column(nullable = false)
    private double montant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeTransaction typeTransaction; // PRET ou EMPRUNT (du point de vue de l'initiateur)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatTransaction etat; // EN_ATTENTE, RENDU/PAS_RENDU (EMPRUNT), REMBOURSE/NON_REMBOURSE (PRET), ANNULE

    @Column(length = 500)
    private String description;

    @CreationTimestamp
    private LocalDateTime dateCreation;

    private LocalDateTime dateConfirmation; // Date de confirmation de la transaction

    // Méthode utilitaire pour obtenir le nom de l'autre partie (utilisateur ou externe)
    public String getNomAutrePartie() {
        if (typeTransaction == TypeTransaction.PRET) {
            return emprunteur != null ? (emprunteur.getNom() + " " + emprunteur.getPrenom()) : nomEmprunteurExterne;
        } else {
            return preteur != null ? (preteur.getNom() + " " + preteur.getPrenom()) : nomPreteurExterne;
        }
    }
}
