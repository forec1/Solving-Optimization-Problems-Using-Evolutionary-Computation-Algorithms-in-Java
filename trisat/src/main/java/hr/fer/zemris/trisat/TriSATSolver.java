package hr.fer.zemris.trisat;

public class TriSATSolver {

	public static void main(String[] args) {
		
		if(args.length != 2) {
			System.err.println("2 arguments were expected!");
			System.exit(0);
		}
		
		String selection = args[0];
		String path = args[1];
		Parser p = new Parser(path);
		
		Clause[] clauses = p.getClauses();
		int numberOfVariables = p.getNumberOfVariables();
		SATFormula formula = new SATFormula(numberOfVariables, clauses);
		
		if(selection.equals("1")) {
			BitVector solution = Algorithms.allPossibilitiesAlg(formula);
			checkSolution(solution);
		} else if(selection.equals("2")) {
			BitVector solution = Algorithms.iterAlg(formula);
			checkSolution(solution);
		} else if(selection.equals("3")) {
			BitVector solution = Algorithms.iterAlgMod(formula);
			checkSolution(solution);
		} else if(selection.equals("4")) {
			BitVector solution = Algorithms.GSAT(formula);
			checkSolution(solution);
		} else if(selection.equals("5")) {
			BitVector solution = Algorithms.RandomWalkSAT(formula);
			checkSolution(solution);
		} else if(selection.equals("6")) {
			BitVector solution = Algorithms.ILS(formula);
			checkSolution(solution);
		}
	}
	
	private static void checkSolution(BitVector solution) {
		if(solution != null) {
			System.out.println("Zadovoljivo: " + solution);
		} else {
			System.out.println("Dokazivanje nije uspjelo jer su potrošeni svi resursi!");
		}
	}

}
