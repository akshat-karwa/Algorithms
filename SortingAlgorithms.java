import java.util.List;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * My implementation of various sorting algorithms.
 *
 * @author AKSHAT KARWA
 */
public class SortingAlgorithms {
    /**
     * Implements insertion sort.
     * It is:
     * in-place
     * stable
     * adaptive
     * 
     * Has a worst case running time of:
     * O(n^2)
     * And a best case running time of:
     * O(n)
     *
     * @param <T>        data type to sort
     * @param arr        the array that is sorted after the method runs
     * @param comparator the Comparator used to compare the data in arr
     * @throws java.lang.IllegalArgumentException if the array or comparator is
     *                                            null
     */
    public static <T> void insertionSort(T[] arr, Comparator<T> comparator) {
        if (arr == null) {
            throw new IllegalArgumentException("The array to sort cannot be null!!");
        } else if (comparator == null) {
            throw new IllegalArgumentException("The comparator to compare the data in the array cannot be null!!");
        }
        int innerIndex = 0;
        T temp = arr[0];
        for (int i = 1; i < arr.length; i++) {
            innerIndex = i;
            while (innerIndex != 0 && comparator.compare(arr[innerIndex], arr[innerIndex - 1]) < 0) {
                // swap entries
                temp = arr[innerIndex];
                arr[innerIndex] = arr[innerIndex - 1];
                arr[innerIndex - 1] = temp;
                // decrement innerIndex
                innerIndex--;
            }
        }
    }

    /**
     * Implements cocktail sort.
     * It is:
     * in-place
     * stable
     * adaptive
     * 
     * Has a worst case running time of:
     * O(n^2)
     * And a best case running time of:
     * O(n)
     *
     * @param <T>        data type to sort
     * @param arr        the array that must be sorted after the method runs
     * @param comparator the Comparator used to compare the data in arr
     * @throws java.lang.IllegalArgumentException if the array or comparator is
     *                                            null
     */
    public static <T> void cocktailSort(T[] arr, Comparator<T> comparator) {
        if (arr == null) {
            throw new IllegalArgumentException("The array to sort cannot be null!!");
        } else if (comparator == null) {
            throw new IllegalArgumentException("The comparator to compare the data in the array cannot be null!!");
        }
        boolean swapsMade = true;
        int startIndex = 0, endIndex = arr.length - 1;
        int swapped = 0;
        T temp = arr[0];
        while (swapsMade) {
            swapsMade = false;
            for (int i = startIndex; i < endIndex; i++) {
                if (comparator.compare(arr[i], arr[i + 1]) > 0) {
                    // swap entries
                    temp = arr[i];
                    arr[i] = arr[i + 1];
                    arr[i + 1] = temp;
                    swapsMade = true;
                    swapped = i;
                }
            }
            endIndex = swapped;
            if (swapsMade) {
                swapsMade = false;
                for (int j = endIndex; j > startIndex; j--) {
                    if (comparator.compare(arr[j - 1], arr[j]) > 0) {
                        temp = arr[j - 1];
                        arr[j - 1] = arr[j];
                        arr[j] = temp;
                        swapsMade = true;
                        swapped = j;
                    }
                }
            }
            startIndex = swapped;
        }
    }

    /**
     * Implements merge sort.
     * It is:
     * out-of-place
     * stable
     * not adaptive
     * 
     * Has a worst case running time of:
     * O(n log n)
     * And a best case running time of:
     * O(n log n)
     * 
     * We create more arrays to run merge sort, but at the end, everything
     * is merged back into the original T[] which was passed in.
     * When splitting the array, if there is an odd number of elements, we put the
     * extra data on the right side.
     *
     * @param <T>        data type to sort
     * @param arr        the array to be sorted
     * @param comparator the Comparator used to compare the data in arr
     * @throws java.lang.IllegalArgumentException if the array or comparator is
     *                                            null
     */
    public static <T> void mergeSort(T[] arr, Comparator<T> comparator) {
        if (arr == null) {
            throw new IllegalArgumentException("The array to sort cannot be null!!");
        } else if (comparator == null) {
            throw new IllegalArgumentException("The comparator to compare the data in the array cannot be null!!");
        }
        if (arr.length <= 1) return;
        else if (arr.length == 2) {
            if (comparator.compare(arr[0], arr[1]) > 0) {
                // swap entries
                T temp = arr[0];
                arr[0] = arr[1];
                arr[1] = temp;
            }
            return;
        } else {
            int midpoint = arr.length / 2;
            T[] left = (T[]) new Object[midpoint], right = (T[]) new Object[arr.length - midpoint];
            
            for (int i = 0; i < arr.length; i++) {
                if (i < midpoint) left[i] = arr[i];
                else right[i - midpoint] = arr[i];
            }
            mergeSort(left, comparator);
            mergeSort(right, comparator);
            
            int i = 0, j = 0;
            while (i < (left.length) && j < (right.length)) {
                if (comparator.compare(left[i], right[j]) <= 0) {
                    arr[i + j] = left[i++];
                } else arr[i + j] = right[j++];
            }
            while (i < left.length) arr[i + j] = left[i++];
            while (j < right.length) arr[i + j] = right[j++];
        }
    }

