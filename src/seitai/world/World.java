package seitai.world;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sun.xml.internal.ws.developer.Serialization;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import seitai.living.Living;
import seitai.living.eater.Eater;
import seitai.living.eater.FleshEater;
import seitai.living.eater.SuperEater;
import seitai.living.plant.Plant;


public class World implements Serializable{

	/**
	 * 唯一のインスタンス
	 */
	private static World theWorld = null;

	/**
	 * 縦の長さ、横の長さ
	 */
	private final int WIDTH, HEIGHT;

	/**
	 * このインスタンスにあるTile
	 */
	private Tile[][] tiles;

	/**
	 * 生物
	 */
	private List<Living> livings;

	/**
	 * 生物数
	 */
	public int plant, eater, flesh, superE;
	/**
	 * 植物数(特別数が多いのでこれだけlong型)
	 */
	public long grass;

	@SuppressWarnings("serial")
	private World(int width, int height){

		//フィールドを初期化
		WIDTH = width;
		HEIGHT = height;
		livings = new ArrayList<Living>(){
			@Override
			public boolean add(Living e) {
				if(e instanceof Plant)plant++;
				if(e instanceof Eater)eater++;
				if(e instanceof FleshEater)flesh++;
				if(e instanceof SuperEater)superE++;
				return super.add(e);
			}

			@Override
			public boolean remove(Object e) {
				if(e instanceof Plant)plant--;
				if(e instanceof Eater)eater--;
				if(e instanceof FleshEater)flesh--;
				if(e instanceof SuperEater)superE--;
				return super.remove(e);
			}
		};
		tiles = new Tile[WIDTH/50][HEIGHT/50];
		for(int w = 0; w < WIDTH/50; w++){
			for(int h = 0; h < HEIGHT/50; h++){
				Tile t = new Tile(this, w, h);
				tiles[w][h] = t;
				grass += t.getGrass();
			}
		}
	}

	/**
	 * 世界の更新処理
	 * @param g
	 */
	public void onUpdate(GraphicsContext g){
		grass = 0;
		for(int w = 0; w < tiles.length ; w++){
			for(int h = 0; h < tiles[0].length; h++){
				tiles[w][h].update(g);
			}
		}


		//罫線を描く
		for(int i = 0; i <= WIDTH/50; i++){
			g.setStroke(Color.BLACK);
			g.strokeLine(i * 50, 0, i * 50, g.getCanvas().getHeight());
		}
		for(int i = 0; i <= HEIGHT/50; i++){
			g.setFill(Color.BLACK);
			g.strokeLine(0, i * 50, g.getCanvas().getWidth(), i * 50);
		}

		//Livingの更新
		Eater.allAtk = Eater.allGrd = Eater.allLife = Eater.allSiz = Eater.allSpd = Eater.allSpn = 0;
		FleshEater.allAtk = FleshEater.allGrd = FleshEater.allLife = FleshEater.allSiz = FleshEater.allSpd = FleshEater.allSpn = 0;
		SuperEater.allAtk = SuperEater.allGrd = SuperEater.allLife = SuperEater.allSiz = SuperEater.allSpd = SuperEater.allSpn = 0;

		for(int i = 0; i < livings.size(); i++){
			Living l = livings.get(i);
			if(l.isDead()){
				livings.remove(i);
				continue;
			}
			livings.get(i).update(g);
		}
	}

	/**
	 * 唯一のWorldインスタンスを生成する
	 * @param width
	 * @param height
	 * @return
	 */
	public static World init(int width, int height){
		theWorld = new World(width, height);
		return theWorld;
	}

	public static World init(World newWorld){
		theWorld = newWorld;
		return theWorld;
	}

	/**
	 * 唯一のWorldインスタンスを返す
	 * @return
	 */
	public static World getInstance(){
		if(theWorld == null){
			try {
				throw new Exception("インスタンスがまだありません。Initメソッドで初期化してください。");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return theWorld;
	}

	public int getWIDTH() {
		return WIDTH;
	}

	public int getHEIGHT() {
		return HEIGHT;
	}

	public Tile[][] getTiles() {
		return tiles;
	}

	public List<Living> getLivings(){
		return livings;
	}

}
