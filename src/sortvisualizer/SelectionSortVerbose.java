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

class SelectionSortVerbose {
    public static Queue<Pair> sort(double[] arr, boolean ascending) {
        /*  Clone the array for calculating steps (the Visualizer uses the
         *  unsorted original to display progress)
         * */
        double[] workingCpy = arr.clone();
        Queue<Pair> sortTransformations = new LinkedList<>();
        int n = workingCpy.length;
        // One by one move boundary of unsorted subarray
        for (int i = 0; i < n - 1; i++) {
            // Find the minimum element in unsorted array
            int min_idx = i;
            for (int j = i + 1; j < n; j++)
                if (ascending) {
                    if (workingCpy[j] <= workingCpy[min_idx]) {
                        min_idx = j;
                    }
                } else {
                    if (workingCpy[j] >= workingCpy[min_idx]) {
                        min_idx = j;
                    }
                }

            /* Enable the disabled check to stop adding equivalent pairs
             (speeds up animation but doesn't accurately reflect how
             the algorithm *does* have to perform check at every index )*/

//            if (min_idx != i){
            sortTransformations.add(new Pair(i, min_idx)); // LOG TRANSFORMS
//            }
            double temp = workingCpy[min_idx]; // DO TRANSFORMS ON COPY
            workingCpy[min_idx] = workingCpy[i];
            workingCpy[i] = temp;

        }
        System.out.println("Pairs in order: ");  // Display calculated instructions
        for (Object p : sortTransformations.toArray()) {
            Pair pair = (Pair) p;
            System.out.print("(" + pair.first + ", " + pair.second + ") ");
        }

        return sortTransformations;
    }
}