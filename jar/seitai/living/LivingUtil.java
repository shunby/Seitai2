package seitai.living;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import seitai.Main;
import seitai.world.Pos;
import seitai.world.Tile;

public class LivingUtil {
	/**
	 *  メモ 色
	 *  毎回色を作っていると遅くなる
	 */
	private static Image[][][] images = new Image[256][256][256];

	/**
	 *  描画用 画像の白い場所を指定された色に変えた画像を作成する
	 * @param img
	 * @param red
	 * @param green
	 * @param blue
	 * @return
	 */
	public static Image createChangedImage(Image img, int red, int green, int blue) {
		if(red > 255)red = 255;
		if(red < 0)red = 0;
		if(green > 255)green = 255;
		if(green < 0)green = 0;
		if(blue > 255)blue = 255;
		if(blue < 0)blue = 0;

		if (images[red][green][blue] != null)
			return images[red][green][blue];

		// 画像サイズを取得
		int width = (int) img.getWidth();
		int height = (int) img.getHeight();

		// 複製したイメージを作成
		WritableImage wImg = new WritableImage(width, height);
		PixelWriter writer = wImg.getPixelWriter();

		// ピクセル配列(フォーマットはARGBの順)を取得
		WritablePixelFormat<IntBuffer> format = WritablePixelFormat.getIntArgbInstance();
		int[] pixels = new int[width * height];
		img.getPixelReader().getPixels(0, 0, width, height, format, pixels, 0, width);

		// ピクセル操作
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++) {
				// ピクセルを取得
				int index = (y * width) + x;
				int pixel = pixels[index];

				// a成分を作成
				int a = pixel >> 24;

				// 赤成分を作成
				int r = ((pixel >> 16) & 0xFF);

				// 緑成分を作成
				int g = ((pixel >> 8) & 0xFF);

				// 青成分を作成
				int b = (pixel & 0xFF);

				if (r == g && g == b && r == 0xFF) {
					r = red;
					g = green;
					b = blue;
				}

				// ピクセルを設定
				pixel = (a << 24) | (r << 16) | (g << 8) | b;
				pixels[index] = pixel;
			}

		// ピクセル配列を設定
		writer.setPixels(0, 0, width, height, format, pixels, 0, width);

