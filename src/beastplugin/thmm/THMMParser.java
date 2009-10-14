package beastplugin.thmm;


import dr.evomodel.substmodel.NucModelType;
import dr.evomodel.substmodel.FrequencyModel;
import dr.evomodel.substmodel.HKY;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.xml.*;

import java.util.logging.Logger;

/**
 * @author Alexei Drummond
 * @author Andrew Rambaut
 */
public class THMMParser extends AbstractXMLObjectParser {

    public static final String KAPPA = "kappa";
    public static final String FREQUENCIES = "frequencies";

    public String getParserName() {
        return "thmmModel";
    }

    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        Variable kappaParam = (Variable) xo.getElementFirstChild(KAPPA);
        FrequencyModel freqModel = (FrequencyModel) xo.getElementFirstChild(FrequencyModel.FREQUENCIES);

        Logger.getLogger("dr.evomodel").info("Creating THMM substitution model. Initial kappa = " +
                kappaParam.getValue(0));

        return new HKY(kappaParam, freqModel);
    }

    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************

    public String getParserDescription() {
        return "This element represents an instance of the Temporal hidden " +
                "Markov Model (Whelan, 2008).";
    }

    public Class getReturnType() {
        return HKY.class;
    }

    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }

    private final XMLSyntaxRule[] rules = {
            new ElementRule(FrequencyModel.FREQUENCIES,
                    new XMLSyntaxRule[]{new ElementRule(FrequencyModel.class)}),
            new ElementRule(KAPPA,
                    new XMLSyntaxRule[]{new ElementRule(Variable.class)})
    };
}