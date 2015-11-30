package particleEnvironment;

import java.awt.Rectangle;
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
		this.parent.rect(this.location.x, this.location.y, this.size.x, this.size.y); // rect(x of upper left corner, y of upper left, width, height)
	}

	@Override
	public void update(List<SceneObject> sceneObjects) {
		for (SceneObject sceneObject : sceneObjects) {
			if (sceneObject instanceof Particle) {
				Particle particle = (Particle) sceneObject;
				float particleBottom = particle.getParticleLocation().y + particle.getParticleRadius();
				float particleTop = particle.getParticleLocation().y - particle.getParticleRadius();
				float particleRight = particle.getParticleLocation().x + particle.getParticleRadius();
				float particleLeft = particle.getParticleLocation().x - particle.getParticleRadius();
				
				// colliding with left
				if ((particleRight > this.location.x) && (particleRight < (this.location.x + this.size.x))) {
					if ((particleBottom > this.location.y) && (particleBottom < (this.location.y + this.size.y))) {
						particle.invertXVelocity();
					}
				}
				// colliding with right
				if ((particleLeft > this.location.x) && (particleLeft < (this.location.x + this.size.x))) {
					if ((particleBottom > this.location.y) && (particleBottom < (this.location.y + this.size.y))) {
						particle.invertXVelocity();
					}
				}
				// colliding with top
				if ((particleBottom > this.location.y) && (particleBottom < (this.location.y + this.size.y))) {
					if (((particleRight) > this.location.x) && ((particleRight) < (this.location.x + this.size.x))) {
						particle.invertYVelocity();
					}
				}
				// colliding with bottom
				if ((particleTop > this.location.y) && (particleTop < (this.location.y + this.size.y))) {
					if (((particleRight) > this.location.x) && ((particleRight) < (this.location.x + this.size.x))) {
						particle.invertYVelocity();
					}
				}
			}
		}
	}
	
	private boolean collidesWith(Particle particle)
	{
		Rectangle particleCollider = new Rectangle((int) (particle.getParticleLocation().x + particle.getParticleRadius()), (int) (particle.getParticleLocation().y + particle.getParticleRadius()), (int) particle.getParticleWidth(), (int) particle.getParticleWidth());
		Rectangle rectangleCollider = new Rectangle((int) this.location.x, (int) this.location.y, (int) this.size.x, (int) this.size.y);
		return particleCollider.intersects(rectangleCollider);
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
