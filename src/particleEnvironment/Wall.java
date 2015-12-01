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
				
				if (collidesWith(particle))
				{
					int deltaXLeft = Integer.MAX_VALUE;
					int deltaYTop = Integer.MAX_VALUE;
					int deltaXRight = Integer.MAX_VALUE;
					int deltaYBottom = Integer.MAX_VALUE;
					
					
					deltaXLeft = (int) Math.min(deltaXLeft, particleRight - this.location.x);
					deltaXRight = (int) Math.min(deltaXRight, this.location.x + this.size.x - particleLeft);
					deltaYTop = (int) Math.min(deltaYTop, particleBottom - this.location.y);
					deltaYBottom = (int) Math.min(deltaYBottom, this.location.y + this.size.y - particleTop);
							
					if (Math.min(deltaXLeft, deltaXRight) < Math.min(deltaYTop, deltaYBottom))
					{
						particle.invertXVelocity();
						if (deltaXLeft > deltaXRight) // Hit left
							particle.getParticleLocation().add(deltaXRight, 0);
						else							
							particle.getParticleLocation().add(-deltaXLeft, 0);
					}
					else
					{
						particle.invertYVelocity();
						if (deltaYTop > deltaYBottom) // Hit left
							particle.getParticleLocation().add(0, deltaYBottom);
						else
						particle.getParticleLocation().add(0, -deltaYTop);					
					}
				}
			}
		}
	}
	
	private boolean collidesWith(Particle particle)
	{
		Rectangle particleCollider = new Rectangle((int) (particle.getParticleLocation().x - particle.getParticleRadius()), (int) (particle.getParticleLocation().y - particle.getParticleRadius()), (int) particle.getParticleWidth(), (int) particle.getParticleWidth());
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
