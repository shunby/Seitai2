package seitai.living.spawner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import seitai.Main;
import seitai.living.Living;
import seitai.living.LivingStatus;
import seitai.living.eater.Eater;
import seitai.world.Pos;

public class Spawner extends Living{
	public static Image image;

	private int spawnFrequency;
	private Class<? extends Living> spawnClass;

	public Spawner(int x, int y, int spawnFrequency, Class<? extends Living> spawnClass) {
		super(x, y, 10000, 0, 0, 0, 20, 0, 0);
		this.spawnFrequency = spawnFrequency;
		this.spawnClass = spawnClass;
	}

	@Override
	protected void draw(GraphicsContext g) {
		Pos p = Pos.toWindowPos(pos.getX(), pos.getY());
		int siz = status.get(LivingStatus.SIZE);

		g.drawImage(image, p.getX() - siz / 2, p.getY() - siz / 2, siz, siz);
	}

	@Override
	protected void onUpdate() {
		if(Main.getTime() % spawnFrequency == 0){
			try {

				Method getCommonInstance = spawnClass.getMethod("getCommonInstance", int.class, int.class);
				Living spawn = (Living)getCommonInstance.invoke(null, pos.getX(), pos.getY());
				world.getLivings().add(spawn);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}

	@Override
	public Living bear(Living l) {
		return null;
	}

	public static Living getCommonInstance(int x, int y){
		return new Spawner(x, y, 16, Eater.class);
	}

}