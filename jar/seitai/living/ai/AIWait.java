package seitai.living.ai;

import seitai.living.Living;

/**
 * 何もせずに待つ
 *
 */
public class AIWait extends AI {

	public AIWait(Living l) {
		super(l);
	}

	@Override
	public boolean isRunnable() {
		return true;
	}

	@Override
	public void run() {
		// 何もしないのがこのクラスの使命
	}
}