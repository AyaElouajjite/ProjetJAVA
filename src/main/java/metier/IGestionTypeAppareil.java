package metier;

import java.util.List;
import dao.TypeAppareil;

public interface IGestionTypeAppareil {
    void ajouterTypeAppareil(TypeAppareil t);

    List<TypeAppareil> listerTypesAppareil();

    void supprimerTypeAppareil(int id);

    TypeAppareil chercherTypeAppareil(String nom);
}
