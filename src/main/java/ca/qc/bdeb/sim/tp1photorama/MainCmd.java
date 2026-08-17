package ca.qc.bdeb.sim.tp1photorama;

import java.io.IOException;

public class MainCmd {

    public static void main(String[] args) {

        int ecartPX = 5;
        double seuilMax = 0.2;

        Gallerie gallerie = new Gallerie(
                "debogage"
                , new ComparateurImagesPixel(ecartPX, seuilMax));

        try {

            gallerie.recupererCheminImages();

            String diff1 = gallerie.getCheminsComplets().get(0);
            String diff2 = gallerie.getCheminsComplets().get(1);
            String diff3 = gallerie.getCheminsComplets().get(2);
            String moyenne1 = gallerie.getCheminsComplets().get(3);
            String moyenne2 = gallerie.getCheminsComplets().get(4);
            String moyenne3 = gallerie.getCheminsComplets().get(5);
            String img1 = gallerie.getCheminsComplets().get(6);
            String img2 = gallerie.getCheminsComplets().get(7);
            String img3 = gallerie.getCheminsComplets().get(8);
            String img4 = gallerie.getCheminsComplets().get(9);

            System.out.println("=== 1. Test des différences de pixels === ");

            testerPixel(gallerie, ecartPX, seuilMax, img1, img2);
            testerPixel(gallerie, ecartPX, seuilMax, img1, img3);

            seuilMax = 0.4;
            testerPixel(gallerie, ecartPX, seuilMax, img1, img3);

            seuilMax = 0.2;
            testerPixel(gallerie, ecartPX, seuilMax, img1, img4);

            ecartPX = 128;
            seuilMax = 0.49;
            testerPixel(gallerie, ecartPX, seuilMax, img1, img4);

            seuilMax = 0.5;
            testerPixel(gallerie, ecartPX, seuilMax, img1, img4);

            seuilMax = 0.51;
            testerPixel(gallerie, ecartPX, seuilMax, img1, img4);

            System.out.println("\n" + "=== 2. Affichage des valeurs de hachage ===");
            gallerie.comparateur = new ComparateurImagesHachageMoyenne(5);//Le seuil na pas d'importance

            afficherHachage(gallerie, moyenne1);
            afficherHachage(gallerie, moyenne2);
            afficherHachage(gallerie, moyenne3);

            gallerie.comparateur = new ComparateurImagesHachageDifference(5);//Le seuil na pas d'importance
            afficherHachage(gallerie, diff1);
            afficherHachage(gallerie, diff2);
            afficherHachage(gallerie, diff3);

            Gallerie gallerie2 = new Gallerie
                    ("airbnb-petit",
                            new ComparateurImagesPixel(ecartPX, seuilMax));
            gallerie2.recupererCheminImages();

            System.out.println("=== 3. Analyse de la qualité des différents algos pour airbnb-petit ===");

            testerAlgo(gallerie2, new ComparateurImagesPixel(8, 0.2), "Comparateur Pixels  (seuil différences=8, pourcentage différences max=0.2)");
            testerAlgo(gallerie2, new ComparateurImagesPixel(20, 0.5), "Comparateur Pixels  (seuil différences=20, pourcentage différences max=0.5)");
            testerAlgo(gallerie2, new ComparateurImagesHachageMoyenne(8), "Comparateur Hachage Moyenne (cases différentes max=8)");
            testerAlgo(gallerie2, new ComparateurImagesHachageMoyenne(16), "Comparateur Hachage Moyenne (cases différentes max=16)");
            testerAlgo(gallerie2, new ComparateurImagesHachageDifference(8), "Comparateur Hachage Différences (cases différentes max=8)");
            testerAlgo(gallerie2, new ComparateurImagesHachageDifference(16), "Comparateur Hachage Différences (cases différentes max=16)");

        } catch (IOException e) {

            //Couleur fournie par chatGPT
            System.out.println("\u001B[31m" + "Erreur lors du chargement du dossier." + "\u001B[0m");
        }
    }

    public static void afficherResultatPixel(int ecart, double seuil, String img1, String img2, Gallerie gallerie) throws IOException {
        System.out.println("seuil=" + ecart + ", max pourcent=" + seuil + " "
                + img1 + " vs " + img2 + " " + gallerie.comparateur.resultatComparaison(img1, img2));
    }

    private static void testerPixel(Gallerie gallerie, int ecart, double seuil, String img1, String img2) throws IOException {
        gallerie.comparateur = new ComparateurImagesPixel(ecart, seuil);
        afficherResultatPixel(ecart, seuil, img1, img2, gallerie);
    }

    public static void afficherHachage(Gallerie gallerie, String chemin) throws IOException {
        ComparateurHachage comp = (ComparateurHachage) gallerie.comparateur;
        System.out.println(chemin);
        comp.creerTableau(chemin);
        comp.afficherTab();
    }

    public static void testerAlgo(Gallerie gallerie, ComparateurImages comparateur, String description) throws IOException {
        System.out.println("Algo: " + description + "\n");
        gallerie.comparateur = comparateur;
        gallerie.regrouperDoublon();
        gallerie.afficherGroupesimages();
    }
}
