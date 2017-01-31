package seitai;

import java.io.Serializable;
import java.util.Random;

public class SaveOption implements Serializable{
	// 乱数
	private static Random rand;

	// 画面が表示されているか
	private boolean isRunning;

	// 実行時間
	private static int runningTime;

	// 時間が進むか
	private static boolean isTimePass;

	public SaveOption(Random r, boolean running, int time, boolean pass) {
		rand = r;
		isRunning = running;
		runningTime = time;
		isTimePass = pass;

	}

	public static Random getRand() {
		return rand;
	}

	public static void setRand(Random rand) {
		SaveOption.rand = rand;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public static int getRunningTime() {
		return runningTime;
	}

	public static void setRunningTime(int runningTime) {
		SaveOption.runningTime = runningTime;
	}

	public static boolean isTimePass() {
		return isTimePass;
	}

	public static void setTimePass(boolean isTimePass) {
		SaveOption.isTimePass = isTimePass;
	}



}
