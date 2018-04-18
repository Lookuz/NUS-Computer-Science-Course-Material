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
//  private Matrix result; // Result of the matrix multiplication.

  MatrixMultiplication(Matrix matrixA, Matrix matrixB, int rowA, int colA,
                       int rowB, int colB, int dimension) {
    this.matrixA = matrixA;
    this.matrixB = matrixB;
    this.rowA = rowA;
    this.rowB = rowB;
    this.colA = colA;
    this.colB = colB;
    this.dimension = dimension;
//    this.result = result;
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
    // TODO
    // If task becomes too small, stop splitting the task.
    if (this.dimension <= Matrix.getThreshold()) {
      return Matrix.multiplyNonRecursively(matrixA, matrixB, rowA, colA,
              rowB, colB, dimension);
    }

    int size = dimension / 2;
    Matrix result = new Matrix(this.dimension);
    MatrixMultiplication A1B1 = new MatrixMultiplication(matrixA, matrixB,
            rowA, colA, rowB, colB, size);
    MatrixMultiplication A2B3 = new MatrixMultiplication(matrixA, matrixB,
            rowA, colA + size, rowB + size, colB, size);
    A2B3.fork();
    A1B1.fork();

    MatrixMultiplication A1B2 = new MatrixMultiplication(matrixA, matrixB,
            rowA, colA, rowB, colB + size, size);
    MatrixMultiplication A2B4 = new MatrixMultiplication(matrixA, matrixB,
            rowA, colA + size, rowB + size, colB + size, size);
    A1B2.fork();
    A2B4.fork();

    MatrixMultiplication A3B1 = new MatrixMultiplication(matrixA, matrixB,
            rowA + size, colA, rowB, colB, size);
    MatrixMultiplication A4B3 = new MatrixMultiplication(matrixA, matrixB,
            rowA + size, colA + size, rowB + size, colB, size);
    A3B1.fork();
    A4B3.fork();

    MatrixMultiplication A3B2 = new MatrixMultiplication(matrixA, matrixB,
            rowA + size, colA, rowB, colB + size, size);
    MatrixMultiplication A4B4 = new MatrixMultiplication(matrixA, matrixB, rowA + size,
            colA + size, rowB + size, colB + size, size);
    A3B2.fork();

    Matrix A22B22 = A4B4.compute();
    Matrix A21B12 = A3B2.join();
    Matrix A22B21 = A4B3.join();
    Matrix A21B11 = A3B1.join();
    Matrix A12B22 = A2B4.join();
    Matrix A11B12 = A1B2.join();
    Matrix A12B21 = A1B1.join();
    Matrix A11B11 = A2B3.join();
    result = result.add(A22B22, A21B12, size, size, size);
    result = result.add(A22B21, A21B11, size, 0, size);
    result = result.add(A12B22, A11B12, 0, size, size);
    result = result.add(A12B21, A11B11, 0, 0, size);

    return result;
  }
}