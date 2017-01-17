package seitai.living.ai;

import seitai.living.Living;
import seitai.world.Pos;

/**
 * ランダムに動く
 */
public class AIRandomMove extends AI {

	//ランダム移動の目的地
	private Pos targetPos;


	public AIRandomMove(Living l) {
		super(l);
	}

	@Override
	public boolean isRunnable() {
		return true;
	}

	@Override
	public void run() {
		if(targetPos == null || rand.nextInt(50) == 0){
			targetPos = new Pos(living.getPos().getX() + rand.nextInt(501) - 250, living.getPos().getY() + rand.nextInt(501) - 250);
		}
		living.go(targetPos.getX(), targetPos.getY());
	}

}
