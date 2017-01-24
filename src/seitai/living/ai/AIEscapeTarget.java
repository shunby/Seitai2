package seitai.living.ai;

import seitai.living.Living;
import seitai.living.LivingUtil;

/**
 * targetのクラスに属するLivingから逃げる
 */
public class AIEscapeTarget extends AI {

	private Class<? extends Living> targetClass;

	private Living target;

	private int chaseTime = 0;

	public AIEscapeTarget(Living l, Class<? extends Living> target) {
		super(l);
		this.targetClass = target;
		setToolTip("逃走" + target.getSimpleName());
	}

	@Override
	public boolean isRunnable() {
		if(chaseTime != 0 && world.getLivings().contains(target)){
			return true;
		}
		target = LivingUtil.search(living, targetClass);
		chaseTime = 16 * 15;
		return target != null;
	}

	@Override
	public void run() {
		chaseTime--;
		living.escape(target.getPos());
	}

	@Override
	public AI getCopyWithNewOwner(Living liv) {
		return new AIEscapeTarget(living, targetClass);
	}

	public static AI getRandomAI(Living liv) {
		return new AIEscapeTarget(liv, LivingUtil.getRandomLivingClass());
	}

}
