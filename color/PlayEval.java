package color;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PlayEval {
	public static void main(String[] args){
		SFrame frame = new SFrame(new File("C:/temp/LearnData/cam"));
	}
}

class SFrame{
	private File[] files;
	private int end = 0;
	private final String txtdir = "C:/temp/horizon/";
	EvalSSD evalssd = new EvalSSD();
	int allcount = 0;
	int hcount = 0;
	
	long sum = 0;

	SFrame(File file){
		if(file.isDirectory()){
			files  = file.listFiles();
			end = files.length;
		}
		 for(int i = 0; i < files.length; i++){
			//System.out.println(files[i].getName());
			imgRead(files[i]);
		 }

		 System.out.println("all count = " + allcount);
		 System.out.println("h count = " + hcount);
		 System.out.println("Average time  = " + (double)sum / allcount);
	}

	public void imgRead(File file){
		try {
			String filename = file.getName();
			filename = filename.replace(".jpg", ".txt");
			//System.out.println(filename.replace("DSC", "").substring(0, 5));
			//System.out.println(txtdir + filename);


			BufferedImage img = ImageIO.read(file);
			//HoughTransform hough = new HoughTransform();
			//hough.though(img, txtdir + filename);
			//hough.though(img);
			LinearDiscriminant li = new LinearDiscriminant();
			Filter f = new Filter();
			LeastSq least = new LeastSq();

			long start = System.currentTimeMillis();
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
				for(int y = 0; y < bin.length; y++){
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
			
			long end = System.currentTimeMillis();
			
			sum += (end - start);
			int buf = evalssd.evalF(a, b, txtdir + filename);
			switch(buf){
			case 1:
				allcount++;
				break;
			case 0:
				allcount++;
				hcount++;
				break;
			}


		}catch(IOException e){
			System.err.println("File can't read or broken");
		}
	}
}


