package ca.qc.bdeb.sim.tp1photorama;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

public class Gallerie {

    private String dossier;
    private ArrayList<String> cheminsComplets = new ArrayList<>();
    private ArrayList<ArrayList<String>> groupesImages = new ArrayList<>();
    public ComparateurImages comparateur;
    private int nbGroupes = 0;

    public Gallerie(String dossier, ComparateurImages comparateur) {
        this.dossier = dossier;
        this.comparateur = comparateur;
    }

    public void recupererCheminImages() throws IOException {

        cheminsComplets.clear();
        var repertoire = new File(dossier); //repertoire représente le dossier sur ton disque.

        if (repertoire.listFiles() == null) { //Vérifier que le dossier existe et contient des fichiers
            throw new FileNotFoundException(dossier + " n'est pas un répertoire");
        }

        for (var fichier : repertoire.listFiles()) { //Parcourir tous les fichiers du dossier
            var nomFichier = fichier.getName();  //Récupérer le nom du fichier (pas le chemin)

            if (nomFichier.endsWith(".png") || nomFichier.endsWith(".jpg")) {
                var cheminFichier = dossier + "/" + nomFichier; //On a l'image avec son chemin

                cheminsComplets.add(cheminFichier);
            }
        }
    }

    public void regrouperDoublon() throws IOException {

        ArrayList<String> imagesRestantes = new ArrayList<>(cheminsComplets);

        // Trier alphabetiquement: https://www.geeksforgeeks.org/java/collections-sort-java-examples/
        Collections.sort(imagesRestantes, String.CASE_INSENSITIVE_ORDER);

        groupesImages.clear();
        while (!imagesRestantes.isEmpty()) {

            //Prendre la premiere image, verifier tous ceux qui sont similaires à lui, les grouper.
            String imageTemp = imagesRestantes.get(0);

            ArrayList<String> groupeSimilaire = new ArrayList<>();
            groupeSimilaire.add(imageTemp);

            for (int i = 1; i < imagesRestantes.size(); i++) {

                if (comparateur.imagesSimilaire(imageTemp, imagesRestantes.get(i))) {
                    groupeSimilaire.add(imagesRestantes.get(i));
                }

            }
            Collections.sort(groupeSimilaire, String.CASE_INSENSITIVE_ORDER);

            groupesImages.add(groupeSimilaire);
            imagesRestantes.removeAll(groupeSimilaire); //Retirer tous ceux qui ont déja été groupés

        }

        //Trier selon premier élément de chaque sous-liste : ligne suivante fournie par ChatGPT
        Collections.sort(groupesImages, (g1, g2) -> g1.get(0).compareToIgnoreCase(g2.get(0)));
    }

    public void afficherGroupesimages() {

        nbGroupes = 0;

        for (ArrayList<String> groupe : groupesImages) {

            this.nbGroupes++;
            System.out.print("[" + nbGroupes + "]");

            for (String nomImage : groupe) {
                System.out.print(cheminCourt(nomImage) + " ");
            }
            System.out.println();
        }
    }

    public ArrayList<String> getCheminsComplets() {
        return cheminsComplets;
    }

    public ArrayList<ArrayList<String>> getGroupesImages() {
        return groupesImages;
    }

    //Source de la méthode suivante: https://www.geeksforgeeks.org/java/path-getnamecount-method-in-java-with-examples/
    public String cheminCourt(String chemin) {
        Path p = Paths.get(chemin);
        return p.getFileName().toString(); // retourne uniquement "zz8.jpg"
    }

    public int CompterNbGroupes() {
        nbGroupes = 0;
        for (ArrayList<String> groupe : groupesImages) {
            this.nbGroupes++;
        }
        return nbGroupes;
    }

}
