package sortvisualizer;

import java.io.Serializable;
/**
 * @author Kryptich
 */

/* Ridiculously simple C/style Pair ADT implementation for storing index pairs
 */

public class Pair<Integer> implements Serializable{
    public int first;
    public int second;
    public Pair(int l, int r){
        this.first = l;
        this.second = r;
    }
    public int[] toArray(){
        return new int[]{this.first,this.second};
    }
    public int[] toSwappedArray(){
        return new int[]{this.second,this.first};
    }
}
