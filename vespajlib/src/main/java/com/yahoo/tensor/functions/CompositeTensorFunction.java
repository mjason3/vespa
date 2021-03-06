// Copyright 2017 Yahoo Holdings. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.
package com.yahoo.tensor.functions;

import com.google.common.annotations.Beta;
import com.yahoo.tensor.Tensor;
import com.yahoo.tensor.evaluation.EvaluationContext;

/**
 * A composite tensor function is a tensor function which can be expressed (less tersely)
 * as a tree of primitive tensor functions.
 *
 * @author bratseth
 */
@Beta
public abstract class CompositeTensorFunction extends TensorFunction {

    /** Evaluates this by first converting it to a primitive function */
    @Override
    public final Tensor evaluate(EvaluationContext context) { return toPrimitive().evaluate(context); }

}
