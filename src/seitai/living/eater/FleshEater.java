package seitai.living.eater;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import seitai.living.Living;
import seitai.living.LivingStatus;
import seitai.living.LivingUtil;
import seitai.living.ai.AIChaseTarget;
import seitai.living.ai.AIEscapeTarget;
import seitai.living.ai.AIRandomMove;
import seitai.world.Pos;

/**
 * 肉食動物 草食動物を食べる
 *
 */
public class FleshEater extends Living {

	public static Image image;

	public static int allLife, allAtk, allGrd, allSpd, allSiz, allSpn;


	protected FleshEater(int x, int y, int hpmax, int attack, int guard, int speed,
			int size, int spine) {
		super(x, y, hpmax, attack, guard, speed, size, spine, 0);
		//ai.putAI(7, new AIChaseTarget(this, Eater.class));
		ai.putAI(7, new AIEscapeTarget(this, SuperEater.class));
		ai.putAI(6, new AIChaseTarget(this, Eater.class));
		ai.putAI(5, new AIRandomMove(this));
	}

	@Override
	protected void onUpdate() {

		allAtk += status.get(LivingStatus.ATTACK);
		allGrd += status.get(LivingStatus.GUARD);
		allLife += status.get(LivingStatus.LIFE);
		allSiz += status.get(LivingStatus.SIZE);
		allSpd += status.get(LivingStatus.SPEED);
		allSpn += status.get(LivingStatus.SPINE);

		//衝突判定
		Living col = LivingUtil.getCollide(this);
		if(col != null && col != this){
			if(col instanceof FleshEater && isFull()){
				world.getLivings().add(bear(col));
				status.set(LivingStatus.HP, status.get(LivingStatus.HP_MAX) * 4 /10);
			}else if(col instanceof Eater){
				LivingUtil.eat(this, col);
			}
		}
	}

	@Override
	protected void draw(GraphicsContext g) {
		Pos p = Pos.toWindowPos(pos.getX(), pos.getY());
		int siz = status.get(LivingStatus.SIZE);

		int spine = status.get(LivingStatus.SPINE);
		if(spine != 0){
			g.setStroke(Color.YELLOW);
			g.strokeLine(p.getX() - spine/2 - siz/2, p.getY() - spine/2 - siz/2, p.getX() + spine/2 + siz/2, p.getY() + spine/2 + siz/2);
			g.strokeLine(p.getX() + spine/2 + siz/2, p.getY() - spine/2 - siz/2, p.getX() - spine/2 - siz/2, p.getY() + spine/2 + siz/2);
		}

		g.drawImage(image, p.getX() - siz / 2, p.getY() - siz / 2, siz, siz);


		g.setFill(Color.YELLOW);
		g.fillText(String.valueOf(status.get(LivingStatus.HP)), p.getX() - siz
				/ 2, p.getY() - siz / 2);
		if(isFull()){
			g.setFill(Color.RED);
			g.fillRect(p.getX() + siz/2 - 3, p.getY() - siz/2 - 3, 3, 3);
		}
	}

	@Override
	public Living bear(Living l) {
		Living liv =  new FleshEater(pos.getX(), pos.getY(), 0, 0,0,0,0,0);
		liv.setStatus(LivingUtil.makeChildStatus(this, l));
		LivingUtil.getChildAI(this, l, liv);
		return liv;
	}


	public static Living getCommonInstance(int x, int y){
		return new FleshEater(x, y, 10000, 50, 10, 10, 20, 0);
	}

}
