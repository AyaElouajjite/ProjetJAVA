package dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Inheritance(strategy = InheritanceType.JOINED)

public abstract class Utilisateurs {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected int id;
	
	@Column(length = 20, nullable = false)
	protected String nom;
	
	@Column(length = 20, nullable = false)
	protected String prenom;
	
	@Column(length = 10, nullable = false)
	protected String numTel;
	
	@Column(unique = true, nullable = false)
	protected String email;
	
	@Column(unique = true, nullable = false)
	protected String CIN;
	
	@Column(nullable = false)
	protected String motDePasse;

	@Column(nullable = false)
	protected boolean actif;

	protected double solde = 0.0;

	// Solde temporaire pour les transactions en attente de confirmation
	protected double soldeTemporaire = 0.0;
}
