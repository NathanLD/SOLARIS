package Solaris;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class Satellite {
	
	private Texture tex;		//aleat
	private float size;			//aleat
	private float distance;		//aleat
	private float[] inclinaison = new float[3];
	private float[] rot = new float[6];		// [X,Y,Z,deltaX,deltaY,deltaZ] rotation sur soi meme
	private float[] rotSun = new float[6];	// [X,Y,Z,deltaX,deltaY,deltaZ] rotation autour du soleil

	public Satellite(Astre pere) {
		this.size = alea(pere.getSize()/7,pere.getSize()/3);
		this.distance = alea(pere.getSize()*1.5f /*(9/10)*/,pere.getSize()*3);
		this.inclinaison = new float[] {alea(0,5), alea(0,5), alea(0,5)};
		this.rot = new float[] {alea(0,90), alea(0,90), alea(0,90), alea(0,10), alea(0,10), alea(0,10)};
		this.rotSun = new float[] {alea(0,90),alea(0,90),alea(0,90), alea(0,10), alea(0,10), alea(0,10)};
		// A CHANGER
		try {
			tex = TextureIO.newTexture(new File("data/Lune.jpg"), true);
		} catch (IOException e) {
			javax.swing.JOptionPane.showMessageDialog(null, e);
		}
	}
	
	/**
	 * @author LECOIN Jean-Baptiste
	 * @param min Nombre minimum
	 * @param max Nombre maximum
	 * @return Un float aléatoire entre min et max
	 */
	public float alea(float min, float max){
		return ((float)Math.random())*(max-min)+(min);
	}

	/**
	 * @author LECOIN Jean-Baptiste
	 * @return La texture du satellite
	 */
	public Texture getTex() {
		return tex;
	}

	/**
	 * @author LECOIN Jean-Baptiste
	 * @return La taille du satellite
	 */
	public float getSize() {
		return size;
	}
	
	/**
	 * @author LECOIN Jean-Baptiste
	 * @return La distance à l'astre du satellite
	 */
	public float getDistance(){
		return distance;
	}
	
	/**
	 * @author LECOIN Jean-Baptiste
	 * @param i 1>x,2>y,3>z,4>deltaX,5>deltaY,6>deltaZ
	 * @return Un paramètre de rotation du satellite sur lui-même
	 */
	public float getRot(int i){
		return rot[i];
	}
	
	/**
	 * @author LECOIN Jean-Baptiste
	 * @param i 1>x,2>y,3>z,4>deltaX,5>deltaY,6>deltaZ
	 * @return Un paramètre de rotation du satellite autour du soleil 
	 */
	public float getRotSun(int i){
		return rotSun[i];
	}
	
	/**
	 * @author LECOIN Jean-Baptiste
	 * @param i 1>x,2>y,3>z
	 * @return L'inclinaison du satellite selon un axe
	 */
	public float getInclinaison(int i){
		return inclinaison[i];
	}
	
	/**
	 * @author LECOIN Jean-Baptiste
	 * @param speed L'angle à ajouter à chaque axe pour chaque frame pour la rotation sur soi-même
	 */
	public void changeRot(float speed){
		for(int i=0;i<3;i++){
			this.rot[i]+=this.rot[i+3]*speed;
		}
	}

	/**
	 * @author LECOIN Jean-Baptiste
	 * @param speed L'angle à ajouter à chaque axe pour chaque frame pour la rotation autour de l'astre
	 */
	public void changeRotSun(float speed) {
		for(int i=0;i<3;i++){
			this.rotSun[i]+=this.rotSun[i+3]*speed;
		}
	}
}
