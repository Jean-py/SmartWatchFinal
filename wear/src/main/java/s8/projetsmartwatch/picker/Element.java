package s8.projetsmartwatch.picker;

/**
 * Created by jp on 26/04/16.
 */
public class Element {

        private String titre;
        private String texte;
        private int color;

        public Element(String titre, String texte, int color) {
            this.titre = titre;
            this.color = color;
            this.texte = texte;
        }

    public String getTitre() {
        return titre;
    }

    public String getTexte() {
        return texte;
    }

    public int getColor() {
        return color;
    }
}
