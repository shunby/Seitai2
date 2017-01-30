package seitai.living.ai;

import java.io.Serializable;

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
		setToolTip("追跡" + target.getSimpleName());
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

	@Override
	public AI getCopyWithNewOwner(Living liv) {
		return new AIChaseTarget(liv, targetClass);
	}

	public static AI getRandomAI(Living liv) {
		return new AIChaseTarget(liv, LivingUtil.getRandomLivingClass());
	}

}
