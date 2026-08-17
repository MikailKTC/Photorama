package ca.qc.bdeb.sim.tp1photorama;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Classe fournie avec quelques méthodes pour gérer la lecture d'images et la conversion en matrices (int[][])
 * <p>
 * Toutes les images sont gardées en tons de gris (grayscale)
 */
public class GestionnaireImages {

    /**
     * Petite optimisation : on réutilise les images déjà chargées
     * plutôt que de les relire à chaque fois. Si l'image change sur
     * le disque entre deux appels à "lireImage()", l'ancienne version
     * sera retournée.
     */
    private static HashMap<String, BufferedImage> imagesChargees = new HashMap<>();

    /**
     * Retourne une image au format BufferedImage, convertie en ton de gris
     * <p>
     * Code adapté de https://stackoverflow.com/a/9131751
     *
     * @param chemin Le chemin du fichier image à lire
     */
    public static BufferedImage lireImage(String chemin) throws IOException {

        BufferedImage gris;

        if (imagesChargees.containsKey(chemin)) {
            gris = imagesChargees.get(chemin);
        } else {
            var image = ImageIO.read(new File(chemin));
            if(image == null) {
                throw new IOException("Image invalide: " + new File(chemin).getName());
            }
            gris = drawImage(image,
                    new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY));
            imagesChargees.put(chemin, gris);
        }

        return gris;
    }

    /**
     * Redimensionne une image
     * <p>
     * Adapté de https://stackoverflow.com/a/9417836
     *
     * @param image L'image à traiter
     * @param w     La nouvelle largeur à utiliser
     * @param h     La nouvelle hauteur à utiliser
     * @return L'image redimensionnée (en tons de gris)
     */
    public static BufferedImage redimensionner(BufferedImage image, int w, int h) {
        return drawImage(image.getScaledInstance(w, h, Image.SCALE_SMOOTH),
                new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY));
    }

    private static BufferedImage drawImage(Image in, BufferedImage out) {
        var g = out.getGraphics();
        g.drawImage(in, 0, 0, null);
        g.dispose();
        return out;
    }

    /**
     * Convertit un BufferedImage en matrice 2D de pixels.
     * Les valeurs dans chaque case peuvent aller de 0 (noir) à 255 (blanc)
     *
     * @param image L'image à convertir
     * @return Une matrice de valeurs équivalente au contenu de l'image
     */
    public static int[][] toPixels(BufferedImage image) {
        int[][] pixels = new int[image.getHeight()][image.getWidth()];

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                // En tons de gris, R = G = B
                // On extrait le rouge seulement ici
                pixels[y][x] = image.getRGB(x, y) & 0xFF;
            }
        }

        return pixels;
    }

    /**
     * Crée un BufferedImage à partir d'une matrice 2D de pixels
     *
     * @param pixels Les pixels à utiliser (tons de gris entre 0 et 255)
     * @return L'image équivalente
     */
    public static BufferedImage fromPixels(int[][] pixels) {
        var image = new BufferedImage(pixels[0].length, pixels.length, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < pixels.length; y++) {
            for (int x = 0; x < pixels[0].length; x++) {
                int color = pixels[y][x] | (pixels[y][x] << 8) | (pixels[y][x] << 16);
                image.setRGB(x, y, 0xFF000000 | color);
            }
        }

        return image;
    }

    /**
     * Méthode qui enregistre une BufferedImage dans un fichier quelque part sur le disque.
     * <p>
     * L'image enregistrée doit être au format .png
     *
     * @param image  L'image à écrire
     * @param chemin Le chemin vers le fichier dans lequel enregistrer l'image
     * @throws IOException
     * @throws IllegalArgumentException si on spécifie un chemin qui ne se termine pas en .png
     */
    public static void ecrireImage(BufferedImage image, String chemin) throws IOException {
        File f = new File(chemin);

        if (!chemin.endsWith(".png")) {
            throw new IllegalArgumentException("Le nom du fichier à enregistrer doit terminer par .png");
        }

        ImageIO.write(image, "PNG", f);
    }
}
