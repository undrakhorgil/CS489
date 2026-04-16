package edu.miu.cs.cs489.lab11.arrayutils;

public class ArrayReversor {

    private final ArrayFlattenerService flattenerService;

    public ArrayReversor(ArrayFlattenerService flattenerService) {
        this.flattenerService = flattenerService;
    }

    public int[] reverseArray(int[][] input) {
        if (input == null) {
            return null;
        }
        int[] flattened = flattenerService.flattenArray(input);
        if (flattened == null) {
            return null;
        }
        int[] reversed = new int[flattened.length];
        for (int i = 0; i < flattened.length; i++) {
            reversed[i] = flattened[flattened.length - 1 - i];
        }
        return reversed;
    }
}

