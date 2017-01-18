package seitai;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import seitai.living.Living;
import seitai.living.eater.Eater;
import seitai.living.eater.FleshEater;
import seitai.living.plant.Plant;
import seitai.world.Tile;
import seitai.world.World;


/**
 *最初に実行するクラス
 *
 */
public class Main extends Application implements Initializable {

	// GUI関係
	private Parent root;

	@FXML
	private SplitPane split;

	@FXML
	private Pane mainPanel;

	@FXML
	private Accordion toolPanel;

	@FXML
	private Canvas canvas;

	@FXML
	private Label numbers;

	@FXML
	private Label time;

	@FXML
	private Label timeSpeed;

	private static Canvas CANVAS;

	private static GraphicsContext graphic;

	// 世界
	private static World world;

	// カメラ位置(タイル)
	private static Tile cameraPos;

	// キーイベント用
	private static boolean up = false, left = false, right = false,
			down = false;

	// 乱数
	private static Random rand;

	// 画面が表示されているか
	private boolean isRunning = true;

	// 実行時間
	private static int runningTime = 0;

	//時間が進むか
	private static boolean isTimePass = true;

	private static double fps = 16;

	@Override
	public void start(Stage stage) throws Exception {
		load();
		stage.setTitle("Seitai");
		stage.setScene(new Scene(root));
		stage.setResizable(false);
		stage.setOnCloseRequest(req -> {
			isRunning = false;
			Platform.exit();
		});
		stage.show();
		initComponents();
		stage.getScene().getRoot().requestFocus();

		rand = new Random();
		world = World.init(2000, 2000);
		cameraPos = world.getTiles()[0][0];

		spawnAnimals();

		Thread th = new Thread(new MainThread());
		th.start();
	}

	/**
	 * 開始する前に動物を世界にちりばめる
	 */
	private void spawnAnimals() {
		for (int i = 0; i < 200; i++) {
			Living l = Eater.getCommonInstance(rand.nextInt(world.getWIDTH()),
					rand.nextInt(world.getHEIGHT()));
			world.getLivings().add(l);
		}
		for(int i = 0; i < 30; i++){
			Living l = FleshEater.getCommonInstance(rand.nextInt(world.getWIDTH()),
					rand.nextInt(world.getHEIGHT()));
			world.getLivings().add(l);
		}
	}

	private void initComponents() {
		graphic = canvas.getGraphicsContext2D();
		CANVAS = canvas;
		// toolPanelもキーイベントに対応させる
		toolPanel.setOnKeyPressed((KeyEvent event) -> {
			keyPressed(event);
		});
		toolPanel.setOnKeyReleased((KeyEvent event) -> {
			keyReleased(event);
		});
		toolPanel.setOnKeyTyped((KeyEvent event) ->{
			keyTyped(event);
		});
		toolPanel.getPanes().get(0).setExpanded(true);

	}

	private void load() throws IOException {
		// FXML読み込み
		URL fxml = getClass().getClassLoader().getResource(
				"res/fxml/MainWindow.fxml");
		FXMLLoader loader = new FXMLLoader(fxml, null);
		loader.setController(this);
		root = loader.load();

		// 画像等読み込み
		Tile.image = loadImage("res/image/tile/Tile.png");
		Living.image = loadImage("res/image/living/Plant.png");
		Plant.image = loadImage("res/image/living/Plant.png");
		Eater.image = loadImage("res/image/living/Eater.png");
		FleshEater.image = loadImage("res/image/living/FleshEater.png");
	}

	private Image loadImage(String url) {
		return new Image(getClass().getClassLoader().getResourceAsStream(url));
	}

	/**
	 * 毎フレーム実行
	 */
	private void update() {
		// 実行時間をプラス
		if(isTimePass)runningTime++;

		//if(world.eater == 0 && world.flesh == 0)spawnAnimals();
		// キー入力に応じてカメラ移動
		moveCam();
		// 画面のクリア
		graphic.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		world.onUpdate(graphic);
		updateWindow();
	}

	private void updateWindow() {
		numbers.setText("Grass: " +  world.grass/ 1000 + "k  Eater: " + world.eater + " Flesh: " + world.flesh);
		int min = (runningTime / 16) / 60;
		int sec = (runningTime / 16) % 60;
		time.setText("実行時間: " + runningTime + "フレーム( " + min + "分" + sec + "秒)");
		timeSpeed.setText("時間速度: " + fps + " ( " + fps / 16 + " 倍速)");
	}

	@FXML
	protected void keyPressed(KeyEvent ev) {
		KeyCode key = ev.getCode();
		switch (key) {
		case UP:
			up = true;
			break;
		case DOWN:
			down = true;
			break;
		case RIGHT:
			right = true;
			break;
		case LEFT:
			left = true;
			break;
		default:
			break;
		}
		ev.consume();
	}

	@FXML
	protected void keyReleased(KeyEvent ev) {
		KeyCode key = ev.getCode();
		switch (key) {
		case UP:
			up = false;
			break;
		case DOWN:
			down = false;
			break;
		case RIGHT:
			right = false;
			break;
		case LEFT:
			left = false;
			break;
		default:
			break;
		}
		ev.consume();
	}

	@FXML
	protected void keyTyped(KeyEvent ev) {
		String key = ev.getCharacter();
		switch(key){
		case "t":    //時間停止
			isTimePass = !isTimePass;
			break;
		case "g":    //半数絶滅
			for(int i = 0; i < world.getLivings().size(); i++){
				if(rand.nextBoolean()){
					world.getLivings().get(i).death();
				}
			}
			break;
		case "f":
			fps = fps + 2 > 500 ? 500 : fps + 2;
			break;
		case "s":
			fps = fps - 2 < 2 ? 2 : fps - 2;
			break;
		default:
			break;
		}
		ev.consume();
	}

	private void moveCam() {
		int x = cameraPos.getX();
		int y = cameraPos.getY();
		if (up) {
			y = y <= 0 ? y : y - 1;
		}
		if (down) {
			y = y >= world.getHEIGHT() / 50 - 1 ? y : y + 1;
		}
		if (right) {
			x = x >= world.getWIDTH() / 50 - 1 ? x : x + 1;
		}
		if (left) {
			x = x <= 0 ? x : x - 1;
		}
		cameraPos = world.getTiles()[x][y];
	}

	class MainThread implements Runnable {
		public void run() {
			while (true) {
				if (!isRunning)
					break;
				Platform.runLater(() -> {
					update();
				});
				try {
					Thread.sleep(Math.round(1000d / fps));// 1000/16
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}

	}

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
	/**
	 * 画面に表示される一番左上のタイルを返す
	 * @return画面左上のタイル
	 */
	public static Tile getCameraPos() {
		return cameraPos;
	}

	public static Canvas getCanvas() {
		return CANVAS;
	}

	/**
	 * すべての乱数生成に使用するRandomインスタンスを返す
	 * @return
	 */
	public static Random getRandom() {
		return rand;
	}

	/**
	 * 実行開始からのフレーム数を返す 16フレーム=1秒
	 * @return 実行時間(isTimePass() == false の時は増加しない)
	 */
	public static int getTime() {
		return runningTime;
	}

	/**
	 * 時間が経過するかを返す
	 * @return
	 */
	public static boolean isTimePass(){
		return isTimePass;
	}


}
