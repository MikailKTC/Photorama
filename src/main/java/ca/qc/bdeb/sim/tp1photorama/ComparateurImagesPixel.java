package ca.qc.bdeb.sim.tp1photorama;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class ComparateurImagesPixel extends ComparateurImages {

    protected int seuilEcartPixel;  //Évaluer écart entre 2 pixels
    protected double seuilPourcentageDiff; //Évaluer pourcentage de pixel différent

    public ComparateurImagesPixel(int ecartPixelMax, double seuilMaxGlobale) {
        this.seuilEcartPixel = ecartPixelMax;
        this.seuilPourcentageDiff = seuilMaxGlobale;
    }

    @Override
    public int[][] tailleTableau(String cheminImage) throws IOException {

        BufferedImage nouvImage = GestionnaireImages.lireImage(cheminImage);
        int[][] tabImage = GestionnaireImages.toPixels(nouvImage);
        return tabImage;
    }

    @Override
    public boolean imagesSimilaire(String image1Chemin, String image2Chemin) throws IOException {

        double compteurDifferent = 0;
        int nombreDePixels = 0;
        boolean similaire = true;
        int[][] tabImage1 = tailleTableau(image1Chemin);
        int[][] tabImage2 = tailleTableau(image2Chemin);

        // Vérifier dimensions
        if (tabImage1.length != tabImage2.length) {
            return false;
        }

        for (int i = 0; i < tabImage1.length; i++) {

            if (tabImage1[i].length != tabImage2[i].length) {

                return false;
            }
        }

        for (int i = 0; i < tabImage1.length; i++) {

            for (int j = 0; j < tabImage1[i].length; j++) {

                //Pour chaque pixel différent, on incrémente le compteur
                if (Math.abs(tabImage1[i][j] - tabImage2[i][j]) > seuilEcartPixel) {
                    compteurDifferent++;
                }
                nombreDePixels++;
            }
        }

        if (compteurDifferent / nombreDePixels > seuilPourcentageDiff) {
            similaire = false;
        }

        return similaire;
    }
}
