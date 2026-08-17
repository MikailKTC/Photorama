package ca.qc.bdeb.sim.tp1photorama;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static ca.qc.bdeb.sim.tp1photorama.GestionnaireImages.redimensionner;

public class ComparateurImagesHachageMoyenne extends ComparateurHachage {

    public ComparateurImagesHachageMoyenne(int seuilHachageFoncee) {
        super(seuilHachageFoncee);
    }

    @Override
    public int[][] tailleTableau(String cheminImage) throws IOException {

        BufferedImage nouvImage = GestionnaireImages.lireImage(cheminImage);
        BufferedImage image8x8 = redimensionner(nouvImage, 8, 8);
        int[][] tabImage = GestionnaireImages.toPixels(image8x8);

        return tabImage;
    }

    @Override
    public int[][] creerTableau(String cheminImage) throws IOException {

        int sommePixelsImage1 = 0;
        double moyenneImage;
        int[][] tab8x8 = tailleTableau(cheminImage);

        for (int i = 0; i < 8; i++) {

            for (int j = 0; j < 8; j++) {

                sommePixelsImage1 += tab8x8[i][j];
            }
        }

        moyenneImage = sommePixelsImage1 / 64;

        for (int i = 0; i < 8; i++) {

            for (int j = 0; j < 8; j++) {

                //https://stackoverflow.com/questions/10336899/what-is-a-question-mark-and-colon-operator-used-for
                tab8x8[i][j] = (tab8x8[i][j] > moyenneImage) ? 1 : 0; //Met 1 si condition vraie, met 2 si condition fausse
            }
        }

        this.tabAffichage = tab8x8;
        return tab8x8;

    }
}
