package beastplugin.thmm;
/*
 * MarkovModulatedModel.java
 *
 * Copyright (C) 2002-2009 Alexei Drummond and Andrew Rambaut
 *
 * This file is part of BEAST.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership and licensing.
 *
 * BEAST is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * BEAST is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with BEAST; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301  USA
 */

import dr.app.beagle.evomodel.substmodel.*;
import dr.evolution.datatype.Codons;
import dr.evolution.datatype.HiddenCodons;
import dr.inference.model.Parameter;
import dr.math.matrixAlgebra.Vector;

/**
 * Mark Suchard wrote MarkovModulatedGY94CodonModel on which this class is based.
 * @author Marc A. Suchard
 * @author Mark T. Holder
 */
public class MarkovModulatedModel extends BaseSubstitutionModel {

    private static final byte RATE = 5;
    protected byte[] rateMap;

    public MarkovModulatedModel(
            HiddenCodons dataType,
            Parameter switchingRates,
            BaseSubstitutionModel model1,
            BaseSubstitutionModel model2,
            FrequencyModel freqModel) {
        this(dataType, switchingRates, model1, model2, freqModel,
                (EigenSystem) new DefaultEigenSystem(dataType.getStateCount()));
    }

    public MarkovModulatedModel(
            HiddenCodons dataType,
            Parameter switchingRates,
            BaseSubstitutionModel model1,
            BaseSubstitutionModel model2,
            FrequencyModel freqModel,
            EigenSystem eigenSystem) {

        super("MarkovModulatedModel", dataType, freqModel, eigenSystem);
        this.subModelArray = new BaseSubstitutionModel[2];
        this.subModelArray[0] = model1;
        this.subModelArray[1] = model1;
        this.hiddenClassCount = dataType.getHiddenClassCount();
        this.switchingRates = switchingRates;
        addVariable(switchingRates);
        this.statesPerSubModelCount = freqModel.getFrequencyCount()/this.hiddenClassCount;

        // Subclassed constructors fill relativeRates with 1
        for (int i = 0; i < relativeRates.length; i++)
            relativeRates[i] = 0.0;
        this.unNormQ = new double[this.statesPerSubModelCount][this.statesPerSubModelCount][this.hiddenClassCount];
    }


    protected void setupRelativeRates(double[] relativeRates) {
        double[] rates = switchingRates.getParameterValues();
        int stateCount = this.stateCount / hiddenClassCount;
        for (int h = 0; h < hiddenClassCount; h++) {
        	BaseSubstitutionModel modelH = this.subModelArray[h];
        	
        	
        }
        for (int h = 0; h < hiddenClassCount; h++) {
        	BaseSubstitutionModel modelH = this.subModelArray[h];	
        	final double [][] subUnnormQ = modelH.calcUnnormalizedQ();
        	for (int i = 0; i < stateCount; i++) {
        		for (int j = i + 1; j < stateCount; j++) {
                	int d = getIndex(h * stateCount + i, h * stateCount + j, this.stateCount);
                    relativeRates[d] = subUnnormQ[i][j];
                }
            }
        }

        double[] freqs = freqModel.getFrequencies();
        int rateIndex = 0;
        for (int g = 0; g < hiddenClassCount; g++) {
            for (int h = g + 1; h < hiddenClassCount; h++) {  // from g -> h
                for (int i = 0; i < stateCount; i++) {
                    int d = getIndex(g * stateCount + i, h * stateCount + i, this.stateCount);
                    // correct for the fact that setupMatrix post-multiplies these rates
                    relativeRates[d] = rates[rateIndex] / freqs[i];
                }
                rateIndex++;
            }
        }
    }

    // Mapping: Matrix[i][j] = Compressed vector[i*(S - 3/2) - i^2 / 2 + j - 1]
    private static int getIndex(int i, int j, int S) {
        return (i * (2 * S - 3) - i * i) / 2 + j - 1;
    }

    
    /*public static void main(String[] args) {
        GY94CodonModel codonModel = new GY94CodonModel(Codons.UNIVERSAL,
                new Parameter.Default(1.0), new Parameter.Default(2.0),
                new FrequencyModel(Codons.UNIVERSAL, new Parameter.Default(61, 1.0 / 61.0)));
        EigenDecomposition ed1 = codonModel.getEigenDecomposition();
        double[][] q = codonModel.getQ();

//        System.err.println("matrixQ = \n"+codonModel.printQ());// new Matrix(q));
        FrequencyModel freqModel = new FrequencyModel(HiddenCodons.UNIVERSAL_HIDDEN_2, new Parameter.Default(122, 1.0 / 122.0));
        System.err.println("freq = " + new Vector(freqModel.getFrequencies()));
//        System.exit(-1);
        MarkovModulatedGY94CodonModel mmCodonModel = new MarkovModulatedGY94CodonModel(HiddenCodons.UNIVERSAL_HIDDEN_2,
                new Parameter.Default(2, 5.0), new Parameter.Default(2, 1.0),
                new Parameter.Default(2.0), freqModel
        );
        EigenDecomposition ed2 = mmCodonModel.getEigenDecomposition();
        System.err.println("matrixQ = \n" + mmCodonModel.printQ());// new Matrix(q));
    } */

    protected double getMINFDIFF() {
        return 1.0E-10;
    }

    protected double getMINFREQ() {
        return 1.0E-10;
    }

    private int statesPerSubModelCount;
    private int hiddenClassCount;
    private Parameter switchingRates;

	@Override
	protected void frequenciesChanged() {
		for (BaseSubstitutionModel s : this.subModelArray) {
			s.frequenciesChanged();
		}
	}

	@Override
	protected void ratesChanged() {
		for (BaseSubstitutionModel s : this.subModelArray) {
			s.ratesChanged();
		}
	}

	BaseSubstitutionModel [] subModelArray;
	double [][][] unNormQ;
    

}
