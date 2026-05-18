# Kitchen Receipt Print

Application de bureau JavaFX qui récupère automatiquement les commandes au format PDF depuis un serveur FTP et les imprime sur une ou plusieurs imprimantes de cuisine (tickets de commande).

L'application est conçue pour les restaurants ayant besoin d'imprimer en continu les commandes déposées sur un serveur FTP par un système de prise de commande externe (caisse, site web, etc.).

## Fonctionnalités

- Interrogation périodique d'un serveur FTP (intervalle configurable, par défaut 60 s).
- Téléchargement des fichiers PDF respectant le format `order_AAAAMMJJHHMMSS.pdf`.
- Impression automatique de chaque PDF sur plusieurs imprimantes simultanément.
- Suppression des fichiers traités sur le serveur FTP.
- Stockage local de la configuration dans une base H2 embarquée (`~/kitchenreceiptprint`).
- Chiffrement du mot de passe FTP via `CryptoUtil` / jBCrypt.
- Interface de configuration (serveur FTP, identifiants, intervalle, sélection multi-imprimantes).
- Internationalisation : Français (par défaut) et Anglais.
- Boutons Démarrer / Pause / Reprendre pour piloter la tâche périodique.
- Empaquetage Windows possible via `jpackage` (script `jpackage.bat` fourni).

## Stack technique

- Java 11
- JavaFX 21.0.2 (controls, fxml, base, graphics)
- Apache Commons Net 3.10 (client FTP)
- Apache PDFBox 3.0.1 (lecture et impression des PDF)
- H2 Database 2.2.224 (stockage de la configuration)
- jBCrypt 0.4 (chiffrement)
- Maven (avec `maven-shade-plugin` pour produire un jar exécutable)
- Java Print Service API (`javax.print`)

## Structure du projet

```
src/main/java/com/kitchenreceiptprint/
├── App.java                    # Point d'entrée JavaFX
├── Main.java                   # Wrapper main pour le jar shadé
├── controller/
│   ├── MainLayoutController.java        # UI principale (démarrer/pause/reprendre)
│   ├── ConfigController.java            # Écran de configuration
│   ├── FtpDownloaderController.java     # Lister / télécharger / supprimer via FTP
│   ├── PdfPrinterController.java        # Impression PDF multi-imprimantes
│   ├── ProcessPdfFilesController.java   # Orchestration FTP → impression → suppression
│   ├── PrintController.java             # Découverte des imprimantes système
│   └── PeriodicTaskRunnerController.java# Ordonnanceur (ScheduledExecutorService)
├── model/
│   └── DatabaseModel.java      # Accès H2 (configuration, imprimantes)
├── util/
│   ├── CryptoUtil.java         # Chiffrement / déchiffrement du mot de passe FTP
│   ├── LocalizationUtil.java   # Chargement des bundles i18n
│   ├── LanguageUtil.java
│   ├── MessageUtil.java        # Journal d'événements dans la TextArea
│   └── ExceptionUtil.java
└── view/
    ├── MainLayoutView.java
    ├── ConfigLayoutView.java
    └── AboutLayoutView.java

src/main/resources/
├── com/kitchenreceiptprint/*.fxml   # Vues JavaFX
├── lang_fr.properties               # Traductions FR
├── lang_en.properties               # Traductions EN
└── icon.png
```

## Prérequis

- JDK 11 ou supérieur (avec `jpackage` pour produire un installeur natif).
- Maven 3.6+ (ou utilisez le wrapper `mvnw` / `mvnw.cmd` fourni).
- Un serveur FTP accessible déposant des fichiers nommés `order_AAAAMMJJHHMMSS.pdf` à la racine.
- Une ou plusieurs imprimantes installées et visibles depuis le système.

## Lancer en développement

Via le plugin JavaFX Maven :

```bash
./mvnw clean javafx:run
```

Sur Windows :

```bat
mvnw.cmd clean javafx:run
```

## Construire un jar exécutable

Le `maven-shade-plugin` produit un jar autonome dans `target/` :

```bash
./mvnw clean package
```

Le fichier généré est `target/kitchenreceiptprint-1.0-SNAPSHOT-shaded.jar`. Il s'exécute avec :

```bash
java -jar target/kitchenreceiptprint-1.0-SNAPSHOT-shaded.jar
```

## Créer un installeur Windows (.exe)

Le script `jpackage.bat` enveloppe le jar shadé dans un installeur natif Windows avec icône, raccourci menu Démarrer et raccourci bureau :

```bat
jpackage.bat
```

> Adaptez les chemins `INPUT_PATH`, `OUTPUT_PATH` et `ICON_PATH` du script à votre environnement avant exécution.

## Utilisation

1. Lancer l'application.
2. Ouvrir **Paramètres** et renseigner :
   - Serveur FTP, nom d'utilisateur et mot de passe.
   - Intervalle d'interrogation en secondes (60 par défaut).
   - Cocher la ou les imprimantes à utiliser dans la liste détectée.
3. Cliquer sur **Confirmer** pour sauvegarder.
4. Cliquer sur **Démarrer** : l'application interroge le FTP à l'intervalle défini, télécharge les PDF `order_*.pdf`, les imprime sur chaque imprimante sélectionnée puis les supprime du serveur.
5. Utiliser **Pause** / **Reprendre** pour contrôler la tâche à la volée.

Les PDF téléchargés sont stockés temporairement dans `${java.io.tmpdir}/pdf`.

## Configuration et stockage

- Base H2 embarquée : `~/kitchenreceiptprint` (fichiers `.mv.db` et `.trace.db` créés dans le dossier utilisateur).
- Identifiants H2 internes : `krp` / `1234` (utilisés uniquement pour la base locale).
- Tables :
  - `configuration(name, value)` — paramètres clé/valeur (FTP, intervalle, …).
  - `printers(name)` — liste des imprimantes sélectionnées.
- Le mot de passe FTP est chiffré avant insertion en base.

## Format attendu des fichiers FTP

Les PDF doivent se trouver à la racine du compte FTP et respecter le motif :

```
order_<14 chiffres>.pdf      ex.: order_20240502143015.pdf
```

Les 14 chiffres représentent la date et l'heure au format `yyyyMMddHHmmss`. Les fichiers sont triés par horodatage croissant avant traitement.

## Licence

Non spécifiée. Tous droits réservés par l'auteur.

## Auteur

Artisan Webmaster
