package seitai;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;

import javax.swing.event.MenuEvent;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seitai.living.Living;
import seitai.living.LivingStatus;
import seitai.living.ai.AI;
import seitai.living.ai.AINone;
import seitai.living.ai.AITable;
import seitai.living.eater.Eater;
import seitai.living.eater.FleshEater;
import seitai.living.eater.SuperEater;
import seitai.living.plant.Plant;
import seitai.living.spawner.Spawner;
import seitai.world.Pos;
import seitai.world.Tile;
import seitai.world.World;

/**
 * 最初に実行するクラス
 *
 */
public class Main extends Application implements Initializable {

	// GUI関係
	private Parent root;

	@FXML
	private SplitPane parentSplit;

	@FXML
	private SplitPane split;

	@FXML
	private Pane mainPanel;

	@FXML
	private ScrollPane toolPanel;

	@FXML
	private Canvas canvas;

	@FXML
	private Label numbers;

	@FXML
	private Label time;

	@FXML
	private Label timeSpeed;

	@FXML
	private Label glassesInfo;

	@FXML
	private ComboBox<EditType> editType;

	@FXML
	private TabPane graphTab;

	@FXML
	private TextField txtFieldWorld;

	@FXML
	private LineChart<String, Number> numbersChart;
	@FXML
	private LineChart<String, Number> lifeChart, attackChart, guardChart,
			speedChart, sizeChart, spineChart;

	private XYChart.Series<String, Number> numEater, numFlesh, numGrass,
			numSuper, lifeEater, lifeFlesh, lifeSuper, atkEater, atkFlesh,
			atkSuper, grdEater, grdFlesh, grdSuper, spdEater, spdFlesh,
			spdSuper, sizEater, sizFlesh, sizSuper, spnEater, spnFlesh,
			spnSuper;

	private static Canvas CANVAS;

	private static GraphicsContext graphic;

	private static Stage stage;

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
	private static  boolean isRunning = true;

	// 実行時間
	private static int runningTime = 0;

	// 時間が進むか
	private static boolean isTimePass = true;

	// 虫眼鏡で選択されている生物
	private static Living glassesSelectedLiving = null;

	private static double fps = 16;

	private static boolean doSerial = false;

