import java.util.concurrent.RecursiveTask;

class MatrixMultiplication extends RecursiveTask<Matrix> {
  public static final long serialVersionUID = 1;
  private Matrix matrixA; // MatrixA to compute matrix multiplication.
  private Matrix matrixB; // MatrixB to compute matrix multiplication.
  private int rowA; // starting row of the first matrix.
  private int rowB; // starting row of the second matrix.
  private int colA; // starting column of the first matrix.
  private int colB; // starting column of the second matrix.
  private int dimension; // Dimension of the current matrix.

  MatrixMultiplication(Matrix matrixA, Matrix matrixB, int rowA, int colA,
      int rowB, int colB, int dimension) {
    this.matrixA = matrixA;
    this.matrixB = matrixB;
    this.rowA = rowA;
    this.rowB = rowB;
    this.colA = colA;
    this.colB = colB;
    this.dimension = dimension;
  }

  @Override
  /**
   * Method that computes the matrix multiplication of the 2 matrices in the
   * current MatrixMultiplication class, matrixA and matrixB.
   * Makes parallel calls to fork the task into smaller tasks while the
   * dimensions of the current matrices are larger than the fork threshold.
   * @return The resulting matrix of the matrix multiplication.
   */
  public Matrix compute() {
    // If task becomes too small, stop splitting the task.
    if (this.dimension <= Matrix.getThreshold()) {
      return Matrix.multiplyNonRecursively(matrixA, matrixB, rowA, colA,
          rowB, colB, dimension);
    }

    int size = dimension / 2;
    Matrix result = new Matrix(this.dimension);
    MatrixMultiplication a1b1 = new MatrixMultiplication(matrixA, matrixB,
        rowA, colA, rowB, colB, size);
    MatrixMultiplication a2b3 = new MatrixMultiplication(matrixA, matrixB,
        rowA, colA + size, rowB + size, colB, size);
    a2b3.fork();
    a1b1.fork();

    MatrixMultiplication a1b2 = new MatrixMultiplication(matrixA, matrixB,
        rowA, colA, rowB, colB + size, size);
    MatrixMultiplication a2b4 = new MatrixMultiplication(matrixA, matrixB,
        rowA, colA + size, rowB + size, colB + size, size);
    a1b2.fork();
    a2b4.fork();

    MatrixMultiplication a3b1 = new MatrixMultiplication(matrixA, matrixB,
        rowA + size, colA, rowB, colB, size);
    MatrixMultiplication a4b3 = new MatrixMultiplication(matrixA, matrixB,
        rowA + size, colA + size, rowB + size, colB, size);
    a3b1.fork();
    a4b3.fork();

    MatrixMultiplication a3b2 = new MatrixMultiplication(matrixA, matrixB,
        rowA + size, colA, rowB, colB + size, size);
    MatrixMultiplication a4b4 = new MatrixMultiplication(matrixA, matrixB, rowA + size,
        colA + size, rowB + size, colB + size, size);
    a3b2.fork();

    Matrix a22b22 = a4b4.compute();
    Matrix a21b12 = a3b2.join();
    Matrix a22b21 = a4b3.join();
    Matrix a21b11 = a3b1.join();
    Matrix a12b22 = a2b4.join();
    Matrix a11b12 = a1b2.join();
    Matrix a12b21 = a1b1.join();
    Matrix a11b11 = a2b3.join();

    result = result.add(a22b22, a21b12, size, size, size);
    result = result.add(a22b21, a21b11, size, 0, size);
    result = result.add(a12b22, a11b12, 0, size, size);
    result = result.add(a12b21, a11b11, 0, 0, size);
    return result;
  }
}
