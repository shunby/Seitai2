package seitai.living;

import java.util.Random;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import seitai.Main;
import seitai.living.ai.AITable;
import seitai.world.Pos;
import seitai.world.Tile;
import seitai.world.World;

/**
 * ある一匹の生物を表す
 */
public abstract class Living {
	public static Image image;
	/**
	 *  現在いるタイル
	 */
	protected Tile tile;

	/**
	 *  現在の座標
	 */
	protected Pos pos;

	/**
	 *  ステータス
	 */
	protected LivingStatus status;

	/**
	 *  年齢(フレーム)
	 */
	protected int age = 0;

	/**
	 *  自分のいる世界
	 */
	protected World world;

	/**
	 * 乱数生成用
	 */
	protected Random rand;

	/**
	 * AI
	 */
	protected AITable ai;

	/**
	 * 死んでいるか
	 */
	protected boolean isDead;

	//TODO:引数が汚い
	protected Living(int x, int y, int hpmax, int attack, int guard, int speed, int size, int spine, int green) {
		pos = new Pos(x, y);
		tile = Pos.getTile(pos.getX(), pos.getY());
		status = new LivingStatus(DNA.HP, hpmax, DNA.ATTACK, attack, DNA.GUARD, guard, DNA.SPEED, speed, DNA.SIZE, size,
				DNA.SPINE, spine, DNA.GREEN, green, DNA.LIFE, (int) (16.0 * 50));
		world = World.getInstance();
		rand = Main.getRandom();
		tile.getLivings().add(this);
		ai = new AITable();
	}
	/**
	 * 位置フレームに一回、World#onUpdate(GraphicContext){@link seitai.world.World#onUpdate(GraphicsContext)}から呼び出される
	 *このLivingインスタンスの更新処理を行う
	 *Main.isTimePass(){@link Main#isTimePass()} == false の時はdraw(){@link Living#draw(GraphicsContext)}のみ実行される
	 *
	 *draw(){@link #draw(GraphicsContext)}が描画処理を行い、onUpdate(){@link #onUpdate()}がAIによる行動などを行い、upDate(){@link #update(GraphicsContext)}がそれらをまとめる
	 * @param g 画面描画用のGraphicContext
	 */
	public void update(GraphicsContext g) {
		if (Pos.isinWindow(pos.getX(), pos.getY()))
			draw(g);
		if (!Main.isTimePass())
			return;
		age++;
		onUpdate();
		ai.update();
		tile.getLivings().remove(this);
		tile = Pos.getTile(pos.getX(), pos.getY());
		tile.getLivings().add(this);

		status.set(LivingStatus.HP, status.get(LivingStatus.HP) - status.getWaste());

		int spine = status.get(LivingStatus.SPINE);
		if (spine != 0) {
			Living col = LivingUtil.getCollide(this);
			if (col != null && col != this) {
				col.getStatus().set(LivingStatus.HP, col.getStatus().get(LivingStatus.HP) - spine);
			}
		}

		// 死亡判定
		if (status.get(LivingStatus.HP) <= 0 || age > status.get(LivingStatus.LIFE)) {
			death();
		}

		if (status.get(LivingStatus.HP) > status.get(LivingStatus.HP_MAX)) {
			status.set(LivingStatus.HP, status.get(LivingStatus.HP_MAX));
		}

	}

	/**
	 * この生物のAIなどによる更新処理を行う
	 * draw(){@link #draw(GraphicsContext)}が描画処理を行い、onUpdate(){@link #onUpdate()}がAIによる行動などを行い、upDate(){@link #update(GraphicsContext)}がそれらをまとめる
	 */
	protected void onUpdate() {

	}
	/**
	 * この生物の描画処理を行う
	 *
	 * draw(){@link #draw(GraphicsContext)}が描画処理を行い、onUpdate(){@link #onUpdate()}がAIによる行動などを行い、upDate(){@link #update(GraphicsContext)}がそれらをまとめる
	 * @param g 画面描画用のGraphicContext
	 */
	protected void draw(GraphicsContext g) {
		Pos p = Pos.toWindowPos(pos.getX(), pos.getY());
		int siz = status.get(LivingStatus.SIZE);
		g.drawImage(image, p.getX() - siz / 2, p.getY() - siz / 2, siz, siz);
		g.setFill(Color.RED);
		g.fillText(String.valueOf(status.get(LivingStatus.HP)), p.getX() - siz / 2, p.getY() - siz / 2);
	}

