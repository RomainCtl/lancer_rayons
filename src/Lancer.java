import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;


/**
 * C'est la classe principale du logiciel : elle crée la fenêtre
 * et lance le calcul
 */
public class Lancer extends JPanel implements ComponentListener
{
    private static final long serialVersionUID = 1L;


    // caméra
    private Point Oeil = new Point(0,0,-Constantes.DISTECRAN);

    // scène à dessiner dans le canvas
    private Scene scene;

    // dimensions et échelle du dessin
    private int largeur;
    private int hauteur;
    private float echelle;

    // le canvas évite de tout redessiner quand une autre fenêtre masque temporairement celle-ci
    private BufferedImage canvas;
    private ProgressMonitor progressMonitor;



    /**
     * dessin d'un pixel en couleur
     * NB: le pixel est dessiné hors écran, dans le canvas
     * @param couleur
     * @param xe
     * @param ye
     */
    public void drawPixel(final Couleur couleur, int xe, int ye)
    {
        // clipping : ignorer les points hors plage
        if (xe < 0 || ye < 0) return;
        if (xe >= largeur || ye >= hauteur) return;

        // définir la couleur du pixel en limitant les composantes à 0..1 (sinon ça fait planter)
        int code_color = couleur.getCode();

        // dessiner le pixel
        canvas.setRGB(xe,  ye, code_color);
    }


    /**
     * calcule la couleur du pixel (xe,ye) (fractionnaire)
     * avec un nombre maximal de reflets possibles
     * @param xe
     * @param ye
     * @param maxReflets
     * @return
     */
    private Couleur CouleurPixel(float xe, float ye, int maxReflets)
    {
        /// créer un rayon qui part de l'oeil et qui passe par le pixel

        // Coordonnée du pixel dans le repère 3D
        float x = (xe - largeur*0.5f) /echelle;
        float y = (hauteur*0.5f - ye) /echelle;

        // créer le rayon oeil->pixel
        Point pixel = new Point(x, y, 0.0f);
        Rayon initial = new Rayon(Oeil, pixel);

        // chercher quel objet de la scène le rencontre au plus près
        if (scene.ChercherIntersection(initial, null)) {
            // il y a un objet
            Couleur couleur = initial.getObjet().Phong(scene, initial, Constantes.MAX_REFLETS);

            // moduler cette couleur par la traversé de la brume
            float k = (initial.getDistance() -2) / 2.0f;
            if (k > 1.0f) k = 1.0f;
            return couleur.mul(1.0f-k).add( Constantes.BRUME.mul(k) );
        } else {
            // c'est la couleur de la brume
            return Constantes.BRUME;
        }
    }


