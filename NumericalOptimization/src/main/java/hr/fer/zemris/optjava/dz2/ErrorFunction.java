package hr.fer.zemris.optjava.dz2;

import Jama.Matrix;

/**
 * Function that represents error of system of linear equations.
 * @author filip
 *
 */
public class ErrorFunction implements IHFunction{
	
	/**
	 * Matrix that holds coefficients of system of linear equations.
	 */
	private Matrix systemMatrix;
	
	/**
	 * Number of rows in systemMatrix
	 */
	private int numOfRows;
	
	/**
	 * Number of columns in systemMatrix
	 */
	private int numOfColumns;
	
	/**
	 * Number of variables in system of linear equations
	 */
	private int numOfVariables;
	
	/**
	 * Column vector of constant terms
	 */
	private Matrix y;
	
	/**
	 * Constructs new error function for given system of linear equations.
	 * @param systemOfLinearEquations
	 */
	public ErrorFunction(Matrix systemOfLinearEquations) {
		this.numOfRows = systemOfLinearEquations.getRowDimension();
		this.numOfColumns = systemOfLinearEquations.getColumnDimension();
		this.systemMatrix = systemOfLinearEquations.getMatrix(0, numOfRows - 1, 0, numOfColumns - 2);
		this.numOfVariables = numOfColumns - 1;
		double[][] yi = new double[numOfRows][1];
		for(int i = 0; i < numOfRows; i++) {
			yi[i][0] = systemOfLinearEquations.get(i, numOfColumns - 1);
		}
		y = new Matrix(yi);
	}
	
	public int getNumberOfVariables() {
		return numOfVariables;
	}

	public double value(Matrix x) {
		if(x.getRowDimension() != systemMatrix.getRowDimension()) {
			throw new IllegalArgumentException("Number of rows of x is wrong!");
		}
		if(x.getColumnDimension() != 1) {
			throw new IllegalArgumentException("x must be vector");
		}
		
		// n1*x1+...+n10*x10 - yi
		double[] error = new double[numOfVariables]; // pogreška za svaku funkciju posebno
		for(int i = 0; i < numOfVariables; i++) {
			for(int j = 0; j < numOfVariables; j++) {
				error[i] += systemMatrix.get(i, j) * x.get(j, 0);
			}
			error[i] -= y.get(i, 0);
		}
		for(int i = 0; i < numOfVariables; i++) {
			error[i] = Math.pow(error[i], 2);
		}
		double sum = 0.0;
		for(int i = 0; i < numOfVariables; i++) {
			sum += error[i];
		}
		return sum;
	}

	public Matrix getGrad(Matrix x) {
		if(x.getRowDimension() != systemMatrix.getRowDimension()) {
			throw new IllegalArgumentException("Number of rows of x is wrong!");
		}
		if(x.getColumnDimension() != 1) {
			throw new IllegalArgumentException("x must be a vector");
		}
		
		double[][] grad = new double[numOfVariables][1];
		
		// 2*(n1*x1+...+n10*x10 - y1)
		// 2*(n1*x1+...+n10*x10 - y2) ...
		Matrix error = systemMatrix.times(x).minus(y).times(2);		// N x 1
		for(int i = 0; i < numOfVariables; i++) {
			double gradi = 0.0;
			for(int j = 0; j < numOfVariables; j++) {
				
				// grad_1 = 2*(n1*x1+...+n10*x10 - y1)*n1 + 2*(n1*x1+...+n10*x10)*n1 + ... 
				// grad_2 = 2*(n1*x1+...+n10*x10 - y1)*n2 + 2*(n1*x1+...+n10*x10)*n2 + ...
				gradi += error.get(j, 0) * systemMatrix.get(j, i);
			}
			grad[i][0] = gradi;
		}
		return new Matrix(grad);
	}

	public Matrix getHesseMatrix(Matrix x) {
		if(x.getRowDimension() != systemMatrix.getRowDimension()) {
			throw new IllegalArgumentException("Number of rows of x is wrong!");
		}
		if(x.getColumnDimension() != 1) {
			throw new IllegalArgumentException("x must be a vector");
		}
		Matrix hesse = new Matrix(numOfVariables, numOfVariables);
		
		/*  
		 * d2f/dx1dx2 = 2*n1*n2 
		 * d2f/dx1dx3 = 2*n1*n3  	*/
		for(int i = 0; i < numOfVariables; i++) {
			for(int j = 0; j < numOfVariables; j++) {
				double val = 0.0;
				for(int k = 0; k < numOfVariables; k++) {
					val += 2 * systemMatrix.get(k, i) * systemMatrix.get(k, j);
				}
				hesse.set(i, j, val);
			}
		}
		return hesse;
	}
}