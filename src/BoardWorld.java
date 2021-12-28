import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BoardWorld extends World {

	private int boardWidth = 700;
	private int boardHeight = boardWidth * 6 / 7;
	private double circleRatio = (double) 4 / 5;
	private int tileWidth = boardWidth / 7;
	private int tileHeight = boardHeight / 6;
	private int board[][] = new int[6][7];
	private boolean clickable = true;
	private boolean gameType;// true - single; false - duo
	private Group g;
	private boolean debug = false;

	public BoardWorld(boolean gameType) {
		super();
		this.gameType = gameType;
		g = new Group();
		Canvas canvas = new Canvas(boardWidth, boardHeight);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.GREEN);
		gc.fillRect(0, 0, boardWidth, boardHeight);
		gc.setFill(Color.WHITE);
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 6; j++) {
				gc.fillOval(i * tileWidth + (tileWidth - tileWidth * circleRatio) / 2,
						j * tileHeight + (tileHeight - tileHeight * circleRatio) / 2, tileWidth * circleRatio,
						tileHeight * circleRatio);
			}
		}
		g.getChildren().add(canvas);
		this.getChildren().add(g);
	}

	@Override
	public void act(long now) {
		// TODO Auto-generated method stub
	}

	public int[] minimax(int[][] b, int d, int alpha, int beta, int color) {
		int[] arr = new int[2];
		if (d == 0 || gameOver(false, b) || gameOver(true, b)) {
			arr[0] = currentGameState(b);
			return arr;
		} else if (color == 2) {
			arr[0] = Integer.MIN_VALUE;
			for (int i = 0; i < board[0].length; i++) {
				if (hasMovesRemaining(i)) {
					if (debug)
						System.out.println("\nDepth: " + d);
					int eval = minimax(boardSimulator(b, i, 2), d - 1, alpha, beta, 1)[0];
					if (arr[0] < eval) {
						arr[0] = eval;
						arr[1] = i;
					}
					alpha = Math.max(alpha, eval);
					if (beta <= alpha)
						break;
				}
			}
			if (debug)
				System.out.println("best score for yellow depth " + d + ": " + arr[0] + "\n\n\n");
			return arr;
		} else {
			arr[0] = Integer.MAX_VALUE;
			for (int i = 0; i < board[0].length; i++) {
				if (hasMovesRemaining(i)) {
					if (debug)
						System.out.println("\nDepth: " + d);
					int eval = minimax(boardSimulator(b, i, 1), d - 1, alpha, beta, 2)[0];
					if (arr[0] > eval) {
						arr[0] = eval;
						arr[1] = i;
					}
					beta = Math.min(beta, eval);
					if (beta <= alpha)
						break;
				}
			}
			if (debug)
				System.out.println("best score for red depth " + d + ": " + arr[0] + "\n\n\n");
			return arr;

		}
	}

	public boolean hasMovesRemaining(int col) {
		return (board[0][col] == 0);
	}

	public int[][] duplicateBoard(int[][] b) {
		int[][] board = new int[6][7];
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				board[i][j] = b[i][j];
			}
		}
		return board;
	}

	public int[][] boardSimulator(int[][] board, int column, int col) {
		int[][] b = duplicateBoard(board);
		boolean bool = true;
		for (int i = 0; i < board.length - 1; i++) {
			if (b[i + 1][column] != 0) {
				b[i][column] = col;
				bool = false;
				break;
			}
		}
		if (bool) {
			b[board.length - 1][column] = col;
		}
		if (debug) {
			System.out.println("Board Simulator: ");
			printBoard(b);
		}

		return b;
	}

	public int currentGameState(int[][] board) {// with respect to computer
		int sum = 0;
		for (int i = 0; i < board.length; i++) {
			if (board[i][3] == 2) {
				sum += 4; // yellow center 4
				if (debug)
					System.out.println("yellow center");
			} else if (board[i][3] == 1) {
				sum -= 3; // red center 3
				if (debug)
					System.out.println("red center");
			}
		}

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (board[i][j] == 0) {

					sum -= 2 * checkSurrounding(1, i, j, 3, board); // red 2 2
					if (checkSurrounding(1, i, j, 3, board) != 0) {
						if (debug)
							System.out.println("red 2");
					}
					sum += 3 * checkSurrounding(2, i, j, 3, board); // yellow 2 3
					if (checkSurrounding(2, i, j, 3, board) != 0) {
						if (debug)
							System.out.println("yellow 2");
					}

					sum -= 10 * checkSurrounding(1, i, j, 4, board); // red 3 100
					if (checkSurrounding(1, i, j, 4, board) != 0) {
						if (debug)
							System.out.println("red 3");
					}
					sum += 15 * checkSurrounding(2, i, j, 4, board); // yellow 3 1000
					if (checkSurrounding(2, i, j, 4, board) != 0) {
						if (debug)
							System.out.println("yellow 3");
					}
					if (gameOver(false, board)) { // yellow 4 1000
						if (debug)
							System.out.println("yellow 4");
						sum += 1000;
					}
					if (gameOver(true, board)) { // red 4 500
						if (debug)
							System.out.println("red 4");
						sum -= 500;
					}
				}
			}
		}
		if (debug)
			System.out.println("Current Game State: " + sum);
		if (debug)
			System.out.println();
		return sum;

	}

	public int checkSurrounding(int color, int row, int col, int len, int[][] board) {
		int x = color;
		int[] arr = new int[8];
		for (int i = 1; i < len; i++) {
			try {
				if (board[row - i][col] == x) {
					arr[0]++;
				}
			} catch (Exception e) {
			}
			try {
				if (board[row + i][col] == x) {
					arr[1]++;
				}
			} catch (Exception e) {
			}
			try {
				if (board[row][col + i] == x) {
					arr[2]++;
				}
			} catch (Exception e) {
			}
			try {
				if (board[row][col - i] == x) {
					arr[3]++;
				}
			} catch (Exception e) {
			}
			try {
				if (board[row + i][col + i] == x) {
					arr[4]++;
				}
			} catch (Exception e) {
			}
			try {
				if (board[row - i][col + i] == x) {
					arr[5]++;
				}
			} catch (Exception e) {
			}
			try {
				if (board[row - i][col - i] == x) {
					arr[6]++;
				}
			} catch (Exception e) {
			}
			try {
				if (board[row + i][col - i] == x) {
					arr[7]++;
				}
			} catch (Exception e) {
			}
		}

		int sum = 0;
		for (int a : arr) {
			if (a == len - 1) {
				sum++;
			}
		}
		return sum;

	}

	public double[] computerDrop() {
		double[] arr = new double[4];
		/*I would add a slider to change difficulties, but there's a bug with difficulty 3 where
		 * you can put 4 in a row from the bottom and it won't  try to stop it. I know what causes the issue, 
		 * but I'm not sure how to solve it, and don't have the time to develop an elegant solution besides increasing the difficulty. 
		 */ 
		int[] minimaxOutput = minimax(board, 4, Integer.MIN_VALUE, Integer.MAX_VALUE, 2);
		int idealscore = minimaxOutput[1];
		if (debug) {
			System.out.println("\nIdeal Score Is: " + idealscore);
			System.out.println("Index of this Score is: " + minimaxOutput[1]);
		}
		// --------------------------------------------------------------------------------
		// Given the matrix, figure out what i should be
		int i = minimaxOutput[1];
//		if (debug)
//			System.out.println("\nFinding Column for Ideal Score:");
//		for (int j = 0; j < board[0].length; j++) {
//			if (currentGameState(boardSimulator(board, j, 2)) == idealscore) {
//				i = j;
//				break;
//			}
//		}
//		if (debug)
//			System.out.println("Column Found: " + i);

		// --------------------------------------------------------------------------------
		arr[0] = i * tileWidth + (tileWidth - tileWidth * circleRatio) / 2;

		boolean bool = true;
		int j;
		for (j = 0; j < board.length; j++) {
			if (board[j][i] != 0) {
				if (j != 0) {
					board[j - 1][i] = 2;
					arr[2] = i;
					arr[3] = j - 1;
					arr[1] = (j - 1) * tileHeight + (tileHeight - tileHeight * circleRatio) / 2;
					bool = false;
					break;
				}
			}
		}
		if (bool) {
			arr[1] = (board.length - 1) * tileHeight + (tileHeight - tileHeight * circleRatio) / 2;
			board[board.length - 1][i] = 2;
			arr[2] = i;
			arr[3] = board.length - 1;
		}
		if (debug) {
			System.out.println("\r\n" + "\r\n" + "    _   _________       __   ________  ______  _   __\r\n"
					+ "   / | / / ____/ |     / /  /_  __/ / / / __ \\/ | / /\r\n"
					+ "  /  |/ / __/  | | /| / /    / / / / / / /_/ /  |/ / \r\n"
					+ " / /|  / /___  | |/ |/ /    / / / /_/ / _, _/ /|  /  \r\n"
					+ "/_/ |_/_____/  |__/|__/    /_/  \\____/_/ |_/_/ |_/   \r\n"
					+ "                                                     \r\n" + "\r\n" + "" + "new turn");
		}
		return arr;

	}

	public boolean gameOver(boolean color, int[][] board) {
		int x;
		if (color) {
			x = 1;
		} else {
			x = 2;
		}
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[0].length; col++) {
				int[] arr = new int[8];
				for (int i = 0; i < 4; i++) {
					try {
						if (board[row - i][col] == x) {
							arr[0]++;
						}
					} catch (Exception e) {
					}
					try {
						if (board[row + i][col] == x) {
							arr[1]++;
						}
					} catch (Exception e) {
					}
					try {
						if (board[row][col + i] == x) {
							arr[2]++;
						}
					} catch (Exception e) {
					}
					try {
						if (board[row][col - i] == x) {
							arr[3]++;
						}
					} catch (Exception e) {
					}
					try {
						if (board[row + i][col + i] == x) {
							arr[4]++;
						}
					} catch (Exception e) {
					}
					try {
						if (board[row - i][col + i] == x) {
							arr[5]++;
						}
					} catch (Exception e) {
					}
					try {
						if (board[row - i][col - i] == x) {
							arr[6]++;
						}
					} catch (Exception e) {
					}
					try {
						if (board[row + i][col - i] == x) {
							arr[7]++;
						}
					} catch (Exception e) {
					}
				}
				for (int a = 0; a < 8; a++) {
					if (arr[a] >= 4) {
						int i = 0;
						int j = 0;
						switch (a) {
						case 0:
							i = -1;
							j = 0;
							break;
						case 1:
							i = 1;
							j = 0;
							break;
						case 2:
							i = 0;
							j = 1;
							break;
						case 3:
							i = 0;
							j = -1;
							break;
						case 4:
							i = 1;
							j = 1;
							break;
						case 5:
							i = -1;
							j = 1;
							break;
						case 6:
							i = -1;
							j = -1;
							break;
						case 7:
							i = 1;
							j = -1;
							break;
						}
						if (i != 0 || j != 0) {
//							drawWinningLine(row, col, i, j);
							return true;
						}

					}
				}

			}
		}
		return false;
	}
	
	public boolean gameOver2(boolean color, int[][] board) {
		int x;
		if (color) {
			x = 1;
		} else {
			x = 2;
		}
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[0].length; col++) {
				int[] arr = new int[8];
				for (int i = 0; i < 4; i++) {
					try {
						if (board[row - i][col] == x) {
							arr[0]++;
						}
					} catch (Exception e) {
					}
					try {
						if (board[row + i][col] == x) {
							arr[1]++;
						}
					} catch (Exception e) {
					}
					try {
						if (board[row][col + i] == x) {
							arr[2]++;
						}
					} catch (Exception e) {
					}
					try {
						if (board[row][col - i] == x) {
							arr[3]++;
						}
					} catch (Exception e) {
					}
					try {
						if (board[row + i][col + i] == x) {
							arr[4]++;
						}
					} catch (Exception e) {
					}
					try {
						if (board[row - i][col + i] == x) {
							arr[5]++;
						}
					} catch (Exception e) {
					}
					try {
						if (board[row - i][col - i] == x) {
							arr[6]++;
						}
					} catch (Exception e) {
					}
					try {
						if (board[row + i][col - i] == x) {
							arr[7]++;
						}
					} catch (Exception e) {
					}
				}
				for (int a = 0; a < 8; a++) {
					if (arr[a] >= 4) {
						int i = 0;
						int j = 0;
						switch (a) {
						case 0:
							i = -1;
							j = 0;
							break;
						case 1:
							i = 1;
							j = 0;
							break;
						case 2:
							i = 0;
							j = 1;
							break;
						case 3:
							i = 0;
							j = -1;
							break;
						case 4:
							i = 1;
							j = 1;
							break;
						case 5:
							i = -1;
							j = 1;
							break;
						case 6:
							i = -1;
							j = -1;
							break;
						case 7:
							i = 1;
							j = -1;
							break;
						}
						if (i != 0 || j != 0) {
							drawWinningLine(row, col, i, j);
							return true;
						}

					}
				}

			}
		}
		return false;
	}

	public double[] dropPieceDouble(double x, double y, boolean col) {
		if (0 < y) {
			return null;
		}
		double[] arr = new double[4];
		int i;
		boolean found = false;
		for (i = 0; i < board[0].length; i++) {
			if (x > i * tileWidth && x < i * tileWidth + tileWidth) {
				arr[0] = i * tileWidth + (tileWidth - tileWidth * circleRatio) / 2;
				found = true;
				break;
			}

		}
		if (found) {
			if (i == 7)
				i = 6;
		} else {
			return null;
		}

		int j;
		for (j = 0; j < board.length; j++) {
			if (board[j][i] != 0) {
				if (j != 0) {
					if (col) {
						board[j - 1][i] = 1;
					} else {
						board[j - 1][i] = 2;
					}
					arr[2] = i;
					arr[3] = j - 1;

					arr[1] = +(j - 1) * tileHeight + (tileHeight - tileHeight * circleRatio) / 2;
					return arr;
				} else {
					System.out.println("invalid move");
					return null;
				}

			}
		}
		arr[1] = (board.length - 1) * tileHeight + (tileHeight - tileHeight * circleRatio) / 2;
		if (col) {
			board[board.length - 1][i] = 1;
		} else {
			board[board.length - 1][i] = 2;
		}
		arr[2] = i;
		arr[3] = board.length - 1;
		return arr;
	}

	public double[] dropPieceSingle(double x, double y) {
		if (0 < y) {
			return null;
		}
		double[] arr = new double[4];
		int i;
		boolean found = false;
		for (i = 0; i < board[0].length; i++) {
			if (x > i * tileWidth && x < i * tileWidth + tileWidth) {
				arr[0] = i * tileWidth + (tileWidth - tileWidth * circleRatio) / 2;
				found = true;
				break;
			}

		}
		if (found) {
			if (i == 7)
				i = 6;
		} else {
			return null;
		}

		int j;
		for (j = 0; j < board.length; j++) {
			if (board[j][i] != 0) {
				if (j != 0) {
					board[j - 1][i] = 1;
					arr[2] = i;
					arr[3] = j - 1;
					arr[1] = (j - 1) * tileHeight + (tileHeight - tileHeight * circleRatio) / 2;
					return arr;
				} else {
					System.out.println("invalid move");
					return null;
				}

			}
		}
		arr[1] = (board.length - 1) * tileHeight + (tileHeight - tileHeight * circleRatio) / 2;

		board[board.length - 1][i] = 1;
		arr[2] = i;
		arr[3] = board.length - 1;

		return arr;

	}

	public void printBoard(int[][] board) {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (board[i][j] == 1) {
					System.out.print("[1]" + " ");
				} else if (board[i][j] == 2) {
					System.out.print("[2]" + " ");
				} else {
					System.out.print("[ ]" + " ");
				}

			}
			System.out.println();
		}
	}

	public int getCircleRadius() {
		return (int) ((tileWidth) * circleRatio / 2);
	}

	public int getTileWidth() {
		return (int) (tileWidth);
	}

	public int getBoardWidth() {
		return (int) boardWidth;
	}

	public int getBoardHeight() {
		return (int) boardHeight;
	}

	public boolean getClickable() {
		return clickable;
	}

	public void setClickable(boolean clickable) {
		this.clickable = clickable;
	}

	public boolean possibleMovesRemain() {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (board[i][j] == 0) {
					return true;
				}
			}
		}
		return false;
	}

	public int[][] getBoard() {
		return board;
	}

	public void drawWinningLine(int row, int col, int i, int j) {
		Group g2 = new Group();
		Canvas canvas = new Canvas(boardWidth, boardHeight);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc = canvas.getGraphicsContext2D();
		gc.setLineWidth(10);
		gc.strokeLine((col + 3 * j) * tileHeight + tileHeight / 2, (row + 3 * i) * tileWidth + tileWidth / 2,
				col * tileHeight + tileHeight / 2, row * tileWidth + tileWidth / 2);
		g2.getChildren().add(canvas);
		this.getChildren().add(g2);
		g2.toFront();
	}

	public boolean getGameType() {
		return gameType;
	}
}
