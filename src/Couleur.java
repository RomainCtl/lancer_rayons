import java.awt.Color;

/**
 *  Cette classe représente une couleur RVB
 * NB : les composantes sont des réels de 0.0 (noir) à 1.0 (blanc)
 * au cours des calculs, les valeurs peuvent dépasser ces limites
 * mais au moment de l'affichage sur écran, elles seront tronquées,
 * voir la méthode clamp.
 */
public class Couleur
{
    // composantes de couleur
    protected float r,v,b;    // 0.0 (noir) à 1.0 (valeur max)


    /**
     *  constructeur par défaut
     */
    public Couleur()
    {
        this(0.0f, 0.0f, 0.0f);
    }

    /**
     * constructeur
     * @param r
     * @param v
     * @param b
     */
    public Couleur(float r, float v, float b)
    {
        this.r = r; this.v = v; this.b = b;
    }
    public Couleur(double r, double v, double b)
    {
        this.r = (float) r; this.v = (float) v; this.b = (float) b;
    }


    /**
     * constructeur à partir d'un vecteur
     * @param v
     */
    public Couleur(Vecteur v)
    {
        this.r = (v.x+1.0f)/2.0f;
        this.v = (v.y+1.0f)/2.0f;
        this.b = (v.z+1.0f)/2.0f;
    }


    /**
     * constructeur à partir d'un seul float
     * @param v
     */
    public Couleur(float g)
    {
        this.r = g;
        this.v = g;
        this.b = g;
    }


    /**
     * retourne une représentation affichable
     * @return
     */
    public String toString()
    {
        return "Couleur("+r+","+v+","+b+")";
    }


    /**
     * calcule c1+c2
     * @param c1
     * @param c2
     * @return
     */
    public static Couleur add(final Couleur c1, final Couleur c2)
    {
        return new Couleur(c1.r + c2.r, c1.v + c2.v, c1.b + c2.b);
    }


    /**
     * retourne this + c
     * @param c
     */
    public Couleur add(final Couleur c)
    {
        return new Couleur(this.r + c.r, this.v + c.v, this.b + c.b);
    }


    /**
     * calcule c1*c2
     * @param c1
     * @param c2
     * @return
     */
    public static Couleur mul(final Couleur c1, final Couleur c2)
    {
        return new Couleur(c1.r * c2.r, c1.v * c2.v, c1.b * c2.b);
    }


    /**
     * calcule c*k
     * @param c
     * @param k
     * @return
     */
    public static Couleur mul(final Couleur c, float k)
    {
        return new Couleur(c.r * k, c.v * k, c.b * k);
    }
    public static Couleur mul(float k, final Couleur c)
    {
        return new Couleur(c.r * k, c.v * k, c.b * k);
    }


    /**
     * retourne this * k
     * @param k
     */
    public Couleur mul(float k)
    {
        return new Couleur(this.r * k, this.v * k, this.b * k);
    }


    /**
     * retourne this * c
     * @param c2
     */
    public Couleur mul(Couleur c)
    {
        return new Couleur(this.r * c.r, this.v * c.v, this.b * c.b);
    }


    /**
     * retourne this / k
     * @param k
     */
    public Couleur div(float k)
    {
        return new Couleur(this.r / k, this.v / k, this.b / k);
    }


    /**
     * applique une correction pour améliorer le rendu des teintes sombres
     * @param gamma
     */
    public Couleur correctionGamma(float gamma)
    {
        gamma = (float) Math.pow(gamma, 0.8);
        return mul(gamma);
    }


    /**
     * force val dans la plage [min,max]
     * @param val
     * @param min
     * @param max
     * @return
     */
    private static float clamp(float val, float min, float max)
    {
        if (val < min) return min;
        if (val > max) return max;
        return val;
    }


    /**
     * retourne le code couleur pour l'affichage dans un BufferedImage
     * @return
     */
    public int getCode()
    {
        return new Color(clamp(r, 0.0f, 1.0f), clamp(v, 0.0f, 1.0f), clamp(b, 0.0f, 1.0f)).getRGB();
    }
}
