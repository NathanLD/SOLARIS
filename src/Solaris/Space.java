package Solaris;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class Space extends JFrame implements GLEventListener, KeyListener {

	
	private float speed=1;
	private boolean view = false;				// true = De face, false = De haut. Modifiable
	private boolean orbites = true;				// Pour voir les orbites. Modifiable
	private final float recul = -750;			// Distance de recul pour voir la scène. Modifiable
	private List<Astre> astres = new ArrayList<Astre>();
	private List<Texture> texAnnexes = new ArrayList<Texture>();
	
	private GLU glu = new GLU();

	// Pour gérer la caméra
	private float eyeX = 0;
	private float eyeY = 0;
	private float eyeZ = 0;
	private float POV_orientation = 0;
	private float POV_speed = 1f;
	private float POV_rotation_speed = 10f;
	private float lx = 0;
	private float lz = -1f;
	private float ly =0;

	/**
	 * Crée une instance de l'espace, avec un KeyListener
	 * @author LE DIGABEL Nathan
	 * @author LECOIN Jean-Baptiste
	 * @param width Largeur de la fenêtre
	 * @param height Hauteur de la fenêtre
	 */
	public Space(int width, int height) {
		super("Représentation du Système Solaire (by LECOIN Jean-Baptiste & LE DIGABEL Nathan)");
		GLProfile profil = GLProfile.get(GLProfile.GL2);
		GLCapabilities capabilities = new GLCapabilities(profil);
		GLCanvas canvas = new GLCanvas(capabilities);
		canvas.addGLEventListener(this);
		canvas.setSize(width, height);
		this.getContentPane().add(canvas);
		this.setSize(this.getContentPane().getPreferredSize());
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setResizable(false);
		canvas.requestFocusInWindow();

		// Création et instanciation du FPSAnimator final
		FPSAnimator animator = new FPSAnimator(canvas, 100, true);
		animator.start();

		canvas.addKeyListener(this);
	}

	/**
	 * Se replace à l'origine du repère
	 * @author LE DIGABEL Nathan
	 * @param gl
	 */
	public void setOrigin(GL2 gl){				// Recul + Choix de la vue (de face/de haut)
		gl.glLoadIdentity();
		gl.glTranslatef(0, 0, recul);
		if(view){
			gl.glRotatef(90, 1, 0, 0);
			gl.glRotatef(180, 0, 1, 0);
		}	
	}
	
	/**
	 * Dessine le système solaire à l'instant actuel
	 * @author LE DIGABEL Nathan
	 * @author LECOIN Jean-Baptiste
	 * @param drawable
	 */
	@Override
	public void display(GLAutoDrawable drawable) {
		// Récupérons notre objet OpenGL
		GL2 gl = drawable.getGL().getGL2();
		// On efface le buffer couleur (ce qu’on affiche ) et le buffer de
		// profondeur
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		// POUR SE DEPLACER DANS LA SCENE
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(45.0f, 1, 1.0, 10000.0);
		glu.gluLookAt(eyeX, eyeY, eyeZ, eyeX + lx, eyeY+ly, eyeZ + lz, 0, 1, 0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		/***
		 * Écrire notre code de dessin ci-dessous
		 */

		setOrigin(gl);
		
		for(int i=1;i<=astres.size();i++){
			setOrigin(gl);
			
			// Dessin des orbites
			
			if(orbites){
			GLUquadric sphere = glu.gluNewQuadric();
			glu.gluQuadricDrawStyle(sphere, GLU.GLU_FILL);
			glu.gluQuadricTexture(sphere, true);
			glu.gluQuadricNormals(sphere, GLU.GLU_SMOOTH);
			
			texAnnexes.get(2).enable(gl);
			texAnnexes.get(2).bind(gl);
			glu.gluDisk(sphere, astres.get(i-1).getDistance(), astres.get(i-1).getDistance()+1, 500, 2);
			
			glu.gluDeleteQuadric(sphere);
			}
			
			/////////////////////////
			// Dessin des planètes //
			/////////////////////////
			
			//Rotation par rapport au soleil
			gl.glRotatef(astres.get(i-1).getRotSun(0), 1,0,0);
			gl.glRotatef(astres.get(i-1).getRotSun(1), 0,1,0);
			gl.glRotatef(astres.get(i-1).getRotSun(2), 0,0,1);
			
			astres.get(i-1).changeRotSun(speed);
			
			// Translation selon la distance au soleil
			gl.glTranslated(astres.get(i-1).getDistance(), 0, 0);
			
			//Rotation sur soi meme
			gl.glRotatef(astres.get(i-1).getRot(0), 1,0,0);
			gl.glRotatef(astres.get(i-1).getRot(1), 0,1,0);
			gl.glRotatef(astres.get(i-1).getRot(2), 0,0,1);
			
			astres.get(i-1).changeRot(speed);
			
			//Inclinaison
			gl.glRotatef(astres.get(i-1).getInclinaison(0), 1,0,0);
			gl.glRotatef(astres.get(i-1).getInclinaison(1), 0,1,0);
			gl.glRotatef(astres.get(i-1).getInclinaison(2), 0,0,1);
			
			
			gl.glCallList(i);
			
			
			/////////////////////////////////////////////
			// Dessin des satellites de chaque planète //
			/////////////////////////////////////////////
			
			for(int j=1;j<=astres.get(i-1).getNbSatellites();j++){

				////// D'abord on se remet à chaque fois dans la position initiale de l'astre père //////
				 
				setOrigin(gl);
				
				//Rotation par rapport au soleil
				gl.glRotatef(astres.get(i-1).getRotSun(0), 1,0,0);
				gl.glRotatef(astres.get(i-1).getRotSun(1), 0,1,0);
				gl.glRotatef(astres.get(i-1).getRotSun(2), 0,0,1);
				
				//PAS CHANGER ROT, ON VEUT JUSTE REVENIR A LA POSITION
				
				// Translation selon la distance au soleil
				gl.glTranslated(astres.get(i-1).getDistance(), 0, 0);
				
				//Rotation sur soi meme
				gl.glRotatef(astres.get(i-1).getRot(0), 1,0,0);
				gl.glRotatef(astres.get(i-1).getRot(1), 0,1,0);
				gl.glRotatef(astres.get(i-1).getRot(2), 0,0,1);
				
				//IDEM
				
				//Inclinaison
				gl.glRotatef(astres.get(i-1).getInclinaison(0), 1,0,0);
				gl.glRotatef(astres.get(i-1).getInclinaison(1), 0,1,0);
				gl.glRotatef(astres.get(i-1).getInclinaison(2), 0,0,1);
				
				////// Maintenant on dessine vraiment le satellite //////
				
				//Inclinaison (pour tricher et faire la rotation autour de l'astre)
				gl.glRotatef(astres.get(i-1).getSatellites().get(j-1).getInclinaison(0), 1,0,0);
				gl.glRotatef(astres.get(i-1).getSatellites().get(j-1).getInclinaison(1), 0,1,0);
				gl.glRotatef(astres.get(i-1).getSatellites().get(j-1).getInclinaison(2), 0,0,1);
				
				//Rotation par rapport à l'astre
				gl.glRotatef(0, 1,0,0);
				gl.glRotatef(0, 0,1,0);
				gl.glRotatef(astres.get(i-1).getSatellites().get(j-1).getRotSun(2), 0,0,1);
				
				astres.get(i-1).getSatellites().get(j-1).changeRotSun(speed);
				
				// Translation selon la distance à l'astre
				gl.glTranslated(astres.get(i-1).getSatellites().get(j-1).getDistance(), 0, 0);
				
				//Rotation sur soi meme
				gl.glRotatef(astres.get(i-1).getSatellites().get(j-1).getRot(0), 1,0,0);
				gl.glRotatef(astres.get(i-1).getSatellites().get(j-1).getRot(1), 0,1,0);
				gl.glRotatef(astres.get(i-1).getSatellites().get(j-1).getRot(2), 0,0,1);
				
				astres.get(i-1).getSatellites().get(j-1).changeRot(speed);
				
				gl.glCallList(i*1000+j);
			}
		}
		
		setOrigin(gl);

		/***
		 * Écrire notre code de dessin ci-dessus
		 */

		// Pour forcer l ’éxécution des différentes instructions OpenGL
		gl.glFlush();
		
		setLight(gl);
	}

	/**
	 * @param drawable
	 */
	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
	}

	/**
	 * Initialisation des lumières, textures, GLLists, etc...
	 * @author LE DIGABEL Nathan
	 * @param drawable
	 */
	@Override
	public void init(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();
		gl.glEnable(GL2.GL_DEPTH_TEST);

		initAstres();
		initTexturesAnnexes();
		
		buildList(gl);
		
		gl.glEnable(GL2.GL_LIGHTING); // Eclairage
		gl.glEnable(GL2.GL_RESCALE_NORMAL); // Remise à l'échelle
		
	}
	
	/**
	 * Placement des 8 lumières autour du soleil
	 * @author LECOIN Jean-Baptiste
	 * @param gl
	 */
	public void setLight(GL2 gl){	// Placement de 8 lumières placées aux coins du cube encadrant le soleil
		float ambient[] = { 0.0f, 0.0f, 0.0f, 1.0f };
        float diffuse[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        
        float l0[] = { -60f, -60f, -60f, 1f };
        float l1[] = { -60f, -60, 60, 1f };
        float l2[] = { -60f, 60f, 60f, 1f };
        float l3[] = { -60, 60f, -60, 1f };
        float l4[] = { 60f, 60f, 60f, 1f };
        float l5[] = { 60, 60, -60f, 1f };
        float l6[] = { 60f, -60f, 60f, 1f };
        float l7[] = { 60, -60, -60f, 1f };

        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, specular, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, l0, 0);
        gl.glEnable(GL2.GL_LIGHT0);

        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, specular, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, l1, 0);
        gl.glEnable(GL2.GL_LIGHT1);

        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_AMBIENT, ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_SPECULAR, specular, 0);
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_POSITION, l2, 0);
        gl.glEnable(GL2.GL_LIGHT2);
        
        gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_AMBIENT, ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_SPECULAR, specular, 0);
        gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_POSITION, l3, 0);
        gl.glEnable(GL2.GL_LIGHT3);
        
        gl.glLightfv(GL2.GL_LIGHT4, GL2.GL_AMBIENT, ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT4, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT4, GL2.GL_SPECULAR, specular, 0);
        gl.glLightfv(GL2.GL_LIGHT4, GL2.GL_POSITION, l4, 0);
        gl.glEnable(GL2.GL_LIGHT4);
        
        gl.glLightfv(GL2.GL_LIGHT5, GL2.GL_AMBIENT, ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT5, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT5, GL2.GL_SPECULAR, specular, 0);
        gl.glLightfv(GL2.GL_LIGHT5, GL2.GL_POSITION, l5, 0);
        gl.glEnable(GL2.GL_LIGHT5);
        
        gl.glLightfv(GL2.GL_LIGHT6, GL2.GL_AMBIENT, ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT6, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT6, GL2.GL_SPECULAR, specular, 0);
        gl.glLightfv(GL2.GL_LIGHT6, GL2.GL_POSITION, l6, 0);
        gl.glEnable(GL2.GL_LIGHT6);
        
        gl.glLightfv(GL2.GL_LIGHT7, GL2.GL_AMBIENT, ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT7, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT7, GL2.GL_SPECULAR, specular, 0);
        gl.glLightfv(GL2.GL_LIGHT7, GL2.GL_POSITION, l7, 0);
        gl.glEnable(GL2.GL_LIGHT7);
	}

	/**
	 * Crée le tableau de textures annexes (anneaux,...)
	 * @author LE DIGABEL Nathan
	 * @author LECOIN Jean-Baptiste
	 */
	private void initTexturesAnnexes() {
		File im;
		Texture t;
		try {
			im = new File ("data/anneau.jpg") ;
			t= TextureIO.newTexture(im, true ) ;
			texAnnexes.add(t) ;
			
			im = new File ("data/asteroides.jpg") ;
			t= TextureIO.newTexture(im, true ) ;
			texAnnexes.add(t) ;
			
			im = new File ("data/white.png") ;
			t= TextureIO.newTexture(im, true ) ;
			texAnnexes.add(t) ;
			}catch(Exception e){
				e. printStackTrace () ;
			}
	}

	/**
	 * Création des GLLists
	 * @author LE DIGABEL Nathan
	 * @param gl
	 */
	private void buildList(GL2 gl) {
		GLUquadric sphere = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(sphere, GLU.GLU_FILL);
		glu.gluQuadricTexture(sphere, true);
		glu.gluQuadricNormals(sphere, GLU.GLU_SMOOTH);

		gl.glGenLists(astres.size());
	
		for(int i=1;i<=astres.size();i++){	// Code pour les planètes
			gl.glNewList(i, GL2.GL_COMPILE);
			astres.get(i-1).getTex().enable(gl);
			astres.get(i-1).getTex().bind(gl);
			glu.gluSphere(sphere, astres.get(i-1).getSize(), (int) astres.get(i-1).getSize()*30, (int) astres.get(i-1).getSize()*30);
			switch(i){
			case 1:	// On dessine la ceinture d'astéroides autour du soleil
				texAnnexes.get(1).enable(gl);
				texAnnexes.get(1).bind(gl);
				glu.gluDisk(sphere, 97, 107, 500, 2); //(sphere, rayon interne, rayon externe, précision cercle, Specifies the number of concentric rings about the origin into which the disk is subdivided.)
				break;
			case 7:	// On dessine les anneaux de Saturne
				texAnnexes.get(0).enable(gl);
				texAnnexes.get(0).bind(gl);
				glu.gluDisk(sphere, 60.3f/3+7, 60.3f/3+17, 1000, 2); //(sphere, rayon interne, rayon externe, précision cercle, Specifies the number of concentric rings about the origin into which the disk is subdivided.)
				break;
			}
			gl.glEndList();
			glu.gluDeleteQuadric(sphere);
			
			for(int j=1;j<=astres.get(i-1).getNbSatellites();j++){	// Code pour les satellites
				gl.glNewList(i*1000+j, GL2.GL_COMPILE);
				astres.get(i-1).getSatellites().get(j-1).getTex().enable(gl);
				astres.get(i-1).getSatellites().get(j-1).getTex().bind(gl);
				glu.gluSphere(sphere, astres.get(i-1).getSatellites().get(j-1).getSize(), (int) astres.get(i-1).getSatellites().get(j-1).getSize()*30, (int) astres.get(i-1).getSatellites().get(j-1).getSize()*30);
				gl.glEndList();
				glu.gluDeleteQuadric(sphere);
			}
		}
		
	}

	/**
	 * Crée le tableau des astres à dessiner
	 * @author LE DIGABEL Nathan
	 */
	private void initAstres() {
		float[] inclinaison;
		float[] rot;
		float[] rotSun;	
		
		// SOLEIL (Ceinture d'astéroides associée. ID = 1)
		inclinaison = new float[] {0f, 0f, 0f};
		rot = new float[] {0f, 0f, 0f, 0f,0f,0.1f};
		rotSun = new float[] {0f, 0f, 0f,0f,0f,0f};
		Astre soleil = new Astre("soleil.bmp",30,0,0,inclinaison,rot,rotSun);
		astres.add(soleil);
		
		// MERCURE
		inclinaison = new float[] {0f, 7f, 0f};
		rot = new float[] {0f, 0f, 0f, 0f,0f,0.2f};
		rotSun = new float[] {0f, 0f, 0f,0f,0f,1/0.2f};
		Astre mercure = new Astre("mercure.jpg",2.4f,65,0,inclinaison,rot,rotSun);
		astres.add(mercure);
		
		// VENUS
		inclinaison = new float[] {0f, 3.4f, 0f};
		rot = new float[] {0f, 0f, 0f, 0f,0f,0.2f};
		rotSun = new float[] {0f, 0f, 0f,0f,0f,1/0.6f};
		Astre venus = new Astre("venus.jpg",6.0f,72,0,inclinaison,rot,rotSun);
		astres.add(venus);
		
		// LA TERRE
		inclinaison = new float[] {0f, 3.3f, 0f};
		rot = new float[] {0f, 0f, 0f, 0f,0f,0.2f};
		rotSun = new float[] {0f, 0f, 0f,0f,0f,1f};
		Astre terre = new Astre("terre.jpg",6.3f,80,1,inclinaison,rot,rotSun);
		astres.add(terre);
		
		// MARS
		inclinaison = new float[] {0,1.9f,0};
		rot = new float[] {0f, 0f, 0f, 0,0f,0.2f};
		rotSun = new float[] {0f, 0f, 0f,0f,0f,1/1.9f};
		Astre mars = new Astre("mars.jpg",3.4f,90,2,inclinaison,rot,rotSun);
		astres.add(mars);
		
		// JUPITER
		inclinaison = new float[] {0,1.3f,0};
		rot = new float[] {0f, 0f, 0f, 0,0f,0.2f};
		rotSun = new float[] {0f, 0f, 0f,0f,0f,1/11.8f};
		Astre jupiter = new Astre("Jupiter.jpg",71.5f/3,140,3,inclinaison,rot,rotSun);
		astres.add(jupiter);
		
		// SATURNE (Anneau associé. ID = 7)
		inclinaison = new float[] {0,7f,0};
		rot = new float[] {0f, 0f, 0f, 0,0f,0.2f};
		rotSun = new float[] {0f, 0f, 0f,0f,0f,1/29.4f};
		Astre saturne = new Astre("saturne.jpg",60.3f/3,210,5,inclinaison,rot,rotSun);
		astres.add(saturne);
		
		// URANUS
		inclinaison = new float[] {0,0.8f,0};
		rot = new float[] {0f, 0f, 0f, 0,0f,0.2f};
		rotSun = new float[] {0f, 0f, 0f,0f,0f,1/84f};
		Astre uranus = new Astre("uranus.jpg",25.6f/3,260,4,inclinaison,rot,rotSun);
		astres.add(uranus);
		
		// URANUS
		inclinaison = new float[] {0,1.8f,0};
		rot = new float[] {0f, 0f, 0f, 0,0f,0.2f};
		rotSun = new float[] {0f, 0f, 0f,0f,0f,1/164f};
		Astre neptune = new Astre("neptune.jpg",24.8f/3,350,3,inclinaison,rot,rotSun);
		astres.add(neptune);
		
		
	}

	/**
	 * @author LE DIGABEL Nathan
	 * @author LECOIN Jean-Baptiste
	 * @param drawable
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		final GL2 gl = drawable.getGL().getGL2();
		if (height <= 0)
			height = 1;
		final float h = (float) width / (float) height;
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(45.0f, h, 1.0, 20.0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	/**
	 * Gestion des touches du clavier
	 * @author LE DIGABEL Nathan
	 * @author LECOIN Jean-Baptiste
	 * @param e
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:			// Se tourner vers la gauche
			POV_orientation -= POV_rotation_speed;
			lx = (float) Math.sin(Math.toRadians(POV_orientation));
			lz = (float) -Math.cos(Math.toRadians(POV_orientation));
			break;
		case KeyEvent.VK_RIGHT:			// Se tourner vers la droite
			POV_orientation += POV_rotation_speed;
			lx = (float) Math.sin(Math.toRadians(POV_orientation));
			lz = (float) -Math.cos(Math.toRadians(POV_orientation));
			break;
		case KeyEvent.VK_UP:			// Avancer
			eyeX += lx * POV_speed;
			eyeZ += lz * POV_speed;
			break;
		case KeyEvent.VK_Z:				// Déplacement vertical (Haut)
			eyeY+=POV_speed;
			break;
		case KeyEvent.VK_S:				// Déplacement vertical (Bas)
			eyeY-=POV_speed;
			break;
		case KeyEvent.VK_D:				// Déplacement horizontal (Droite)
			eyeX+=POV_speed;
			break;
		case KeyEvent.VK_Q:				// Déplacement horizontal (Gauche)
			eyeX-=POV_speed;
			break;
		case KeyEvent.VK_DOWN:			// Reculer
			eyeX -= lx * POV_speed;
			eyeZ -= lz * POV_speed;
			break;
		case KeyEvent.VK_BACK_SPACE:	// Retour à la position initale
			eyeX = 0;
			eyeY = 0;
			eyeZ = 0;
			lx = 0;
			lz = -1;
			break;
		case KeyEvent.VK_ADD:			// Accélérer le temps
			speed = (speed<100000) ? speed*2 : speed;
			break;
		case KeyEvent.VK_SUBTRACT:		// Ralentir le temps
			speed = (speed>0.00001) ? speed/2 : speed;
			break;
		case KeyEvent.VK_EQUALS:		// Remettre le temps à la vitesse normale
			speed = 1;
			break;	
		case KeyEvent.VK_V:				// Changer le mode de vue (de face/d'en haut)
			view = (view)? false:true;
			break;	
		case KeyEvent.VK_O:				// Afficher ou non les orbites
			orbites = (orbites)? false:true;
			break;	
		default:
			break;
		}

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}
