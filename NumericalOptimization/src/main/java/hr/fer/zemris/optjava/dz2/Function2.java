package hr.fer.zemris.optjava.dz2;

import Jama.Matrix;

/**
 * Funkcija oblika f(x1, x2) = (x1 - 1)^2 + 10 * (x2 - 2)^2
 * @author filip
 *
 */
public class Function2 implements IHFunction{

	public int getNumberOfVariables() {
		return 2;
	}

	public double value(Matrix x) {
		double val = Math.pow(x.get(0, 0) - 1, 2) + 10 * Math.pow(x.get(1, 0) - 2, 2);
		return val;
	}

	public Matrix getGrad(Matrix x) {
		double grad1 = 2 * (x.get(0, 0) - 1);
		double grad2 = 2 * 10 * (x.get(1, 0) - 2);
		double[][] grad = {{grad1},
						   {grad2}};
		
		return new Matrix(grad);
	}

	public Matrix getHesseMatrix(Matrix x) {
		double[][] h = {{2, 0},
						  {0, 20}};
		return new Matrix(h);
	}
	

}