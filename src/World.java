import java.util.*;
import javafx.animation.AnimationTimer;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;

public abstract class World extends javafx.scene.layout.Pane {

	private AnimationTimer timer;
	HashSet<KeyCode> keyCodes;

	public World() {
		super();
		timer = new AnimationTimer() {

			@Override
			public void handle(long now) {
				act(now);
				for (Actor a : getObjects(Actor.class)) {
					a.act(now);
				}
			}

		};
		keyCodes = new HashSet<KeyCode>();
	}

	public void addKC(KeyCode kc) {
		keyCodes.add(kc);
	}

	public void removeKC(KeyCode kc) {
		keyCodes.remove(kc);
	}

	public boolean isKCDown(KeyCode kc) {
		return keyCodes.contains(kc);
	}

	public abstract void act(long now);

	@SuppressWarnings("unchecked")
	public <A extends Actor> List<A> getObjects(java.lang.Class<A> cls) {
		List<A> result = new ArrayList<A>();
		ObservableList<Node> temp = getChildren();
		for (Node n : temp) {
			if (cls.isInstance(n)) {
				result.add((A) n);
			}
		}
		return result;
	}

	public void remove(Actor actor) {
		getChildren().remove(actor);
	}

	public void add(Actor actor) {
		getChildren().add(actor);
	}

	public void stop() {
		timer.stop();
	}

	public void start() {
		timer.start();
	}

}
