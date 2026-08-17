package ca.qc.bdeb.sim.tp1photorama;

import java.io.IOException;

public abstract class ComparateurImages {

    public abstract boolean imagesSimilaire(String image1Chemin, String image2Chemin) throws IOException;

    public abstract int[][] tailleTableau(String cheminImage) throws IOException;

    public String resultatComparaison(String imageChemin1, String imageChemin2) throws IOException {
        boolean similaire = imagesSimilaire(imageChemin1, imageChemin2);

        if (similaire) {
            return "SIMILAIRE";
        } else {
            return "DIFFÉRENT";
        }
    }

}
