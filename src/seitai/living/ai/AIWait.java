package seitai.living.ai;

import seitai.living.Living;

/**
 * 何もせずに待つ
 *
 */
public class AIWait extends AI {

	public AIWait(Living l) {
		super(l);
		setToolTip("待機");
	}

	@Override
	public boolean isRunnable() {
		return true;
	}

	@Override
	public void run() {
		// 何もしないのがこのクラスの使命
	}

	@Override
	public AI getCopyWithNewOwner(Living liv) {
		return new AIWait(liv);
	}

	public static AI getRandomAI(Living liv) {
		return new AIWait(liv);
	}
}