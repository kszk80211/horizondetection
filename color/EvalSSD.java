package color;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

public class EvalSSD {
	double angle = 0;
	double intercept = 0;
	int hcount = 0;

	boolean flag = true;
	public void textRead(String filepath){
		int[] pos = new int[4];
		File file = new File(filepath);
		try{
			BufferedReader br =
				new BufferedReader(new FileReader(file));
			String str1;
			int count = 0;
			while((str1 = br.readLine()) != null){
				Scanner sc = new Scanner(str1);
				sc.useDelimiter(" ");
				while(sc.hasNext()){
					pos[count] = Integer.parseInt(sc.next());
					count++;
				}
			}
		}catch(IOException e){
			flag = false;
			return;
		}
		func(pos);
	}

	public void func(int[] pos){
		int lx = pos[0];
		int ly = pos[1];
		int rx = pos[2];
		int ry = pos[3];

		if((rx - lx) == 0 ){
			angle = 0;
		}else{
			angle = (double)(ry - ly) / (rx - lx);
		}
		intercept = ry - angle * rx;
		//System.out.println(angle + "x" + "+" + intercept);
	}

	//calculate ssd
	public int  eval(int[][] plotX, int[][] plotY, double[][] grays, String filepass){
		textRead(filepass);
		if(flag == false){
			System.out.println("EMPTY");
			flag = true;
			return -1;
		}
		int max = 0;
		int bc = 0;

		//LinkedList<Integer> mlist = new LinkedList<Integer>();

		for(int i = 0; i < plotX.length; i++){
			int dif = 0;
			//LinkedList<Integer> list = new LinkedList<Integer>();
			for(int j = 0; plotX[i][j] != -1; j++){
				int px = plotX[i][j];
				int py = plotY[i][j];
				try{
					int abs = (int)Math.abs(grays[py + 5][px] - grays[py - 5][px]);
					dif += abs;
					//list.offer(abs);
				}catch(Exception e){

				}

			}
			if(max < dif){
				bc  = i;
				max = dif;
				//mlist = list;
			}
		}

		double ssd = 0;
		int evalcnt = 0;
		for(int i = 0; plotX[bc][i] != -1; i++){
			int px = plotX[bc][i];
			int py = plotY[bc][i];
			evalcnt++;
			//System.out.println((py - (angle * px + intercept)));
			ssd += (py - (angle * px + intercept)) * (py - (angle * px + intercept));
		}

		/*
		System.out.println("SSD = " + ssd);
		System.out.println("SSD AVERAGE = " + (ssd / evalcnt));
		*/
		System.out.println(ssd + "," + (ssd / evalcnt));
		//mlist.offer(-1);
		//variance(mlist, max);
		if(bc == 0){
			return 1;
		}
		return 0;
	}
	
	//calculate ssd
	public int  evalF(double a, double b, String filepath){
		textRead(filepath);
		if(flag == false){
			System.out.println("EMPTY");
			flag = true;
			return -1;
		}

		int width = 1128;
		double ssd = 0;
		int evalcnt = 0;
		
		for(int i = 0; i <= width; i++){
			int px = i;
			int py = (int)(a * i + b);
			evalcnt++;
			//System.out.println((py - (angle * px + intercept)));
			ssd += (py - (angle * px + intercept)) * (py - (angle * px + intercept));
		}

		/*
		System.out.println("SSD = " + ssd);
		System.out.println("SSD AVERAGE = " + (ssd / evalcnt));
		*/
		System.out.println(ssd + "," + (ssd / evalcnt));
		//mlist.offer(-1);
		//variance(mlist, max);
		return 0;
	}


	public double variance(LinkedList<Integer> list, int sum){
		double average = (double)sum / (list.size() - 1);
		double vsum = 0;

		for(int i = 0; list.get(i) != -1; i++){
			vsum += list.get(i) * list.get(i);
		}

		double rv = vsum / (list.size() - 1)	- average  * average;
		System.out.println(sum + "," + average + "," + rv);
		return rv;
	}

}
