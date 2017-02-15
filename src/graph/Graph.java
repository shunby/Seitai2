package graph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

import javax.imageio.ImageIO;

public class Graph {
	static Random r;

	public static void main(String[] args) {
		r = new Random();
	    BufferedImage readImage = null;
	    if (readImage == null){
	      readImage = new BufferedImage(1000, 1000,
	        BufferedImage.TYPE_INT_BGR);
	    }

	    Graphics2D off = readImage.createGraphics();

	    drawGraph(off);

	    try {
	       ImageIO.write(readImage, "png", new File("graph.png"));
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	  }

	private static void drawGraph(Graphics2D g) {
		int ge_num = 30000;
		int fe_num = 200;
		int se_num = 0;
		int time = 0;

		double ge_increase =gen();
		double ge_decrease = gen();
		double fe_increase = gen();
		double fe_decrease = gen();
		double fe_decrease_2 = gen();
		double se_increase = gen();
		double se_decrease = gen();
		System.out.println("gi="+ge_increase + "\n"
				 + "gd=" + ge_decrease + "\n"
				 + "fi=" + fe_increase + "\n"
				 + "fd0=" + fe_decrease + "\n"
				 + "fd1=" + fe_decrease_2 + "\n"
				 + "si=" + se_increase + "\n"
				 + "sd=" + se_decrease + "\n");

		System.out.println("  ge  |  fe   |   se ");

		for(time = 0; time <= 100; time++){
			double ga = ge_increase * ge_num - ge_decrease * ge_num * fe_num;
			double fa = fe_increase * ge_num * fe_num - fe_decrease * fe_num - fe_decrease_2 * fe_num * se_num;
			double sa = se_increase * fe_num * se_num - se_decrease * se_num;

			int r = 10;
			ge_num += ga;
			fe_num += fa;
			se_num += sa;
			ge_num = ge_num < 0 ? 0 : ge_num;
			se_num = se_num < 0 ? 0 : se_num;
			fe_num = fe_num < 0 ? 0 : fe_num;

			g.setColor(Color.yellow);
			g.drawOval(time, 1000 - (int) (ge_num), r, r);

			g.setColor(Color.red);
			g.drawOval(time,1000 -  (int) (fe_num), r, r);

			g.setColor(Color.blue);
			g.drawOval(time, 1000 - (int) (se_num), r, r);

			System.out.printf("%05d | %05d | %05d\n", (int)ge_num, (int)fe_num, (int)se_num);

		}




	}

	static double gen(){
		return r.nextInt(10000000) / 100000000d;
	}

}
