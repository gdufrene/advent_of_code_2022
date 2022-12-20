package advent2022;

import static java.lang.Integer.parseInt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class Day20 implements AdventDay {
    
    
    public static void main(String[] args) throws Exception {
        new Day20()
            .load( Filename.DATA.filename() )
            .part1();
    }
    
    ArrayList<Integer> lines = new ArrayList<>(4000);
    
    
    private Day20 load(String f) throws IOException {
        Files.lines( dayFile(f) )
                .map(Integer::parseInt)
                .forEach( lines::add );
        return this;
    }

    public void part1() throws IOException {
        ArrayList<Integer> indexes = new ArrayList<>(4000);
        int size = lines.size();
        for (int i = 0; i < size; i++) {
            indexes.add(i);
        }
        
        mix( indexes );
        
        int zero = lines.indexOf(0);
        int sum = 0;
        sum += lines.get( (zero + 1000) % size );
        sum += lines.get( (zero + 2000) % size );
        sum += lines.get( (zero + 3000) % size );
        
        // -5520 xxx
        // -8370 xxx
        // -5447 xxx
        // 988 IS OK !!
        System.out.println( sum );
        
    }
    
    private void mix(ArrayList<Integer> indexes) {
        int size = indexes.size();
        int delta = 0;
        int size2 = size-1; // when remove one element ...
        for (int i = 0; i < size; i++) {
            int idelta = (i + delta) % size;
            while ( indexes.get(idelta) != i ) {
                delta = (delta + 1) % size;
                idelta = (i + delta) % size;
            }
            int val = lines.remove(idelta);
            indexes.remove(idelta);

            int nidx = idelta;
            
            if ( val < 0 ) {
                nidx += val;
                while ( nidx < 0 ) nidx += size2;
                if ( nidx == 0 ) nidx = size2;
            } else {
                nidx = (nidx+val)%size2;
                if (nidx == size2) nidx = 0;
            }
            
            lines.add(nidx, val);
            indexes.add(nidx, i);
//            System.out.format( "%d moves at %d\n", val, nidx);
//            show();
        }
    }
    
    private void show() {
        lines.forEach( v -> System.out.print(v + " ") );
        System.out.println();
    }
    
    public void part2() throws IOException {
        
    }
    
    

    
    
}

