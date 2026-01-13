package metier;

import java.util.List;
import dao.Proprietaire;
import dao.Reparateur;
import dao.Utilisateurs;
import exception.DuplicateException;

public interface IGestionUtilisateurs {

	public Utilisateurs seConnecter(String cin, String mdp);

	public void inscriptionProprietaire(Proprietaire p) throws DuplicateException;

	public void ajouterReparateur(Reparateur r, int idBoutique) throws DuplicateException;

	/**
	 * Rechercher un utilisateur par nom ou prénom
	 */
	public List<Utilisateurs> chercherUtilisateurParNom(String nomOuPrenom);

}