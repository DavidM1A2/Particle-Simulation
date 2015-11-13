package particleEnvironment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import processing.core.PApplet;
import processing.core.PVector;

public class ParticleSystem
{
	// A class to describe a group of Particles
	// An ArrayList is used to manage the list of Particles
	private final PVector origin;
	private final PApplet parent;
	private final List<SceneObject> sceneObjects = new ArrayList<SceneObject>();
	private PVector currentAccelerationModifier = new PVector(0, 0.1f);
	private boolean needToReorderObjects = false;

	public ParticleSystem(PVector location, PApplet parent)
	{
		BlackHole.loadIcon(parent);
		this.origin = location.copy();
		this.parent = parent;
	}

	public void addParticle()
	{
		boolean isValid = true;
		for (SceneObject sceneObject : this.sceneObjects)
		{
			if (sceneObject instanceof Particle)
			{
				Particle particle = (Particle) sceneObject;
				if (this.origin.dist(particle.getParticleLocation()) <= (particle.getParticleWidth()))
				{
					isValid = false;
				}
			}
		}
		if (isValid)
		{
			this.addSceneObject(new Particle(this.origin, this.parent, this.currentAccelerationModifier.copy()));
		}
	}

	public void run(boolean isPaused)
	{
		if (this.needToReorderObjects)
		{
			Collections.sort(this.sceneObjects);
		}
		Iterator<SceneObject> sceneObjectsIterator = this.sceneObjects.iterator();
		while (sceneObjectsIterator.hasNext())
		{
			SceneObject sceneObject = sceneObjectsIterator.next();
			sceneObject.display();
			if (!isPaused)
			{
				sceneObject.update(this.sceneObjects);
			}
			if (sceneObject.isDead())
			{
				sceneObjectsIterator.remove();
			}
		}
	}

	public void resetSimulation()
	{
		this.clearParticles();
		this.clearBlackHoles();
		this.setAccelerationForAllParticles(0, 0.1f);
	}

	public void setOrigin(PVector newOrigin)
	{
		this.origin.set(newOrigin);
	}

	public void clearParticles()
	{
		Iterator<SceneObject> sceneObjectsIterator = this.sceneObjects.iterator();
		while (sceneObjectsIterator.hasNext())
		{
			SceneObject sceneObject = sceneObjectsIterator.next();
			if (sceneObject instanceof Particle)
			{
				sceneObjectsIterator.remove();
			}
		}
	}

	public void clearBlackHoles()
	{
		Iterator<SceneObject> sceneObjectsIterator = this.sceneObjects.iterator();
		while (sceneObjectsIterator.hasNext())
		{
			SceneObject sceneObject = sceneObjectsIterator.next();

			if (sceneObject instanceof BlackHole)
			{
				sceneObjectsIterator.remove();
			}
		}
	}

	public void addBlackHole(float x, float y)
	{
		this.addSceneObject(new BlackHole(x, y, this.parent));
	}

	public void addAccelerationToAllParticles(float x, float y)
	{
		for (SceneObject sceneObject : this.sceneObjects)
		{
			if (sceneObject instanceof Particle)
			{
				((Particle) sceneObject).getAcceleration().add(x, y);
			}
		}
		this.currentAccelerationModifier.add(x, y);
	}

	public void setAccelerationForAllParticles(float x, float y)
	{
		for (SceneObject sceneObject : this.sceneObjects)
		{
			if (sceneObject instanceof Particle)
			{
				((Particle) sceneObject).setAcceleration(new PVector(x, y));
			}
		}
		this.currentAccelerationModifier = new PVector(x, y);
	}

	public PVector getCurrentAccelerationModifier()
	{
		return this.currentAccelerationModifier;
	}

	public void addSceneObject(SceneObject toAdd)
	{
		this.needToReorderObjects = true;
		this.sceneObjects.add(toAdd);
	}
}