	/**
	 * このLivingを殺す
	 */
	public void death() {
		onDeath();
		world.getLivings().remove(this);
		tile.getLivings().remove(this);
		isDead = true;
	}

	/**
	 * 目的地に近づく
	 * @param tx 目標x座標
	 * @param ty 目標y座標
	 */
	public void go(int tx, int ty) {
		int x = pos.getX();
		int y = pos.getY();

		double vx = tx - x;// 移動量(x)
		double vy = ty - y;// 移動量(y)
		double length = Math.sqrt(vx * vx + vy * vy);
		if (length < 0.0001d) {
			length = 1.0d;
		}
		vx /= length;
		vy /= length;
		double speed = status.get(LivingStatus.SPEED);
		if(speed < 0)speed = 0;
		vx *= (speed/ 3.0);
		vy *= (speed / 3.0);
		pos.setX((int) Math.round(x + vx));
		pos.setY((int) Math.round(y + vy));

		// 移動量に応じて体力を減らす
		status.set(LivingStatus.HP,
				(int) (status.get(LivingStatus.HP) - status.getWaste() * (Math.abs(vx) * Math.abs(vy)) / 8));
	}

	/**
	 * 目的地から遠ざかる
	 * @param tx 目標x座標
	 * @param ty 目標y座標
	 */
	public void escape(int tx, int ty) {
		int x = pos.getX();
		int y = pos.getY();

		double vx = tx - x;// 移動量(x)
		double vy = ty - y;// 移動量(y)
		double length = Math.sqrt(vx * vx + vy * vy);
		if (length < 0.0001d) {
			length = 1.0d;
		}
		vx /= length;
		vy /= length;
		vx *= ((double) status.get(LivingStatus.SPEED) / 3.0);
		vy *= ((double) status.get(LivingStatus.SPEED) / 3.0);
		pos.setX((int) Math.round(x - vx));
		pos.setY((int) Math.round(y - vy));

		// 移動量に応じて体力を減らす
		status.set(LivingStatus.HP,
				(int) (status.get(LivingStatus.HP) - status.getWaste() * (Math.abs(vx) * Math.abs(vy)) / 8));
	}

	/**
	 * 座標を変更する
	 * @param p
	 */
	public void go(Pos p) {
		go(p.getX(), p.getY());
	}

	public void escape(Pos p) {
		escape(p.getX(), p.getY());
	}

	/**
	 * 繁殖する
	 * @param l 相手
	 * @return
	 */
	public abstract Living bear(Living l);

	/**
	 * このLivingの死亡時の処理
	 */
	protected void onDeath() {

	}

	/**
	 * 満腹かどうかを返す
	 * @return
	 */
	public boolean isFull() {
		return status.get(LivingStatus.HP) > status.get(LivingStatus.HP_MAX) * 6 / 10;
	}
	/**
	 * 現在いるタイルを返す
	 * @return
	 */
	public Tile getTile() {
		return tile;
	}
	/**
	 * 現在いる座標を返す
	 * @return
	 */
	public Pos getPos() {
		return pos;
	}
	/**
	 * ステータスを返す
	 * @return
	 */
	public LivingStatus getStatus() {
		return status;
	}
	/**
	 * ステータスを変更する
	 * @param s
	 */
	public void setStatus(LivingStatus s) {
		this.status = s;
	}

	/**
	 * 生成されてからの時間
	 */
	public int getAge(){
		return age;
	}

	/**
	 *	全く新しいインスタンスを生成する
	 */
	public static Living getCommonInstance(int x, int y){
		return new  Living(x, y, 10000, 20, 10, 10, 10, 0, 0) {

			@Override
			public Living bear(Living l) {
				// TODO 自動生成されたメソッド・スタブ
				return null;
			}
		};
	}

	/**
	 * 死んでいるか
	 */
	public boolean isDead(){
		return isDead;
	}
}
