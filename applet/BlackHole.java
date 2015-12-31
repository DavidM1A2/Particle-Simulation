package particleEnvironment;

import java.util.List;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class BlackHole extends SceneObject
{
	private final PVector location;
	private final PApplet parent;
	private final PVector velocity;
	private static PImage imageSource;
	private PImage myImage;
	private int maxAttractionRange;
	private int accretionDiskRadius;
	private float blackHoleWidth;
	private float blackHoleHeight;
	private float massEaten = 0;
	private boolean isDead = false;

	public BlackHole(float x, float y, PApplet parent)
	{
		this.myImage = BlackHole.imageSource.copy();
		this.location = new PVector(x, y);
		this.velocity = new PVector(0, 0);
		this.parent = parent;
		this.blackHoleWidth = this.myImage.pixelWidth;
		this.blackHoleHeight = this.myImage.pixelHeight;
		this.maxAttractionRange = (int) ((this.blackHoleHeight + this.blackHoleWidth) / 2);
		this.accretionDiskRadius = (int) ((this.blackHoleHeight + this.blackHoleWidth) / 15);
	}

	@Override
	public void display()
	{
		float imageLocationX = this.location.x - (this.blackHoleWidth / 2);
		float imageLocationY = this.location.y - (this.blackHoleHeight / 2);
		this.parent.image(this.myImage, imageLocationX, imageLocationY);
		String size = String.format("%.2f", this.massEaten);
		this.parent.textSize(20.0f);
		this.parent.fill(255, 255, 255);
		this.parent.text(size, this.location.x - (this.parent.textWidth(size) / 2), this.location.y);
	}

	@Override
	public void update(List<SceneObject> sceneObjects)
	{
		if (!this.isDead())
		{
			this.velocity.mult(0.95f);
			this.location.add(this.velocity);

			for (SceneObject sceneObject : sceneObjects)
			{
				if (sceneObject instanceof Particle)
				{
					Particle particle = (Particle) sceneObject;
					float distance = particle.getParticleLocation().dist(this.location);
					float force = (this.maxAttractionRange - distance) / 300f;
					if (distance < this.accretionDiskRadius)
					{
						particle.addLifespan(-40);
						if (particle.getLifespan() <= 0)
						{
							this.massEaten = this.massEaten + particle.getParticleMass();

							this.recalculateSize();
						}
					}

					if (force > 0)
					{
						if (this.location.x > particle.getParticleLocation().x)
						{
							particle.addVelocity(force, 0);
						}
						else
						{
							particle.addVelocity(-force, 0);
						}

						if (this.location.y > particle.getParticleLocation().y)
						{
							particle.addVelocity(0, force);
						}
						else
						{
							particle.addVelocity(0, -force);
						}
					}
				}
				else if (sceneObject instanceof BlackHole)
				{
					BlackHole blackHole = (BlackHole) sceneObject;

					if (blackHole != this)
					{
						float distance = this.location.dist(blackHole.location);
						if (distance < (this.accretionDiskRadius + blackHole.accretionDiskRadius))
						{
							if (this.massEaten >= blackHole.massEaten)
							{
								blackHole.isDead = true;
								this.massEaten = blackHole.massEaten + this.massEaten;

								this.recalculateSize();
							}
						}
						else if (distance < (this.maxAttractionRange + blackHole.maxAttractionRange))
						{
							float force = (this.maxAttractionRange - distance) / 10000f;
							if (force > 0)
							{
								if (this.location.x > blackHole.location.x)
								{
									blackHole.velocity.add(force, 0);
								}
								else
								{
									blackHole.velocity.add(-force, 0);
								}

								if (this.location.y > blackHole.location.y)
								{
									blackHole.velocity.add(0, force);
								}
								else
								{
									blackHole.velocity.add(0, -force);
								}
							}
						}
					}
				}
			}
		}
	}

	private void recalculateSize()
	{
		this.myImage = BlackHole.imageSource.copy();
		this.blackHoleWidth = this.myImage.pixelWidth + (this.massEaten * 0.3f);
		this.blackHoleHeight = this.myImage.pixelHeight + (this.massEaten * 0.3f);
		this.maxAttractionRange = (int) ((this.blackHoleWidth + this.blackHoleHeight) / 2);
		this.accretionDiskRadius = (int) ((this.blackHoleWidth + this.blackHoleHeight) / 15);
		this.myImage.resize(Math.round(this.blackHoleWidth), Math.round(this.blackHoleHeight));
	}

	@Override
	public boolean isDead()
	{
		return this.isDead;
	}

	public static void loadIcon(PApplet parent)
	{
		BlackHole.imageSource = parent.loadImage("http://icons.iconarchive.com/icons/zairaam/bumpy-planets/256/blackhole-icon.png", "png");
	}

	@Override
	public int renderPriority()
	{
		return 4;
	}
}
