
public class Lampe
{

    protected Point position = new Point(0, 0, 0);
    protected Couleur couleur = new Couleur(1, 1, 1);

    public final Point getPosition() {
        return position;
    }
    public final void setPosition(Point position) {
        this.position = position;
    }
    public final Couleur getCouleur() {
        return couleur;
    }
    public final void setCouleur(Couleur couleur) {
        this.couleur = couleur;
    }

}
