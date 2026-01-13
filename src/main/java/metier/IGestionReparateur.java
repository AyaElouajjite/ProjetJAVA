package metier;

import java.util.List;
import dao.Reparateur;

public interface IGestionReparateur {

    void ajouterReparateur(Reparateur reparateur, int idBoutique);

    List<Reparateur> listerReparateursParBoutique(int idBoutique);

    Reparateur chercherReparateur(int idReparateur);

    void supprimerReparateur(int idReparateur);

	void modifierReparateur(Reparateur reparateur);
}