		images[red][green][blue] = wImg;
		return wImg;
	}

	/**
	 * 渡された二つのLivingStatusから子供のステータスを生成して返す
	 * @param l1
	 * @param l2
	 * @return
	 */
	public static LivingStatus makeChildStatus(LivingStatus l1, LivingStatus l2){
    	LivingStatus child;

    	Random rand = Main.getRandom();
    	int changeProbability = 10;
    	boolean isSuddenChange = rand.nextInt(100) < changeProbability;

    	int guard = getChangedValue(l1.get(LivingStatus.GUARD), l2.get(LivingStatus.GUARD), isSuddenChange, 1, 3);

    	int attack = getChangedValue(l1.get(LivingStatus.ATTACK), l2.get(LivingStatus.ATTACK), isSuddenChange, 1, 3);

    	int green = 0;
    	if(l1.get(LivingStatus.GREEN) != 0 && l2.get(LivingStatus.GREEN) != 0)
    		green = getChangedValue(l1.get(LivingStatus.GREEN), l2.get(LivingStatus.GREEN), isSuddenChange, 1, 3);

    	int size = getChangedValue(l1.get(LivingStatus.SIZE), l2.get(LivingStatus.SIZE), isSuddenChange, 1, 3);

    	int speed = getChangedValue(l1.get(LivingStatus.SPEED), l2.get(LivingStatus.SPEED), isSuddenChange, 1, 3);

    	int spine = getChangedValue(l1.get(LivingStatus.SPINE), l2.get(LivingStatus.SPINE), isSuddenChange, 1, 3);

    	int life = getChangedValue(l1.get(LivingStatus.LIFE), l2.get(LivingStatus.LIFE), isSuddenChange, 16 * 5, 1 * 15);

    	if(size < 3)size = 3;
    	if(speed < 4)speed = 4;

    	child = new LivingStatus(
    			DNA.HP, size * 500,
    			DNA.GUARD, guard,
    			DNA.ATTACK, attack,
    			DNA.GREEN, green,
    			DNA.SIZE, size,
    			DNA.SPEED, speed,
    			DNA.SPINE,spine,
    			DNA.LIFE, life
    			);
    	return child;
    }

    private static int getChangedValue(int status1, int status2, boolean isSuddenChange, int normalChangeRange, int suddenChangeRange){
    	Random rand = Main.getRandom();
    	int status = rand.nextBoolean() ? status1: status2;
    	int defaultStatus = status;
    	if(!isSuddenChange && status <= 0)return 0;
    	status = isSuddenChange ? status + rand.nextInt(normalChangeRange * 2 + 1) - normalChangeRange : status + rand.nextInt(suddenChangeRange * 2 + 1) - suddenChangeRange;
    	status = defaultStatus != 0 && status <= 0 && !isSuddenChange ? 1: status;
    	return status < 0 ? 0 : status;
    }

    /**
     * lがtargetを捕食する
     * targetの体力をある程度まで減らせたら完全に捕食できたことになる
     * @param l
     * @param target
     * @return 完全に捕食できたらtrueを返す そうでなければfalseを返す
     */
  	public static boolean eat(Living l, Living target){
  		LivingStatus target_stat = target.getStatus();
  		int damage = l.getStatus().get(LivingStatus.ATTACK) - target_stat.get(LivingStatus.GUARD);
  		damage = damage < 0 ? 0 : damage;

  		if(target_stat.get(LivingStatus.HP) < target_stat.get(LivingStatus.HP_MAX) * 2 / 10){
  			l.getStatus().set(LivingStatus.HP, l.getStatus().get(LivingStatus.HP) + target.getStatus().get(LivingStatus.HP_MAX));
  			target.death();
  			return true;
  		}else{
  			target_stat.set(LivingStatus.HP, target_stat.get(LivingStatus.HP) - damage);
  			l.getStatus().set(LivingStatus.HP, l.getStatus().get(LivingStatus.HP) + damage);
  			return false;
  		}
  	}

  	/**
  	 * livingとtargetが衝突しているかを返す
  	 */
  	public static boolean isCollide(Living living,Living target){
		Tile t = target.getTile();
		//タイルが違えば衝突していない(若干ズレが有るが気にしないことにする)
		if(t != living.getTile())return false;

		int x  = living.getPos().getX();
		int y  = living.getPos().getY();
		int siz = living.getStatus().get(LivingStatus.SIZE);
		int tsiz = target.getStatus().get(LivingStatus.SIZE);
		int tx = target.getPos().getX();
		int ty = target.getPos().getY();
		//二者の距離
		double distance = Math.sqrt((x-tx)*(x-tx) + (y-ty)*(y-ty) );
		return distance < siz * 2 + tsiz * 2 && target != living;
	}

  	/**
  	 * livと衝突しているLivingを返す　無ければnullを返す
  	 * @param liv
  	 * @return
  	 */
	public static Living getCollide(Living liv){
		List<List<Living>> list = new ArrayList<>();
		list.add(liv.getTile().getLivings());

		for(int list_i = 0; list_i < list.size(); list_i++){
			for(int i = 0; i<list.get(list_i).size(); i++){
				Living l = list.get(list_i).get(i);
				if(isCollide(liv, l) && l != liv){
					//接触していればそれを返す
					return l;
				}
			}
		}
		//見つからなければnullを返す
		return null;
	}

	/**
	 * livingの周囲1タイルのfindClassのインスタンスを返す 無ければnullを返す
	 * @param living
	 * @param findClass
	 * @return
	 */
	public static Living search(Living living, Class<? extends Living> findClass){
		Pos p = living.getPos();
		Set<Tile> tile = new HashSet<>();
		tile.add(living.getTile());
		tile.add(Pos.getTile(p.getX() + 50, p.getY()));
		tile.add(Pos.getTile(p.getX() - 50, p.getY()));
		tile.add(Pos.getTile(p.getX(), p.getY() + 50));
		tile.add(Pos.getTile(p.getX(), p.getY() - 50));
		List<Living> livings = new ArrayList<>();

		tile.forEach((Tile t)->{
			t.getLivings().forEach((Living l)->{
				if(l.getClass() == findClass){
					livings.add(l);
				}
			});
		});
		livings.sort((Living o1, Living o2)-> {
			int d1 = Pos.getDistance(living.getPos(), o1.getPos());
			int d2 = Pos.getDistance(living.getPos(), o2.getPos());
			return d1 - d2;
		});
		Living l = livings.isEmpty() ? null :livings.get(0);
		if(l != null){
			if(Pos.getDistance(living.getPos(), l.getPos()) > 150)l = null;
		}
		return l;
	}

}
