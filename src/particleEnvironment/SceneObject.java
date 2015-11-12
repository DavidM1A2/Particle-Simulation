package particleEnvironment;

import java.util.List;

public interface SceneObject
{
	public abstract void display();

	public abstract void update(List<SceneObject> sceneObjects);

	public abstract boolean isDead();
}
