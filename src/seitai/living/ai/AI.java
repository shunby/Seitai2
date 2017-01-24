package seitai.living.ai;

import java.util.Random;

import seitai.Main;
import seitai.living.Living;
import seitai.world.World;

public abstract class AI {

	/**
	 * このAIを持つLiving
	 */
	protected Living living;

	/**
	 * 乱数生成用
	 */
	protected Random rand;

	/**
	 * livingのworld
	 */
	protected World world;

	private String toolTip;

	public AI(Living l){
		living = l;
		rand = Main.getRandom();
		world = World.getInstance();
	}

	/**
	 * 実行条件を満たしているか
	 * @return
	 */
	public abstract boolean isRunnable();

	/**
	 * このAIの処理を実行する
	 */
	public abstract void run();

	/**
	 * 違うLiivingで同じ種類のAIを作る
	 */
	public abstract AI getCopyWithNewOwner(Living liv);

	@Override
	public String toString() {
		return toolTip;
	}

	protected void setToolTip(String str){
		toolTip = str;
	}

}
