package com.anubhav.grpc.client;

import com.anubhav.grpc.MatrixServiceGrpc;
import com.anubhav.grpc.MatrixPair;
import com.anubhav.grpc.Matrix;
import com.anubhav.grpc.MatrixResponse;
import com.anubhav.grpc.Row;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();
        MatrixServiceGrpc.MatrixServiceBlockingStub blockingStub = MatrixServiceGrpc.newBlockingStub(channel);

        int[][] matrixA = getMatrixFromUser("Matrix A");
        int[][] matrixB = getMatrixFromUser("Matrix B");

        MatrixPair request = buildMatrixPair(matrixA, matrixB);

        multiplyMatrices(blockingStub, request);

        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    private static int[][] getMatrixFromUser(String matrixName) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number of rows for " + matrixName + ": ");
        int rows = scanner.nextInt();
        System.out.println("Enter the number of columns for " + matrixName + ": ");
        int columns = scanner.nextInt();

        int[][] matrix = new int[rows][columns];
        System.out.println("Enter the elements of " + matrixName + ": ");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[i][j] = scanner.nextInt();
            }
        }
        return matrix;
    }

    private static MatrixPair buildMatrixPair(int[][] matrixA, int[][] matrixB) {
        Matrix.Builder matrixABuilder = Matrix.newBuilder();
        for (int[] row : matrixA) {
            Row.Builder rowBuilder = Row.newBuilder();
            for (int element : row) {
                rowBuilder.addElements(element);
            }
            matrixABuilder.addRows(rowBuilder.build());
        }

        Matrix.Builder matrixBBuilder = Matrix.newBuilder();
        for (int[] row : matrixB) {
            Row.Builder rowBuilder = Row.newBuilder();
            for (int element : row) {
                rowBuilder.addElements(element);
            }
            matrixBBuilder.addRows(rowBuilder.build());
        }

        return MatrixPair.newBuilder()
                .setMatrixA(matrixABuilder.build())
                .setMatrixB(matrixBBuilder.build())
                .build();
    }

    private static void multiplyMatrices(MatrixServiceGrpc.MatrixServiceBlockingStub stub, MatrixPair request) {
        MatrixResponse response = stub.multiplyMatrices(request);
        printMatrix(response);
    }

    private static void printMatrix(MatrixResponse response) {
        System.out.println("Result Matrix:");
        for (Row row : response.getRowsList()) {
            for (int element : row.getElementsList()) {
                System.out.print(element + " ");
            }
            System.out.println();
        }
    }
}