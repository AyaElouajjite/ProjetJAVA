package util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    // Hasher un mot de passe
    public static String hasherMotDePasse(String motDePasse) {
        return BCrypt.hashpw(motDePasse, BCrypt.gensalt(12));
    }

    // Vérifier un mot de passe
    public static boolean verifierMotDePasse(String motDePasse, String hash) {
        return BCrypt.checkpw(motDePasse, hash);
    }
}