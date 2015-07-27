package com.roodie.model.util;

import com.google.common.base.Preconditions;

/**
 * Created by Roodie on 07.07.2015.
 */
public class IntUtils {

    public static int weightedAverage(int... values) {
        Preconditions.checkArgument(values.length % 2 == 0, "values must have a multiples of 2");

        int sum = 0;
        int sumWeight = 0;

        for (int i = 0; i < values.length; i += 2) {
            int value = values[i];
            int weight = values[i + 1];

            sum += (value * weight);
            sumWeight += weight;
        }

        return sum / sumWeight;
    }

}
