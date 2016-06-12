
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;


public class Bitmap {
	public int[] data;
	public final int width, height;
	public Bitmap(String name) throws IOException {
		
		BufferedImage img = ImageIO.read(Utils.getResourceAsStream("/resources/"+name));
		
		width = img.getWidth();
		height = img.getHeight();
		data = new int[width*height];
		img.getRGB(0, 0, width, height, data, 0, width);

	}

	
	public Bitmap(int width, int height) {
		this.width = width;
		this.height = height;
		data = new int[width*height];
	}
	
	public void draw(Screen d, int x, int y) {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int c = data[i+j*width];
				if(c != 16777215){
					d.setPixel(i + x, j + y, c);
				}
				
			}
		}
	}
	
	public void drawPart(Screen screen, Vector2 pos, float dir, float rayRad, Block b, int part) {
		float dis1 = 0;
		float dis2 = 0;
		float theta1 = 0;
		float theta2 = 0;
		if(part == 1){
			dis1 = pos.dis(new Vector2(b.x, b.y));
			dis2 = pos.dis(new Vector2(b.x+b.width, b.y));
			theta1 = (float) Math.atan2((pos.y-b.y), (pos.x-b.x));
			theta2 = (float) Math.atan2((pos.y-b.y), (pos.x-b.x-b.width));
		}else if(part == 2){
			dis1 = pos.dis(new Vector2(b.x+b.width, b.y));
			dis2 = pos.dis(new Vector2(b.x+b.width, b.y+b.length));
			theta1 = (float) Math.atan2((pos.y-b.y), (pos.x-b.x-b.width));
			theta2 = (float) Math.atan2((pos.y-b.y-b.length), (pos.x-b.x-b.width));
		}
		else if(part == 3){
			dis2 = pos.dis(new Vector2(b.x, b.y+b.length));
			dis1 = pos.dis(new Vector2(b.x+b.width, b.y+b.length));
			theta2 = (float) Math.atan2((pos.y-b.y-b.length), (pos.x-b.x));
			theta1 = (float) Math.atan2((pos.y-b.y-b.length), (pos.x-b.x-b.width));
		}
		else if(part == 4){
			dis2 = pos.dis(new Vector2(b.x, b.y));
			dis1 = pos.dis(new Vector2(b.x, b.y+b.length));
			theta2 = (float) Math.atan2((pos.y-b.y), (pos.x-b.x));
			theta1 = (float) Math.atan2((pos.y-b.y-b.length), (pos.x-b.x));
			
		}
		theta1+=Math.PI;
		theta2+=Math.PI;
		float d1 = dir-theta1;
		float d2 = dir-theta2;
		if(d1 < -Math.PI/2) d1+=Math.PI*2;
		else if(d1 > Math.PI/2) d1-=Math.PI*2;
		if(d2 < -Math.PI/2) d2+=Math.PI*2;
		else if(d2 > Math.PI/2) d2-=Math.PI*2;

		int xpos1 = (int) (((d1+Math.PI/8)/(Math.PI/4))*Main.WIDTH);
		int xpos2 = (int) (((d2+Math.PI/8)/(Math.PI/4))*Main.WIDTH);

		
		for(int i = (int)xpos1; i < (int)xpos2; i++){
			if(i < 0) i = 0;
			if(i > Main.WIDTH)break;
			float t = ((float)i-(float)xpos1)/((float)xpos2-(float)xpos1);
			float deltaDis = dis1 <= dis2?(dis1-dis2)*t+dis2:(dis2-dis1)*t+dis1;
			if(screen.getZBuff(i+(Main.HEIGHT/2)*Main.WIDTH) < deltaDis){
				continue;
			}
			float id = ((float)i-(float)xpos1)/((float)xpos2-(float)xpos1)*(float)width;
			if(id < 0 || id >= width) continue;
			
			int minh = (int)(500/dis2);
			int maxh = (int)(500/dis1);
			
			float height = ((minh-maxh)*t+maxh);
			
			int start = (int) Math.max(Main.HEIGHT/2-height/2 , 0);
			float end = Math.min(Main.HEIGHT/2+height/2, Main.HEIGHT);
			for(int j = start; j < end; j++){

				
				float jd = (j-((float)Main.HEIGHT/2-(float)height/2f))/((float)height)*(float)this.height;
				
				int index = (int)id+(int)jd*width;
				
				if(index >= data.length || index < 0){
					continue;
				}
				int c = data[index];
				
					
				
				if(part == 2 || part == 4){
					//shade one side
					int red = (c >> 16) & 0xFF;
					int green = (c >> 8) & 0xFF;
					int blue = c & 0xFF;
					float shade = .66f;
					red*=shade;
					green*=shade;
					blue*=shade;
					c = (0xff << 24) | (red << 16 ) | (green<<8) | blue;
				}
				if(screen.setPixel(i, j, c))
					screen.zBuffer[i+j*Main.WIDTH] = deltaDis;
			}
		}
	}


	public void draw(Screen screen, Vector2 position, float curDir, float rayRad, Block b) {
		if(b == null) return;
		if(b.drawing1 && !b.drawn1 && screen.getBlock(b.x, b.y-1) == null){
			b.drawn1 = true;
			b.drawing1 = false;
			drawPart(screen, position, curDir,rayRad, b, 1);
		}
		else if(b.drawing3 && !b.drawn3 && screen.getBlock(b.x, b.y+1) == null){
			b.drawn3 = true;
			b.drawing3 = false;
			drawPart(screen, position, curDir,rayRad, b, 3);
		}
		if(b.drawing2 && !b.drawn2 && screen.getBlock(b.x+1, b.y) == null){
			b.drawn2 = true;
			b.drawing2 = false;
			drawPart(screen, position, curDir,rayRad, b, 2);
		}
		
		else if(b.drawing4 && !b.drawn4 && screen.getBlock(b.x-1, b.y) == null){
			b.drawn4 = true;
			b.drawing4 = false;
			drawPart(screen, position, curDir,rayRad, b, 4);
		}
	}


	public int getRGB(int i, int j) {
		return data[i+j*width];
	}
}
