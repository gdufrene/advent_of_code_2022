package advent2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Day03 {
    public static void main(String[] args) throws Exception {
        part2();
    }
    
    
    public static void part1() throws IOException {

        int sum = 0;
        
        
        for (String line : Files.readAllLines(Paths.get("src/main/resources", "day03", "input1.txt"))) {
            int len = line.length();
            int mid = len/2;
            int found = -1;
            for (int i = 0; i < mid; i++) {
                found = line.indexOf( line.charAt(i), mid );
                if ( found >= 0 ) break;
            }
            char c = line.charAt(found);
            int add = c >= 'a' ? c - 'a' + 1 : c - 'A' + 27;
            
            sum += add;
            System.out.format("%s -> +%d = %d\n", ""+c, add, sum);
        }
        
        System.out.println( sum );
    }
    
    public static void part2() throws IOException {
        int sum = 0;
        int ln = 0;
        List<String> current = new ArrayList<>(4);
        
        for (String line : Files.readAllLines(Paths.get("src/main/resources", "day03", "input1.txt"))) {
            ln++;
            
            current.add(line);
            if ( ln % 3 != 0 ) continue;
            
            final List<String> _current = current; 
            int add = _current.get(0).chars() 
                .filter( i -> _current.get(1).indexOf(i) >= 0 )
                .filter( i -> _current.get(2).indexOf(i) >= 0 )
                .map( c -> {System.out.print((char)c); return c; } )
                .map( c -> c >= 'a' ? c - 'a' + 1 : c - 'A' + 27 )
                .findFirst()
                .orElseThrow( () -> new RuntimeException("No common element ? in \n"+_current) );
            
            sum += add;
            System.out.format(" -> +%d = %d\n", add, sum);
            
            current = new ArrayList<>(4); 
        }
        
        System.out.println( sum );
    }
}
