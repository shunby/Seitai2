package seitai.living.ai;

import seitai.living.Living;

public class AINone extends AI {

	public AINone(Living l) {
		super(l);
	}

	@Override
	public boolean isRunnable() {
		return false;
	}

	@Override
	public void run() {

	}

	@Override
	public AI getCopyWithNewOwner(Living liv) {
		// TODO 自動生成されたメソッド・スタブ
		return new AINone(liv);
	}

}
