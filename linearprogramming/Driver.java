/**
 * Driver class for testing linear programming classes
 */
package linearprogramming;

/**
 * @author Marshall Farrier
 * @version 4/27/11
 */
public class Driver {
	private static void show(double[] arr) {
		if (arr == null) {
			System.out.println("unbounded");
			return;
		}
		int len = arr.length;
		
		System.out.print("[");
		if (len > 0) System.out.print(arr[0]);
		for (int i = 1; i < len; ++i) {
			System.out.print(", " + arr[i]);
		}
		System.out.println("]");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		// Verify book example, CLRS, p. 865 
		double[][] A = {{1, 1, 3}, {2, 2, 5}, {4, 1, 2}};
		double[] b = {30, 24, 36};
		double[] c = {3, 1, 2};
		*/
		/*
		// Problem 29.3-5, p. 878 
		double[][] A = {{1, 1}, {1, 0}, {0, 1}};
		double[] b = {20, 12, 16};
		double[] c = {18, 12.5};
		*/
		
		// Problem 29.3-6, p. 879 
		double[][] A = {{1, -1}, {2, 1}};
		double[] b = {1, 2};
		double[] c = {5, -3};
		//*/
		Simplex s = new Simplex(A, b, c);
		double[] x = s.getResult();
		show(x);
	}

}
