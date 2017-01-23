package seitai.world;

import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import seitai.Main;
import seitai.living.Living;
import seitai.living.LivingStatus;

/**
 * ワールド上のタイルを表す
 * 世界は広いため、分割して処理をすることで速度を上げる
 */
public class Tile {

	public static Image image;

	/**
	 * 子のタイルが何番目のタイルに当たるか(World.getTileのインデックス)
	 */
	private int x,y;

	/**
	 * このタイル上のLiving
	 */
	private List<Living> livings;

	/**
	 * 子のタイルに生えている草の数
	 */
	private int grass;

	/**
	 * このタイルのある世界
	 *
	 */
	private World world;

	public Tile(World w, int posx, int posy){
		this.x = posx;
		this.y = posy;
		livings = new ArrayList<>();
		grass = 1000;
		world = w;
	}

	/**
	 * タイルの更新処理
	 * @param g
	 */
	public void update(GraphicsContext g){
		world.grass+=this.grass;
		if(Pos.isinWindow(getX() * 50 + 25, getY() * 50 + 25))draw(g);
		if(!Main.isTimePass())return;
		if(livings.size() > 8){
			livings.forEach(new Consumer<Living>() {
			public void accept(Living t) {
				t.getStatus().set(LivingStatus.HP, t.getStatus().get(LivingStatus.HP) - 100);
			};
			});
		}
		onUpdate();

	}

	protected void onUpdate(){
		this.grass =grass > 10000 ? grass : grass + 10;
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

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public List<Living> getLivings(){
		return livings;
	}

	public int getGrass(){
		return grass;
	}

	public void setGrass(int grass){
		if(grass < 0)grass = 0;
		this.grass = grass;
	}

}
