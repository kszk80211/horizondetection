package color;

public class LeastSq {
	double a = 0;
	double b = 0;

	public void leastsquare(int[] x, int[] y){
		int sumx2	= 0;
		int sumx	= 0;
		int sumy	= 0;
		int sumxy	= 0;

		for(int i = 0; i < x.length; i++){
			sumx2	+= x[i] * x[i];
			sumx	+= x[i];
			sumy	+= y[i];
			sumxy	+= x[i] * y[i];
		}

		double cof = (double)1 / (sumx2 * x.length - sumx * sumx);

		a = cof * (x.length * sumxy - sumx * sumy);
		b = cof * (-sumx * sumxy + sumx2 * sumy);
	}

	public void leastsquare(double[] x, double[] y){
		double sumx2 = 0;
		double sumx  = 0;
		double sumy  = 0;
		double sumxy = 0;

		for(int i = 0; i < x.length; i++){
			sumx2 += x[i] * x[i];
			sumx  += x[i];
			sumy  += y[i];
			sumxy += x[i] * y[i];
		}

		double cof = (double)1 / (sumx2 * x.length - sumx * sumx);

		a = cof * (x.length * sumxy - sumx * sumy);
		b = cof * (-sumx * sumxy + sumx2 * sumy);
	}

	public void Mestimation(double[] x, double[] y){
		double W	= 2;
		double[] w	= new double[x.length];

		for(int i = 0; i < x.length; i++){
			double d = Math.abs(y[i] - a * x[i] - b);

			if(d <= W){
				w[i] = (1 - (d / W) * (d / W)) * (1 - (d / W) * (d / W));
			}else{
				w[i] = 0;
			}

		}

		double sumx2 = 0;
		double sumx	 = 0;
		double sumy	 = 0;
		double sumxy = 0;
		double sumw	 = 0;

		for(int i = 0; i < x.length; i++){
			sumx2 += x[i] * x[i] * w[i];
			sumx  += x[i] * w[i];
			sumy  += y[i] * w[i];
			sumxy += x[i] * y[i] * w[i];
			sumw  += w[i];
		}

		double cof = (double)1 / (sumx2 * sumw - sumx * sumx);

		a = cof * (sumw * sumxy - sumx * sumy);
		b = cof * (-sumx * sumxy + sumx2 * sumy);
	}
	
	public void MestimationF(double[] x, double[] y, double[] alpha){
		double W	= 2;
		double[] w	= new double[x.length];

		for(int i = 0; i < x.length; i++){
			double d = Math.abs(y[i] - a * x[i] - b);

			if(d <= W){
				w[i] = (1 - (d / W) * (d / W)) * (1 - (d / W) * (d / W));
			}else{
				w[i] = 0;
			}

		}

		double sumx2 = 0;
		double sumx	 = 0;
		double sumy	 = 0;
		double sumxy = 0;
		double sumw	 = 0;

		for(int i = 0; i < x.length; i++){
			sumx2 += x[i] * x[i] * w[i] * alpha[i];
			sumx  += x[i] * w[i] * alpha[i];
			sumy  += y[i] * w[i] * alpha[i];
			sumxy += x[i] * y[i] * w[i] * alpha[i];
			sumw  += w[i] * alpha[i];
		}

		double cof = (double)1 / (sumx2 * sumw - sumx * sumx);

		a = cof * (sumw * sumxy - sumx * sumy);
		b = cof * (-sumx * sumxy + sumx2 * sumy);
	}

	public double getAngle(){
		return a;
	}

	public double getIntercept(){
		return b;
	}

	public static void main(String[] args){
		LeastSq least = new LeastSq();

		double x[] = { 0.0, 0.2, 0.4, 0.6, 0.8, 1.0, 1.2 };
		double y[] = { 1.0, 1.9, 3.2, 4.3, 4.8, 6.1, 7.2 };

		//least.leastsquare(x, y);
		least.Mestimation(x, y);

		System.out.println("a = " + least.getAngle() + "	" + "b = " + least.getIntercept());
	}
}
