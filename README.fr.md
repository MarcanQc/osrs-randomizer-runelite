# OSRS Randomizer — RuneLite 0.1.1

Prototype Java autonome : choisir sa prochaine activité depuis le panneau latéral RuneLite. Interface anglaise, noms d’activités repris du catalogue de l’application Electron 1.0.1 : **24 Skills, 53 Bosses et 4 Raids**.

## Utilisation

1. Ouvrir le panneau avec le logo OSRS Randomizer dans la barre latérale.
2. Choisir Skills, Bosses ou Raids.
3. Cliquer sur les lignes pour inclure ou exclure les activités : doré = inclus. Utiliser **All / None** pour toute la catégorie, y compris les lignes masquées. Les filtres **Show all / Selected only / Excluded only** changent uniquement les lignes affichées, pas la sélection du tirage.
4. Cliquer sur **Draw**, puis cliquer à nouveau pour un nouveau résultat.

Chaque clic tire une seule activité. Le bouton utilise la protection suivante : les trois derniers résultats de la catégorie sont exclus. Si tous les choix sont bloqués, on libère les résultats du plus ancien au plus récent jusqu’à rendre un choix disponible. Avec deux activités, les résultats alternent ; avec trois, les deux derniers résultats restent exclus ; avec une seule, elle revient à chaque tirage. Une sélection vide désactive le bouton Draw.

Les sélections sont conservées par `ConfigManager` dans la configuration RuneLite, y compris une sélection vide. Les cinq derniers résultats par catégorie sont sauvegardés par personnage dans ConfigManager. À la connexion, le dernier résultat et la mémoire anti-répétition sont restaurés. Changer de catégorie ou de sélection ne réinitialise pas cette mémoire.

Le plugin ne fait aucun appel réseau, ne demande aucune clé et n’exécute aucune action dans le jeu. Les téléchargements de compilation et le fonctionnement normal du client RuneLite requièrent leur connexion habituelle. Pas d’audio dans cette version.

## Compiler et lancer sous Windows

Pré requis : JDK 17 ou 21 installé (prototype vérifié avec JDK 21), connexion pour le premier téléchargement des dépendances publiques. Le code du plugin cible Java 11, conformément au modèle RuneLite.

Depuis ce dossier dans PowerShell :

```powershell
.\gradlew.bat clean test jar
.\gradlew.bat run
```

Ou double-cliquer sur **Lancer-RuneLite.bat** pour lancer le client de développement avec le plugin chargé. Il s’agit d’un client de développement distinct du lancement habituel. Chercher « OSRS Randomizer » dans les plugins si le panneau n’apparaît pas.

Le fichier `build/libs/osrs-randomizer-0.1.0.jar` est un JAR de plugin, sans client intégré : il ne s’installe pas par double-clic et ne doit pas être copié au hasard dans le client normal. Le lancement pris en charge ici est `gradlew run`, selon le modèle officiel.

Pour un compte Jagex, suivre soi-même le [guide officiel RuneLite](https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts). Aucun identifiant ni fichier de connexion n’a besoin d’être transmis. Le panneau peut déjà être essayé sans se connecter à un compte.

## Validation

Vingt-cinq tests automatisés couvrent le catalogue, les sélections sauvegardées, les contrôles Swing, les catégories indépendantes et les changements de sélection. Le test du moteur effectue 500 tirages pour chaque taille de sélection de 1 à 24, soit 12 000 tirages.

À vérifier manuellement dans RuneLite :

- Choisir quatre activités et tirer plusieurs fois : aucun des trois derniers résultats ne doit revenir immédiatement.
- Choisir deux activités, puis une : vérifier l’alternance, puis le résultat unique.
- Cliquer sur None : le bouton Draw doit être désactivé.
- Passer de Skills à Raids puis revenir : choix et dernier résultat restent propres à chaque catégorie.
- Redémarrer le plugin puis se connecter au même personnage : sélections et historique restaurés, anti-répétition conservée.
- Vérifier le défilement de la liste Bosses, les noms longs et la disparition du bouton latéral à la désactivation.

