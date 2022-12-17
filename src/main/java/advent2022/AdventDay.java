package advent2022;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public interface AdventDay {
    
    static final String RESOURCE_DIR = "src/main/resources";
    
    static enum Filename {
        SAMPLE("sample.txt"),
        DATA("input1.txt");
        private String filename;
        Filename(String str) {
            this.filename = str;
        }
        String filename() { return filename; }
    }
    
    default Path dayFile(String filename) {
        return Paths.get(RESOURCE_DIR, getClass().getSimpleName().toLowerCase(), filename);
    }
    
    static byte[] inverse(byte[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            byte temp = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = temp;
        }
        return array;
    }
    
    static String inverse(String array) {
        byte[] arr = array.getBytes();
        return new String( inverse(arr) );
    }
    
    /**
     *  Iterative Heap Algorithm as an Iterable of list.
     */
    static <T> Iterable<List<T>> permutations(List<T> toOpen) {
        return new Iterable<List<T>>() {
            @Override
            public Iterator<List<T>> iterator() {
                return new Iterator<>() {
                    List<T> lst = new ArrayList<>( toOpen );
                    int c[] = new int[lst.size()];
                    int i = -1;
                    @Override public boolean hasNext() {
                        if (lst.isEmpty()) return false;
                        if ( i < 0 ) return true;
                        int z = i;
                        while( z < c.length && c[z] >= z ) z++;
                        return z != c.length;
                    }
                    @Override public List<T> next() {
                        if ( i < 0 ) {
                            i = 0;
                            return lst;
                        }                        
                        while ( c[i] >= i ) {
                            c[i] = 0;
                            i++;
                            if ( i >= c.length ) return lst;
                        }
                        int j = (i % 2 == 0) ? 0 : c[i];
                        T tmp = lst.set(i, lst.get(j));
                        lst.set(j, tmp);
                        c[i]++;
                        i = 1;
                        return lst;
                    }
                };
            }
        };
    }
}
