package dao;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppareilReparation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    // @JoinColumn(name = "reparation_id", nullable = false) -> Removed to Fix JPA
    // 2.0 Compat
    private Reparation reparation;

    @ManyToOne
    // @JoinColumn(name = "type_appareil_id") -> Removed to Fix JPA 2.0 Compat
    private TypeAppareil typeAppareil;

    private String imei;

    @Column(length = 1000)
    private String description;

    private Double montant; // Prix de la réparation pour CET appareil

    @Enumerated(EnumType.STRING)
    private EtatReparation etat;

    // Helper to get formatted name
    public String toString() {
        return (typeAppareil != null ? typeAppareil.getNom() : "Appareil") + " (" + imei + ")";
    }
}
