package dao;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@ToString(exclude = { "boutique" }) // 🔴 TRÈS IMPORTANT

public class Reparateur extends Utilisateurs {

    @CreationTimestamp
    private LocalDateTime dateInscription;

    private Double pourcentage;

    // 🔗 Relation avec Boutique
    @ManyToOne
    private Boutique boutique;
}
