package hr.fer.zemris.optjava.dz7.nn;

import java.util.LinkedList;
import java.util.List;

import hr.fer.zemris.optjava.dz7.dataset.IReadOnlyDataSet;
import hr.fer.zemris.optjava.dz7.function.ITransferFunction;

public class FFANN {
	
	private int[] networkConfiguration;
	private ITransferFunction[] transferFunctions;
	private IReadOnlyDataSet dataset;
	private int numberOfWeights;
	private List<Neuron> neurons;
	
	public FFANN(int[] networkConfiguration, ITransferFunction[] transferFunctions, IReadOnlyDataSet dataset) {
		if(networkConfiguration[0] != dataset.numberOfInputs()) {
			throw new RuntimeException("Input layer must have " + dataset.numberOfInputs() + " neurons");
		}
		if(networkConfiguration[networkConfiguration.length - 1] != dataset.numberOfOutputs()) {
			throw new RuntimeException("Output layer must have " + dataset.numberOfOutputs() + " neurons");
		}
		this.networkConfiguration = networkConfiguration;
		this.transferFunctions = transferFunctions;
		this.dataset = dataset;
		this.neurons = new LinkedList<Neuron>();
		calcNumberOfWeights();
		createNetwork();
	}
	
	private void createNetwork() {
		int wOffset = 0;
		int iOffset = 0;
		int oOffset = 0;
		for(int i = 0, n = networkConfiguration.length; i < n; i++) {
			for(int j = 0; j < networkConfiguration[i]; j++) {
				int weightsStartIndex = wOffset;
				int inputsStartIndex = iOffset;
				int numberOfInputs = 0;
				ITransferFunction transferFunction = null;
				boolean isInputLayer = true;
				if(i != 0) {
					numberOfInputs = networkConfiguration[i - 1];
					wOffset += numberOfInputs;
					wOffset += 1;	//tezina samo za taj neuron, ako nije u ulaznom sloju
					transferFunction = transferFunctions[i - 1];
					isInputLayer = false;
				}
				neurons.add(new Neuron(transferFunction, weightsStartIndex, inputsStartIndex, numberOfInputs, oOffset, isInputLayer));
				oOffset++;				
			}
			if(i != 0) {
				iOffset += networkConfiguration[i - 1];
			}
		}
	}
	
	public IReadOnlyDataSet getDataset() {
		return dataset;
	}
	
	public void calcOutputs(double[] inputs, double[] weights, double[] outputs) {
		double[] inputsAndOutputs = new double[gerNumberOfNeurons()];
		for(int i = 0, n = inputs.length; i < n; i++) {
			inputsAndOutputs[i] = inputs[i];
		}
		
		for(Neuron neuron : neurons) {
			inputsAndOutputs = neuron.calcOutput(weights, inputsAndOutputs); 	// moguca greska zbog referenci
		}
		int lastLayer = dataset.numberOfOutputs();
		for(int n = inputsAndOutputs.length, i = n - lastLayer, j = 0; i < n; i++, j++) {
			outputs[j] = inputsAndOutputs[i];
		}
		
	}
	
	public int getWeightsCount() {
		return numberOfWeights;
	}
	
	private int gerNumberOfNeurons() {
		int numOfOutputs = 0;
		for(int inLayer : networkConfiguration) {
			numOfOutputs += inLayer;
		}
		return numOfOutputs;
	}
	
	private void calcNumberOfWeights() {
		numberOfWeights = 0;
		for(int i = 0, n = networkConfiguration.length - 1; i < n; i++) {
			numberOfWeights += networkConfiguration[i] * networkConfiguration[i + 1];
			numberOfWeights += networkConfiguration[i + 1];
		}
	}
	
	
}
