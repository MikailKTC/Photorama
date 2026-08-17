package ca.qc.bdeb.sim.tp1photorama;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static ca.qc.bdeb.sim.tp1photorama.GestionnaireImages.redimensionner;

public class ComparateurImagesHachageDifference extends ComparateurHachage {

    public ComparateurImagesHachageDifference(int seuilHachageFoncee) {
        super(seuilHachageFoncee);
    }

    @Override
    public int[][] tailleTableau(String cheminImage) throws IOException {

        BufferedImage nouvImage = GestionnaireImages.lireImage(cheminImage);
        BufferedImage image8x9 = redimensionner(nouvImage, 8, 9);

        return GestionnaireImages.toPixels(image8x9);
    }

    @Override
    public int[][] creerTableau(String cheminImage) throws IOException {

        int[][] tab8x9 = tailleTableau(cheminImage);
        int[][] hachage8x8 = new int[8][8]; // matrice finale après hachage

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                hachage8x8[i][j] = (tab8x9[i][j] <= tab8x9[i + 1][j]) ? 0 : 1; //0 si vrai, 1 si faux
            }
        }

        this.tabAffichage = hachage8x8;
        return hachage8x8;
    }
}
