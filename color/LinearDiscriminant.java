package color;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class LinearDiscriminant {
    private  int allpix;
    private int[] hist;
    private int[] ac;
    private BufferedImage fimg;


    public double[][] binimg(double[][] gray){
        allpix = (int)(gray[0].length * gray.length);
        hist    = histogram(gray);
        ac      = accumulation(hist);

        double t = threshold();
       // System.out.println("threshold = " + t);
        return binarize(gray, t);
    }

    private BufferedImage imgRead(String file_path){
        BufferedImage img = null;

        try{
            img     = ImageIO.read(new File(file_path));
            return img;
        }catch(Exception e){
            return null;
        }
    }

    public double[][] grayscale(BufferedImage img){
        double[][] gray = new double[img.getHeight()][img.getWidth()];

        for(int y = 0; y < img.getHeight(); y++){
            for(int x = 0; x < img.getWidth(); x++){
                int rgb = img.getRGB(x, y);
                rgb -= 0xFF000000;
                int r    = (rgb & 0xFF0000) >> 16;
                int g    = (rgb & 0xFF00) >> 8    ;
                int b    = rgb & 0xFF;
                double l = (double)(b + g + r) / 3;
                gray[y][x] = l;
            }
        }
        return gray;
    }

    private double[][] gaussian_filter(double[][] gray){
        double[] GAUSSIAN = {0.0625, 0.125, 0.25};
        double[][] buf = new double[gray.length][gray[0].length];

        for(int y = 1; y < buf.length - 1; y++){
            for(int x = 1; x < buf[0].length - 1; x++){
                double sum = 0;
                sum = (gray[y-1][x-1] + gray[y-1][x+1] + gray[y+1][x-1] + gray[y+1][x+1]) * GAUSSIAN[0]
                           + (gray[y-1][x] + gray[y][x-1] + gray[y][x+1] + gray[y+1][x]) * GAUSSIAN[1]
                           + gray[y][x] * GAUSSIAN[2];
                buf[y][x] = sum;
            }
        }
        return buf;
    }

    private int[] histogram(double[][] gray){
        int[] histogram = new int[256];

        for(int y = 0; y  < gray.length; y++){
            for(int x = 0; x < gray[0].length; x++){
                histogram[(int)(Math.round(gray[y][x]))]++;
            }
        }
        return histogram;
    }

    private int[] accumulation(int[] hist){
        int ac[] = new int[hist.length];
        int sum = 0;

        for(int i = 0; i < hist.length; i++){
            sum += hist[i];
            ac[i] = sum;
        }
        return ac;
    }

    private double threshold(){
        double threshold = 0;
        BigDecimal max = BigDecimal.ZERO;
        BigDecimal separation_metrics;
        BigDecimal pbB, pwB, mul;

        for(double t = 0.5; t < 255; t++){
            int pb    = ac[(int)(t - 0.5)];
            int pw    = allpix - pb;

            double mb    = classBlackMean(t);
            double mw    = classWhiteMean(t);

            if(mb == -1 || mw == -1){
                continue;
            }

            //System.out.println(t + " : " +mb);
            pbB    = BigDecimal.valueOf(pb);
            pwB    = BigDecimal.valueOf(pw);
            mul    = pbB.multiply(pwB);

            separation_metrics = mul.multiply(BigDecimal.valueOf(Math.pow((mb - mw), 2)));
            //System.out.println(separation_metrics);

            if(separation_metrics.compareTo(max) > 0){
                max = separation_metrics;
                //System.out.println("MAX = " + max);
                threshold = t;
            }
        }
        return threshold;
    }

    private double classBlackMean(double t){
        double sum = 0;
        double mean;

        for(int i = 0; i < t; i++){
            sum += hist[i] * i;
        }
        if(ac[(int)t] == 0){
            return -1;
        }else{
            mean = sum / ac[(int)t];
            return mean;
        }
    }

    private double classWhiteMean(double t){
        double sum = 0;
        double mean;

        for(int i = (int)(t + 0.5); i <= 255; i++){
            sum += hist[i] * i;
        }
        if(allpix - ac[(int)t] == 0){
            return -1;
        }else{
            mean = sum / (allpix - ac[(int)t]);
            return mean;
        }
    }

    private double[][] binarize(double[][] gray, double t){
        for(int y = 0; y < gray.length; y++){
            for(int x = 0; x < gray[0].length; x++){
                if(gray[y][x] > t){
                    gray[y][x] = 1; //white
                }else{
                    gray[y][x] = 0; //black
                }
            }
        }
        return gray;
    }

    public BufferedImage getImg(){
        return fimg;
    }
}


class DispImg extends JFrame {
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
