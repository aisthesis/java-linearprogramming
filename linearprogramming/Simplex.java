/**
 * Implementation of CLRS, pp. 869ff.
 * The constructor allows input of the initial parameters.
 * Then the getResult() method returns the result.
 * A, B, and c are the inputs in standard form
 * 
 * To improve (5/24/11) after reading 29.5:
 * - Add a variable double[] result
 * - Have the constructor execute the program, setting
 *   result to null if the solution is unbounded or if there
 *   is no solution
 * - have getResult() return a copy of the solution (to
 *   keep actual solution protected from tampering)
 * - add public static final int fields: HAS_FINITE_SOLUTION,
 *   INFEASIBLE, UNBOUNDED
 * - add a private int field solvability and a public method
 *   solvability() to determine whether program is unbounded,
 *   infeasible, or has finite solution.
 */
package linearprogramming;

/**
 * @author Marshall Farrier
 * @version 0.1 4/25/11
 */
public class Simplex {
	// Following CLRS notation (cf. p. 856)
	private int n;	// number of non-basic variables
	private int m;	// number of basic variables
	private int[] N;	// list of non-basic variables
	private int[] B;	// list of basic variables
	private double[][] A;	// constraint matrix
	private double[] b;		// constants for inequality constraints
	private double[] c;		// coefficients of objective function
	private double v;		// constant in objective function
	
	public Simplex(double[][] A1, double[] b1, double[] c1) {
		m = A1.length;
		n = A1[0].length;
		A = new double[m][n];
		b = new double[m];
		c = new double[n];
		int i, j;
		// Initialize A, b
		for (i = 0; i < m; ++i) {
			for (j = 0; j < n; ++j) {
				A[i][j] = A1[i][j];
			}
			b[i] = b1[i];
		}
		// Initialize c
		for (j = 0; j < n; ++j) {
			c[j] = c1[j];
		}
		// Initialize N, B and v
		initializeSimplex();
	}
	
	/**
	 * This should implement the simplex() function CLRS, p. 871.
	 * Returns null if the objective function is unbounded
	 */
	public double[] getResult() {
		double[] delta = new double[m];	// cf. CLRS errata
		double[] x = new double[n];		// will hold result
		int e = findEnteringIndex(), l;
		int i;
		while (e >= 0) {
			// Find an appropriate leaving index
			for (i = 0; i < m; ++i) {
				if (A[i][e] > 0) {
					delta[i] = b[i] / A[i][e];
				}
				else {
					delta[i] = Double.MAX_VALUE;
				}
			}
			l = indexOfMinValue(delta);
			if (delta[l] == Double.MAX_VALUE) return null;
			pivot(l, e);
			e = findEnteringIndex();
			
		}
		// Note that x has already been initialized to all 0s
		for (i = 0; i < m; ++i) {
			if (B[i] < n) x[B[i]] = b[i];
		}
		return x;
	}
	
	// TODO verify in section 29.5
	/**
	 * Initializes N, B, and v appropriately
	 * Non-basic variables are initialized to 0, ..., n - 1
	 * Basic variables are initialized to n, ... n + m - 1
	 */
	private void initializeSimplex() {
		N = new int[n];
		B = new int[m];
		int i;
		
		for (i = 0; i < n; ++i) {
			N[i] = i;
		}
		for (i = 0; i < m; ++i) {
			B[i] = i + n;
		}
		v = 0;
	}
	
	// CLRS, p. 869
	/**
	 * Parameters l and e are the INDICES in N and B 
	 * of the respective variables. The entering variable
	 * is the non-basic variable (that becomes basic); the leaving
	 * variable is the basic variable (that becomes non-basic)
	 */
	private void pivot(int l, int e) {
		int i, j;
		// CLRS creates a new array, but this seems to be doable in place
		// double[][] A2 = new double[m][n];
		// Cf. CLRS line 3
		b[l] = b[l] / A[l][e];
		// Enter values for the l-th row of A2 (except the e-th column)
		// This is the new equation for the entering variable
		for (j = 0; j < n; ++j) {
			if (j != e) {
				A[l][j] = A[l][j] / A[l][e];
			}
		}
		A[l][e] = 1 / A[l][e];
		// Compute the coefficients for the remaining constraints
		for (i = 0; i < m; ++i) {
			if (i != l) {
				b[i] = b[i] - A[i][e] * b[l];
				for (j = 0; j < n; ++j) {
					if (j != e) {
						A[i][j] = A[i][j] - A[i][e] * A[l][j];
					}
				}
				A[i][e] = - A[i][e] * A[l][e];
			}
		}
		
		// Compute the objective function
		v = v + b[l] * c[e];
		for (j = 0; j < n; ++j) {
			if (j != e) {
				c[j] = c[j] - c[e] * A[l][j];
			}
		}
		c[e] = - c[e] * A[l][e];
		
		// Compute the new sets of basic and non-basic variables.
		// (swap N[e] and B[l])
		int tmp = N[e];
		N[e] = B[l];
		B[l] = tmp;
	}
	
	/**
	 * Used in lines 3 and 4 of the simplex() code in CLRS, p. 871.
	 * Returns -1 if there is no index i s.t. c[i] > 0.
	 * Otherwise returns the index with the smallest variable value, thus
	 * preventing cycling using Bland's rule (p. 877). Note that we
	 * are not choosing the smallest i s.t. c[i] > 0 but rather that
	 * value of i s.t. if c[j] > 0 then N[i] < N[j]
	 * @return
	 */
	private int findEnteringIndex() {
		// Initialize to a value higher than any valid index
		int smallest = m + n;
		for (int i = 0; i < n; ++i) {
			if (c[i] > 0 && N[i] < smallest) {
				smallest = i;
			}
		}
		if (smallest < n) return smallest;
		return -1;
	}
	
	/**
	 * Assumes that input array has length m
	 * Used in line 9 of simplex() code, CLRS, p. 871
	 * @param delta
	 * @return
	 */
	private int indexOfMinValue(double[] delta) {
		int result = 0, i;
		double min = delta[0];
		for (i = 1; i < m; ++i) {
			if (delta[i] < min) {
				result = i;
				min = delta[i];
			}
		}
		// Deal with ties according to Bland's rule (p. 877)
		for (i = 0; i < m; ++i) {
			// This difference will be considered a tie
			if (delta[i] < min + 16 * Double.MIN_NORMAL && B[i] < B[result]) {
				result = i;
			}
		}
		return result;
	}
}
