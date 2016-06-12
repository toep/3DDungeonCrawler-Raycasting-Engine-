
public class Ray {
	private Vector2 position;
	private Vector2 direction;
	
	public Ray(Vector2 pos, Vector2 dir){
		position = pos;
		direction = dir;
	}
	
	public Vector2 getPosition() {return position;}
	public Vector2 getDirection() {return direction;}
}