La compilation, les tests hors jeu et l’aperçu Swing ne remplacent pas cette vérification dans le client.

## Structure et publication éventuelle

`Category` contient le catalogue, `DrawEngine` le tirage, `SelectionCodec` la lecture des sélections, `RandomizerPanel` l’interface et `OsrsRandomizerPlugin` l’intégration RuneLite. `OsrsRandomizerLauncher` charge le plugin dans le client de développement.

Projet indépendant : aucun fichier de l’application Electron ni de son dépôt n’a été modifié. Le catalogue a été repris tel quel ; cette version n’est pas une vérification exhaustive de tous les contenus actuellement disponibles dans OSRS.

Projet basé sur le [modèle officiel](https://github.com/runelite/example-plugin), avec métadonnées `build=standard`, Java 11 et sans dépendance de production supplémentaire. Les consignes du [Plugin Hub](https://github.com/runelite/plugin-hub), les [fonctionnalités refusées](https://github.com/runelite/runelite/wiki/Rejected-or-Rolled-Back-Features) et les [modalités de revue](https://github.com/runelite/runelite/wiki/Plugin-Hub-Review) ont été consultées le 15 octobre 2026. Le plugin propose une activité ; il ne fournit pas d’aide aux mécaniques de combat.

Aucun dépôt distant, aucune publication et aucune soumission au Plugin Hub n’ont été créés. Une éventuelle soumission devra passer par la revue RuneLite ; son acceptation n’est pas garantie.

## Icônes

Les catégories, les activités et le résultat affichent les icônes reprises des ressources de l’application Electron. Les 84 images sont embarquées en PNG transparents de 32 × 32 pixels maximum de contenu, proportions conservées. Aucun téléchargement au moment du tirage. Les visuels OSRS appartiennent à leurs titulaires respectifs, notamment Jagex ; la licence BSD du code ne leur attribue pas une nouvelle licence.


La sélection est présentée en une liste de lignes compactes de 38 pixels, avec des icônes de 20 pixels, avec des onglets Skills / Bosses / Raids. Les noms longs reviennent à la ligne. Toute l’interface du plugin est en anglais ; ce guide reste en français.



Le champ de recherche et le texte explicatif sous Draw ont été retirés. L’anti-répétition reste active.


## Résultat, historique et animation

Le résultat utilise une icône de 48 pixels et un nom agrandi. **Recent draws** affiche les cinq derniers résultats de la catégorie, du plus récent au plus ancien. Cet historique est sauvegardé après chaque résultat terminé et restauré à la connexion du personnage, même après fermeture du client.

Une animation automatique dure environ 0,8 seconde avant le résultat. La case Animate draws est retirée. Pendant l’animation, Draw est désactivé. Seul le résultat final est comptabilisé, pas les icônes de prévisualisation. Un changement de catégorie, de sélection ou de filtre annule l’animation sans consommer de tirage. Avec une seule activité sélectionnée, le résultat est immédiat.

Validation manuelle : essayer Draw avec animation, vérifier l’ordre des cinq résultats, changer de catégorie pendant une animation, puis désactiver et réactiver le plugin. Les tests automatisés couvrent les annulations et l’arrêt du minuteur à la désactivation.



## Filtre selon le personnage

Dans les réglages RuneLite du plugin (engrenage OSRS Randomizer), utiliser **Available for my character** pour exclure les activités bloquées par l’abonnement et par les quêtes et diaries pris en charge. Les règles de quête et de diary couvrent 18 activités (14 bosses, 2 raids et 2 skills), dont Artio, Spindel et Calvar’ion qui nécessitent le Wilderness Diary medium. Les lignes bloquées sont grisées ; leur infobulle indique la quête ou le diary à terminer. Les sélections personnelles sont conservées. Le filtre attend la connexion du personnage et se met à jour environ toutes les six secondes.

La couverture reste partielle : les niveaux et autres conditions ne sont pas encore vérifiés. Les activités non prises en charge restent disponibles, avec une infobulle explicite. Voir [QUEST-REQUIREMENTS.md](QUEST-REQUIREMENTS.md) pour les règles, sources et vérifications manuelles. Cette option est activée par défaut et ne figure plus dans le panneau principal. Un choix déjà sauvegardé est conservé. Les changements prennent effet immédiatement.



### Indication des activités bloquées
Les activités bloquées affichent un petit cadenas doré à droite et le prérequis exact en dessous du nom, en anglais. Les lignes disponibles restent compactes ; celles avec un prérequis long prennent uniquement la hauteur nécessaire. Les données en attente affichent Checking requirements… sans cadenas de blocage confirmé.

### Skills et abonnement
- Sailing nécessite Pandemonium ; Herblore nécessite Druidic Ritual.
- Sans abonnement : neuf skills membres, les raids et les bosses membres affichent Members only et sont exclus des tirages.
- Brutus, Obor et Bryophyta restent dans la liste gratuite du catalogue. Les autres conditions non prises en charge restent à vérifier (notamment la quête de Brutus).
- Les jours d’abonnement du personnage permettent de reconnaître un membre même sur un monde gratuit. Un monde membre confirme aussi l’accès membre.
- Les sélections sont conservées lors du déblocage d’une quête, d’un changement d’abonnement ou de la désactivation du filtre.
- Runecraft reste disponible sans Rune Mysteries : cette quête ne bloque pas toutes les méthodes d’entraînement.
- Les aperçus Skills utilisent des données simulées. Lecture réelle de l’abonnement et des deux quêtes à vérifier en jeu.

## Historique persistant
Les cinq derniers tirages de chaque catégorie sont stockés par personnage et par profil de configuration RuneLite. La mémoire anti-répétition est reconstruite à partir des trois plus récents, dans leur ordre original. Les animations annulées ne sont pas enregistrées.

Un changement de personnage charge son propre historique. La déconnexion masque le précédent jusqu’à une nouvelle connexion. Les tirages effectués sans connexion, lorsque le filtre est désactivé, restent temporaires et ne sont pas attribués au dernier compte utilisé. Les anciens historiques perdus avant cette modification ne sont pas récupérables.

Validation manuelle : faire plusieurs tirages connecté, fermer normalement le client, le relancer et se reconnecter au même personnage ; vérifier Recent draws et le prochain résultat. Répéter avec un autre personnage pour vérifier la séparation.

## Version 0.1.1
The Fractured Archive rejoint l’onglet Raids. Le filtre vérifie l’abonnement et While Guthix Sleeps ; une icône générique de raid est utilisée. Les sélections existantes restent intactes : cliquer sur le nouveau raid ou sur All pour l’ajouter aux tirages. La miniquête d’introduction n’est pas vérifiée.

## 0.1.2 — Diaries
Nouvel onglet compact : 48 choix, soit 12 régions × Easy / Medium / Hard / Elite. Icônes de livre originales colorées par difficulté, sélections sauvegardées et historique par personnage. Le filtre exclut les comptes gratuits et les paliers terminés. Les niveaux, quêtes et objets des tâches individuelles ne sont pas vérifiés. Les données inconnues restent bloquées jusqu'à synchronisation ; désactiver le filtre permet une sélection manuelle libre. 28 tests automatisés réussis ; synchronisation à valider en jeu.

### Filtre niveaux et quêtes — 22 octobre 2026
Les 48 paliers vérifient désormais les prérequis connus de RuneLite : niveaux de base, quêtes commencées/terminées, points de quête et combat. Premier prérequis manquant dans la ligne ; liste complète en infobulle. Les boosts et tâches individuelles déjà terminées ne sont pas pris en compte. Les objets, étapes précises de quêtes partielles, conditions de cape de quête et certains accès particuliers restent hors couverture. 33 tests réussis ; version dev, à valider en jeu avant publication.
