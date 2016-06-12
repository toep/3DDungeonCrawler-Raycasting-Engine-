import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;


public class Screen extends Canvas implements KeyListener, MouseMotionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Robot robot;
	BufferedImage img;
	int[] data;
	float[] zBuffer;
	Main main;
	float[] sin = new float[4196];
	float PI_2 = (float) (2*Math.PI);
	float PIO2 = (float) (Math.PI/2);
	float yRot = (float) (Math.PI/2), zRot = 0, xRot = 0;
	boolean[] keys = new boolean[256];
	boolean focused = true;
	int mouseX, mouseY;
	Vector2 position;
	Vector2 direction;
	
	Bitmap brickwall;
	Bitmap level;
	Block[][] blocks = new Block[38][18];
	public Screen(Main main) throws IOException, AWTException {
		robot = new Robot();//for grabbing mouse
		this.main = main;
		img = new BufferedImage(Main.WIDTH, Main.HEIGHT,
				BufferedImage.TYPE_INT_ARGB);
		data = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
		
		for(int i = 0; i < sin.length; i++){
			sin[i] = (float) Math.sin((double)(i*2.0f*Math.PI/sin.length));
		}
		addKeyListener(this);
		addMouseMotionListener(this);
		
		position = new Vector2(2.5293996f, 7.907601f);
		direction = new Vector2(0.9857315f, 0.16832535f);
		zBuffer = new float[data.length];
		brickwall = new Bitmap("wall.png");
		level = new Bitmap("level.png");
		for(int i = 0; i < blocks.length; i++){
			for(int j = 0; j < blocks[0].length; j++){
				if(level.getRGB(i, j) == 0xffff0000)
					blocks[i][j] = new Block(i, j);
			}
		}
		//blocks.add(new Block(1, 5));
	}
	
	public float sin(double rad){
		
		while(rad < 0)
			rad+=PI_2;
		//while(rad > PI_2)
		//	rad -=PI_2;
		int index = (int)((rad/PI_2)*(sin.length-1));
		
		return sin[index%sin.length];
	}
	public float cos(double rad){
		return sin(rad + PIO2);
	}
	public void paint() {
		for(int i = 0; i < zBuffer.length; i++){
			zBuffer[i] = Float.MAX_VALUE;
		}
		
		float curDir = (float) Math.atan2(direction.y, direction.x);
		if(curDir < 0)
			curDir+=2*Math.PI;
		float dr = (float) (Math.PI/1024f);
		//draw ceil and floor
		for(int i = Main.WIDTH*Main.HEIGHT/2; i < Main.WIDTH*Main.HEIGHT; i++){
			data[i] = 0xff514312;
		}
		for(float rad = (float) (-Math.PI/8f-Math.PI/16f); rad <= Math.PI/8+Math.PI/16f; rad+=dr)
		{
			
			Block b = fillArrayWithBlocksInSight(position.x, position.y, cos(curDir+rad), sin(curDir+rad));
			brickwall.draw(this, position, curDir, rad, b);
		}
		for(int i = 0; i < blocks.length; i++)
			for(int j = 0; j < blocks[0].length; j++)
				if(blocks[i][j] != null)
					blocks[i][j].resetRenderingVariables();
		
		for(int i = 0; i < data.length; i++) {
			float brightness;
			int c = data[i];
			
			if(zBuffer[i] == Float.MAX_VALUE) 
				brightness = (Math.abs(Main.HEIGHT/2-i/Main.WIDTH)/(float)(Main.HEIGHT/2f)*255f)-25;
			else 
				brightness = (500.0f/ zBuffer[i]);
			
			if(brightness < 0) brightness = 0;
			if(brightness > 255) brightness = 255;
			int red = (c >> 16) & 0xFF;
			int green = (c >> 8) & 0xFF;
			int blue = c & 0xFF;
			red*=(brightness/255f);
			green*=(brightness/255f);
			blue*=(brightness/255f);
			data[i] = (0xff << 24) | (red << 16 ) | (green<<8) | blue;
			
		}
	}
	public Block fillArrayWithBlocksInSight(float fx,float fy,float rayDirX, float rayDirY) {
		
		int mapX = (int)fx;
		int mapY = (int)fy;
		float sideDistX;
		float sideDistY;
		float deltaDistX = (float) Math.sqrt(1 + (rayDirY*rayDirY) / (rayDirX*rayDirX));
		float deltaDistY = (float) Math.sqrt(1 + (rayDirX*rayDirX) / (rayDirY*rayDirY));
		
	    int stepX, stepY;
	    if (rayDirX < 0)
	    {
	        stepX = -1;
	        sideDistX = (fx - mapX) * deltaDistX;
	    }
	    else
	    {
	        stepX = 1;
	        sideDistX = (mapX + 1.0f - fx) * deltaDistX;
	    }
	    if (rayDirY < 0)
	    {
	        stepY = -1;
	        sideDistY = (fy - mapY) * deltaDistY;
	    }
	    else
	    {
	        stepY = 1;
	        sideDistY = (mapY + 1.0f - fy) * deltaDistY;
	    }
	  //Loop to find where the ray hits a wall
	    while(true) {
	        //Jump to next square
	        if (sideDistX < sideDistY)
	        {
	            sideDistX += deltaDistX;
	            mapX += stepX;
	            
	        }
	        else
	        {
	            sideDistY += deltaDistY;
	            mapY += stepY;
	        }
	        
	        //Check if ray has hit a wall
	        if(mapX >= 0 && mapY >= 0 && mapX < blocks.length && mapY < blocks[0].length){
	        	if(blocks[mapX][mapY] != null){
	        		blocks[mapX][mapY].setDrawing(position);
	        		return blocks[mapX][mapY];
	        	}
	        }
	        else return null;
	    }
	}

	public void render() {
		Graphics g = getGraphics();	
		g.drawImage(img, 0, 0, getParent().getWidth(), getParent().getHeight(), null);
	}

	private void clear(int col) {
		for (int i = 0; i < data.length; i++) {
			data[i] = col;
		}
	}
	
	public void clear() {
		clear(0xff2f2f2f);
	}
	public void update() {
		
		if(keys[KeyEvent.VK_LEFT]){
			float d = (float) Math.atan2(direction.y, direction.x);
			d+=Math.PI/2;
			position.x+=cos(d)/10f;
			position.y+=sin(d)/10f;
			
		}else if(keys[KeyEvent.VK_RIGHT]){
			float d = (float) Math.atan2(direction.y, direction.x);
			d-=Math.PI/2;
			position.x+=cos(d)/10f;
			position.y+=sin(d)/10f;
		}
		if(keys[KeyEvent.VK_UP]){
			position.x+=direction.x/5f;
			position.y+=direction.y/5f;
		}
		else if(keys[KeyEvent.VK_DOWN]){
			position.x-=direction.x/5f;
			position.y-=direction.y/5f;
		}
	}

		public boolean setPixel(int x, int y, int cc) {
		if(x < 0 || y < 0 || x >= Main.WIDTH || y > Main.HEIGHT)return false;
		int c = (x+y*Main.WIDTH);
		if(c < data.length && c > 0 ){
			if(cc != 0xffff00ff)
				data[c] = cc;
			return true;
		}
		return false;
	}

		@Override
		public void keyTyped(KeyEvent e) {
			
		}

		@Override
		public void keyPressed(KeyEvent e) {
			keys[e.getKeyCode()] = true;
		}

		private void rotate(float f) {
			float curDir = (float) Math.atan2(direction.y, direction.x);
			curDir+=f;
			direction.x = (float) Math.cos(curDir);
			direction.y = (float) Math.sin(curDir);
		}

		@Override
		public void keyReleased(KeyEvent e) {
			keys[e.getKeyCode()] = false;
		}

		public float getZBuff(int i) {
			if(i < 0 || i >= zBuffer.length) return -1;
			return zBuffer[i];
		}

		public Block getBlock(int x, int y) {
			if(x < 0 || x > blocks.length || y < 0 || y >= blocks[0].length) return null;
			return blocks[x][y];
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if(e.getX() == getParent().getWidth()/2 ) {
				return;
			}
			int dx = e.getX()-392;
			rotate(-dx/600f);
			robot.mouseMove(getParent().getWidth()/2+main.getX(), getParent().getHeight()/2+main.getY());
		}
}
