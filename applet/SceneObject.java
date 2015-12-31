package particleEnvironment;

import java.util.List;

public abstract class SceneObject implements Comparable<SceneObject>
{
	public abstract void display();

	public abstract void update(List<SceneObject> sceneObjects);

	public abstract boolean isDead();

	public abstract int renderPriority();

	@Override
	public int compareTo(SceneObject other)
	{
		return this.renderPriority() - other.renderPriority();
	}
}
