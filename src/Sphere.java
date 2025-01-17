/**
 * Cette classe représente une sphère à dessiner
 */
public class Sphere
{
    // note: toutes ces variables sont publiques afin de ne pas recourir à des getters

    /// coordonnées du centre
    protected Point centre = new Point();

    /// demi-diamètre
    protected float rayon = 0.0f;

    /// couleur diffuse
    protected Couleur Kd = new Couleur();

    /// couleur réfléchie
    protected Couleur Ks = new Couleur();
    protected float Ns = 0.0f;


    /**
     * constructeur par défaut
     */
    public Sphere()
    {
    }


    /**
     * constructeur
     * @param centre
     * @param rayon
     */
    public Sphere(Point centre, float rayon)
    {
        this.centre = centre;
        this.rayon = rayon;
    }


    /**
     * calcule la distance du point d'intersection entre this et le rayon
     * ne renvoie pas de point situé "derrière" le rayon
     * @param incident
     * @return Constantes.INFINI si pas d'intersection correcte
     */
    public float Intersection(Rayon incident)
    {
        // calculer B et C
        // a vaut 1 car nous avons normalise le vecteur (xv² + yv² + zv²)
        Vecteur cp = new Vecteur(this.centre, incident.P);
        float b = 2.0f * incident.V.dot(cp);
        float c = cp.dot(cp) - rayon * rayon;

        // résoudre Ak² + Bk + C = 0 => k1 et k2
        float delta = b*b - 4.0f*c;

        // aucun point de contact
        if (delta < 0.0) return Constantes.INFINI;

        // calcul de k1 et k2, racine du polynome
        float sqrt_delta = (float) Math.sqrt(delta);
        float k1 = (-b + sqrt_delta) / 2.0f;
        float k2 = (-b - sqrt_delta) / 2.0f;

        // plus petit non negatif
        if (k1 <= 0) k1 = Constantes.INFINI;
        if (k2 <= 0) k2 = Constantes.INFINI;

        if (k1 < k2) return k1;
        else return k2;
    }


    /**
     * calcule la couleur de la sphère au point désigné par incident
     * si la sphère est réfléchissante, on peut relancer au maximum
     * profondeur rayons indirects
     * @param scene
     * @param incident
     * @param profondeur
     * @return
     */
    public Couleur Phong(final Scene scene, final Rayon incident, int profondeur)
    {
        // modèle de Lambert = somme des éclairements diffus
        Couleur finale = new Couleur(0, 0, 0);

        // calculer le vecteur N au point de contact avec le rayon
        Vecteur n = new Vecteur(incident.getObjet().centre, incident.contact);
        n.normaliser();

        /// Calculer le mirrior de -V par rapport à N
        // (on le calcul 1 fois, alors que si on devait calculer le mirroir de L, on aurait du le refaire à chaque iteration)
        // reflet (mirroir de la lampe): r = 2*(n*v)*n - v
        Vecteur sub_v = incident.getV().neg();
        float nv = n.dot(sub_v);
        Vecteur r = n.mul( nv ).mul(2).sub(sub_v);
        r.normaliser();

        // chaque lampe contribue à l'éclairage
        for (Lampe lampe : scene.getLampes()) {
            // calculer le Vecteur L
            Vecteur l = new Vecteur(incident.contact, lampe.getPosition());
            l.normaliser();

            // est-ce qu'il y a un objet entre la lampe et le point de contact ?
            Rayon vers_lampe = new Rayon(lampe.getPosition(), incident.contact);
            vers_lampe.P = lampe.getPosition();
            scene.ChercherIntersection(vers_lampe, null);

            // est-ce bien "moi" qui suis, la/le plus proche de la lampe ?
            if (vers_lampe.getObjet() == this) {
                /// Eclairement diffus
                // calculer dot(L, N) * Kd * couleur de la lampe
                float nl =  n.dot(l);
                if (nl > 0) {
                    finale = Couleur.add(finale, Kd.mul(nl).mul(lampe.getCouleur()));

                    /// Eclairement Spéculaire
                    // Equation de Phong
                    float rl = r.dot(l);
                    if (rl > 0) finale = Couleur.add(finale, Kd.mul( (float) Math.pow(rl, this.Ns) ).mul(lampe.getCouleur()));
                }
            }
        }

        if (profondeur > 0) {
            // reflets
            Rayon reflet = new Rayon(incident.contact, r);

            // chercher quel objet de la scène le rencontre au plus proche
            if (scene.ChercherIntersection(reflet, this)) {
                // il y a un objet
                finale =  finale.add( reflet.getObjet().Phong(scene, reflet, profondeur-1).mul(this.Ks) );
            } else {
                // c'est le ciel
                finale = finale.add( reflet.Ciel().mul(this.Ks) );
            }
        }

        return finale;
    }


    public Point getCentre()
    {
        return centre;
    }


    public void setCentre(final Point centre)
    {
        this.centre = centre;
    }


    public float getRayon()
    {
        return rayon;
    }


    public void setRayon(float rayon)
    {
        this.rayon = rayon;
    }


    public Couleur getKd()
    {
        return Kd;
    }


    public void setKd(final Couleur kd)
    {
        Kd = kd;
    }


    public Couleur getKs()
    {
        return Ks;
    }


    public void setKs(final Couleur ks)
    {
        Ks = ks;
    }


    public float getNs()
    {
        return Ns;
    }


    public void setNs(float ns)
    {
        Ns = ns;
    }
}
