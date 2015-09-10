package com.anykey.uaspec;

/**
 * Created by Anton Horodchuk on 013 21.05.15.
 *
 */

import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

public class CurveAnalyzer {

    /*Provides interpolation of given points*/
    public double[] getCoefficients(double[][] data, int accuracy){
        WeightedObservedPoints points = new WeightedObservedPoints();

        for (double[] point : data){
            points.add(point[1],point[0]);
        }
        final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(accuracy);
        return fitter.fit(points.toList());
    }

    public UnivariateDifferentiableFunction getFunction(double[] data, int accuracy){
        return null;
    }



}
