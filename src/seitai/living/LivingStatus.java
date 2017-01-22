package seitai.living;

import java.util.HashMap;

/**
 *あるLivingインスタンスの現在のステータスを表す
 *DNAの情報からLivingStatusが作られる
 *LivingStatusの情報は生成されてから変わることがあるが、
 *このクラスの情報は基本的に変わらない
 *@see seitai.living.DNA
 */
public class LivingStatus {
	private DNA dna;
	private HashMap<Integer, Integer> stats;
	/**
	 * HP:体力 LIFE:寿命
	 */
	public static final int HP = 0, HP_MAX = 8, ATTACK = 1, GUARD = 2, SPEED = 3, SIZE = 4, SPINE = 5, GREEN = 6, LIFE = 7;
	public static final int MAX_VAR_NUM = 8;
	private Integer waste = null;

	/**
	 * @param status キー、値 の組を並べて引数とする
	 */
	public LivingStatus(Integer... status) {
		stats = new HashMap<>();

		for(int i = 0; i <= MAX_VAR_NUM; i++){
			stats.put(i, 0);
		}

		dna = new DNA(status);

		set(HP_MAX, dna.get(DNA.HP) + (dna.get(DNA.SIZE) - 20) * 100);
		set(ATTACK, dna.get(DNA.ATTACK) + (dna.get(DNA.SIZE) - 20) * 3);
		set(GUARD, dna.get(DNA.GUARD) + (dna.get(DNA.SIZE) - 20) * 3);
		set(SPEED, dna.get(DNA.SPEED) + (dna.get(DNA.SIZE) - 20) * -1);
		set(SIZE, dna.get(DNA.SIZE) + (dna.get(DNA.SPINE)));
		set(SPINE, dna.get(DNA.SPINE));
		set(GREEN, dna.get(DNA.GREEN) + (dna.get(DNA.SIZE) - 20));
		set(LIFE, dna.get(DNA.LIFE));
		set(HP, get(HP_MAX) * 2 / 10);


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
	 */
	public void set(int key, int value){
		if(key < 0)key=0;
		stats.put(key, value);
	}

	public Integer getWaste(){
		if(waste == null)
			waste = (stats.get(SIZE) + stats.get(SPEED))/10;
		return waste;
	}

	public DNA getDNA(){
		return dna;
	}

	public void setDNA(DNA dna){
		this.dna = dna;
	}
}
