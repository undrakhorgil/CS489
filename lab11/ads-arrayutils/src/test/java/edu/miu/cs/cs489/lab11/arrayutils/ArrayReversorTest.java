package edu.miu.cs.cs489.lab11.arrayutils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArrayReversorTest {

    @Mock
    private ArrayFlattenerService flattenerService;

    @InjectMocks
    private ArrayReversor arrayReversor;

    @Test
    void reverseArray_whenInputIsLegit2DNestedArray_returnsReversedFlattenedArray_andInvokesService() {
        int[][] input = new int[][]{{1, 3}, {0}, {4, 5, 9}};
        when(flattenerService.flattenArray(input)).thenReturn(new int[]{1, 3, 0, 4, 5, 9});

        int[] result = arrayReversor.reverseArray(input);

        assertArrayEquals(new int[]{9, 5, 4, 0, 3, 1}, result);
        verify(flattenerService, times(1)).flattenArray(input);
        verifyNoMoreInteractions(flattenerService);
    }

    @Test
    void reverseArray_whenInputIsNull_returnsNull_andDoesNotInvokeService() {
        assertNull(arrayReversor.reverseArray(null));
        verifyNoInteractions(flattenerService);
    }
}

