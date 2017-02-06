package seitai.world.tile;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import seitai.world.Pos;
import seitai.world.World;

public class Plain extends Tile {
	public static Image image;

	public Plain(World w, int posx, int posy, int grass) {
		super(w, posx, posy, grass, 20, 1, 20, 1);
	}

	@Override
	protected void onUpdate() {
	}

	protected void draw(GraphicsContext g){
		Pos p = Pos.toWindowPos(getX() * 50 + 25, getY() * 50 + 25);
		g.drawImage(image, p.getX() - 25, p.getY() - 25, 50, 50);
		double green = grass / 3000.0 > 1 ? 0.6 : (grass / 3000.0) * 0.6;
		g.setFill(new Color(0.0, 1, 0.0, green));
		g.fillRect(p.getX() - 25, p.getY() - 25, 50, 50);
		g.setFill(Color.WHITE);
		g.fillText(String.valueOf(grass), p.getX() - 10, p.getY() - 10);
	}

}
