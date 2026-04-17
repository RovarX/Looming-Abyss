package exception;

/**非法的放置位置*/
public class IllegalPlacementException extends RuntimeException{
	public IllegalPlacementException(String message){
		super(message);
	}
}
