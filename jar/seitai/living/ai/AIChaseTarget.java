package seitai.living.ai;

import seitai.living.Living;
import seitai.living.LivingUtil;

public class AIChaseTarget extends AI{

	private Class<? extends Living> targetClass;

	private Living target;

	/**
	 * targetClassに属するLivingを追いかける
	 */
	public AIChaseTarget(Living l, Class<? extends Living> target) {
		super(l);
		this.targetClass = target;
	}

	@Override
	public boolean isRunnable() {
		target = LivingUtil.search(living, targetClass);
		return target != null;
	}

	@Override
	public void run() {
		living.go(target.getPos());
	}

}
