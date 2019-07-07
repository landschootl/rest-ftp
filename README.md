# Serveur rest
####Université Lille 1 - Master Miage FA - CAR
####Ludovic Landschoot

##Présentation
Le but du projet est la réalisation d'un serveur rest permettant une gestion de fichier.

####Comment le démarrer ?

* Exécuter la commande suivante à la racine du projet ou se trouve le pom :
```
mvn spring-boot:run
```
Info : A l'éxécution de spring boot, il se chargera de compiler et de builder la partie angular dans son dossier 'static'.

#### Comment elle fonctionne ?

Information : Au lancement de l'application, aucun dossier et fichier seront présent, il faudra les ajouter depuis l'application, ou dans le répertoire de travail par défaut : /tmp/restftp/public.
Le chemin du répertoire de travail est modifiable via le fichier de config : "ServerConfig.java".

* Démarrer le serveur.
* (optionnel) Modifier le chemin du répertoire de travail dans le fichier "ServerConfig.java".
* (optionnel) Ajouter des dossiers et fichiers dans se répertoire.
    * Possibilité de le faire depuis l'application.
* Lancer un navigateur (Chrome de préférence) et rendez-vous sur "localhost:8080".
* Utiliser l'application !

##Architecture du projet
```
| src 
    | main                      
        | java                  
            | config            : Ce qui concerne la configuration du serveur
            | controller        : Possède les controllers
            | entity            : Possède les entités manipulés
            | exception         : Possède les exceptions
        | ressources   
        | webapp                : La partie front (angular) 
```

##Les différentes méthodes de l'api
Se référer à la javadoc sur les controllers.

##Les différentes fonctionnalités de l'application
* Récupérer l'ensemble des fichiers d'un répertoire.
* Récupérer l'ensemble des fichiers et sous fichiers d'un répertoire de manière récursif.
* Rechercher avec un filtre des fichiers ou répertoires.
* Navigation entre les répertoires.
* Accéder à un répertoire par une url.
* Créer un répertoire.
* Upload un fichier.
* Supprimer un fichier ou un dossier.
* Copier un fichier.
* Renommage d'un répertoire.
* Télécharger un fichier.
* Gestion de message d'erreur et de notification.

##Contact
Pour toutes questions sur le tp --> Ludovic.isn@hotmail.fr