package org.hg.energy;

import java.util.function.BinaryOperator;

public enum FunctionsTemperature {
    Sum(Double::sum),
    Min(Math::min),
    Max(Math::max),
    ;
    BinaryOperator<Double> operator;

    FunctionsTemperature(BinaryOperator<Double> operator) {
        this.operator = operator;
    }

    public BinaryOperator<Double> getOperator() {
        return operator;
    }
}
