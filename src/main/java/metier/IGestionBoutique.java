package metier;

import java.util.List;
import dao.Boutique;
import dao.Proprietaire;

public interface IGestionBoutique {

    // CREATE
    void ajouterBoutique(Boutique boutique);

    // READ
    List<Boutique> listerBoutiquesParProprietaire(Proprietaire proprietaire);

    Boutique chercherBoutique(int idBoutique);

    // UPDATE
    void modifierBoutique(Boutique boutique);

    // DELETE
    void supprimerBoutique(int idBoutique);
}
