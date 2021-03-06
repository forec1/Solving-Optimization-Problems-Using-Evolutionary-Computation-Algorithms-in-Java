package hr.fer.zemris.trisat;

import java.util.Comparator;

/**
 * Komparator binarni vektora na osnovu njihovih statistika
 * @author filip
 *
 */
public class BitVectorStatComparator implements Comparator<BitVector>{
	
	private SATFormulaStats formulaStats;
	
	public BitVectorStatComparator(SATFormulaStats formulaStats) {
		this.formulaStats = formulaStats;
	}

	public int compare(BitVector o1, BitVector o2) {
		formulaStats.setAssignment(o1, false);
		int Z = formulaStats.getNumberOfSatisfied();
		double fit1 = Z + formulaStats.getPercentageBonus();
		
		formulaStats.setAssignment(o2, false);
		Z = formulaStats.getNumberOfSatisfied();
		double fit2 = Z + formulaStats.getPercentageBonus();
		
		if(fit1 > fit2) {
			return -1;
		} else if (fit1 < fit2) {
			return 1;
		} 
		return 0;
	}

}
