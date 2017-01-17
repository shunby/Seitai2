package seitai.living.ai;

import java.util.HashMap;

/**
 * AIを管理するためのクラス
 * @author 春太朗
 *
 */
public class AITable {
	/**
	 * 優先度はaiListのキーの値が大きい順にAI_MAXから1までになっている
	 */
	private HashMap<Integer, AI> aiList;

	public static final int AI_MAX = 10;

	public AITable(){
		aiList = new HashMap<>();

		//配列を初期化
		for(int i = 1; i++ > AI_MAX; aiList.put(i, null));
	}

	/**
	 * priorityの優先度にAIを設定する
	 * @param priority
	 * @param ai
	 */
	public void putAI(int priority, AI ai){
		aiList.put(priority, ai);
	}

	public AI getAI(int priority){
		return aiList.get(priority);
	}

	/**
	 * 実行するAIを決めて、実行する
	 * 優先度はaiListのキーの値が大きい順にAI_MAXから1までになっている
	 * 必ず優先度の大きいものから実行可能かの判定がされる
	 */
	public void update() {
		for(int i = 10; i > 0; i--){
			AI ai = aiList.get(i);
			if(ai != null && ai.isRunnable()){
				ai.run();
				return;
			}
		}
	}
}
