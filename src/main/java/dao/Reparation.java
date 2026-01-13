package dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;

import dao.TypeAppareil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Reparation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String codeUnique;

    // ✅ NEW Multi Device
    @OneToMany(mappedBy = "reparation", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<AppareilReparation> appareils = new ArrayList<>();

    // Keep global state roughly matching the progress
    @Enumerated(EnumType.STRING)
    private EtatReparation etat;

    // Total Amount (Sum of devices)
    private double montant;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation;

    @ManyToOne
    private Client client;

    @ManyToOne
    private Boutique boutique;

    @ManyToOne
    private Reparateur reparateur; // Peut être null au début
}
