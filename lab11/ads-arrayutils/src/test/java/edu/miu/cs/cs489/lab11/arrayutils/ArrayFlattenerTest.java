package edu.miu.cs.cs489.lab11.arrayutils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArrayFlattenerTest {

    private final ArrayFlattener arrayFlattener = new ArrayFlattener();

    @Test
    void flattenArray_whenInputIsLegit2DNestedArray_returnsFlattenedArray() {
        int[][] input = new int[][]{{1, 3}, {0}, {4, 5, 9}};

        int[] result = arrayFlattener.flattenArray(input);

        assertArrayEquals(new int[]{1, 3, 0, 4, 5, 9}, result);
    }

    @Test
    void flattenArray_whenInputIsNull_returnsNull() {
        assertNull(arrayFlattener.flattenArray(null));
    }
}

