package hr.fer.zemris.trisat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class Algorithms {
	
	
	/**
	 * Provjerava dali je dana formula zadovoljiva tako da provjerava sva
	 * moguća rješenja i ako postoji vrača ga. U slučaju da postoji više od
	 * jednog rješenja vrača se samo jedno.
	 * @param formula Formula koja se provjerava
	 * @return Rješenje dane formule ako postoji, null inače
	 */
	public static BitVector allPossibilitiesAlg(SATFormula formula) {
		
		int numberOfVariables = formula.getNumberOfVariables();
		BitVector ret = null;
		MutableBitVector bits = new MutableBitVector(numberOfVariables);
		
		for(int i = 1; i <= Math.pow(2, numberOfVariables); i++) {
			if(formula.isSatisfied(bits)) {
//				System.out.println(bits);
				if(ret == null) {
					
					//nova referenca za rijesenje
					ret = bits.copy();
				}
			}
			bits.set(numberOfVariables - 1, !bits.get(numberOfVariables - 1));
			
			int temp = numberOfVariables - 1;
			while(temp > 0) {
				if(i % Math.pow(2, temp) == 0) {
					int index = numberOfVariables - temp - 1;
					boolean value = bits.get(index);
					bits.set(index, !value);
				}
				temp--;
			}
		}
		return ret;
	}
	
	/**
	 * Iterativni algoritam pretraživanja koji provjerava dali je 
	 * dana formula zadovoljiva i vrača rješenje ako da. U slučaju
	 * da postoji više od jednog rješenja vrača samo jedno.
	 * @param formula Formula koja se provjerava
	 * @return Rješenje dane formule ako postoji, null inače
	 */
	public static BitVector iterAlg(SATFormula formula) {
		
		int numberOfVariables = formula.getNumberOfVariables();
		BitVector solution = new BitVector(new Random(), numberOfVariables);
		int paramIter = 100000;
		while(paramIter > 0) {
			
			if(formula.isSatisfied(solution)) {
				System.out.println("iteracija: " + (100000 - paramIter));
				return solution;
			}
			
			double fit = fit(solution, formula);
			BitVector[] neighborhood = shiftFunction(solution);
			List<BitVector> bestSolutions = getBestSolutions(neighborhood, formula);
					
			
			// ako je maksimalna dobrota susjeda manja od dobrote rjesenja dojavi neuspjeh
			if(fit(bestSolutions.get(0), formula) < fit) { 
				System.out.println("Local minimum! Failure!");
				return null;
			}
			
			//biranje nasumičnog rjesenja
			int index = (new Random()).nextInt(bestSolutions.size());
			solution = bestSolutions.get(index);
			
			paramIter--;
		}
		return null;
	}
	
	/**
	 * Modificiarni iterativni algoritam pretraživanja koji provjerava dali je 
	 * dana formula zadovoljiva i vrača rješenje ako da. U slučaju
	 * da postoji više od jednog rješenja vrača samo jedno.
	 * @param formula Formula koja se provjerava
	 * @return Rješenje dane formule ako postoji, null inače
	 */
	public static BitVector iterAlgMod(SATFormula formula) {
		int numberOfVariables = formula.getNumberOfVariables();
		BitVector solution = new BitVector(new Random(), numberOfVariables);
		SATFormulaStats formulaStats = new SATFormulaStats(formula);
		int paramIter = 100000;
		while(paramIter > 0) {
			if(formula.isSatisfied(solution)) {
				System.out.println("iteracija: " + (100000 - paramIter));
				return solution;
			}
			
			formulaStats.setAssignment(solution, true);
			
			List<BitVector> neighborhood = Arrays.asList(shiftFunction(solution));
			Collections.sort(neighborhood, new BitVectorStatComparator(formulaStats));
			int numberOfBest = 2;
			List<BitVector> bestSolutions = neighborhood.subList(0, numberOfBest);
			int index = (new Random()).nextInt(numberOfBest);
			solution = bestSolutions.get(index);
			
			paramIter--;
		}
		return null;
	}
	
	/**
	 * Multistart Local Search algoritam koji provjerava dali je 
	 * dana formula zadovoljiva i vrača rješenje ako da. U slučaju
	 * da postoji više od jednog rješenja vrača samo jedno.
	 * @param formula Formula koja se provjerava
	 * @return Rješenje dane formule ako postoji, null inače
	 */
	public static BitVector GSAT(SATFormula formula) {
		int numberOfVariables = formula.getNumberOfVariables();
		
		//parameter
		int paramMaxTries = 100000;
		for(int restart = 1; restart < paramMaxTries; restart++) {
			BitVector T = new BitVector(new Random(), numberOfVariables);
			
			//parameter
			int paramMaxFlips = 50;
			for(int promjena = 1; promjena < paramMaxFlips; promjena++) {
				if(formula.isSatisfied(T)) {
					System.out.println("iteracija: " + restart);
					return T;
				}
				
				int index = getBestToFlip(T, formula);
				MutableBitVector tmp = T.copy();
				tmp.set(index, !tmp.get(index));
				T = tmp;
			}
		}
		return null;
	}
	
	/**
	 * Multistart Local Search algoritam kod kojeg lokalna pretraga
	 * nije deterministički pohlepna koji provjerava dali je 
	 * dana formula zadovoljiva i vrača rješenje ako da. U slučaju
	 * da postoji više od jednog rješenja vrača samo jedno.
	 * @param formula Formula koja se provjerava
	 * @return Rješenje dane formule ako postoji, null inače
	 */
	public static BitVector RandomWalkSAT(SATFormula formula) {
		int numberOfVariables = formula.getNumberOfVariables();
		Random rand = new Random(System.currentTimeMillis());
		
		//parameter
		int paramMaxTries = 100000;
		
		for(int restart = 1; restart < paramMaxTries; restart++) {
			BitVector T = new BitVector(rand, numberOfVariables);
			
			//parameters
			int paramMaxFlips = 10;
			double probability = 0.5;
			
			for(int promjena = 1; promjena < paramMaxFlips; promjena++) {
				if(formula.isSatisfied(T)) {
					System.out.println("iteracija: " + restart);
					return T;
				}
				Clause clause = getUnsatisfied(T, formula);
				if(rand.nextDouble() <= probability) {
					MutableBitVector temp = T.copy();
					int index = rand.nextInt(clause.getSize());
					int indexOfLiteral = Math.abs(clause.getLiteral(index)) - 1;
					temp.set(index, !temp.get(indexOfLiteral));
					
				} else {
					int index = getBestToFlip(T, formula);
					MutableBitVector tmp = T.copy();
					tmp.set(index, !tmp.get(index));
					T = tmp;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Modificiarni iterativni algoritam pretraživanja koji provjerava dali je 
	 * dana formula zadovoljiva i vrača rješenje ako da. U slučaju
	 * da postoji više od jednog rješenja vrača samo jedno.
	 * @param formula Formula koja se provjerava
	 * @return Rješenje dane formule ako postoji, null inače
	 */
	public static BitVector ILS(SATFormula formula) {
		int numberOfVariables = formula.getNumberOfVariables();
		BitVector solution = new BitVector(new Random(), numberOfVariables);
		int paramIter = 100000;
		while(paramIter > 0) {
			
			if(formula.isSatisfied(solution)) {
				System.out.println("iteracija: " + (100000 - paramIter));
				return solution;
			}
			double fit = fit(solution, formula);
			BitVector[] neighborhood = shiftFunction(solution);
			List<BitVector> bestSolutions = getBestSolutions(neighborhood, formula);
						
			int index = (new Random()).nextInt(bestSolutions.size());
			solution = bestSolutions.get(index);
			
			if(fit(solution, formula) < fit) { 
				Random rand = new Random();
				int n = rand.nextInt(numberOfVariables);
				int[] randomIndexes = rand.ints(0, numberOfVariables).distinct()
					.limit(n).toArray();
				MutableBitVector temp = solution.copy();
				for(int randomIndex : randomIndexes) {
					temp.set(randomIndex, !temp.get(randomIndex));
				}
			}
			
			paramIter--;
		}
		return null;
	}
	
	private static List<BitVector> getBestSolutions(BitVector[] neighborhood, SATFormula formula) {
		List<BitVector> bestSolutions = new ArrayList<BitVector>();
		double max = 0;
		for(int i = 0; i < neighborhood.length; i++) {
			double newFit = fit(neighborhood[i], formula);
			if(newFit > max) {
				bestSolutions.clear();
				bestSolutions.add(neighborhood[i]);
				max = newFit;
			} else if (Math.abs(newFit - max) < 10e-6) {
				bestSolutions.add(neighborhood[i]);
			}
		}
		return bestSolutions;
	}
	
	private static int getBestToFlip(BitVector T, SATFormula formula) {
		BitVector[] neighborhood = shiftFunction(T);
		double max = 0;
		int index = 0;
		for(int i = 0; i < neighborhood.length; i++) {
			double fit = fit(neighborhood[i], formula);
			if(fit > max) {
				max = fit;
				index = i;
			}
		}
		return index;
	}
	
	private static Clause getUnsatisfied(BitVector solution, SATFormula formula) {
		List<Clause> unsatisfied = new ArrayList<Clause>();
		int numberOfClauses = formula.getNumberOfClauses();
		for(int i = 0; i < numberOfClauses; i++) {
			Clause clause = formula.getClause(i);
			if(!clause.isSatisfied(solution)) {
				unsatisfied.add(clause);
			}
		}
		int index = (new Random()).nextInt(unsatisfied.size());
		return unsatisfied.get(index);
	}
	
	private static double fit(BitVector solution, SATFormula formula) {
		int numberOfClauses = formula.getNumberOfClauses();
		int numberOfSatisfiedClauses = 0;
		for(int i = 0; i < numberOfClauses; i++) {
			boolean satisfied = formula.getClause(i).isSatisfied(solution);
			numberOfSatisfiedClauses += satisfied ? 1 : 0;
		}
		return numberOfSatisfiedClauses / numberOfClauses;
	}
	
	private static BitVector[] shiftFunction(BitVector solution) {
		BitVectorNGenerator gen = new BitVectorNGenerator(solution);
		return gen.createNeighborhood();
		
	}
}
