package seitai.world;

import seitai.Main;

/**
 * World内のある一点の座標を表す
 *
 */
public class Pos {

	/**
	 * 座標
	 * @deprecated 直接編集するのは極力避け、getter/setterメソッドを使う
	 */
	private int x, y;
	private static World world = World.getInstance();

	public Pos(int x, int y) {
		setX(x);
		setY(y);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		if(x<0)x=world.getWIDTH()-20;
		if(x>world.getWIDTH() - 1)x=20;
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		if(y<0)y=world.getHEIGHT()-20;
		if(y>world.getHEIGHT() - 1)y=20;
		this.y = y;
	}

		/**
		 * 与えられた座標にあるタイルを返す
		 * @param x
		 * @param y
		 * @return
		 */
	public static Tile getTile(int x, int y){
		if(y<0)y=0;
		if(y>world.getHEIGHT() - 1)y=world.getHEIGHT()-1;
		if(x<0)x=0;
		if(x>world.getWIDTH() - 1)x=world.getWIDTH()-1;
		return world.getTiles()[x/50][y/50];
	}

	public static Tile getTile(Pos p){
		return getTile(p.getX(), p.getY());
	}

	/**
	 * 描画用 与えられた座標が画面内に入っているかを返す
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean isinWindow(int x, int y){
		int wx = Main.getCameraPos().getX() * 50;
		int wy = Main.getCameraPos().getY() * 50;
		int cw = (int) Main.getCanvas().getWidth();
		int ch = (int) Main.getCanvas().getHeight();
		return wx <= x && x <= wx + cw && wy <= y && y <= wy + ch;
	}

	/**
	 * 描画用 ワールド座標をウィンドウ上の座標に変換する
	 * @param x
	 * @param y
	 * @return
	 */
	public static Pos toWindowPos(int x, int y){
		return new Pos(x - Main.getCameraPos().getX() * 50, y - Main.getCameraPos().getY() * 50);
	}

	public static int getDistance(int x1, int y1, int x2, int y2){
		return (int)Math.round(Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)));
	}

	public static int getDistance(Pos p1, Pos p2){
		return getDistance(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}
}
