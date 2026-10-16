# 🔧 Gestion de Réparations — Application de bureau Java

![Java](https://img.shields.io/badge/Java-8+-ED8B00?logo=openjdk&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-JPA-59666C?logo=hibernate&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?logo=apachemaven&logoColor=white)

Application de bureau **Java Swing** pour la gestion d'une chaîne de **boutiques de réparation d'appareils** : boutiques, réparateurs, clients, réparations et **transactions financières (prêts et emprunts)**.

---

## ✨ Fonctionnalités

### 👔 Espace Propriétaire
- Tableau de bord de l'activité
- Création, modification et liste des **boutiques**
- Gestion des **réparateurs** rattachés à chaque boutique
- Gestion des **types d'appareils**

### 🛠️ Espace Réparateur
- Tableau de bord et liste des tâches
- Enregistrement des **réparations** (plusieurs appareils par réparation, IMEI, montant)
- Suivi de l'état : *En attente*, *En cours*, *Terminé*
- Gestion des **clients** (avec photo)
- **Transactions financières** : prêts et emprunts entre utilisateurs, avec suivi de l'état (rendu / pas rendu)
- Gestion du profil

### 🔐 Sécurité
- Inscription et connexion
- Mots de passe chiffrés avec **BCrypt**

## 🛠️ Technologies

- **Java 8+**, interface graphique **Swing**
- **Hibernate / JPA** pour la persistance
- **MySQL** (via XAMPP)
- **Lombok**, **Maven**

## 🏗️ Architecture

Le projet suit une architecture en couches :

```
src/main/java/
├── dao/            # Entités JPA (Boutique, Client, Reparation, TransactionFinanciere…)
├── metier/         # Logique métier (interfaces IGestion* + implémentations)
├── presentation/   # Fenêtres et panneaux Swing
│   └── panels/     # boutique, reparateur, reparation, client, transaction…
├── exception/      # Exceptions personnalisées
└── util/           # Utilitaires (chiffrement des mots de passe)
```

## 🚀 Installation

**Prérequis :** JDK 8+, Maven, MySQL (XAMPP recommandé)

1. **Démarrer MySQL** (par défaut, l'application utilise le port **3307**).
2. **Configurer la connexion** dans `src/main/resources/META-INF/persistence.xml` (URL, utilisateur, mot de passe). La base `gest_Rep` est créée automatiquement.
3. **Lancer l'application** :

```bash
git clone https://github.com/AyaElouajjite/ProjetJAVA.git
cd ProjetJAVA
run.bat
```

Ou manuellement :

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="presentation.MainApp"
```

Pour plus de détails, voir [`SETUP.md`](SETUP.md) et [`XAMPP_SETUP.md`](XAMPP_SETUP.md).

## 👩‍💻 Auteur

**Aya Elouajjite** — [@AyaElouajjite](https://github.com/AyaElouajjite)
