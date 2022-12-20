package advent2022;

import static java.lang.Integer.parseInt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class Day20 implements AdventDay {
    
    
    public static void main(String[] args) throws Exception {
        
        String datafile = Filename.DATA.filename();
        
        new Day20()
            .load( datafile )
            .part1();
        
        new Day20()
            .load( datafile )
            .part2();
    }
    
    ArrayList<Long> lines = new ArrayList<>(4000);
    
    
    private Day20 load(String f) throws IOException {
        Files.lines( dayFile(f) )
                .map(Integer::parseInt)
                .map( Long::valueOf )
                .forEach( lines::add );
        return this;
    }

    public void part1() throws IOException {
        ArrayList<Integer> indexes = generateIndexes();
        mix( indexes );
        long sum = groove();
        // -5520 xxx
        // -8370 xxx
        // -5447 xxx
        // 988 IS OK !!
        System.out.println( "PART1 ===> " + sum );
        
    }
    
    public void part2() throws IOException {
        ArrayList<Integer> indexes = generateIndexes();
        
        lines = lines.stream()
                .map( l -> l * 811_589_153L )
                .collect( () -> new ArrayList<>(4000), ArrayList::add, ArrayList::addAll );
        for (int i = 0; i < 10; i++) {            
            mix( indexes );
//            System.out.println();
//            System.out.println("After " + (i+1) + " rounds of mixing:");
//            show();
        }
        long sum = groove();
        // 7768531372516 is OK (but example does not work ???)
        System.out.println( "PART2 ===> " + sum );
    }
    
    private long groove() {
        int size = lines.size();
        int zero = lines.indexOf(0L);
        long sum = 0;
        sum += lines.get( (zero + 1000) % size );
        sum += lines.get( (zero + 2000) % size );
        sum += lines.get( (zero + 3000) % size );
        return sum;
    }
    
    private ArrayList<Integer> generateIndexes() {
        ArrayList<Integer> indexes = new ArrayList<>(4000);
        int size = lines.size();
        for (int i = 0; i < size; i++) {
            indexes.add(i);
        }
        return indexes;
    }
    

    private void mix(ArrayList<Integer> indexes) {
        final int size = indexes.size();
        final int size2 = size-1; // when remove one element ...
        for (int i = 0; i < size; i++) {
            int idelta = indexes.indexOf(i);
            long val = lines.remove(idelta);
            indexes.remove(idelta);
            int nidx = (int) Math.floorMod(idelta + val, size2);
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
    

    
    

    
    
}

