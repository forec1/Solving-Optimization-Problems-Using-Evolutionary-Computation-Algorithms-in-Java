package hr.fer.zemris.optjava.dz2;

import Jama.Matrix;

public interface IFunction {
	
	/**
	 * @return Number of variables
	 */
	public int getNumberOfVariables();
	
	/**
	 * Calculates value of the function for given input. Input x 
	 * must be a vector with dimensions (N x 1) where N is 
	 * function's number of variables. 
	 * @param x Input vector
	 * @return Value of the function for given input
	 */
	public double value(Matrix x);
	
	/**
	 * Calculates gradient vector of the function in specified point.
	 * Input x must be a vector with dimensions (N x 1) where N is 
	 * function's number of variables. 
	 * @param x Input vector
	 * @return Gradient vector in specified point
	 */
	public Matrix getGrad(Matrix x);
}
