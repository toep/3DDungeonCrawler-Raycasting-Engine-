
public class Block {
	
	public int x;
	public int y;
	public final float width = 1;
	public final float length = 1;
	
	Vector2 collidingVector1;
	static float distanceRay;
	Vector2 collidingVector2;
	//static float distanceRay2;
	Vector2 collidingVector3;
	//static float distanceRay3;
	Vector2 collidingVector4;
	//static float distanceRay4;
	boolean drawn1 = false;
	boolean drawn2 = false;
	boolean drawn3 = false;
	boolean drawn4 = false;
	boolean drawing1 = false;
	boolean drawing2 = false;
	boolean drawing3 = false;
	boolean drawing4 = false;
	public Block(int x, int y){
		this.x = x;
		this.y = y;
	}
	Vector2 get_line_intersection(float p0_x, float p0_y, float p1_x, float p1_y, 
		    float p2_x, float p2_y, float p3_x, float p3_y)
	{
	    float s02_x, s02_y, s10_x, s10_y, s32_x, s32_y, s_numer, t_numer, denom, t;
	    s10_x = p1_x - p0_x;
	    s10_y = p1_y - p0_y;
	    s32_x = p3_x - p2_x;
	    s32_y = p3_y - p2_y;

	    denom = s10_x * s32_y - s32_x * s10_y;
	    if (denom == 0)
	        return null; // Collinear
	    boolean denomPositive = denom > 0;

	    s02_x = p0_x - p2_x;
	    s02_y = p0_y - p2_y;
	    s_numer = s10_x * s02_y - s10_y * s02_x;
	    if ((s_numer < 0) == denomPositive)
	        return null; // No collision

	    t_numer = s32_x * s02_y - s32_y * s02_x;
	    if ((t_numer < 0) == denomPositive)
	        return null; // No collision

	    if (((s_numer > denom) == denomPositive) || ((t_numer > denom) == denomPositive))
	        return null; // No collision
	    // Collision detected
	    t = t_numer / denom;
	    return new Vector2(p0_x + (t * s10_x), p0_y + (t * s10_y));
	}
	public boolean collidesWithRay(Ray in){
		Vector2 res1 = get_line_intersection(in.getPosition().x, in.getPosition().y, in.getPosition().x+(float)(80*(in.getDirection().x)), in.getPosition().y+(float)(80*(in.getDirection().y)), x, y, x+width, y);
		if(res1 != null && (res1.y > in.getPosition().y)){
			collidingVector1 = res1;
			drawing1 = true;
			return true;
		}
		//right wall
		Vector2 res2 = get_line_intersection(in.getPosition().x, in.getPosition().y, in.getPosition().x+(float)(80*(in.getDirection().x)), in.getPosition().y+(float)(80*(in.getDirection().y)), x+width, y, x+width, y+length);
		if(res2 != null && (res2.x < in.getPosition().x) ){
			collidingVector2 = res2;
			drawing2 = true;
			return true;
		}
		//back wall
		Vector2 res3 = get_line_intersection(in.getPosition().x, in.getPosition().y, in.getPosition().x+(float)(80*(in.getDirection().x)), in.getPosition().y+(float)(80*(in.getDirection().y)), x, y+length, x+width, y+length);
		if(res3 != null && (res3.y < in.getPosition().y)){
			collidingVector3 = res3;
			drawing3 = true;
			return true;
		}
		//left
		Vector2 res4 = get_line_intersection(in.getPosition().x, in.getPosition().y, in.getPosition().x+(float)(80*(in.getDirection().x)), in.getPosition().y+(float)(80*(in.getDirection().y)), x, y, x, y+length);
		if(res4 != null && (res4.x > in.getPosition().x) ){
			collidingVector4 = res4;
			drawing4 = true;
			return true;
		}
		return false;
	}
	public void resetRenderingVariables() {
		collidingVector1 = null;
		collidingVector2 = null;
		collidingVector3 = null;
		collidingVector4 = null;
		drawn1 = false;
		drawn2 = false;
		drawn3 = false;
		drawn4 = false;
		drawing1 = false;
		drawing2 = false;
		drawing3 = false;
		drawing4 = false;
	}
	public void setDrawing(Vector2 pos) {
		if(pos.y > y) drawing3 = true;
		if(pos.y-1 < y) drawing1 = true;
		if(pos.x > x) drawing2 = true;
		if(pos.x-1 < x) drawing4 = true;
	}
}
