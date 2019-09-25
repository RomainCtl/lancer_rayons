import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Cette classe représente l'ensemble des objets à dessiner ainsi que les lampes
 */
public class Scene
{
    // objets
    private ArrayList<Sphere> Objets = new ArrayList<>();

    // lumières
    private ArrayList<Lampe> Lampes = new ArrayList<>();


    /**
     * cette méthode affecte objet, distance et contact avec le plus proche
     * objet rencontré par ce rayon. Elle passe en revue tous les objets sauf
     * celui qui est indiqué dans le paramètre sauflui.
     * Elle calcule les coordonnées du point de contact.
     * @param incident
     * @param sauflui
     * @return
     */
    public boolean ChercherIntersection(Rayon incident, final Sphere sauflui)
    {
        // initialisation à aucun contact
        incident.setDistanceObjet(Constantes.INFINI, null);

        // recherche du plus proche objet intersecté par incident
        for (Sphere sphere : Objets) {
            if (sphere == sauflui) continue; //

            // y-a-t'il une intersection entre Sphere et incident
            float distance = sphere.Intersection(incident);

            // est-ce mieux ?
            if (distance < incident.getDistance()) {
                incident.setDistanceObjet(distance, sphere);
            }
        }

        // retourne true s'il y a un contact et dans ce cas, calcule ses coordonnées, false sinon
        return incident.calcContact();
    }


    /**
     * cette méthode affecte objet, distance et contact avec le premier
     * objet rencontré par ce rayon. Elle passe en revue tous les objets sauf
     * celui qui est indiqué dans le paramètre sauflui.
     * Elle calcule les coordonnées du point de contact.
     * @param incident
     * @param sauflui
     * @return
     */
    public boolean ChercherPremiereIntersection(Rayon incident, final Sphere sauflui)
    {
        // initialisation à aucun contact
        incident.setDistanceObjet(Constantes.INFINI, null);

        // FIXME programmer la recherche d'un objet intersecté par incident, n'importe lequel

        // retourne true s'il y a un contact et dans ce cas, calcule ses coordonnées, false sinon
        return incident.calcContact();
    }


    /**
     * constructeur : charge un fichier de description d'une scène
     * @param nom du fichier scène à charger
     */
    public Scene(String nom) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(nom));
        try {
            Sphere sphere_courante = null;
            Lampe lampe_courante = null;

            String line = br.readLine();
            while (line != null) {
                String[] mots = line.trim().split("[ \t]");
                switch (mots[0].toLowerCase()) {

                case "objet":
                    sphere_courante = new Sphere();
                    Objets.add(sphere_courante);
                    break;

                case "centre":
                    // centre <x> <y> <z>
                    Point centre = new Point(Float.parseFloat(mots[1]), Float.parseFloat(mots[2]), Float.parseFloat(mots[3]));
                    sphere_courante.setCentre(centre);
                    break;

                case "rayon":
                    // rayon <n>
                    sphere_courante.setRayon(Float.parseFloat(mots[1]));
                    break;

                case "kd":
                    // kd <r> <v> <b>
                    Couleur kd = new Couleur(Float.parseFloat(mots[1]), Float.parseFloat(mots[2]), Float.parseFloat(mots[3]));
                    sphere_courante.setKd(kd);
                    break;

                case "kr":
                case "ks":
                    // Kr ou Ks <r> <v> <b>
                    Couleur ks = new Couleur(Float.parseFloat(mots[1]), Float.parseFloat(mots[2]), Float.parseFloat(mots[3]));
                    sphere_courante.setKs(ks);
                    break;

                case "ns":
                    // ns <n>
                    sphere_courante.setNs(Float.parseFloat(mots[1]));
                    break;


                case "lampe":
                    // lampe <numero>
                    lampe_courante = new Lampe();
                    Lampes.add(lampe_courante);
                    break;

                case "position":
                    // position <x> <y> <z>
                    Point position = new Point(Float.parseFloat(mots[1]), Float.parseFloat(mots[2]), Float.parseFloat(mots[3]));
                    lampe_courante.setPosition(position);
                    break;

                case "couleur":
                    // couleur <r> <v> <b>
                    Couleur couleur = new Couleur(Float.parseFloat(mots[1]), Float.parseFloat(mots[2]), Float.parseFloat(mots[3]));
                    lampe_courante.setCouleur(couleur);
                    break;
                }

                line = br.readLine();
            }
        } finally {
            br.close();
        }
        System.out.println(Objets.size()+" sphères, "+Lampes.size()+" lampes");
    }


    /**
     * retourne la liste des lampes
     * @return liste des lampes
     */
    public final ArrayList<Lampe> getLampes()
    {
        return Lampes;
    }
}
