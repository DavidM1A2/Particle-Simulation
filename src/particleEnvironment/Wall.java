package particleEnvironment;

import java.util.List;

import processing.core.PApplet;
import processing.core.PVector;

public class Wall extends SceneObject {
	private final PApplet parent;
	private final PVector location;
	private final PVector size;

	public Wall(PVector location, PApplet parent, PVector size) {
		this.location = location;
		this.parent = parent;
		this.size = size;
	}

	@Override
	public void display() {
		this.parent.rect(this.location.x, this.location.y, this.size.x, this.size.y);
	}

	@Override
	public void update(List<SceneObject> sceneObjects) {
		for (SceneObject sceneObject : sceneObjects) {
			if (sceneObject instanceof Particle) {
				Particle particle = (Particle) sceneObject;
				// colliding with right
				if (((particle.getParticleLocation().x + particle.getParticleRadius()) > this.location.x)
						&& ((particle.getParticleLocation().x + particle.getParticleRadius()) < (this.location.x
						+ this.size.x))) {
				if (((particle.getParticleLocation().y + particle.getParticleRadius()) > this.location.y)
						&& ((particle.getParticleLocation().y + particle.getParticleRadius()) < (this.location.y
						+ this.size.y))) {
						particle.invertXVelocity();
					}
				}
				// colliding with top
				if (((particle.getParticleLocation().y + particle.getParticleRadius()) > this.location.y)
						&& ((particle.getParticleLocation().y + particle.getParticleRadius()) < (this.location.y
						+ this.size.y))) {
				if (((particle.getParticleLocation().x + particle.getParticleRadius()) > this.location.x)
						&& ((particle.getParticleLocation().x + particle.getParticleRadius()) < (this.location.x
						+ this.size.x))) {
						particle.invertYVelocity();
					}
				}
			}
		}
	}

	@Override
	public boolean isDead() {
		return false;
	}

	@Override
	public int renderPriority() {
		return 3;
	}

}
