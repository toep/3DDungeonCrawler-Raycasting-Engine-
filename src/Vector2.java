
public class Vector2 {
	public float x, y;
	
	public Vector2(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[" + x + ", " + y + "]";
	}


	public float dis(Vector2 p) {
		return (float) Math.sqrt((x-p.x)*(x-p.x)+(y-p.y)*(y-p.y));
	}
}
