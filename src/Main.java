import java.awt.AWTException;
import java.io.IOException;

import javax.swing.JFrame;


public class Main extends JFrame implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Screen screen;
	Thread thread;
	boolean running = false;
	boolean vsync = false;
	public static final int WIDTH = 400, HEIGHT = 300;
	

	public Main() throws IOException, AWTException {
		screen = new Screen(this);
		add(screen);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(WIDTH*2 + 16, HEIGHT*2 + 39);
		setVisible(true);
		//requestFocusInWindow();
		requestFocus();
		running = true;

		start();
	}

	public synchronized void start() {

		running = true;
		thread = new Thread(this, "Display");
		thread.start();
	}
	public synchronized void stop() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double ns = 1000000000.0 / 60;
		double delta = 0;
		int frames = 0;
		int updates = 0;
		//screen.clear();
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				update();
				delta--;
				updates++;
				if(vsync)
				{
					render(true);
					frames++;
				}
			}
			//clear?
			if(!vsync){
			render(true);
			frames++;
			}
			//screen.clear();
			if (System.currentTimeMillis() - timer >= 1000) {
				//if(!isApplet)
					setTitle("Everything | " + ("FPS: " + frames + ", UPS: " + updates));
				frames = 0;
				updates = 0;
				timer = System.currentTimeMillis();
			}
		}
	}
	private void render(boolean clear) {
		if(clear)
			screen.clear();
		screen.paint();
		
		screen.render();
	}
	private void update() {
		
		screen.update();
	}
	public static void main(String[] args) throws IOException, AWTException {
		new Main();
	}
}
