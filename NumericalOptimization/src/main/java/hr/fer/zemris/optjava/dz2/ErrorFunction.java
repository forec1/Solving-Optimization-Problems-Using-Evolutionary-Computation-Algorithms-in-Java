package hr.fer.zemris.optjava.dz2;

import Jama.Matrix;

public class ErrorFunction implements IHFunction{
	
	private Matrix systemMatrix;
	private int numOfRows;
	private int numOfColumns;
	private int numOfVariables;
	private double[] yi;
	
	public ErrorFunction(Matrix systemMatrix) {
		this.systemMatrix = systemMatrix;
		this.numOfRows = systemMatrix.getRowDimension();
		this.numOfColumns = systemMatrix.getColumnDimension();
		this.numOfVariables = numOfColumns - 1;
		yi = new double[numOfRows];
		for(int i = 0; i < numOfRows; i++) {
			yi[i] = systemMatrix.get(i, numOfColumns - 1);
		}
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
		double val = 0.0;
		for(int i = 0; i < numOfRows; i++) {
			for(int j = 0; j < numOfColumns - 1; j++) {
				val += systemMatrix.get(i, j) * x.get(j, 0);
			}
			val = Math.pow(val - yi[i], 2);
		}
		return val;
	}

	public Matrix getGrad(Matrix x) {
		if(x.getRowDimension() != systemMatrix.getRowDimension()) {
			throw new IllegalArgumentException("Number of rows of x is wrong!");
		}
		if(x.getColumnDimension() != 1) {
			throw new IllegalArgumentException("x must be vector");
		}
		double[][] grad = new double[numOfVariables][1];
		for(int i = 0; i < numOfRows; i++) {
			for(int j = 0; j < numOfColumns - 1; j++) {
				grad[i][0] += systemMatrix.get(i, j) * x.get(j, 0);
			}
			grad[i][0] = 2 * (grad[i][0] - yi[i]) * x.get(i, 0); 
		}
		return new Matrix(grad);
	}

	public Matrix getHesseMatrix(Matrix x) {
		// TODO Auto-generated method stub
		return null;
	}

}