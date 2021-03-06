package particleEnvironment;

import java.util.List;

import processing.core.PApplet;
import processing.core.PVector;

public class Particle extends SceneObject
{
	private PVector location;
	private PVector velocity;
	private PVector acceleration;
	private float lifespan;
	private static float restitutionConstant = 1.0f;
	private final float particleMass;
	private final int myColor;
	private final PApplet parent;
	private static final float MAX_SIZE = 8.0f;
	private static final float MIN_SIZE = 5.0f;

	public Particle(PVector location, PApplet parent, PVector acceleration)
	{
		this.parent = parent;
		this.velocity = new PVector(parent.random(-1, 1), parent.random(-1, 1));
		this.acceleration = acceleration;
		this.location = location.copy();
		this.myColor = parent.color(parent.random(255), parent.random(255), parent.random(255));
		this.lifespan = 255f;
		this.particleMass = parent.random(MIN_SIZE, MAX_SIZE);
		// this.restitutionConstant = this.createRestitutionConstantByColor();
	}

	// Method to update location
	@Override
	public void update(List<SceneObject> sceneObjects)
	{
		// Hit the right side
		if ((this.location.x + this.getRadius()) > this.parent.width)
		{
			this.invertXVelocity();
			this.location.x = this.parent.width - this.getRadius();
		}
		// Hit the left side
		else if ((this.location.x - this.getRadius()) < 0)
		{
			this.invertXVelocity();
			this.location.x = this.getRadius();
		}

		// Hit the bottom
		if ((this.location.y + this.getRadius()) > this.parent.height)
		{
			this.invertYVelocity();
			this.location.y = this.parent.height - this.getRadius();
		}
		// Hit the top
		else if ((this.location.y - this.getRadius()) < 0)
		{
			this.invertYVelocity();
			this.location.y = this.getRadius();
		}

		for (SceneObject sceneObject : sceneObjects)
		{
			if (sceneObject instanceof Particle)
			{
				Particle particle = (Particle) sceneObject;
				if (particle != this)
				{
					if (this.isColliding(particle))
					{
						this.resolveCollision(particle);
					}
				}
			}
		}

		this.velocity.add(this.acceleration);
		this.location.add(this.velocity);
		this.lifespan = this.lifespan - 0.1f;
	}

	// Method to display
	@Override
	public void display()
	{
		this.parent.stroke(255, this.lifespan);
		this.parent.fill(this.myColor, this.lifespan);
		this.parent.ellipse(this.location.x, this.location.y, this.getParticleWidth(), this.getParticleWidth());
		this.parent.fill(255, this.lifespan);
		String textToPrint = String.format("%.1f", this.particleMass);
		this.parent.textSize((int) (this.particleMass * 3));
		this.parent.text(textToPrint, this.location.x - (this.parent.textWidth(textToPrint.toCharArray(), 0, textToPrint.length()) / 2), this.location.y + (this.parent.textDescent() / 2));
	}

	// Is the particle dead because it's out of life?
	@Override
	public boolean isDead()
	{
		return this.lifespan < 0.0;
	}

	public boolean isColliding(Particle other)
	{
		return (this.location.dist(other.location) < (other.getRadius() + this.getRadius()));
	}

	public void resolveCollision(Particle other)
	{
		float distance = this.location.dist(other.location);
		PVector delta = this.location.copy().sub(other.location);

		PVector minimumTranslationDistance = delta.copy().mult(((this.getRadius() + other.getRadius()) - distance) / distance);

		// Inverse mass quantities
		float im1 = 1.0f / this.getParticleMass();
		float im2 = 1.0f / other.getParticleMass();

		PVector newLocation1 = minimumTranslationDistance.copy().mult(im1 / (im1 + im2));
		PVector newLocation2 = minimumTranslationDistance.copy().mult(im2 / (im1 + im2));

		this.location = this.location.copy().add(newLocation1);
		other.location = other.location.copy().sub(newLocation2);

		PVector v = (this.velocity.copy().sub(other.velocity));
		float vn = v.dot(minimumTranslationDistance.normalize());

		// sphere intersecting but moving away from each other already
		if (vn > 0.0f)
		{
			return;
		}

		// collision impulse
		float i = (-(1.0f + this.getParticleRestitutionConstant()) * vn) / (im1 + im2);
		PVector impulse = minimumTranslationDistance.copy().mult(i);

		// collision impulse
		float i2 = (-(1.0f + other.getParticleRestitutionConstant()) * vn) / (im1 + im2);
		PVector impulse2 = minimumTranslationDistance.copy().mult(i2);

		// change in momentum
		this.velocity = this.velocity.add(impulse.copy().mult(im1));
		other.velocity = other.velocity.sub(impulse2.copy().mult(im2));
	}

	public float getRadius()
	{
		return this.getParticleWidth() / 2;
	}

	public float getParticleWidth()
	{
		return 8 * this.getParticleMass();
	}

	public float getParticleMass()
	{
		return this.particleMass;
	}

	public PVector getParticleLocation()
	{
		return this.location;
	}

	public float getParticleRestitutionConstant()
	{
		return this.restitutionConstant;
	}

	public float createRestitutionConstantByColor()
	{
		float r = this.parent.red(this.myColor);
		float g = this.parent.green(this.myColor);
		float b = this.parent.blue(this.myColor);

		float redPercentage = r / 255f;
		float greenPercentage = g / 255f;
		float bluePercentage = b / 255f;

		return (redPercentage + greenPercentage + bluePercentage) / 3f;
	}

	public void setAcceleration(PVector acceleration)
	{
		this.acceleration = acceleration;
	}

	public PVector getAcceleration()
	{
		return this.acceleration;
	}

	public void addVelocity(float x, float y)
	{
		this.velocity.add(x, y);
	}

	public void addLifespan(float lifespan)
	{
		this.lifespan = this.lifespan + lifespan;
	}

	public float getLifespan()
	{
		return this.lifespan;
	}

	@Override
	public int renderPriority()
	{
		return 5;
	}

	public void invertXVelocity()
	{
		this.velocity.x = -this.velocity.x;
	}

	public void invertYVelocity()
	{
		this.velocity.y = -this.velocity.y;
	}
	
	public static void setBouncy(float argument){
		restitutionConstant = argument;
	}
}
