package sortvisualizer;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Kryptich
 */

/*Based on GeeksForGeeks Insertion Sort Java implementation by Rajat Mishra.
 * this version does not modify original array and instead generates a queue
 * of pairs containing the 'instructions' or steps required to sort the array.
 * For insertion sort, there is a phenomenon of 'sliding insert' -- the key is
 * moved to the left to its correct position and the section between these two
 * indices (key and target) 'slides' over by one (requiring [distance] number
 * of swaps). Rather than document each swap in this slide behavior, the visualizer
 * class just expects us to provide the key and target pair. It will move
 * elements 'inside' the swap automatically to the right.
 */

public class InsertionSortVerbose {


    public static Queue<Pair> sort(double[] arr, boolean ascending) {
        /*  Clone the array for calculating steps (the Visualizer uses the
         *  unsorted original to display progress)
         * */
        double[] workingCpy = arr.clone();
        Queue<Pair> sortTransformations = new LinkedList<>();
        int n = workingCpy.length;

        for (int i = 1; i < n; i++) {
            double key = workingCpy[i];
            int j = i - 1;

        /* Move elements of workingCpy[0..i-1], that are
           greater than key, to one position ahead
           of their current position */
            if (ascending) {
                while (j >= 0 && workingCpy[j] > key) {
                    workingCpy[j + 1] = workingCpy[j];
                    j = j - 1;
                }
            } else {
                while (j >= 0 && workingCpy[j] < key) {
                    workingCpy[j + 1] = workingCpy[j];
                    j = j - 1;
                }
            }

            workingCpy[j + 1] = key;

            /* Enable the disabled check to stop adding equivalent pairs
             (speeds up animation but doesn't accurately reflect how
             the algorithm *does* have to perform check at every index )*/

//            if (j+1 != i){
            sortTransformations.add(new Pair(i, j + 1)); // LOG TRANSFORMS
//            }

        }
        System.out.println("Pairs in order: "); // Display calculated instructions
        for (Object p : sortTransformations.toArray()) {
            Pair pair = (Pair) p;
            System.out.print("(" + pair.first + ", " + pair.second + ") ");
        }
        return sortTransformations;
    }
}
