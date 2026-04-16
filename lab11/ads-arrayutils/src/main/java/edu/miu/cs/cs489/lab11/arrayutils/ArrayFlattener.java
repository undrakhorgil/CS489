package edu.miu.cs.cs489.lab11.arrayutils;

import java.util.Arrays;

public class ArrayFlattener {

    public int[] flattenArray(int[][] input) {
        if (input == null) {
            return null;
        }
        return Arrays.stream(input)
                .filter(arr -> arr != null)
                .flatMapToInt(Arrays::stream)
                .toArray();
    }
}

