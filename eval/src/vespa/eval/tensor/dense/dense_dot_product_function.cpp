// Copyright 2017 Yahoo Holdings. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.

#include "dense_dot_product_function.h"
#include "dense_tensor.h"
#include "dense_tensor_view.h"
#include <vespa/eval/eval/value.h>
#include <vespa/eval/tensor/tensor.h>

namespace vespalib::tensor {

using CellsRef = DenseTensorView::CellsRef;

DenseDotProductFunction::DenseDotProductFunction(size_t lhsTensorId_, size_t rhsTensorId_)
    : _lhsTensorId(lhsTensorId_),
      _rhsTensorId(rhsTensorId_),
      _hwAccelerator(hwaccelrated::IAccelrated::getAccelrator())
{
}

namespace {

CellsRef
getCellsRef(const eval::Value &value)
{
    const DenseTensorView &denseTensor = static_cast<const DenseTensorView &>(value);
    return denseTensor.cellsRef();
}

}

const eval::Value &
DenseDotProductFunction::eval(ConstArrayRef<eval::Value::CREF> params, Stash &stash) const
{
    DenseTensorView::CellsRef lhsCells = getCellsRef(params[_lhsTensorId]);
    DenseTensorView::CellsRef rhsCells = getCellsRef(params[_rhsTensorId]);
    size_t numCells = std::min(lhsCells.size(), rhsCells.size());
    double result = _hwAccelerator->dotProduct(lhsCells.cbegin(), rhsCells.cbegin(), numCells);
    return stash.create<eval::DoubleValue>(result);
}

}

