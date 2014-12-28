package color;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.*;

public class PlayImage {
	public static void main(String[] args){
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				ImageFrame frame = new ImageFrame(new File("C:/temp/LearnData/cam/"));
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}
}

class ImageFrame extends JFrame {
	private File[] files;

	private BufferedImage current_img;
	private final int WIDTH, HEIGHT;
	private int count = 0;
	private int end;

	double angle = 0;
	double intercept = 0;

	private boolean datae = false;
	LinkedList<Integer> pos = new LinkedList<Integer>();
	private final String wdir = "C:/temp/horizon/";
	private String curfname = null;

	LinearDiscriminant li = new LinearDiscriminant();
	Filter f = new Filter();

	ImageFrame(File file){
		if(file.isDirectory()){
			files  = file.listFiles();
			end = files.length;
		}else{
			//files[0] = file;
			//end = files.length;
		}

		current_img = imgRead(files[0]);
		WIDTH	= current_img.getWidth();
		HEIGHT	= current_img.getHeight();

		setSize(WIDTH, HEIGHT);

		addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				int keycode = e.getKeyCode();
				switch(keycode){
				case KeyEvent.VK_RIGHT:
					//System.out.println("Pressed RIGHT key");
					count = (count + files.length + 1) % files.length;
					imgRead(files[count]);
					break;
				case KeyEvent.VK_LEFT:
					//System.out.println("Pressed LEFT key");
					count = (count + files.length - 1) % files.length;
					imgRead(files[count]);
					break;
				case KeyEvent.VK_UP:
					count = (count + files.length + 10) % files.length;
					imgRead(files[count]);
					break;
				case KeyEvent.VK_DOWN:
					count = (count + files.length - 10) % files.length;
					imgRead(files[count]);
					break;
				}
			}
		});
	}

	public int[][] grayscale(BufferedImage img){
		int[][] grayimg = new int[img.getHeight()][img.getWidth()];

		for(int y = 0; y < HEIGHT; y++){
			for(int x = 0; x < WIDTH; x++){
				int rgb = img.getRGB(x, y);
				rgb -= 0xFF000000;
				int r	= (rgb & 0xFF0000) >> 16;
			int g	= (rgb & 0xFF00) >> 8	;
			int b	= rgb & 0xFF;
			int gray = (b + g + r) / 3;
			grayimg[y][x] = gray;
			}
		}
		return grayimg;
	}

	public BufferedImage setImg(int[][] gray, BufferedImage img){
		for(int y = 0; y < gray.length; y++){
			for(int x = 0; x < gray[0].length; x++){
				int grayscale = gray[y][x];
				if(grayscale == 700){
					img.setRGB(x, y, Integer.decode("0x" + "FF0000"));
				}else{
					int rgb = (grayscale & 0xFF) << 16;
					rgb = rgb | ((grayscale & 0xFF)<<8);
					rgb = rgb | (grayscale & 0xFF);
					img.setRGB(x, y, rgb);
				}
			}
		}
		return img;
	}

	public BufferedImage imgRead(File file){
		this.setTitle(file.getName());

		try {
			curfname = (file.getName()).replace(".jpg", "");
			current_img = ImageIO.read(file);
			BufferedImage img = current_img;

			//FT

			LeastSq least = new LeastSq();

			double[][] gray = f.grayscale(img);
			double[][] sx = f.sobelX(gray);
			double[][] sy = f.sobelY(gray);
			double[][] mgn = f.gradient_magnitudes(sx, sy);
			double[][] bin = li.binimg(gray);

			double[] alpha = new double[bin[0].length];
			double[] ax = new double[bin[0].length];
			double[] ay = new double[bin[0].length];

			int count = 0;
			for(int x = 0; x < bin[0].length; x++){
				for(int y = 0; y < bin.length - 400; y++){
					if(bin[y][x] < 1){
						alpha[x] = mgn[y][x];
						ax[count] = x;
						ay[count] = y;

						//System.out.println(x + " " + y);
						count++;
						break;
					}
				}
			}

			least.leastsquare(ax, ay);
			least.MestimationF(ax, ay, alpha);


			double a = least.getAngle();
			double b = least.getIntercept();


			//END
			drawDetect(a, b, current_img);

			//drawData(current_img);
			repaint();
			return current_img;
		}catch(IOException e){
			System.err.println("File can't read or broken");
		}
		return null;
	}

	public BufferedImage drawData(BufferedImage img){
		textRead();
		func(pos);
		for(int i = 0; i < current_img.getWidth(); i++){
			int y = (int)(Math.round(angle * i + intercept));
			current_img.setRGB(i, y, 255);
		}
		return current_img;
	}

	public BufferedImage drawDetect(double a, double b, BufferedImage img){
		for(int i = 0; i < current_img.getWidth(); i++){
			int y = (int)(Math.round(a * i + b));
			current_img.setRGB(i, y, Integer.decode("0x"+"ff0000"));
		}
		return current_img;
	}

	public LinkedList<Integer> textRead(){

		File file = new File(wdir + curfname +".txt");
		try{
			pos = new LinkedList<Integer>();
			BufferedReader br =
					new BufferedReader(new FileReader(file));
			String str1;
			//System.out.println("POSREAD");
			while((str1 = br.readLine()) != null){
				Scanner sc = new Scanner(str1);
				sc.useDelimiter(" ");
				while(sc.hasNext()){
					pos.offer(Integer.parseInt(sc.next()));
				}
			}

			for(int i = 0; i < pos.size(); i++){
				System.out.println(pos.get(i));
			}
		}catch(IOException e){
			pos = null;
		}
		return pos;
	}

	public void func(LinkedList<Integer> list){
		int lx = list.get(0);
		int ly = list.get(1);
		int rx = list.get(2);
		int ry = list.get(3);

		if((rx - lx) == 0 ){
			angle = 0;
		}else{
			angle = (double)(ry - ly) / (rx - lx);
		}
		intercept = ry - angle * rx;
	}



	public void paint(Graphics g){
		g.clearRect(0, 0, 2000, 2000);
		g.drawImage(current_img, 40 , 40,  this);
	}
}


