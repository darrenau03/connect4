import javafx.scene.image.Image;

public class Checker extends Actor{
	
	private double h = 0;
	private int vel;
	private int accel = 3;
	private boolean canMove;
	private boolean hitBottom;
	private boolean color;
	
	public Checker(boolean color) {
		if(color) {
			setImage(new Image("red.png",100,100,false,true));
		}else {
			setImage(new Image("yellow.png",100,100,false,true));
		}
		this.color = color;
		vel = 0;
		canMove = false;
		hitBottom = false;
	}
	
	@Override
	public void act(long now) {
		if(canMove) {
			vel += accel;
			if(getY() < h) {
				setY(getY() + vel);
			}else if(getY() > h) {
				setY(h);
				hitBottom = true;
			}
		}
	}
	
	public boolean hitBottom() {
		return hitBottom;
	}
	
	public void setH(double d) {
		h = d;
	}
	
	public void canMove() {
		canMove = true;
	}
	
	public boolean getColor() {
		return color;
	}

}