	@Override
	public void start(Stage stage) throws Exception {
		load();
		Main.stage = stage;
		stage.setTitle("Seitai");
		stage.setScene(new Scene(root));
		stage.setResizable(false);
		stage.setOnCloseRequest(req -> {
			isRunning = false;
			System.out.println("closing...");
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
		for (int i = 0; i < 30; i++) {
			Living l = FleshEater.getCommonInstance(
					rand.nextInt(world.getWIDTH()),
					rand.nextInt(world.getHEIGHT()));
			world.getLivings().add(l);
		}
		for (int i = 0; i < 15; i++) {
			Living l = SuperEater.getCommonInstance(
					rand.nextInt(world.getWIDTH()),
					rand.nextInt(world.getHEIGHT()));
			world.getLivings().add(l);
		}
	}

	private void initComponents() {

		graphic = canvas.getGraphicsContext2D();
		CANVAS = canvas;

		// toolPanelもキーイベントに対応させる
		register(toolPanel);
		register(editType);
		register(graphTab);
		register(parentSplit);
		editType.requestFocus();
		editType.getItems().addAll(EditType.values());
		// //なぜ使えない
		// initSerieses("eater", numEater, lifeEater, atkEater, grdEater,
		// spdEater, sizEater, spnEater);
		//
		// initSerieses("flesheater", numFlesh, lifeFlesh, atkFlesh, grdFlesh,
		// spdFlesh, sizFlesh, spnFlesh);
		//
		String e = "eater";
		String fe = "flesheater";
		String g = "grass";
		String se = "supereater";
		numEater = initSeries(e);
		lifeEater = initSeries(e);
		atkEater = initSeries(e);
		grdEater = initSeries(e);
		spdEater = initSeries(e);
		sizEater = initSeries(e);
		spnEater = initSeries(e);

		numFlesh = initSeries(fe);
		lifeFlesh = initSeries(fe);
		atkFlesh = initSeries(fe);
		grdFlesh = initSeries(fe);
		spdFlesh = initSeries(fe);
		sizFlesh = initSeries(fe);
		spnFlesh = initSeries(fe);

		numSuper = initSeries(se);
		lifeSuper = initSeries(se);
		atkSuper = initSeries(se);
		grdSuper = initSeries(se);
		spdSuper = initSeries(se);
		sizSuper = initSeries(se);
		spnSuper = initSeries(se);

		numGrass = initSeries(g);

		numbersChart.getData().addAll(numFlesh, numEater, numGrass, numSuper);
		numbersChart.setTitle("生物数");

		lifeChart.getData().addAll(lifeFlesh, lifeEater, lifeSuper);
		lifeChart.setTitle("平均寿命");

		attackChart.getData().addAll(atkFlesh, atkEater, atkSuper);
		attackChart.setTitle("平均攻撃力");

		guardChart.getData().addAll(grdFlesh, grdEater, grdSuper);
		guardChart.setTitle("平均防御力");

		speedChart.getData().addAll(spdFlesh, spdEater, spdSuper);
		speedChart.setTitle("平均素早さ");

		sizeChart.getData().addAll(sizFlesh, sizEater, sizSuper);
		sizeChart.setTitle("平均大きさ");

		spineChart.getData().addAll(spnFlesh, spnEater, spnSuper);
		spineChart.setTitle("平均棘");

	}

	private void register(Node n) {
		n.setOnKeyPressed((KeyEvent event) -> {
			keyPressed(event);
		});
		n.setOnKeyReleased((KeyEvent event) -> {
			keyReleased(event);
		});
		n.setOnKeyTyped((KeyEvent event) -> {
			keyTyped(event);
		});
	}

	private Series<String, Number> initSeries(String name) {
		Series<String, Number> series = new Series<>();
		series.setName(name);
		return series;
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
		SuperEater.image = loadImage("res/image/living/SuperEater.png");
		Spawner.image = loadImage("res/image/living/Spawner.png");
	}

	private Image loadImage(String url) {
		return new Image(getClass().getClassLoader().getResourceAsStream(url));
	}

	/**
	 * 毎フレーム実行
	 */
	private void update() {
		// 実行時間をプラス
		if (isTimePass)
			runningTime++;

		// if(world.eater == 0 && world.flesh == 0)spawnAnimals();
		// キー入力に応じてカメラ移動
		moveCam();
		// 画面のクリア
		graphic.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		world.onUpdate(graphic);
		updateWindow(graphic);
	}

	private void updateWindow(GraphicsContext g) {
		numbers.setText("Grass: " + world.grass / 1000 + "k  Eater: "
				+ world.eater + " Flesh: " + world.flesh + " Super: "
				+ world.superE);
		int min = (runningTime / 16) / 60;
		int sec = (runningTime / 16) % 60;
		time.setText("実行時間: " + runningTime + "フレーム( " + min + "分" + sec + "秒)");
		timeSpeed.setText("時間速度: " + fps + " ( " + fps / 16 + " 倍速)");
		updateCharts();
		if (glassesSelectedLiving != null) {
			Living selected = glassesSelectedLiving;
			StringBuilder builder = new StringBuilder();
			String br = System.getProperty("line.separator");
			appendAll(builder, selected.getClass().getName(),
					(selected.isDead() ? "_Dead" : ""), br, "体力:", selected
							.getStatus().get(LivingStatus.HP), "/", selected
							.getStatus().get(LivingStatus.HP_MAX), br, "年齢:",
					selected.getAge(), br, "寿命:",
					selected.getStatus().get(LivingStatus.LIFE), br, "攻撃:",
					selected.getStatus().get(LivingStatus.ATTACK), br, "防御:",
					selected.getStatus().get(LivingStatus.GUARD), br, "大きさ:",
					selected.getStatus().get(LivingStatus.SIZE), br, "速さ:",
					selected.getStatus().get(LivingStatus.SPEED), br, "棘:",
					selected.getStatus().get(LivingStatus.SPINE), br, "ai: ",
					br);
			for (int i = AITable.AI_MAX; i > 0; i--) {
				AI ai = selected.getAI().getAI(i);
				if (!(ai instanceof AINone))
					appendAll(builder, i, ":", ai.toString(), br);
			}

			glassesInfo.setText(builder.toString());
			cameraPos = Pos.getTile(selected.getPos().getX() - 50 * 7, selected
					.getPos().getY() - 50 * 5);
			Pos p = Pos.toWindowPos(selected.getPos().getX(), selected.getPos()
					.getY());
			g.setFill(Color.PURPLE);
			g.fillRect(p.getX() - 10, p.getY() - 10, 10, 10);
		}
	}

	private void updateCharts() {
		if (isRunning && runningTime % (16 * 5) == 0) {

			numFlesh.getData().add(
					new XYChart.Data<String, Number>(Integer
							.toString(runningTime / 16), world.flesh));
			numEater.getData().add(
					new Data<String, Number>(
							Integer.toString(runningTime / 16), world.eater));
			numGrass.getData().add(
					new XYChart.Data<String, Number>(Integer
							.toString(runningTime / 16), world.grass / 1000));
			numSuper.getData().add(
					new XYChart.Data<String, Number>(Integer
							.toString(runningTime / 16), world.superE / 1000));

			lifeEater.getData().add(
					new Data<String, Number>(
							Integer.toString(runningTime / 16),
							world.eater > 0 ? (double) Eater.allLife
									/ (double) world.eater : 0.0));
			lifeFlesh.getData().add(
					new Data<String, Number>(
							Integer.toString(runningTime / 16),
							world.flesh > 0 ? (double) FleshEater.allLife
									/ (double) world.flesh : 0.0));
			lifeSuper.getData().add(
					new Data<String, Number>(
							Integer.toString(runningTime / 16),
							world.superE > 0 ? (double) SuperEater.allLife
									/ (double) world.superE : 0.0));

			atkEater.getData().add(
					new Data<String, Number>(
							Integer.toString(runningTime / 16),
							world.eater > 0 ? (double) Eater.allAtk
									/ (double) world.eater : 0.0));
			atkFlesh.getData().add(
					new Data<String, Number>(
							Integer.toString(runningTime / 16),
							world.flesh > 0 ? (double) FleshEater.allAtk
									/ (double) world.flesh : 0.0));
			atkSuper.getData().add(
					new Data<String, Number>(
							Integer.toString(runningTime / 16),
							world.superE > 0 ? (double) SuperEater.allAtk
									/ (double) world.superE : 0.0));

			grdEater.getData().add(
					new Data<String, Number>(
							Integer.toString(runningTime / 16),
							world.eater > 0 ? (double) Eater.allGrd
									/ (double) world.eater : 0.0));
			grdFlesh.getData().add(
					new Data<String, Number>(
							Integer.toString(runningTime / 16),
							world.flesh > 0 ? (double) FleshEater.allGrd
									/ (double) world.flesh : 0.0));
			grdSuper.getData().add(
					new Data<String, Number>(
							Integer.toString(runningTime / 16),
							world.superE > 0 ? (double) SuperEater.allGrd
									/ (double) world.superE : 0.0));

			spdEater.getData().add(
					new Data<String, Number>(
							Integer.toString(runningTime / 16),
							world.eater > 0 ? (double) Eater.allSpd
									/ (double) world.eater : 0.0));
			spdFlesh.getData().add(
					new Data<String, Number>(
							Integer.toString(runningTime / 16),
							world.flesh > 0 ? (double) FleshEater.allSpd
									/ (double) world.flesh : 0.0));
			spdSuper.getData().add(
					new Data<String, Number>(
							Integer.toString(runningTime / 16),
							world.superE > 0 ? (double) SuperEater.allSpd
									/ (double) world.superE : 0.0));

			sizEater.getData().add(
					new Data<String, Number>(
							Integer.toString(runningTime / 16),
							world.eater > 0 ? (double) Eater.allSiz
									/ (double) world.eater : 0.0));
			sizFlesh.getData().add(
					new Data<String, Number>(
							Integer.toString(runningTime / 16),
							world.flesh > 0 ? (double) FleshEater.allSiz
									/ (double) world.flesh : 0.0));
			sizSuper.getData().add(
					new Data<String, Number>(
							Integer.toString(runningTime / 16),
							world.superE > 0 ? (double) SuperEater.allSiz
									/ (double) world.superE : 0.0));

			spnEater.getData().add(
					new Data<String, Number>(
							Integer.toString(runningTime / 16),
							world.eater > 0 ? (double) Eater.allSpn
									/ (double) world.eater : 0.0));
			spnFlesh.getData().add(
					new Data<String, Number>(
							Integer.toString(runningTime / 16),
							world.flesh > 0 ? (double) FleshEater.allSpn
									/ (double) world.flesh : 0.0));
			spnSuper.getData().add(
					new Data<String, Number>(
							Integer.toString(runningTime / 16),
							world.superE > 0 ? (double) SuperEater.allSpn
									/ (double) world.superE : 0.0));

		}
	}

	private void appendAll(StringBuilder builder, Object... strings) {
		for (Object str : strings) {
			builder.append(str);
		}
	}

	@FXML
	protected void keyPressed(KeyEvent ev) {
		KeyCode key = ev.getCode();

		if (key == KeyCode.UP || key == KeyCode.DOWN || key == KeyCode.RIGHT
				|| key == KeyCode.LEFT)
			glassesSelectedLiving = null;

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
		switch (key) {
		case "t": // 時間停止
			isTimePass = !isTimePass;
			break;
		case "g": // 半数絶滅
			for (int i = 0; i < world.getLivings().size(); i++) {
				if (rand.nextBoolean()) {
					world.getLivings().get(i).getStatus()
							.set(LivingStatus.HP, -1000000000);
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

	@FXML
	protected void onMouseClicked(MouseEvent ev) {
		EditType type = editType.getValue();
		if (type == null)
			return;
		switch (type) {
		case Glasses:
			setSelectedLiving(ev);
			break;
		case Eraser:
			deleteLiving(ev, false);
			break;
		case FleshEater:
			int x = (int) ev.getX();
			int y = (int) ev.getY();
			x += getCameraPos().getX() * 50;
			y += getCameraPos().getY() * 50;
			world.getLivings().add(FleshEater.getCommonInstance(x, y));
			break;
		case GrassEater:
			x = (int) ev.getX();
			y = (int) ev.getY();
			x += getCameraPos().getX() * 50;
			y += getCameraPos().getY() * 50;
			world.getLivings().add(Eater.getCommonInstance(x, y));
			break;
		case BigEraser:
			deleteLiving(ev, true);
			break;
		case SuperEater:
			x = (int) ev.getX();
			y = (int) ev.getY();
			x += getCameraPos().getX() * 50;
			y += getCameraPos().getY() * 50;
			world.getLivings().add(SuperEater.getCommonInstance(x, y));
			break;
		case SpawnerE:
			x = (int) ev.getX();
			y = (int) ev.getY();
			x += getCameraPos().getX() * 50;
			y += getCameraPos().getY() * 50;
			Spawner espawner = new Spawner(x, y, 5, Eater.class);
			world.getLivings().add(espawner);
			break;
		case SpawnerFE:
			x = (int) ev.getX();
			y = (int) ev.getY();
			x += getCameraPos().getX() * 50;
			y += getCameraPos().getY() * 50;
			Spawner fespawner = new Spawner(x, y, 5, FleshEater.class);
			world.getLivings().add(fespawner);
			break;
		case SpawnerSE:
			x = (int) ev.getX();
			y = (int) ev.getY();
			x += getCameraPos().getX() * 50;
			y += getCameraPos().getY() * 50;
			Spawner sespawner = new Spawner(x, y, 5, SuperEater.class);
			world.getLivings().add(sespawner);
			break;
		default:
		}
	}

	private void setSelectedLiving(MouseEvent ev) {
		int x = (int) Math.round(ev.getX());
		int y = (int) Math.round(ev.getY());
		x += getCameraPos().getX() * 50;
		y += getCameraPos().getY() * 50;
		Tile tile = Pos.getTile(x, y);
		List<Living> list = tile.getLivings();
		boolean containsSelected = false;
		for (int i = 0; i < list.size(); i++) {
			Living l = list.get(i);
			if (l == glassesSelectedLiving) {
				containsSelected = true;
				continue;
			}
			Pos p = l.getPos();
			if (Math.abs(p.getX() - x) < 20 && Math.abs(p.getY() - y) < 20) {
				glassesSelectedLiving = l;
				return;
			}
		}
		glassesSelectedLiving = containsSelected ? glassesSelectedLiving : null;
	}

	private void deleteLiving(MouseEvent ev, boolean deleteAll) {
		int x = (int) Math.round(ev.getX());
		int y = (int) Math.round(ev.getY());
		x += getCameraPos().getX() * 50;
		y += getCameraPos().getY() * 50;
		Tile tile = Pos.getTile(x, y);
		List<Living> list = tile.getLivings();

		for (int i = 0; i < list.size(); i++) {
			Living l = list.get(i);
			Pos p = l.getPos();
			if (!deleteAll) {
				if (Math.abs(p.getX() - x) < 20 && Math.abs(p.getY() - y) < 20) {
					l.getStatus().set(LivingStatus.HP, -100000000);
					return;
				}
			} else {
				l.getStatus().set(LivingStatus.HP, -1000000000);
			}
		}

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

	@FXML
	private void loadWorld() {
		String load = txtFieldWorld.textProperty().get();
		if (load == "")return;
		if(secretCommands(load))return;
		if (!load.endsWith(".seitai")) {
			System.out.println("拡張子は.seitaiで無ければなりません");
			return;
		}
		File worldFile = new File(load);
		File propertiesFile = new File(worldFile.getParentFile().getPath() + "/" + worldFile.getName() + "_option.properties");

		if (!worldFile.exists() || !propertiesFile.exists()) {
			System.out.println("指定されたファイルが存在しません");
			return;
		}

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(worldFile))) {
			World deWorld = (World) ois.readObject();
			world = World.init(deWorld);
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}


		try(InputStream ins = new FileInputStream(propertiesFile)){
			Properties properties = new Properties();
			properties.load(ins);
			isTimePass = Boolean.valueOf(properties.getProperty("isTimePass"));
			runningTime = Integer.valueOf(properties.getProperty("runningTime"));
			doSerial = Boolean.valueOf(properties.getProperty("doSerial"));

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@FXML
	private void saveWorld() {
		String save = txtFieldWorld.textProperty().get();
		if (save == "")return;
		if(secretCommands(save))return;
		if (!save.endsWith(".seitai"))return;

		File worldFile = new File(save);
		File propertiesFile = new File(worldFile.getParentFile().getPath()  + "/"+ worldFile.getName() + "_option.properties");
		try {
			if(worldFile.exists()){
				worldFile.delete();
			}
			if(propertiesFile.exists()){
				propertiesFile.delete();
			}

			if (!worldFile.createNewFile()) {
				return;
			}
			if(!propertiesFile.createNewFile()){
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		try (ObjectOutputStream oos1 = new ObjectOutputStream(new FileOutputStream(worldFile))) {
			oos1.writeObject(world);
			oos1.flush();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		try(InputStream ins = new FileInputStream(propertiesFile)){
			Properties properties = new Properties();
			properties.load(ins);
			properties.setProperty("isTimePass", Boolean.toString(isTimePass));
			properties.setProperty("runningTime", Integer.toString(runningTime));
			properties.setProperty("doSerial", Boolean.toString(doSerial));

			try(FileOutputStream fos = new FileOutputStream(propertiesFile)){
				properties.store(fos, "settings");
			}catch(Exception e){
				e.printStackTrace();
			}

		}catch(Exception e){
			e.printStackTrace();
		}

	}

	private boolean secretCommands(String text) {
		if(!text.startsWith("cmd:"))return false;

		text = text.replace("cmd:", "");

		switch(text){
		case "serialStart":
			System.out.println("fdffd");
			Serial.init();
			break;
		default:
			return false;
		}
		return true;
	}

	public static boolean isRunning() {
		return isRunning;
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
	 *
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
	 *
	 * @return
	 */
	public static Random getRandom() {
		return rand;
	}

	/**
	 * 実行開始からのフレーム数を返す 16フレーム=1秒
	 *
	 * @return 実行時間(isTimePass() == false の時は増加しない)
	 */
	public static int getTime() {
		return runningTime;
	}

	/**
	 * 時間が経過するかを返す
	 *
	 * @return
	 */
	public static boolean isTimePass() {
		return isTimePass;
	}

	public static boolean doSerial() {
		return doSerial;
	}

}
