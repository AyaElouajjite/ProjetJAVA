package dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class SingletonConnection {

    private static final String PERSISTENCE_UNIT_NAME = "TechRepair";
    private static EntityManagerFactory factory;

    // Bloc statique => exécuté une seule fois au chargement de la classe
    static {
        try {
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        } catch (Throwable ex) {
            System.err.println("❌ Initialisation de la SessionFactory a échoué." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManager getEntityManager() {
        return factory.createEntityManager();
    }

    public static void close() {
        if (factory != null) {
            factory.close();
        }
    }
}
