package seitai.living;

import java.util.HashMap;

/**
 *生物を生成するためのDNAの情報を返す
 *この情報からLivingStatusが作られる
 *LivingStatusの情報は生成されてから変わることがあるが、
 *このクラスの情報は基本的に変わらない
 *@see seitai.living.LivingStatus
 */
public class DNA {
	private HashMap<Integer, Integer> stats;
	public static final int HP = 0, ATTACK = 1, GUARD = 2, SPEED = 3, SIZE = 4, SPINE = 5, GREEN = 6, LIFE = 7;
	public static final int MAX_VAR_NUM = 7;

	/**
	 * @param status キー、値 の組を並べて引数とする
	 */
	public DNA(Integer... status) {
		stats = new HashMap<>();
		if(status.length % 2 != 0){
			throw new IllegalArgumentException("引数の数は2の倍数でなければなりません");
		}
		for(int i = 0; i <= MAX_VAR_NUM; i++){
			stats.put(i, 0);
		}
		for(int index = 0; index < status.length; index+=2){
			stats.put(status[index], status[index+1]);
		}
	}

	/**
	 *
	 * @param key このクラスのfinalな定数の中から選ぶ
	 * @return
	 */
	public int get(int key){
		return stats.get(key);
	}
	/**
	 *
	 * @param key このクラスのfinalな定数の中から選ぶ
	 * @param value
	 */
	public void set(int key, int value){
		if(key < 0)key=0;
		stats.put(key, value);
	}
}
