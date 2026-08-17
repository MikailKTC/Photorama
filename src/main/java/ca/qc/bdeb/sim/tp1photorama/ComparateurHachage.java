package ca.qc.bdeb.sim.tp1photorama;

import java.io.IOException;

public abstract class ComparateurHachage extends ComparateurImages {

    protected int[][] tabAffichage;
    protected int seuilDifferenceMaxHachage; //Évaluer le nombre maximal de pixel différents accepté

    public ComparateurHachage(int seuilHachageFoncee) {
        this.seuilDifferenceMaxHachage = seuilHachageFoncee;
    }

    public boolean imagesSimilaire(String image1Chemin, String image2Chemin) throws IOException {

        return hachageComparer(image1Chemin, image2Chemin);
    }

    public abstract int[][] creerTableau(String cheminImage) throws IOException;

    public boolean hachageComparer(String image1Chemin, String image2Chemin) throws IOException {

        int[][] tabImage1 = creerTableau(image1Chemin);
        int[][] tabImage2 = creerTableau(image2Chemin);
        boolean similaire = true;

        int nbPixelsDifferents = 0;

        for (int i = 0; i < tabImage1.length; i++) {

            for (int j = 0; j < tabImage1[i].length; j++) {

                if (tabImage1[i][j] != tabImage2[i][j]) {

                    nbPixelsDifferents++;
                }
            }
        }

        if (nbPixelsDifferents > seuilDifferenceMaxHachage) {
            similaire = false;
        }

        return similaire;
    }

    public void afficherTab() {

        for (int i = 0; i < tabAffichage.length; i++) {

            for (int j = 0; j < tabAffichage[i].length; j++) {

                if (tabAffichage[i][j] == 1) {
                    System.out.print("1");

                } else if (tabAffichage[i][j] == 0) {
                    System.out.print(" ");
                }

            }
            System.out.println();
        }
    }
}
