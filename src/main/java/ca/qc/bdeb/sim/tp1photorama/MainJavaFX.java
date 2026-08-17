package ca.qc.bdeb.sim.tp1photorama;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainJavaFX extends Application {

    private Stage stage;

    private String niveauTolerance = "Faible";
    private String differencePixelMax = "Pixels";
    private int ecartPixelMax = 20;
    private double seuilMaxGlobale = 0.1;
    private int seuilHachageIntense = 10;

    private ComparateurImages comparateur = new ComparateurImagesPixel(ecartPixelMax, seuilMaxGlobale);
    private Gallerie gallerie = new Gallerie("temp", comparateur); // créer un objet gallerie par défaut

    private Image imagePolaroid = new Image("polaroid.png");
    private ImageView imageViewPolaroid = new ImageView(imagePolaroid);
    private Label labelDoublonDetecte = creerLabel("Doublons détectés", 13, 20, 0, 0, 0);
    private Label labelNbGroupes = creerLabel("Mes Photos (" + gallerie.CompterNbGroupes() + ")", 17, 0, 0, 10, 0);

    private BorderPane root = new BorderPane();
    private VBox colonneDroite = new VBox();
    private VBox colonneGauche = new VBox(5);

    private HBox rangeeToutesImages = new HBox(20);
    private ScrollPane scrollPaneToutesImages = new ScrollPane(rangeeToutesImages);
    private HBox rangeeImagesDoublons = new HBox(20);
    private ScrollPane scrollPaneImagesDoublons = new ScrollPane(rangeeImagesDoublons);

    private HBox rangeeDoublonDetecte = new HBox();
    private HBox rangeeNbGroupes = new HBox();
    private HBox rangeeImagePrincipale = new HBox();
    private HBox rangee2 = new HBox(5);
    private HBox rangee3 = new HBox(20);

    private ImageView imageViewSelectionnee = null;
    private String cheminImageSelectionnee = null;
    private Image imageSelectionnee = null;

    private boolean cliqueEffectue = false;

    @Override
    public void start(Stage stage) {

        var scene = new Scene(root, 1000, 750);
        stage.setScene(scene);
        stage.setTitle("Photorama");
        this.stage = stage;
        root.setLeft(colonneGauche);

        var rangeeLogoTitre = new HBox();
        var logoImage = new Image("logo.png");
        var logoVisuel = new ImageView(logoImage);
        logoVisuel.setFitWidth(50);
        logoVisuel.setFitHeight(50);

        var titre = new Label("Photorama", logoVisuel);
        titre.setFont(Font.font("Calibri", 26));
        titre.setContentDisplay(ContentDisplay.LEFT); // position du logo à gauche du texte
        rangeeLogoTitre.getChildren().add(titre);

        //Label: taille s'adapte aux textes, + polyvalent que Text : https://www.geeksforgeeks.org/java/javafx-label/
        var sousTitre = creerLabel("Ouvrir une gallerie", 20, 30, 0, 7, 0);
        var detectionDoublon = creerLabel("Détection de doublons :", 13, 0, 0, 0, 0);

        var menuDeroulant = new ChoiceBox<String>();
        menuDeroulant.getItems().addAll("Pixels", "Hachage (Moyenne)", "Hachage (Différences)");
        menuDeroulant.setPrefHeight(10);
        menuDeroulant.setValue("Pixels");
        configurerRangee(rangee2, scrollPaneToutesImages, Pos.TOP_CENTER);
        configurerRangee(rangee3, scrollPaneImagesDoublons, Pos.TOP_LEFT);

        var tolerance = creerLabel("Tolérance aux différences :", 14, 7, 0, 0, 0);
        var choixTolerance = new HBox();
        var faible = new RadioButton("Faible");
        var elevee = new RadioButton("Élevée");
        var groupeTolerance = new ToggleGroup(); // gère l’exclusivité
        faible.setToggleGroup(groupeTolerance);
        elevee.setToggleGroup(groupeTolerance);
        faible.setSelected(true);
        choixTolerance.getChildren().addAll(faible, elevee);

        var ouvrirDossier = new Button("Ouvrir un dossier...");

        modifierVbox(colonneGauche, Pos.TOP_LEFT, 20, 20, 20, 20,
                rangeeLogoTitre, sousTitre, detectionDoublon, menuDeroulant, tolerance, choixTolerance, ouvrirDossier);

        imageViewPolaroid.setFitHeight(300);
        imageViewPolaroid.setFitWidth(300);
        var centrerPolaroid = new VBox();
        modifierVbox(centrerPolaroid, Pos.TOP_CENTER, 0, 0, 0, 0, imageViewPolaroid);
        root.setCenter(centrerPolaroid);

        modifierHbox(rangeeImagePrincipale, Pos.CENTER, 0, 10, 20, 30);
        rangee2.setAlignment(Pos.TOP_CENTER);
        rangee3.setPadding(new Insets(5, 0, 0, 0));
        labelNbGroupes.setAlignment(Pos.TOP_LEFT);
        modifierHbox(rangeeNbGroupes, Pos.TOP_LEFT, 0, 0, 0, 0, labelNbGroupes);
        labelDoublonDetecte.setVisible(false);

        modifierHbox(rangeeDoublonDetecte, Pos.TOP_LEFT, 0, 0, 0, 0, labelDoublonDetecte);
        modifierVbox(colonneDroite, Pos.CENTER, 0, 20, 30, 20);

        styleScrollPane(scrollPaneToutesImages, 150);
        styleScrollPane(scrollPaneImagesDoublons, 100);


        ouvrirDossier.setOnAction(actionEvent -> {
            ouvrirDossierEtGererException();
        });

        menuDeroulant.setOnAction(actionEvent -> {
            differencePixelMax = menuDeroulant.getValue();
            mettreAJourComprateurEtGererException();
        });

        faible.setOnMouseClicked(mouseEvent -> {
            definirTolerance(false);
            mettreAJourComprateurEtGererException();
        });

        elevee.setOnMouseClicked(mouseEvent -> {
            definirTolerance(true);
            mettreAJourComprateurEtGererException();
        });

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                Platform.exit();
            }
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private void definirTolerance(boolean elevee) {

        if (elevee) {
            niveauTolerance = "Élevée";
            ecartPixelMax = 30;
            seuilMaxGlobale = 0.4;
            seuilHachageIntense = 15;

        } else {
            niveauTolerance = "Faible";
            ecartPixelMax = 20;
            seuilMaxGlobale = 0.1;
            seuilHachageIntense = 10;

        }
    }

    private void mettreAJourComparateur() throws IOException {

        switch (differencePixelMax) {

            case "Pixels":
                comparateur = new ComparateurImagesPixel(ecartPixelMax, seuilMaxGlobale);
                break;
            case "Hachage (Moyenne)":
                comparateur = new ComparateurImagesHachageMoyenne(seuilHachageIntense);
                break;
            case "Hachage (Différences)":
                comparateur = new ComparateurImagesHachageDifference(seuilHachageIntense);
                break;
        }

        gallerie.comparateur = comparateur;
        labelDoublonDetecte.setVisible(false);

        if (!gallerie.getCheminsComplets().isEmpty()) {
            gallerie.regrouperDoublon();
            afficherToutesImages();

            if (cliqueEffectue) { //S'assurer qu'on est cliqué sur une image
                afficherGroupeDoublons(rangeeImagesDoublons, cheminImageSelectionnee, labelDoublonDetecte);
                afficherImageEnGrand(imageSelectionnee, rangeeImagePrincipale, imageViewSelectionnee);
            }
        }
    }

    private void mettreAJourComprateurEtGererException() {
        try {
            mettreAJourComparateur();
        } catch (IOException e) {
            afficherMessageErreur("Erreur lors de l'ouverture de la gallerie d'images");
            viderToutesImages();
        }
    }

    private String ouvrirDossier(Stage stage) {
        DirectoryChooser selecteur = new DirectoryChooser();
        selecteur.setTitle("Sélectionnez un dossier d'images");
        selecteur.setInitialDirectory(new File("."));
        File dossierChoisi = selecteur.showDialog(stage);
        if (dossierChoisi != null) {
            return dossierChoisi.getAbsolutePath();
        }
        return null;
    }

    private void gererOuvrirDossier() throws IOException {
        String chemin = ouvrirDossier(stage);
        if (chemin != null) {
            cliqueEffectue = false;
            gallerie = new Gallerie(chemin, comparateur);
            gallerie.recupererCheminImages();
            gallerie.regrouperDoublon();
        }
    }

    private void ouvrirDossierEtAfficher() throws IOException {
        labelDoublonDetecte.setVisible(false);
        gererOuvrirDossier();
        afficherToutesImages();
    }

    private void ouvrirDossierEtGererException() {
        try {
            ouvrirDossierEtAfficher();
        } catch (IOException e) {
            afficherMessageErreur("Erreur lors de l'ouverture de la gallerie d'images");
            viderToutesImages();
        }
    }

    private void afficherToutesImages() {

        root.setBottom(null);
        viderToutesImages();
        //setManaged et setVisible : https://codingtechroom.com/question/hide-elements-javafx-no-space
        colonneDroite.setManaged(true);
        colonneDroite.setVisible(true);

        if (gallerie == null || gallerie.getGroupesImages().isEmpty()) {
            afficherMessageErreur("Veuillez d'abord ouvrir un fichier.");
            return;
        }

        labelNbGroupes.setText("Mes Photos (" + gallerie.CompterNbGroupes() + ")");

        for (ArrayList<String> groupe : gallerie.getGroupesImages()) {

            for (String chemin : groupe) {

                var imageChemin = new Image(new File(chemin).toURI().toString());
                var image = new ImageView(imageChemin);
                image.setFitWidth(170);
                image.setFitHeight(120);
                rangeeToutesImages.getChildren().add(image);

                // Cliquer sur une image => voir le groupe et afficher en grand
                image.setOnMouseClicked(mouseEvent -> {
                    cliquerSurImage(chemin, imageChemin, image);
                });
            }
        }
        // Réintégrer toutes les rangées dans la structure
        colonneDroite.getChildren().addAll(rangeeImagePrincipale, rangeeNbGroupes,
                rangee2, rangeeDoublonDetecte, rangee3);
        root.setCenter(colonneDroite);
    }

    private void afficherGroupeDoublons(HBox rangeeImageDoublon, String chemin, Label doublonDetecte) {

        doublonDetecte.setVisible(true);
        rangeeImageDoublon.getChildren().clear();
        cheminImageSelectionnee = chemin;

        for (ArrayList<String> groupeActuel : gallerie.getGroupesImages()) {
            if (groupeActuel.contains(chemin)) {
                for (String cheminDoublon : groupeActuel) {

                    File fileDoublon = new File(cheminDoublon);
                    var imageCheminDoublon = new Image(fileDoublon.toURI().toString());
                    var imageDoublon = new ImageView(imageCheminDoublon);
                    imageDoublon.setFitWidth(130);
                    imageDoublon.setFitHeight(80);

                    //Afficher aussi en grand le doublon si cliqué dessus
                    imageDoublon.setOnMouseClicked(mouseEvent ->
                            afficherImageEnGrand(imageCheminDoublon, rangeeImagePrincipale, imageDoublon));

                    rangeeImageDoublon.getChildren().add(imageDoublon);
                }
            }
        }
    }

    private void afficherImageEnGrand(Image imageChemin, HBox rangee1, ImageView image) {

        if (imageViewSelectionnee != null) {
            imageViewSelectionnee.setStyle("");
        }

        imageViewSelectionnee = image;
        imageSelectionnee = imageChemin;
        rangee1.getChildren().clear();

        // Effet de surbrillance fourni par ChatGPT
        image.setStyle("-fx-border-color: #4da3ff; " + "-fx-border-width: 4; " + "-fx-border-radius: 5; " +
                "-fx-effect: dropshadow(gaussian, rgba(77,163,255,0.5), 10, 0.3, 0, 0);");

        // Style fourni par ChatGPT
        var imageVersionGrande = new ImageView(imageChemin);
        imageVersionGrande.setStyle("""
                -fx-border-color: #4287f5;-fx-border-width: 4;
                -fx-border-radius: 6;-fx-effect: dropshadow(three-pass-box, #4287f5, 10, 0, 0, 0);""");
        imageVersionGrande.setFitWidth(500);
        imageVersionGrande.setFitHeight(350);
        rangee1.getChildren().add(imageVersionGrande);
    }

    private void cliquerSurImage(String chemin, Image imageChemin, ImageView image) {
        cliqueEffectue = true;
        imageViewPolaroid.setManaged(false);
        imageViewPolaroid.setVisible(false);
        afficherGroupeDoublons(rangeeImagesDoublons, chemin, labelDoublonDetecte);
        afficherImageEnGrand(imageChemin, rangeeImagePrincipale, image);
    }

    private void viderToutesImages() {
        rangeeToutesImages.getChildren().clear();
        rangeeImagePrincipale.getChildren().clear();
        rangeeImagesDoublons.getChildren().clear();
        rangeeNbGroupes.getChildren().clear();
        rangeeNbGroupes.getChildren().add(labelNbGroupes);
        colonneDroite.getChildren().clear();
    }

    private void afficherMessageErreur(String messageErreur) {

        var rangeeMessageErreur = new HBox();
        rangeeMessageErreur.setAlignment(Pos.CENTER);

        //Style fourni par ChatGPT
        rangeeMessageErreur.setStyle("""
                -fx-background-color: #ffd6d6;  /* fond rouge pâle */
                -fx-padding: 10px;""");

        //Étendre sur tout l'écran: https://codingtechroom.com/question/java-double-max-value
        rangeeMessageErreur.setMaxWidth(Double.MAX_VALUE);
        var textMessageErreur = new Label("ERREUR: " + messageErreur);
        rangeeMessageErreur.getChildren().add(textMessageErreur);

        //Style fourni par chatGPT
        textMessageErreur.setStyle("""
                -fx-text-fill: #b30000; -fx-font-size: 16px;
                 -fx-background-color: #ffd6d6; -fx-padding: 8px 10px;""");

        root.setBottom(rangeeMessageErreur);
    }

    private void styleScrollPane(ScrollPane scrollHorizontal, int hauteur) {

        //Style fourni par ChatGPT
        scrollHorizontal.setStyle(
                "-fx-focus-color: transparent; -fx-faint-focus-color: transparent; " +
                        "-fx-background-insets: 0; -fx-background-color: transparent;");

        //https://www.tutorialspoint.com/javafx/javafx_scrollpane.htm
        // https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/ScrollPane.ScrollBarPolicy.html
        scrollHorizontal.setFitToHeight(true);
        scrollHorizontal.setPrefHeight(hauteur);
        scrollHorizontal.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); //Désactive le scroll vertical
        scrollHorizontal.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); //Gère la visibilité
        scrollHorizontal.setPannable(true); // permet de "glisser" à la souris
    }

    private HBox modifierHbox(HBox hbox, Pos alignment, int a, int b, int c, int d, Node... enfants) {

        //Node... enfants pour addAll tous les enfants :
        //Source : https://stackoverflow.com/questions/3158730/what-do-3-dots-next-to-a-parameter-type-mean-in-java

        hbox.setAlignment(alignment);
        hbox.setPadding(new Insets(a, b, c, d));
        hbox.getChildren().addAll(enfants);
        return hbox;
    }

    private VBox modifierVbox(VBox vbox, Pos alignment, int a, int b, int c, int d, Node... enfants) {
        vbox.setAlignment(alignment);
        vbox.setPadding(new Insets(a, b, c, d));
        vbox.getChildren().addAll(enfants);
        return vbox;
    }

    private void configurerRangee(HBox rangee, ScrollPane scroll, Pos alignement) {
        rangee.getChildren().add(scroll);
        rangee.setAlignment(alignement);
        rangee.setManaged(true);
    }

    private Label creerLabel(String texte, int taille, int a, int b, int c, int d) {
        Label label = new Label(texte);
        label.setFont(Font.font("Calibri", taille));
        label.setPadding(new Insets(a, b, c, d));
        return label;
    }

}
