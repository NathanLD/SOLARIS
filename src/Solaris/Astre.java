package Solaris;

import java.awt.Component;
import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class Astre {

	
	private Texture tex;
	private float size;
	private float distance;
	private int nbSatellites;		//	Nombre de satellites. Seront générés automatiquement
	private ArrayList<Satellite> satellites = new ArrayList<Satellite>();
	private float[] inclinaison = new float[3];
	private float[] rot = new float[6];		// [X,Y,Z,deltaX,deltaY,deltaZ] rotation sur soi meme
	private float[] rotSun = new float[6];	// [X,Y,Z,deltaX,deltaY,deltaZ] rotation autour du soleil
	
	/**
	 * @author LE DIGABEL Nathan
	 * @param picture
	 * @param size
	 * @param distance
	 * @param nbSatellites
	 * @param inclinaison
	 * @param rot
	 * @param rotSun
	 */
	public Astre(String picture, float size, int distance, int nbSatellites, float[] inclinaison, float[] rot, float[] rotSun) {
		try {
			tex = TextureIO.newTexture(new File("data/"+picture), true);
		} catch (IOException e) {
			javax.swing.JOptionPane.showMessageDialog(null, e);
		}
		this.size=size;
		this.distance=distance;
		this.nbSatellites=nbSatellites;
		this.inclinaison=inclinaison;
		this.rot=rot;
		this.rotSun=rotSun;
		for(int i=0;i<nbSatellites;i++){
			satellites.add(new Satellite(this));
		}
	}

	/**
	 * @author LE DIGABEL Nathan
	 * @return La texture de l'astre
	 */
	public Texture getTex() {
		return tex;
	}

	/**
	 * @author LE DIGABEL Nathan
	 * @return La taille de l'astre
	 */
	public float getSize() {
		return size;
	}
	
	/**
	 * @author LE DIGABEL Nathan
	 * @return La distance du soleil à l'astre
	 */
	public float getDistance(){
		return distance;
	}
	
	/**
	 * @author LE DIGABEL Nathan
	 * @return Les satellites de l'astre
	 */
	public ArrayList<Satellite> getSatellites(){
		return satellites;
	}
	
	/**
	 * @author LE DIGABEL Nathan
	 * @param i 1>x,2>y,3>z,4>deltaX,5>deltaY,6>deltaZ
	 * @return Un paramètre de rotation de l'astre sur lui-même
	 */
	public float getRot(int i){
		return rot[i];
	}
	
	/**
	 * @author LE DIGABEL Nathan
	 * @param i 1>x,2>y,3>z,4>deltaX,5>deltaY,6>deltaZ
	 * @return Un paramètre de rotation de l'astre autour du soleil 
	 */
	public float getRotSun(int i){
		return rotSun[i];
	}
	
	/**
	 * @author LE DIGABEL Nathan
	 * @param i 1>x,2>y,3>z
	 * @return L'inclinaison de l'astre selon un axe
	 */
	public float getInclinaison(int i){
		return inclinaison[i];
	}
	
	/**
	 * @author LE DIGABEL Nathan
	 * @param speed L'angle à ajouter à chaque axe pour chaque frame pour la rotation sur soi-même
	 */
	public void changeRot(float speed){
		for(int i=0;i<3;i++){
			this.rot[i]+=this.rot[i+3]*speed;
		}
	}

	/**
	 * @author LE DIGABEL Nathan
	 * @param speed L'angle à ajouter à chaque axe pour chaque frame pour la rotation autour du soleil
	 */
	public void changeRotSun(float speed) {
		for(int i=0;i<3;i++){
			this.rotSun[i]+=this.rotSun[i+3]*speed;
		}
	}
	
	/**
	 * @author LE DIGABEL Nathan
	 * @return Le nombre de satellites de l'astre
	 */
	public int getNbSatellites(){
		return nbSatellites;
	}
}
