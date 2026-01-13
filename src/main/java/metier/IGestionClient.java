package metier;

import java.util.List;
import dao.Client;
import exception.DuplicateException;

public interface IGestionClient {
    void ajouterClient(Client c) throws DuplicateException;

    List<Client> listerClients();

    List<Client> chercherParNom(String mc);

    void modifierClient(Client c);

    void supprimerClient(int id);

    Client getClient(int id);
}
