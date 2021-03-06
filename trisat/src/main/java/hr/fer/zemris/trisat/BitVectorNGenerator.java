package hr.fer.zemris.trisat;

import java.util.Iterator;

/**
 * Predstavlja funkciju pomaka.
 * @author filip
 *
 */
public class BitVectorNGenerator implements Iterable<MutableBitVector>{
	
	/**
	 * Binarni vektor.
	 */
	private BitVector assignment;
	
	/**
	 * Duljina binarnog vektora
	 */
	private int size;
	
	/**
	 * Stvara novi BitVectorNGenerator za zadani binarni
	 * vektor.
	 * @param assignment Binarni vektor
	 */
	public BitVectorNGenerator(BitVector assignment) {
		this.assignment = assignment.copy();
		size = assignment.getSize();
	}
	
	/**
	 * Vraća iterator koji na svaki next() računa sljedećeg susjeda.
	 */
	public Iterator<MutableBitVector> iterator() {
		Iterator<MutableBitVector> iterator = new Iterator<MutableBitVector>() {
			
			private int currentIndex = 0;
			
			public boolean hasNext() {
				if(currentIndex < size) {
					return true;
				}
				return false;
			}

			public MutableBitVector next() {
				boolean newValue = !assignment.get(currentIndex);
				MutableBitVector temp = assignment.copy();
				temp.set(currentIndex, newValue);
				currentIndex++;
				return temp;
			}
		};
		return iterator;
	}
	
	/**
	 * Stvara cijelo susjedstvo zadanog binarnog vektora i 
	 * vrača ga kao jedno polje.
	 * @return susjedstvo binarnog vektora
	 */
	public BitVector[] createNeighborhood() {
		BitVector[] neighborhood = new BitVector[size];
		for(int i = 0; i < size; i++) {
			boolean newValue = !assignment.get(i);
			MutableBitVector temp = assignment.copy();
			temp.set(i, newValue);
			neighborhood[i] = temp;
		}
		return neighborhood;
	}
	
}
