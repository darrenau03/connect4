import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Driver extends Application {

	private Stage stage;
	private Button one;
	private Button two;
	private Button mainMenu;
	private Button restart;
	private BoardWorld bw;
	private boolean turn;
	private Checker c;
	private boolean followMouse;
	private double[] arr;
	private double screenWidth;
	private double screenHeight;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage s) throws Exception {
		screenWidth = 1920;
		screenHeight = 1080;
		stage = s;
		stage.setScene(homeScreen());
//		stage.setScene(gameOverScreen(true));
		stage.show();
	}

	public Scene gameOverScreen(int col, BoardWorld bw) {
		Group rootnode = new Group();

		rootnode.getChildren().add(bw);
		bw.relocate(screenWidth / 2 - bw.getBoardWidth() / 2, screenHeight / 2 - bw.getBoardHeight() / 2);
		bw.toFront();

		Image titleImage = new Image("gameover.png", 500, 200, true, true);
		ImageView title = new ImageView(titleImage);
		rootnode.getChildren().add(title);
		title.relocate(screenWidth / 2 - titleImage.getWidth() / 2, 10);

		String s;
		if (col == 0) {
			s = "Nobody";
		} else if (col == 1) {
			s = "Red";
		} else {
			s = "Yellow";
		}
		Label l = new Label(s + " Won");
		l.setFont(new Font("Serif", 50));
		rootnode.getChildren().add(l);
		l.relocate(screenWidth / 2 - 100, 900);

		mainMenu = new Button("Return to Main Menu");
		mainMenu.setMinWidth(100);
		mainMenu.setMinHeight(100);
		mainMenu.setFont(new Font("Restart", 30));

		rootnode.getChildren().add(mainMenu);
		mainMenu.relocate(screenWidth / 2 - l.getWidth() / 2 - 500, 900);

		restart = new Button("Restart");
		restart.setMinWidth(100);
		restart.setMinHeight(100);
		restart.setFont(new Font("Arial", 30));
		rootnode.getChildren().add(restart);
		restart.relocate(screenWidth / 2 - l.getWidth() / 2 + 300, 900);

		Scene scene = new Scene(rootnode, screenWidth, screenHeight);

		GeneralButtonHandler gbh = new GeneralButtonHandler();
		mainMenu.setOnMouseClicked(gbh);
		restart.setOnMouseClicked(gbh);
		return scene;
	}

	public Scene homeScreen() {
		BorderPane rootnode = new BorderPane();

		BorderPane top = new BorderPane();
		rootnode.setTop(top);
		Image title = new Image("connect4.png", 1000, 1000, true, true);
		top.setCenter(new ImageView(title));

		HBox mid = new HBox();
		mid.setAlignment(Pos.CENTER);

		one = new Button("1 Player");
		one.setMinWidth(100);
		one.setMinHeight(100);
		one.setFont(new Font("Arial", 50));
		one.setPadding(new Insets(50));
		mid.getChildren().add(one);

		two = new Button("2 Player");
		two.setMinWidth(100);
		two.setMinHeight(100);
		two.setFont(new Font("Arial", 50));
		two.setPadding(new Insets(50));
		mid.getChildren().add(two);

		HBox.setMargin(one, new Insets(100));
		HBox.setMargin(two, new Insets(100));
		rootnode.setCenter(mid);

		Scene scene = new Scene(rootnode, screenWidth, screenHeight);

		GeneralButtonHandler gbh = new GeneralButtonHandler();
		one.setOnMouseClicked(gbh);
		two.setOnMouseClicked(gbh);
		return scene;
	}

	private class GeneralButtonHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {

			if (event.getSource() != mainMenu) {
				Group rootnode1 = new Group();
				c = new Checker(true);
				if (event.getSource() == one) {
					bw = new BoardWorld(true);
					bw.setOnMouseClicked(new MouseClickedHandlerSingle());
				} else if (event.getSource() == two) {
					turn = true;
					bw = new BoardWorld(false);
					bw.setOnMouseClicked(new MouseClickedHandlerDouble());
				} else if (event.getSource() == restart) {
					bw = new BoardWorld(bw.getGameType());
					if (bw.getGameType()) {
						bw.setOnMouseClicked(new MouseClickedHandlerSingle());
					} else {
						turn = true;
						bw.setOnMouseClicked(new MouseClickedHandlerDouble());
					}
				}
				bw.setOnMouseMoved(new MouseMovedHandler());
				followMouse = true;
				bw.start();
				bw.add(c);
				rootnode1.getChildren().add(bw);
				bw.relocate(screenWidth / 2 - bw.getBoardWidth() / 2, screenHeight / 2 - bw.getBoardHeight() / 2);
				stage.setScene(new Scene(rootnode1, screenWidth, screenHeight));
			} else {
				stage.setScene(homeScreen());
			}
		}
	}

	private class MouseClickedHandlerSingle implements EventHandler<MouseEvent> {

		public void handle(MouseEvent event) {
			if (bw.getClickable()) {
				arr = bw.dropPieceSingle(event.getX(), event.getY());
				if (arr != null) {
					dropCurrentChecker(c);
					if (bw.gameOver2(true, bw.getBoard())) {
						prepareEndGame(1);
					} else if (!bw.possibleMovesRemain()) {
						prepareEndGame(0);
					} else {
						createNewCheckerAtMouse(true, event);
						prepareComputerDrop();
					}
				}

			}

		}
	}

	private class GameOverDelay implements EventHandler<WorkerStateEvent> {
		private int b;

		public GameOverDelay(int b) {
			this.b = b;
		}

		@Override
		public void handle(WorkerStateEvent event) {
			stage.setScene(gameOverScreen(b, bw));
		}

	}

	private class DropDelay implements EventHandler<WorkerStateEvent> {

		@Override
		public void handle(WorkerStateEvent start) {

			arr = bw.computerDrop();
			Checker c2 = new Checker(false);
			dropCurrentChecker(c2);
			bw.add(c2);
			if (bw.gameOver2(false, bw.getBoard())) {
				prepareEndGame(2);
			} else if (!bw.possibleMovesRemain()) {
				prepareEndGame(0);
			} else {
				bw.add(c);
				followMouse = true;
				bw.setClickable(true);
			}
		}
	}

	private class MouseClickedHandlerDouble implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			if (bw.getClickable()) {
				arr = bw.dropPieceDouble(event.getX(), event.getY(), turn);
				if (arr != null) {
					dropCurrentChecker(c);
					if (bw.gameOver2(turn, bw.getBoard())) {
						if (turn) {
							prepareEndGame(1);
						} else {
							prepareEndGame(2);
						}

					} else if (!bw.possibleMovesRemain()) {
						prepareEndGame(0);
					} else {
						createNewCheckerAtMouse(!turn, event);
						bw.add(c);
					}
					turn = !turn;
				}
			}
		}

	}

	private class MouseMovedHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			if (followMouse) {
				c.setX(event.getX() - c.getWidth() / 2);
				c.setY(event.getY() - c.getHeight() / 2);
			}
		}

	}

	public void prepareComputerDrop() {
		Task<Void> sleeper = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				try {
					bw.setClickable(false);
					Thread.sleep(1000);
				} catch (InterruptedException e) {

				}
				return null;
			}
		};
		sleeper.setOnSucceeded(new DropDelay());
		new Thread(sleeper).start();
	}

	public void createNewCheckerAtMouse(boolean color, MouseEvent event) {
		c = new Checker(color);
		followMouse = true;
		c.setX(event.getX() - c.getWidth() / 2);
		c.setY(event.getY() - c.getHeight() / 2);
	}

	public void dropCurrentChecker(Checker check) {
		followMouse = false;
		check.setH(arr[1] - (bw.getTileWidth() / 2 - bw.getCircleRadius()));
		check.setX(arr[0] - (bw.getTileWidth() / 2 - bw.getCircleRadius()));
		check.canMove();
	}

	public void prepareEndGame(int winner) {
		Task<Void> sleeper = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					bw.setClickable(false);
					Thread.sleep(1000);
				} catch (InterruptedException e) {

				}
				return null;
			}
		};
		sleeper.setOnSucceeded(new GameOverDelay(winner));
		new Thread(sleeper).start();
	}

}
