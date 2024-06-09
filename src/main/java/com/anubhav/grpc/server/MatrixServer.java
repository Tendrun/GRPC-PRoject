package com.anubhav.grpc.server;

import com.anubhav.grpc.MatrixServiceGrpc;
import com.anubhav.grpc.MatrixPair;
import com.anubhav.grpc.MatrixResponse;
import com.anubhav.grpc.Matrix;
import com.anubhav.grpc.Row;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GRpcService
public class MatrixServer extends MatrixServiceGrpc.MatrixServiceImplBase {
    private static final Logger logger = LoggerFactory.getLogger(MatrixServer.class);

    @Override
    public void multiplyMatrices(MatrixPair request, StreamObserver<MatrixResponse> responseObserver) {
        logger.info("Received matrix multiplication request");

        int[][] matrixA = convertToArray(request.getMatrixA());
        int[][] matrixB = convertToArray(request.getMatrixB());

        int[][] resultMatrix = multiply(matrixA, matrixB);

        MatrixResponse response = convertToResponse(resultMatrix);
        responseObserver.onNext(response);
        responseObserver.onCompleted();

        logger.info("Sent matrix multiplication response");
    }

    private int[][] convertToArray(Matrix matrix) {
        int[][] array = new int[matrix.getRowsCount()][matrix.getRows(0).getElementsCount()];
        for (int i = 0; i < matrix.getRowsCount(); i++) {
            for (int j = 0; j < matrix.getRows(i).getElementsCount(); j++) {
                array[i][j] = matrix.getRows(i).getElements(j);
            }
        }
        return array;
    }

    private MatrixResponse convertToResponse(int[][] matrix) {
        MatrixResponse.Builder responseBuilder = MatrixResponse.newBuilder();
        for (int[] row : matrix) {
            Row.Builder rowBuilder = Row.newBuilder();
            for (int element : row) {
                rowBuilder.addElements(element);
            }
            responseBuilder.addRows(rowBuilder.build());
        }
        return responseBuilder.build();
    }

    private int[][] multiply(int[][] matrixA, int[][] matrixB) {
        int rowsA = matrixA.length;
        int colsA = matrixA[0].length;
        int colsB = matrixB[0].length;

        int[][] result = new int[rowsA][colsB];

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                for (int k = 0; k < colsA; k++) {
                    result[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }
        return result;
    }
}
