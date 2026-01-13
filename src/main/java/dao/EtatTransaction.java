package dao;

public enum EtatTransaction {
    EN_ATTENTE,      // Transaction créée, ajustements temporaires appliqués
    // États pour EMPRUNT
    RENDU,           // Emprunt rendu
    PAS_RENDU,       // Emprunt pas encore rendu
    // États pour PRET
    REMBOURSE,       // Prêt remboursé
    NON_REMBOURSE,   // Prêt non remboursé
    ANNULE           // Transaction annulée, ajustements temporaires annulés
}
