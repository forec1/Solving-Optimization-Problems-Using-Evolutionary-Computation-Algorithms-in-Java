package hr.fer.zemris.optjava.dz2;

import Jama.Matrix;

public class NumOptAlgorithms {
	
	/**
	 * Gradient descent algorithm
	 * @param function Function
	 * @param maxIter Maximum number of iterations
	 * @param x0 Starting point
	 * @param callerClass Class that calls this method
	 * @return Minimum of function
	 */
	public static <T> Matrix gradDescentAlg(IFunction function, long maxIter, Matrix x0, Class<T> callerClass) {
		Matrix x = x0;
		ImageTrajectory im = null;
		if(callerClass.equals(Jednostavno.class)) {
			im = new ImageTrajectory(x);
		}
		for(int i = 0; i < maxIter; i++) {
			printVector(x);
			System.out.println("iteracija: " + i);
			Matrix grad = function.getGrad(x);
			if(isNullVector(grad)) {
				if(callerClass.equals(Jednostavno.class)) {
					im.save("/home/filip/Pictures/grad_descent_traj.PNG");
				}
				return x;
			}
			double lambda = optimizeLambda(function, x, grad);
			
			// x = x - lambda * grad
			x = x.minus(grad.times(lambda));
			if(callerClass.equals(Jednostavno.class)) {
				im.put(x);
			}
			
		}
		if(callerClass.equals(Jednostavno.class)) {
			im.save("/home/filip/Pictures/grad_descent_traj.PNG");
		}
		return x;
	}
	
	/**
	 * Newton method algorithm
	 * @param function Function
	 * @param maxIter Maximum number of iterations
	 * @param x0 Starting point
	 * @param callerClass Class that calls this method
	 * @return Minimum of function
	 */
	public static <T> Matrix newtonMethodAlg(IHFunction function, long maxIter, Matrix x0, Class<T> callerClass) {
		Matrix x = x0;
		ImageTrajectory im = null;
		if(callerClass.equals(Jednostavno.class)) {
			im = new ImageTrajectory(x);
		}
		for(int i = 0; i < maxIter; i++) {
			printVector(x);
			System.out.println("iteracija: " + i);
			Matrix grad = function.getGrad(x);
			if(isNullVector(grad)) {
				if(callerClass.equals(Jednostavno.class)) {
					im.save("/home/filip/Pictures/newton_traj.PNG");
				}
				return x;
			}
			Matrix hesse = function.getHesseMatrix(x);
			//tau = -H(x)^-1 * grad 
			Matrix tau = hesse.inverse().times(grad).times(-1);
			double lambda = optimizeLambda(function, x, grad);
			
			//x = x + lambda * tau
			x = x.plus(tau.times(lambda));
			if(callerClass.equals(Jednostavno.class)) {
				im.put(x);
			}
			
		}
		if(callerClass.equals(Jednostavno.class)) {
			im.save("/home/filip/Pictures/newton_traj.PNG");
		}
		return x;
	}
	
	private static void printVector(Matrix A) {
		for(int i = 0, n = A.getRowDimension(); i < n; i++) {
			System.out.println("x" + i + " = " + A.get(i, 0));
		}
		System.out.println();
	}
	
	private static double optimizeLambda(IFunction function, Matrix x, Matrix grad) {
		double lambda = 0.0;
		double lambdaLower = 0.0;
		double lambdaUpper = 1.0;
		
		double dThetadLambda = derivateTheta(function, grad, x, lambda);
		while(dThetadLambda < 0.0) {
			dThetadLambda = derivateTheta(function, grad, x, lambdaUpper);
			if(dThetadLambda < 0) { lambdaUpper *= 2; }
		}
		while(Math.abs(lambdaUpper - lambdaLower) > 10e-3) {
			lambda = (lambdaLower + lambdaUpper) / 2;
			dThetadLambda = derivateTheta(function, grad, x, lambda);
			if(Math.abs(dThetadLambda) < 10e-3) {
				break;
			} else if (dThetadLambda > 0) {
				lambdaUpper = lambda;
			} else if(dThetadLambda < 0) {
				lambdaLower = lambda;
			}
		}
		return lambda;
	}
	
	private static double derivateTheta(IFunction function, Matrix grad, Matrix x, double lambda) {
		// xShift = x - lambdaUpper * grad
		Matrix xShift = x.minus(grad.times(lambda));		// N x 1
		Matrix gradShift = function.getGrad(xShift);	// N x 1
		
		// (1 x N) * (N x 1) = (1 x 1)
		Matrix dThetadLambda = gradShift.times(-1).transpose().times(grad); //scalar
		return dThetadLambda.get(0, 0);
	}
	
	private static boolean isNullVector(Matrix vector) {
		int n = vector.getRowDimension();
		boolean isNullVector = true;
		for(int i = 0; i < n; i++) {
			if(Math.abs(vector.get(i, 0)) > 10e-2) {
				isNullVector = false;
			}
		}
		return isNullVector;
	}
}