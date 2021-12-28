import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.ImageView;

public abstract class Actor extends ImageView{
	
	public Actor() {
		super();
	}
	
	public void move(double dx, double dy) {
		setX(getX() + dx);
		setY(getY() + dy);
	}
	
	public World getWorld() {
		return (World)getParent();
	}
	
	public double getWidth() {
		return getBoundsInParent().getWidth();
	}
	
	public double getHeight() {
		return getBoundsInParent().getHeight();
	}
	
	public <A extends Actor> java.util.List<A> getIntersectingObjects(java.lang.Class<A> cls){
		List<A> result = new ArrayList<A>();
		List<A> objects = getWorld().getObjects(cls);
		for(A a : objects) {
			if(a != this && this.intersects(a.getBoundsInParent())) {
				result.add(a);
			}
		}
		return result;
	}
	
	public <A extends Actor> A getOneIntersectingObject(java.lang.Class<A> cls) {
		List<A> objects = getWorld().getObjects(cls);
		for(A a : objects) {
			if(a != this && this.intersects(a.getBoundsInParent())) {
				return a;
			}
		}
		return null;
	}
	
	public abstract void act(long now);
}
