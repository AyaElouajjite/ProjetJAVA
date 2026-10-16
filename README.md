# Gestion de Réparations — Application de bureau Java

![Java](https://img.shields.io/badge/Java-8+-ED8B00?logo=openjdk&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-JPA-59666C?logo=hibernate&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?logo=apachemaven&logoColor=white)

Application de bureau Java Swing destinée à la gestion d'un réseau de boutiques de réparation d'appareils électroniques. Elle couvre la gestion des boutiques, des réparateurs, des clients, des réparations et des transactions financières entre utilisateurs (prêts et emprunts).

## Fonctionnalités

**Espace propriétaire**
- Tableau de bord de l'activité
- Création, modification et consultation des boutiques
- Gestion des réparateurs rattachés à chaque boutique
- Gestion des types d'appareils

**Espace réparateur**
- Tableau de bord et liste des tâches
- Enregistrement des réparations (plusieurs appareils par réparation, IMEI, montant)
- Suivi de l'état des réparations : en attente, en cours, terminée
- Gestion des clients
- Transactions financières (prêts et emprunts) avec suivi du remboursement
- Gestion du profil

**Sécurité**
- Inscription et authentification
- Hachage des mots de passe avec BCrypt

## Points techniques

- Architecture en couches : **DAO / métier / présentation**
- Couche métier définie par des interfaces (`IGestion*`) et leurs implémentations
- Persistance avec **Hibernate / JPA** : relations `OneToMany` et `ManyToOne`, énumérations, horodatage automatique
- Connexion à la base gérée par un singleton
- Exceptions métier personnalisées
- Réduction du code répétitif avec **Lombok**
- Build et exécution automatisés avec **Maven**

## Stack technique

Java 8+ · Swing · Hibernate / JPA · MySQL · Lombok · Maven · BCrypt

## Architecture

```
src/main/java/
├── dao/            # Entités JPA
├── metier/         # Logique métier (interfaces et implémentations)
├── presentation/   # Fenêtres et panneaux Swing
├── exception/      # Exceptions métier
└── util/           # Utilitaires (hachage des mots de passe)
```

## Installation

**Prérequis :** JDK 8 ou supérieur, Maven, MySQL (XAMPP recommandé).

1. Démarrer MySQL. L'application utilise par défaut le port 3307.
2. Adapter les paramètres de connexion dans `src/main/resources/META-INF/persistence.xml`. La base `gest_Rep` est créée automatiquement.
3. Lancer l'application :

```bash
git clone https://github.com/AyaElouajjite/ProjetJAVA.git
cd ProjetJAVA
mvn clean compile
mvn exec:java -Dexec.mainClass="presentation.MainApp"
```

Sous Windows, le script `run.bat` effectue ces étapes. La configuration détaillée est décrite dans [SETUP.md](SETUP.md) et [XAMPP_SETUP.md](XAMPP_SETUP.md).

## Contact

**Aya Elouajjite** — [github.com/AyaElouajjite](https://github.com/AyaElouajjite)
