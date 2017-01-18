package seitai.living.plant;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import seitai.living.Living;
import seitai.living.LivingStatus;
import seitai.world.Pos;
/**
 * 植物を表すクラス 現在は使用されていないが、この先新しい種類の植物として再登場するかも
 *
 */
public class Plant extends Living {

	public static Image image;

	protected Plant(int x, int y, int hp, int guard,
			int size, int spine, int green) {
		super(x, y, hp, 0, guard, 0, size, spine, green);
	}

	@Override
	protected void onUpdate() {
		status.set(LivingStatus.HP, status.get(LivingStatus.HP) + status.get(LivingStatus.GREEN));
		if(status.get(LivingStatus.HP) > 2000){
			Living child = bear(null);
			world.getLivings().add(child);
			status.set(LivingStatus.HP, status.get(LivingStatus.HP) - 1000);
		}
	}

	@Override
	protected void draw(GraphicsContext g){
		Pos p = Pos.toWindowPos(pos.getX(), pos.getY());
		int siz = status.get(LivingStatus.SIZE);
		g.drawImage(image, p.getX() - siz/2, p.getY() - siz/2, siz , siz);
		g.setFill(Color.RED);
		g.fillText(String.valueOf(status.get(LivingStatus.HP)), p.getX() - siz/2, p.getY() - siz/2);
	}

	@Override
	public Living bear(Living l) {
		return new Plant(pos.getX()+ (rand.nextInt(status.get(LivingStatus.SIZE) * 3 *2 + 1) - status.get(LivingStatus.SIZE) * 3), pos.getY()+(rand.nextInt(status.get(LivingStatus.SIZE) *3 *2 + 1) - status.get(LivingStatus.SIZE)*3), 1000, status.get(LivingStatus.GUARD), status.get(LivingStatus.SIZE), status.get(LivingStatus.SPINE), status.get(LivingStatus.GREEN));
	}

}
