
/**
 * Cette classe représente une demi-droite partant d'un point P et de vecteur directeur V
 * elle est donc orientée dans le sens V.
 * Cette classe représente aussi le point de contact avec le plus proche objet
 * rencontré : le numéro de l'objet, sa distance à P et le point de contact
 */
public class Rayon
{
    /// origine de la demi-droite
    protected Point P;

    /// vecteur directeur
    protected Vecteur V;

    // plus proche objet rencontré ou null si aucun
    protected Sphere objet;

    /// distance du point de contact ou Constantes.INFINI si aucun
    protected float distance;

    /// coordonnées du point de contact
    protected Point contact;


    /**
     * constructeur par défaut
     */
    public Rayon()
    {
    }


    /**
     * construit un rayon partant de P2 et allant dans la direction P1,P2
     * @param P1
     * @param P2
     */
    public Rayon(final Point P1, final Point P2)
    {
        this.P = P2;
        this.V = new Vecteur(P1, P2);
        this.V.normaliser();
    }


    /**
     * construit un rayon partant de P et allant dans la direction V
     * @param P
     * @param V
     */
    Rayon(final Point P, final Vecteur V)
    {
        this.P = P;
        this.V = V;
        this.V.normaliser(); // FIXME inutile ??
    }


    public Point getP()
    {
        return P;
    }


    public Vecteur getV()
    {
        return V;
    }


    public Sphere getObjet()
    {
        return objet;
    }


    public float getDistance()
    {
        return distance;
    }


    /**
     * retourne le carré de la distance de contact avec l'objet
     * @return
     */
    public float getDistance2()
    {
        return distance*distance;
    }


    /**
     * initialise la distance de contact et l'objet concerné
     * @param distance
     * @param objet
     */
    public void setDistanceObjet(float distance, Sphere objet)
    {
        this.distance = distance;
        this.objet = objet;
    }


    /**
     * calcule les coordonnées du point de contact s'il y en a un
     * @return true si c'est le cas, false si aucun contact
     */
    public boolean calcContact()
    {
        // calculer les coordonnées du point de contact en fonction de P, V et distance
        if (distance < Constantes.INFINI) {
            this.contact = Point.add(P, Vecteur.mul(distance, V));
            return true;
        } else {
            return false;
        }
    }


    /**
     * renvoie la couleur du "ciel" vu dans cette direction
     * @return du bleu si le rayon va vers le haut, du vert/marron s'il va vers le bas
     */
    public Couleur Ciel()
    {
        // deux hémisphères, selon que V va vers le haut ou vers le bas
        if (V.y > 0.0f) {
            // dégradé de bleu selon V.y
            final Couleur bleu_clair = new Couleur(0.7, 0.7, 1.0);
            final Couleur bleu_sombre = new Couleur(0.0, 0.0, 1.0);
            return Couleur.add( bleu_clair.mul(1.0f-V.y), bleu_sombre.mul(V.y) );
        } else {
            // léger dégradé de marron clair/vert   ATTENTION, ce n'est pas un "sol" car il est à l'infini.
            return new Couleur(0.5, 0.4+0.2*V.y, 0.1);
        }
    }
}
