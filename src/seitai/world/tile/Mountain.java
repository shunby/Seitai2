package seitai.world.tile;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import seitai.world.Pos;
import seitai.world.World;

public class Mountain extends Tile {
	public static Image image;
	public Mountain(World w, int posx, int posy, int grass) {
		super(w, posx, posy, grass, 80, 4, 13, 1);
	}

	@Override
	protected void onUpdate() {
	}

	@Override
	protected void draw(GraphicsContext g) {
		Pos p = Pos.toWindowPos(getX() * 50 + 25, getY() * 50 + 25);
		g.drawImage(image, p.getX() - 25, p.getY() - 25, 50, 50);
		g.setFill(Color.WHITE);
		g.fillText(String.valueOf(grass), p.getX() - 10, p.getY() - 10);
	}

}
