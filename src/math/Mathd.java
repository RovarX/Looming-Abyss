package math;

public class Mathd {
	public static double DOUBLE_ROUNDING_ERROR = 1E-7;

	public static boolean equals(double a, double b){
		return Math.abs(a-b)<=DOUBLE_ROUNDING_ERROR;
	}
}
