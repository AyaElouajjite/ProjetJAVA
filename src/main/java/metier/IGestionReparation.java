package metier;

import java.util.List;
import dao.Reparation;
import dao.EtatReparation;

public interface IGestionReparation {
    void ajouterReparation(Reparation r);

    List<Reparation> listerToutesReparations();

    List<Reparation> listerReparationsParBoutique(int boutiqueId);

    List<Reparation> listerReparationsParReparateur(int reparateurId);

    Reparation getReparation(int id);

    void modifierReparation(Reparation r);

    void changerEtat(int id, EtatReparation nouvelEtat);

    void changerEtatAppareil(int appareilId, EtatReparation nouvelEtat);

    void supprimerReparation(int id);
}
