package color;

import gakkai.CannyEdge;

import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Filter {
	private int IMG_WIDTH;
	private int IMG_HEIGHT;
	private BufferedImage img = null;
	
	public void inputImg(BufferedImage img){
		IMG_WIDTH	= img.getWidth();
		IMG_HEIGHT	= img.getHeight();
		this.img = img;
	}
	
	public double[][] grayscale(BufferedImage img){
		double[][] gray = new double[img.getHeight()][img.getWidth()];
		
		for(int y = 0; y < gray.length; y++){
			for(int x = 0; x < gray[0].length; x++){
				int rgb = img.getRGB(x, y);
				rgb -= 0xFF000000;
				int r	= (rgb & 0xFF0000) >> 16;
				int g	= (rgb & 0xFF00) >> 8	;
				int b	= rgb & 0xFF;
				double l = (double)(b + g + r) / 3;
				gray[y][x] = l;
			}
		}
		return gray;
	}
	
	public void dispPix(int[][] img){
		for(int y = 0; y < img.length; y++){
			for(int x = 0; x < img[0].length; x++){
				System.out.print(img[y][x] + "	");
			}
			System.out.println();
		}
	}
	
	public double[][] gaussian_filter(double[][] gray){
		double[] GAUSSIAN_FILTER = {0.0625, 0.125, 0.25};
		double[][] buf_img = new double[IMG_HEIGHT][IMG_WIDTH];
		
		for(int y = 1; y < IMG_HEIGHT - 1; y++){
			for(int x = 1; x < IMG_WIDTH - 1; x++){
				double sum = 0;
				sum =  (gray[y-1][x-1] + gray[y-1][x+1] + gray[y+1][x-1] + gray[y+1][x+1]) * GAUSSIAN_FILTER[0] 
				                                                                                             + (gray[y-1][x] + gray[y][x-1] + gray[y][x+1] + gray[y+1][x]) * GAUSSIAN_FILTER[1] + gray[y][x] * GAUSSIAN_FILTER[2];
				buf_img[y][x] = sum;
			}
		}
		return buf_img;
	}
	
	public double[][] differentiationX(double[][] gray){
		double[][] buf_img = new double[IMG_HEIGHT][IMG_WIDTH];
		
		for(int y = 1; y < IMG_HEIGHT - 1; y++){
			for(int x = 1; x < IMG_WIDTH - 1; x++){
				buf_img[y][x] = gray[y][x + 1] - gray[y][x - 1];
			}
		}
		return buf_img;
	}
	
	public double[][] differentiationY(double[][] gray){
		double[][] buf_img = new double[IMG_HEIGHT][IMG_WIDTH];

		for(int y = 1; y < IMG_HEIGHT - 1; y++){
			for(int x = 1; x < IMG_WIDTH - 1; x++){
				buf_img[y][x] = gray[y + 1][x] - gray[y - 1][x];
			}
		}
		return buf_img;
	}
	
	public double[][] sobelX(double[][] gray){
		int[] SOBEL = {1, 2, 1};
		double[][] buf_img = new double[gray.length][gray[0].length];
		
		for(int y = 1; y < gray.length - 1; y++){
			for(int x = 1; x < gray[0].length - 1; x++){
				double sum = 0;
				sum = gray[y - 1][x + 1] - gray[y - 1][x - 1] + SOBEL[0] * (gray[y][x + 1] - gray[y][x - 1]) + gray[y + 1][x + 1] - gray[y + 1][x - 1];
				buf_img[y][x] = sum;
			}
		}
		return buf_img;
	}
	
	public double[][] sobelY(double[][] gray){
		int[] SOBEL = {1, 2, 1};
		double[][] buf_img = new double[gray.length][gray[0].length];
		
		for(int y = 1; y < gray.length - 1; y++){
			for(int x = 1; x < gray[0].length - 1; x++){
				double sum = 0;
				sum = (gray[y - 1][x - 1] + gray[y - 1][x + 1]) * -SOBEL[0] + (gray[y + 1][x - 1] + gray[y + 1][x + 1]) + (gray[y - 1][x]) * -SOBEL[1] + gray[y + 1][x] * SOBEL[1];
				buf_img[y][x] = sum;
				//System.out.println(sum);
			}
		}
		return buf_img;
	}
	
	//3 * 3 filter
	public double[][]  laplacian(double[][] gray){
		int[] LAPLACIAN = {-1, 8};
		double[][] buf_img = new double[IMG_HEIGHT][IMG_WIDTH];
		
		for(int y = 1; y < IMG_HEIGHT - 1; y++){
			for(int x = 1; x < IMG_WIDTH - 1; x++){
				double sum = 0;
				sum = LAPLACIAN[0] * (gray[y - 1][x - 1] + gray[y - 1][x + 1] + gray[y + 1][x - 1] 
				                                                                                   + gray[y + 1][x + 1] + gray[y - 1][x] +gray[y + 1][x] + gray[y][x - 1] + gray[y][x + 1]) + LAPLACIAN[1] * gray[y][x];
				buf_img[y][x] = sum;
			}
		}
		return buf_img;
	}
	
	public BufferedImage setImg(double[][] gray, BufferedImage img){
		for(int y = 0; y < gray.length; y++){
			for(int x = 0; x < gray[0].length; x++){
				int grayscale = (int)gray[y][x];
				if(gray[y][x] == 1){
					grayscale = 255;
				}
				
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

	public short[][] horizon(short[][] gray){
		int threshold = 12;
		for(int y = 0; y < gray.length; y++){
			for(int x = 0; x < gray[0].length; x++){
				if(gray[y][x] > threshold){
					gray[y][x] = 0;
				}
			}
		}
		
		int sum = 0;
		int max = 0;
		int colum = 0;
		for(int y = 1; y < gray.length - 1; y++){
			for(int x = 0; x < gray[0].length; x++){
				sum += Math.abs(gray[y + 1][x] - gray[y - 1][x]);
			}
			if(sum > max){
				max = sum;
				colum = y;
			}
			sum = 0;
		}
		
		for(int x = 0; x < gray[0].length; x++){
			gray[colum][x] = 700;
		}
		
		System.out.println("colum = " + colum);
		return gray;
	}
	
	public double[][] gradient_magnitudes(double[][] grayX, double[][] grayY){
		double[][] gradient_m = new double[grayX.length][grayX[0].length];
		
		for(int y = 0; y < grayX.length; y++){
			for(int x = 0; x < grayX[0].length; x++){
				gradient_m[y][x] = Math.sqrt(Math.pow(grayX[y][x], 2) + Math.pow(grayY[y][x], 2));
			}
		}
		
		return gradient_m;
	}
	
	public int[][] nabla(double[][] xd, double[][] yd){
		int[][] nabla = new int[xd.length][xd[0].length];

		for(int y = 0; y < nabla.length; y++){
			for(int x = 0;  x < nabla[0].length; x++){
				if(xd[y][x] == 0 && yd[y][x] == 0){
					nabla[y][x] = -1;
				}else if(xd[y][x] == 0){
					nabla[y][x] = 90;//90
				}else{
					nabla[y][x] = (int)Math.toDegrees(Math.atan((double)yd[y][x] / xd[y][x]));
				}
			}
		}
		return nabla;
	}
	
	public double[][] gradient(double[][] grayX, double[][] grayY){
		double[][] gradient = new double[grayX.length][grayX[0].length];
		
		for(int y = 0; y < grayX.length; y++){
			for(int x = 0; x < grayX[0].length; x++){
				double theta;
				
				if(grayX[y][x] != 0){
					theta = Math.atan(grayY[y][x] / grayX[y][x]);
					theta = Math.toDegrees(theta);
					
					if(theta >= 0 && theta <= 1){
						theta = 700;
					}
					
					if(theta < 0){
						theta += 180;
					}
					
				}else if(grayY[y][x] != 0){
					theta = 1000;
				}else{
					theta = 0;
				}
				gradient[y][x] = theta;
			}
		}
		
		return gradient;
	}
	
	public static void main(String[] args) throws IOException{
		/*
		CannyEdge canny = new CannyEdge();
		LinearDiscriminant li = new LinearDiscriminant();
		Filter f = new Filter();
		File file = new File("C:\\temp\\LearnData\\cam\\0.jpg");
		BufferedImage img = ImageIO.read(file);
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
		
		System.out.println(a);
		
		for(int i = 0; i < ax.length; i++){
			img.setRGB(i, (int)(a * i + b), 255);
		}
		*/
		LinearDiscriminant li = new LinearDiscriminant();
		Filter f = new Filter();
		File file = new File("C:\\temp\\LearnData\\cam\\23.jpg");
		BufferedImage img = ImageIO.read(file);
		f.inputImg(img);
		
		double[][] gray = f.grayscale(img);
		//gray = f.sobelY(gray);
		//gray = f.sobelY(gray);
		gray = li.binimg(gray);
		
		/*
		
		int[] hist = new int[256];
		
		for(int y = 0; y < gray.length; y++){
			for(int x = 0; x < gray[0].length; x++){
				hist[(int)gray[y][x]]++;
			}
		}
		
		
		for(int i = 0; i < hist.length; i++){
			System.out.println(hist[i]);
		}
		*/
		
		
		img = f.setImg(gray, img);
		
		try {
			ImageIO.write(img, "jpeg", new File("C:/temp/ft23bin.jpeg"));
			} catch (Exception e) {
			  e.printStackTrace();
			}
		
		
		
		
		DispImg disp = new DispImg(img);
		disp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		disp.setVisible(true);
	}
}
