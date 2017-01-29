package graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import seitai.Main;

public class Graph {

	public static void main(String[] args) {

	    BufferedImage readImage = null;
	    if (readImage == null){
	      readImage = new BufferedImage(1000, 1000,
	        BufferedImage.TYPE_INT_BGR);
	    }

	    Graphics2D off = readImage.createGraphics();

	    drawGraph(off);

	    try {
	      boolean result =
	       ImageIO.write(readImage, "png", new File("graph.png"));
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	  }

	private static void drawGraph(Graphics2D g) {
		int ge_num = 300;
		int fe_num = 20;
		int se_num = 0;
		int time = 0;

		double ge_increase = 0.0125d;
		double ge_decrease = 0.0025d;
		double fe_increase = 0.005d;
		double fe_decrease = 0.0025d;
		double fe_decrease_2 = 0.00125d;
		double se_increase = 0.00075d;
		double se_decrease = 0.00025d;

		for(time = 0; time <= 1000; time++){
			double ga = ge_increase * ge_num - ge_decrease * ge_num * fe_num;
			double fa = fe_increase * ge_num * fe_num - fe_decrease * fe_num - fe_decrease_2 * fe_num * se_num;
			double sa = se_increase * fe_num * se_num - se_decrease * se_num;

			int r = 10;
			ge_num += ga;
			fe_num += fa;
			se_num += sa;
			ge_num = ge_num < 10 ? 10 : ge_num;
			se_num = se_num < 10 ? 10 : se_num;
			fe_num = fe_num < 10 ? 10 : fe_num;

			g.setColor(Color.yellow);
			g.drawOval(time, 1000 - (int) (ge_num), r, r);

			g.setColor(Color.red);
			g.drawOval(time,1000 -  (int) (fe_num), r, r);

			g.setColor(Color.blue);
			g.drawOval(time, 1000 - (int) (se_num), r, r);

		}




	}

}
