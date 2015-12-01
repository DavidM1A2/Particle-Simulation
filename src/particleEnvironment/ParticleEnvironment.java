package particleEnvironment;

import java.awt.Toolkit;

import ddf.minim.AudioInput;
import ddf.minim.Minim;
import processing.core.PApplet;
import processing.core.PVector;

public class ParticleEnvironment extends PApplet
{
	private ParticleSystem particleSystem;
	private boolean createParticles = false;
	private boolean pause = false;
	private int backgroundColor;
	private boolean shiftDown = false;
	private boolean hideTextOverlay = false;
	private boolean disableSoundInput = false;
	private Minim min;
	private AudioInput in;
	private float sum;
	private CircularBuffer buf;

	public static void main(String args[])
	{
		PApplet.main(new String[]
		{ "--present", "particleEnvironment.ParticleEnvironment" });
	}

	@Override
	public void settings()
	{
		this.size(Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height);
	}

	@Override
	public void setup()
	{
		this.particleSystem = new ParticleSystem(new PVector(this.width / 2, 50), this);
		this.backgroundColor = this.color(this.random(255), this.random(255), this.random(255));
		this.min = new Minim(this);
		this.in = this.min.getLineIn();
		listener blah = new listener();
		blah.start();
	}

	@Override
	public void draw()
	{
		this.background(this.backgroundColor);

		if (this.createParticles)
		{
			if (!this.pause)
			{
				this.particleSystem.addParticle();
				// this.particleSystem.setAccelerationForAllParticles(x, y);
			}
		}

		this.particleSystem.run(this.pause);
		this.particleSystem.setOrigin(new PVector(this.mouseX, this.mouseY));

		this.drawTextOverlay();
	}

	@Override
	public void mousePressed()
	{
		if (this.mouseButton == LEFT)
		{
			this.createParticles = !this.createParticles;
		}
		else if (this.mouseButton == RIGHT)
		{
			this.particleSystem.addParticle();
		}
	}

	@Override
	public void keyPressed()
	{
		if (this.keyCode == SHIFT)
		{
			this.shiftDown = true;
		}
		if (this.keyCode == RIGHT)
		{
			this.particleSystem.addAccelerationToAllParticles(this.shiftDown ? 1.0f : 0.1f, 0);
		}
		if (this.keyCode == LEFT)
		{
			this.particleSystem.addAccelerationToAllParticles(this.shiftDown ? -1.0f : -0.1f, 0);
		}
		if (this.keyCode == DOWN)
		{
			this.particleSystem.addAccelerationToAllParticles(0, this.shiftDown ? 1.0f : 0.1f);
		}
		if (this.keyCode == UP)
		{
			this.particleSystem.addAccelerationToAllParticles(0, this.shiftDown ? -1.0f : -0.1f);
		}
		if (this.keyCode == ' ')
		{
			this.pause = !this.pause;
		}
		if ((this.keyCode == 'r') || (this.keyCode == 'R'))
		{
			this.particleSystem.resetSimulation();
			this.createParticles = false;
		}
		if ((this.keyCode == 'b') || (this.keyCode == 'B'))
		{
			this.backgroundColor = this.color(this.random(255), this.random(255), this.random(255));
		}
		if ((this.keyCode == 'h') || (this.keyCode == 'H'))
		{
			this.particleSystem.addBlackHole(this.mouseX, this.mouseY);
		}
		if ((this.keyCode == 's') || (this.keyCode == 'S'))
		{
			this.hideTextOverlay = !this.hideTextOverlay;
		}
		if ((this.keyCode == 'o') || (this.keyCode == 'O'))
		{
			this.particleSystem.addWall(this.mouseX, this.mouseY, this.random(100, 400), this.random(100, 400));
		}
		if ((this.keyCode == 'n') || ((this.keyCode) == 'N'))
		{
			this.disableSoundInput = !this.disableSoundInput;
		}
	}

	@Override
	public void keyReleased()
	{
		if (this.keyCode == SHIFT)
		{
			this.shiftDown = false;
		}
		if (this.keyCode == ESC)
		{
			System.exit(0);
		}
	}

	public void drawTextOverlay()
	{
		this.fill(255);
		this.textSize(14f);

		if (!this.hideTextOverlay)
		{
			this.text(String.format("Particle Acceleration Y = %.1f", this.particleSystem.getCurrentAccelerationModifier().x), 5, 15);
			this.text(String.format("Particle Acceleration X = %.1f", this.particleSystem.getCurrentAccelerationModifier().y), 5, 30);
			this.text("Controls:\nSpace to pause\nArrow keys to change gravity\nR to reset the environment\nHolding shift increments acceleration by +/- 1\nUse B to change the background color\nS to hide this overlay\nH to create a black hole\nN to disable sound input; Currenly: " + (this.disableSoundInput ? "Disabled" : "Enabled") + "\nPress O to create a wall\nRight click to spawn a single particle", 5, 45);
			this.text(String.format("Frame rate: %.0f", this.frameRate), this.width - this.textWidth(String.format("Frame rate: %.0f", this.frameRate)) - 10, 15);
		}
		else
		{
			this.text("Press S to expand", 5, 15);
		}

		if (this.pause)
		{
			this.textSize(40f);
			this.text("Simulation Paused", (this.width / 2) - (this.textWidth("Simulation Paused".toCharArray(), 0, "Simulation Paused".length()) / 2), this.height / 2);
			this.text("" + this.buf.avg(), (this.width / 2) - (this.textWidth("Simulation Paused".toCharArray(), 0, "Simulation Paused".length()) / 2), (this.height / 2) + 50);
		}
	}

	private class listener extends Thread
	{

		public listener()
		{
			ParticleEnvironment.this.buf = new CircularBuffer(30);

		}

		@Override
		public void run()
		{

			while (true)
			{
				try
				{
					Thread.sleep(50);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for (int i = 0; i < (ParticleEnvironment.this.in.bufferSize() - 1); i++)
				{
					ParticleEnvironment.this.sum = Math.abs(((ParticleEnvironment.this.in.left.get(i)) + (ParticleEnvironment.this.in.right.get(i))) * 10);
					if (ParticleEnvironment.this.sum > 2)
					{
						ParticleEnvironment.this.sum = 2;
					}
					ParticleEnvironment.this.buf.addSum(ParticleEnvironment.this.sum);

					if (!ParticleEnvironment.this.disableSoundInput)
					{
						Particle.setBouncy((float) ParticleEnvironment.this.buf.avg());
					}
					else
					{
						Particle.setBouncy(1.0f);
					}

				}

			}

		}

	}
}