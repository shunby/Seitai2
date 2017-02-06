package seitai.world.tile;

import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import seitai.Main;
import seitai.living.Living;
import seitai.living.LivingStatus;
import seitai.world.Pos;
import seitai.world.World;

/**
 * ワールド上のタイルを表す
 * 世界は広いため、分割して処理をすることで速度を上げる
 */
public abstract class Tile implements Serializable {

	public static Image image;

	/**
	 * 子のタイルが何番目のタイルに当たるか(World.getTileのインデックス)
	 */
	protected int x,y;

	/**
	 * このタイル上のLiving
	 */
	protected List<Living> livings;

	/**
	 * 子のタイルに生えている草の数
	 */
	protected int grass, wet, high, temperature, visibility;

	/**
	 * このタイルのある世界
	 *
	 */
	protected World world;

	public Tile(World w, int posx, int posy, int grass, int wet, int high, int temperature, int visibility){
		this.x = posx;
		this.y = posy;
		this.wet = wet;
		this.high = high;
		this.temperature = temperature;
		this.visibility = visibility;
		livings = new ArrayList<>();
		this.grass = grass;
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

		grass += (temperature - 20)/2 + (wet - 50)/10 + (3 - high)* 2+ 10;
		if(grass > 10000)grass = 10000;
		onUpdate();

	}

	protected void onUpdate(){
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

	public int getWet() {
		return wet;
	}

	public void setWet(int wet) {
		if(wet < 0)wet = 0;
		else if(wet > 100)wet = 100;
		this.wet = wet;
	}

	public int getHigh() {
		return high;
	}

	public void setHigh(int high) {
		if(high > 5)high = 5;
		else if(high < 0)high = 0;
		this.high = high;
	}

	public int getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		if(temperature > 80)temperature = 80;
		else if(temperature < -40)temperature = -40;
		this.temperature = temperature;
	}

	public int getVisibility() {
		return visibility;
	}

	public void setVisibility(int visibility) {
		if(visibility == 0){
			this.visibility = 0;
		}else{
			this.visibility = 1;
		}
	}



}