    /**
     * dessine la totalité de l'image
     * @param largeur
     * @param hauteur
     * @param task
     */
    public void TracerImage(int largeur, int hauteur, TaskTracerImage task)
    {
        // taille de la vue
        this.largeur = largeur;
        this.hauteur = hauteur;

        // facteur d'agrandissement qui dépend de la taille de la vue
        echelle = Math.max(largeur, hauteur) * Constantes.CHAMP;

        // brouillon rapide (ce dessin est fait en une fraction de secondes)
        final int N = 4;
        for (int ye = 0; ye < hauteur; ye+=N) {
            for (int xe = 0; xe < largeur; xe+=N) {
                // couleur du pixel au centre du carré N*N
                Couleur couleur = CouleurPixel(xe+N*0.5f, ye+N*0.5f, 1);

                // correction gamma
                couleur = couleur.correctionGamma(0.8f);

                // dessiner les pixels du carré NxN
                for (int dy=0; dy<N; dy++) {
                    for (int dx=0; dx<N; dx++) {
                        drawPixel(couleur, xe+dx,ye+dy);
                    }
                }
            }
        }
        // afficher le brouillon
        repaint();

        // avancement du dessin lent
        progressMonitor.setMaximum(hauteur);
        long startTime = System.nanoTime();

        // passer en revue tous les pixels de l'écran
        for (int ye = 0; ye < hauteur; ye++) {
            // avancement ou arrêt
            if (task.progress(ye)) break;
            // passer en revue tous les pixels de la ligne
            for (int xe = 0; xe < largeur; xe++) {
                // couleur du pixel
                Couleur couleur = CouleurPixel(xe, ye, Constantes.MAX_REFLETS);

                // correction gamma
                couleur = couleur.correctionGamma(0.8f);

                // dessiner le pixel de cette couleur
                drawPixel(couleur, xe,ye);
            }
        }

        if (! task.isCancelled()) {
            long temps = (System.nanoTime() - startTime) / 1000000L;
            System.out.println("Temps: "+temps+" ms soit "+(largeur*hauteur*1000/temps)+" pixels par seconde");

            // afficher l'image (forcer au cas où la fenêtre soit masquée)
            repaint();

            // enregistrement de l'image dans un fichier
            try {
                File outputfile = new File(Constantes.NOM_IMAGE);
                ImageIO.write(canvas, "png", outputfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    /**
     * Cette fonction a pour but de limiter la casse quand il y a un bug
     * dans les parties à programmer.
     * Quelques tests simples sont effectués pour vérifier les calculs.
     * Le bon déroulement ne prouve pas que le programme est correct, mais
     * une erreur ici prouve qu'il n'est pas correct.
     * @throws Exception
     */
    private static void VerifierLogiciel() throws Exception
    {
        // test de la soustraction de deux Vecteur
        Vecteur sub = Vecteur.sub(new Vecteur(5,-3,7), new Vecteur(4,-6,2));
        if (Math.abs(sub.x - 1.0f)>Constantes.EPSILON || Math.abs(sub.y - 3.0f)>Constantes.EPSILON || Math.abs(sub.z - 5.0f)>Constantes.EPSILON) {
            throw new Exception("La fonction Vecteur.sub(Vecteur, Vecteur) est mauvaise");
        }

        // test de la construction d'un vecteur par deux points
        Vecteur V = new Vecteur(new Point(4,-6,2), new Point(5,-3,7));
        if (Math.abs(V.x - 1.0f)>Constantes.EPSILON || Math.abs(V.y - 3.0f)>Constantes.EPSILON || Math.abs(V.z - 5.0f)>Constantes.EPSILON) {
            throw new Exception("Le constructeur Vecteur(Point, Point) est mauvais");
        }

        // test du produit scalaire
        float ps = new Vecteur(-2,-3,-4).dot(new Vecteur(5,6,7));
        if (Math.abs(ps - -56.0f)>Constantes.EPSILON) {
            throw new Exception("La fonction Vecteur.dot(Vecteur) est mauvaise");
        }

        // test de la normalisation
        Vecteur norm = new Vecteur(2,-3,4);
        norm.normaliser();
        if (Math.abs(norm.x - 0.371391f)>Constantes.EPSILON || Math.abs(norm.y - -0.557086f)>Constantes.EPSILON || Math.abs(norm.z - 0.742781f)>Constantes.EPSILON) {
            throw new Exception("La methode Vecteur.normaliser() est mauvaise");
        }

        // test du constructeur d'un rayon
        Rayon R = new Rayon(new Point(2,-3,4), new Point(5,6,-7));
        if (Math.abs(R.P.x - 5.0f)>Constantes.EPSILON || Math.abs(R.P.y - 6.0f)>Constantes.EPSILON || Math.abs(R.P.z - -7.0f)>Constantes.EPSILON ||
            Math.abs(R.V.x - 0.206529f)>Constantes.EPSILON || Math.abs(R.V.y - 0.619586f)>Constantes.EPSILON || Math.abs(R.V.z - -0.757271f)>Constantes.EPSILON) {
            throw new Exception("Le constructeur Rayon::Rayon(Point,Point) est mauvais");
        }

        // tests de ChercherIntersection
        Sphere sphere = new Sphere(new Point(0,0,10), 2.0f);
        Rayon R1 = new Rayon(new Point(0,0,-10), new Point(0.95f,0,0));
        float d1 = sphere.Intersection(R1);
        if (Math.abs(d1 - 9.215490f) > Constantes.EPSILON) {
            throw new Exception("La methode Sphere::Intersection est mauvaise, cas n°1");
        }
        Rayon R2 = new Rayon(new Point(2,0,-10), new Point(2.001f,0,0));
        float d2 = sphere.Intersection(R2);
        if (d2 < 1e9f) {
            throw new Exception("La methode Sphere::Intersection est mauvaise, cas n°2");
        }

        // ok, on continue, mais c'est pas entièrement certain que tout soit ok.
    }


    /**
     * Cette classe gère le dessin en arrière-plan pour ne pas bloquer l'interface
     */
    class TaskTracerImage extends SwingWorker<Void, Void>
    {
        @Override
        public Void doInBackground()
        {
            int largeur = getWidth();
            int hauteur = getHeight();

            TracerImage(largeur, hauteur, this);
            return null;
        }

        /**
         * met à jour la jauge d'avancement et regarde si un arrêt a été demandé
         * @param ye ligne en cours de traitement
         * @return true s'il faut interrompre le dessin, false s'il faut continuer
         */
        public boolean progress(int ye)
        {
            // on s'en soucie seulement 1 ligne sur 8
            if (ye % 8 > 0) return false;

            // si la tâche a été annulée, quitter
            if (isCancelled()) return true;

            // jauge d'avancement
            progressMonitor.setProgress(ye);
            if (progressMonitor.isCanceled() || isDone()) {
                // annuler la tâche
                cancel(true);
                return true;
            }
            if (ye % 16 == 0) repaint();
            return false;
        }

        @Override
        public void done()
        {
            progressMonitor.close();
            repaint();
        }
    }


    private TaskTracerImage task = null;

    private void startTracerImage()
    {
        if (task != null) task.cancel(true);
        task = new TaskTracerImage();
        task.execute();
    }


    /**
     * méthode principale : elle vérifie le logiciel, crée l'interface, charge la scène et dessine l'image
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        // vérifier les calculs du logiciel
        VerifierLogiciel();

        // charger la scène
        final Scene scene = new Scene(Constantes.NOM_SCENE);

        // fenêtre
        final JFrame frame = new JFrame("lancer de rayons");

        // panneau pour dessiner le résultat
        final Lancer panel = new Lancer(scene, Constantes.LARGEUR_IMAGE, Constantes.HAUTEUR_IMAGE);
        frame.add(panel);

        // faire afficher l'image quand on active la fenêtre
        frame.addWindowListener(new WindowListener() {

            @Override
            public void windowActivated(WindowEvent e) {
                panel.repaint();
            }
            @Override public void windowDeiconified(WindowEvent e) {
                panel.repaint();
            }

            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
            @Override public void windowClosing(WindowEvent e) {}
            @Override public void windowClosed(WindowEvent e) {}
        });

        // assemblage de la fenêtre
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public Lancer(final Scene scene, int width, int height)
    {
        this.scene = scene;
        canvas = new BufferedImage(width,  height, BufferedImage.TYPE_INT_RGB);
        addComponentListener(this);
        progressMonitor = new ProgressMonitor(this, "Dessin en cours", "", 0, Constantes.HAUTEUR_IMAGE);
    }


    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(canvas.getWidth(), canvas.getHeight());
    }


    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        // recopier le canvas sur l'écran
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(canvas, null, null);
    }


    @Override
    public void componentResized(ComponentEvent e)
    {
        // créer un pixmap de la taille de la fenêtre
        int largeur = getWidth();
        int hauteur = getHeight();
        canvas = new BufferedImage(largeur,  hauteur, BufferedImage.TYPE_INT_RGB);

        // dessiner les objets 3D dans le pixmap
        startTracerImage();     // c'est là que tout se passe
    }

    @Override
    public void componentMoved(ComponentEvent e) {}

    @Override
    public void componentShown(ComponentEvent e) {}

    @Override
    public void componentHidden(ComponentEvent e) {}
}
