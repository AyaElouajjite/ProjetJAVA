package dao;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Boutique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 30, nullable = false)
    private String nom;

    @Column(length = 50)
    private String adresse;

    @Column(length = 30)
    private String patente;

    @Column(length = 15)
    private String numTel;

    @CreationTimestamp
    private LocalDateTime dateCreation;

    @ManyToOne
    private Proprietaire proprietaire;

    @OneToMany(mappedBy = "boutique", cascade = CascadeType.ALL)
    private List<Reparateur> reparateurs;

    private double solde = 0.0;

    // ✅ AJOUTE ICI
    @Override
    public String toString() {
        return nom; // ce qui s’affiche dans le JComboBox
    }
}
