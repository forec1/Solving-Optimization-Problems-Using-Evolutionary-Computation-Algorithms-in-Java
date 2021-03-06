package hr.fer.zemris.trisat;

/**
 * Statistički podatci formule za predano rješenje.
 * @author filip
 *
 */
public class SATFormulaStats {
	
	private SATFormula formula;
	private int numberOfSatisfied;
	private boolean isSatisfied;
	private double precentageBonus;
	private double[] post;
	private final double precentageConstantUp = 0.01;
	private final double precentageConstantDown = 0.1;
	private final double precentageUnitAmount = 50;
	
	public SATFormulaStats(SATFormula formula) {
		this.formula = formula;
		int numberOfClaueses = formula.getNumberOfClauses();
		post = new double[numberOfClaueses];
		for(int i = 0; i < numberOfClaueses; i++) {
			post[i] = 0.0;
		}
	}
	
	public void setAssignment(BitVector assignment, boolean updatePercentages) {
		int numberOfClauses = formula.getNumberOfClauses();
		precentageBonus = 0;
		numberOfSatisfied = 0;
		for(int i = 0; i < numberOfClauses; i++) {
			Clause clause = formula.getClause(i);
			boolean isSatisfied = clause.isSatisfied(assignment);
			if(isSatisfied && updatePercentages) {
				numberOfSatisfied++;
				post[i] += (1 - post[i]) * precentageConstantUp; 
				precentageBonus += precentageUnitAmount * (1 - post[i]);
			} else if (!isSatisfied && updatePercentages) {
				post[i] += (0 - post[i]) * precentageConstantDown;
				precentageBonus += precentageUnitAmount * (1 - post[i]);
			} else if (isSatisfied) {
				numberOfSatisfied++;
				precentageBonus += precentageUnitAmount * (1 - post[i]);
			} else if (!(isSatisfied)) {
				precentageBonus -= precentageUnitAmount * (1 - post[i]);
			}
		}
		isSatisfied = formula.isSatisfied(assignment);
	}
	
	public int getNumberOfSatisfied() {
		return numberOfSatisfied;
	}
	
	public boolean isSatisfied() {
		return isSatisfied;
	}
	
	public double getPercentageBonus() {
		return precentageBonus;
	}
	
	public double getPercentage(int index) {
		return post[index];
	}
}
