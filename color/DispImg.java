package color;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

public class DispImg extends JFrame { 
	BufferedImage img;
	final int param = 1;
	
	DispImg(BufferedImage img){
		this.img = img;
		setSize(img.getWidth() / param + 4, img.getHeight() / param + 38);
	}
	
	public void paint(Graphics g){
		g.drawImage(img, 8, 30, img.getWidth() / param, img.getHeight() / param, this);
	}
}
