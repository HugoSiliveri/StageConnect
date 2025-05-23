# StageConnect
Hugo Siliveri

Lien du Git : https://github.com/HugoSiliveri/StageConnect

## Environnement

- Minimum SDK : API 24 (Nougat)
- Appareil virtuel utilisé : Pixel 8 API 35 (Android 15)
- Appareil physique utilisé : Samsung S20 API 33 (Android 13)

## Architecture

L'architecture actuelle de l'application est :

**Front** (Jetpack Compose):
- `MainActivity.kt` : Contient le controller pour naviguer entre les vues
- `model` : Contient les données 
  - `repository` : Connexion avec Firebase
- `ui` : Contient les vues de l'application
  - `auth` : Les vues liées à la connexion et à l'inscription
  - `company` : Le package contient `CompanyScreen.kt` qui permet de naviguer entre les vues accessibles de type "Entreprise"
- `viewmodel` : Contient la logique métier te fait le pont entre les modèles et les vues

**Back** (Firebase) :
- `Firestore Database` :
    - `users` : Collection des utilisateurs (id, type, email, téléphone, adresse, prénom (facultatif), nom (facultatif), nom d'organisation (facultatif)). Les utilisateurs peuvent être du type "Stagiaire", "Entreprise", ou "Etablissement de formation"
- `Authentification` : Connexion email/mot de passe (utilise la vue users pour la connexion)

## Liste des fonctionnalités réalisés

Connexion/inscription avec Firebase

<img src="screenshots/connexion.png" alt="connexion" width="200"/>
<img src="screenshots/inscription.png" alt="inscription" width="200"/>

- Utilisateur Entreprise : 
  - Affichage des offres de stage de l'entreprise (faux stages)

<img src="screenshots/company_offers.png" alt="company_offers" width="200"/>