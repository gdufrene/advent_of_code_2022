package advent2022;

import static java.lang.Integer.parseInt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class Day08 implements AdventDay {
    
    byte[][] trees;
    int width;
    
    
    public static void main(String[] args) throws Exception {
        new Day08()
            .load( Filename.DATA )
            .part2();
    }
    
    List<String> lines;
    
    
    private Day08 load(Filename f) throws IOException {
        AtomicInteger i = new AtomicInteger();
        
        Files.lines( dayFile(f.filename()) )
                .map( str -> str.getBytes() )
                .forEach( arr -> {
                    if (trees == null) {
                        width = arr.length;
                        trees = new byte[width][]; 
                    }
                    trees[i.getAndIncrement()] = arr;
                });
                
        return this;
    }
    
    int tree(int x, int y) {
        return trees[y][x] - '0';
    }
    
    public void part1() throws IOException {
        // 2224 <-- xxx
        // 2342 <-- xxx
        // 4275 <-- xxx
        
        int sum = width*4 - 4; // edges tree
        int end = width - 1;
        

        for (int y = 1; y < end; y++) {
            for (int x = 1; x < end; x++) {
                boolean v = isVisible(x, y);
                if ( v ) sum++;
                System.out.print( v ? 'x' : ' ');
            }
            System.out.println();
        }
        
        // 1827
        System.out.println("PART1 ===> " + sum);
    }

    private boolean isVisible(int x, int y) {
        
        int h = tree(x,y);
        int i;
        
        for ( i = 0; i < x; i++ ) { 
            if ( tree(i,y) >= h ) break;
        }
        if (i == x ) return true;
        
        for ( i = 0; i < y; i++ ) { 
            if ( tree(x,i) >= h ) break;
        }
        if (i == y ) return true;
        
        for ( i = width-1; i > x; i-- ) {
            if ( tree(i,y) >= h ) break;
        }
        if (i == x ) return true;

        for ( i = width-1; i > y; i-- ) {
            if ( tree(x,i) >= h ) break;
        }
        if (i == y ) return true;
        
        return false;
    }
    
    private int scenicScore(int x, int y) {

        int score = 0;
        int v = 0;
        int h = tree(x,y);
        int i;
        
        for ( i = x-1; i >= 0; i-- ) {
            v++;
            if ( tree(i,y) >= h ) break;
        }
        score = v;
        
        // if (i == x ) return true;
        v = 0;
        for ( i = y-1; i >= 0; i-- ) {
            v++;
            if ( tree(x,i) >= h ) break;
        }
        score *= v; 
        
        v = 0;
        for ( i = x+1; i < width; i++ ) {
            v++;
            if ( tree(i,y) >= h ) break;
        }
        score *= v;

        v = 0;
        for ( i = y+1; i < width; i++ ) {
            v++;
            if ( tree(x,i) >= h ) break;
        }
        score *= v;
        
        return score;
    }

    public void part2() throws IOException {
        int score = 0; 
        int end = width - 1;

        for (int y = 1; y < end; y++) {
            for (int x = 1; x < end; x++) {
                int s = scenicScore(x, y);
                if ( s > score ) {
                    score = s;
                    System.out.format("Candidate %d,%d -> %s\n", x, y, score);
                }
            }
        }
        
        // 1827
        System.out.println("PART2 ===> " + score);
    }    
    
}

