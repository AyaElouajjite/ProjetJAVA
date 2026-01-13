package dao;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Proprietaire extends Utilisateurs {

    @Column(nullable = false)
    private boolean estReparateur = false;

    @CreationTimestamp
    private LocalDateTime dateInscription;

    // 🔗 Relation avec Boutique
    @OneToMany(mappedBy = "proprietaire", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Boutique> boutiques;
}