    /**
     * Implements quick sort.
     * We use the provided random object to select our pivots. For example if you
     * need a pivot between a (inclusive) and b (inclusive) where b > a, we use
     * the following code:
     * int pivotIndex = rand.nextInt(b - a + 1) + a;
     * It is:
     * in-place
     * unstable
     * not adaptive
     * 
     * Has a worst case running time of:
     * O(n^2)
     * And a best case running time of:
     * O(n log n)
     *
     * @param <T>        data type to sort
     * @param arr        the array that must be sorted after the method runs
     * @param comparator the Comparator used to compare the data in arr
     * @param rand       the Random object used to select pivots
     * @throws java.lang.IllegalArgumentException if the array or comparator or
     *                                            rand is null
     */
    public static <T> void quickSort(T[] arr, Comparator<T> comparator,
                                     Random rand) {
        if (arr == null) {
            throw new IllegalArgumentException("The array to sort cannot be null!!");
        } else if (comparator == null) {
            throw new IllegalArgumentException("The comparator to compare the data in the array cannot be null!!");
        } else if (rand == null) {
            throw new IllegalArgumentException("The random object to select pivots cannot be null!!");
        }
        inPlaceQuicksort(arr, 0, (arr.length - 1), comparator, rand);
    }

    /**
     * Private Helper Method for recursively sorting the array.
     * @param arr is the array to sort
     * @param start is the start index of the array to sort
     * @param end is the end index of the array to sort
     * @param comparator is the comparator used to compare elements
     * @param rand the Random object used to select pivots
     * @param <T> data type to sort
     */
    private static <T> void inPlaceQuicksort(T[] arr, int start, int end, Comparator<T> comparator, Random rand) {
        if (end - start < 1) {
            return;
        } else if (end - start == 1) {
            if (comparator.compare(arr[start], arr[end]) > 0) {
                // swap entries
                T temp = arr[start];
                arr[start] = arr[end];
                arr[end] = temp;
            }
            return;
        } else {
            int pivotIndex = rand.nextInt(end - start + 1) + start;
            T pivotVal = arr[pivotIndex];
            // swap entries
            arr[pivotIndex] = arr[start];
            arr[start] = pivotVal;
            int i = start + 1;
            int j = end;
            T temp = arr[start];
            while (i <= j) {
                while (i <= j && comparator.compare(arr[i], pivotVal) <= 0) {
                    i++;
                }
                while (j >= i && comparator.compare(arr[j], pivotVal) >= 0) {
                    j--;
                }
                if (i <= j) {
                    // swap entries, increment i, decrement j
                    temp = arr[i];
                    arr[i++] = arr[j];
                    arr[j--] = temp;
                }
            }
            // swap entries
            temp = arr[start];
            arr[start] = arr[j];
            arr[j] = temp;
            inPlaceQuicksort(arr, start, j - 1, comparator, rand);
            inPlaceQuicksort(arr, j + 1, end, comparator, rand);
        }
    }

    /**
     * Implements the LSD (least significant digit) radix sort.
     * It is:
     * out-of-place
     * stable
     * not adaptive
     * 
     * Has a worst case running time of:
     * O(kn)
     * And a best case running time of:
     * O(kn)
     * We make an initial O(n) passthrough of the array to determine the number
     * of iterations we need based on the number with the largest magnitude.
     * 
     * @param arr the array to be sorted
     * @throws java.lang.IllegalArgumentException if the array is null
     */
    public static void lsdRadixSort(int[] arr) {
        if (arr == null) {
            throw new IllegalArgumentException("The array to sort cannot be null!!");
        }
        int numberOfIterations = 1;
        for (int i = 0; i < arr.length; i++) {
            int element = arr[i], currIterations = 1;
            while (element < -9 || element > 9) {
                element = element / 10;
                currIterations++;
            }
            if (currIterations > numberOfIterations) {
                numberOfIterations = currIterations;
            }
        }
        int currDigit = 1;
        while (numberOfIterations > 0) {
            LinkedList<Integer>[] array = new LinkedList[19];
            for (int i = 0; i < arr.length; i++) {
                int num = arr[i];
                int digit = num, digitPlace = currDigit;
                while (digitPlace != 0) {
                    digit = num % 10;
                    num /= 10;
                    digitPlace--;
                }
                digit += 9;
                if (array[digit] == null) {
                    LinkedList<Integer> list = new LinkedList<>();
                    array[digit] = list;
                }
                (array[digit]).add(arr[i]);
            }
            currDigit++;
            numberOfIterations--;
            int indexToAdd = 0;
            for (LinkedList<Integer> list : array) {
                if (list != null) {
                    for (Object element : list) {
                        arr[indexToAdd++] = (int) element;
                    }
                }
            }
        }
    }

    /**
     * Implements heap sort.
     * It is:
     * out-of-place
     * unstable
     * not adaptive
     * 
     * Has a worst case running time of:
     * O(n log n)
     * And a best case running time of:
     * O(n log n)
     * 
     * Uses java.util.PriorityQueue as the heap. Note that in this
     * PriorityQueue implementation, elements are removed from smallest
     * element to largest element.
     * Initializes the PriorityQueue using its build heap constructor (look at
     * the different constructors of java.util.PriorityQueue).
     * Returns an int array with a capacity equal to the size of the list. The
     * returned array should have the elements in the list in sorted order.
     *
     * @param data the data to sort
     * @return the array with length equal to the size of the input list that
     * holds the elements from the list is sorted order
     * @throws java.lang.IllegalArgumentException if the data is null
     */
    public static int[] heapSort(List<Integer> data) {
        if (data == null) {
            throw new IllegalArgumentException("The data to sort cannot be null!!");
        }
        int[] arr = new int[data.size()];
        PriorityQueue<Integer> queue = new PriorityQueue<>(data);
        int index = 0;
        while (index != data.size()) {
            arr[index++] = queue.remove();
        }
        return arr;
    }
}
