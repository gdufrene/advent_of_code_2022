package advent2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Day04 {
    public static void main(String[] args) throws Exception {
        part2();
    }
    
    
    public static void part1() throws IOException {

        int sum = 0;
        
        List<Assign> assigns= Files.lines(Paths.get("src/main/resources", "day04", "input1.txt"))
                .filter( Predicate.not(String::isBlank) )
                .flatMap( line -> Arrays.stream(line.split(",")) )
                .map( part -> {
                    // System.out.println(part);
                    String[] p = part.split("-");
                    return new Assign( Integer.parseInt(p[0]), Integer.parseInt(p[1]) );
                })
                .collect( Collectors.toList() );
                /* */
        
        for (int i = 0; i < assigns.size(); i+=2) {
            Assign a = assigns.get(i);
            Assign b = assigns.get(i+1);
            if ( 
                ( a.from() >= b.from() && a.to() <= b.to() ) || 
                ( b.from() >= a.from() && b.to() <= a.to() ) 
            ) {
                System.out.println("["+i+"] " + a + " overlap " + b);
                sum++; 
            }
        }
        
        // 453
        System.out.println( sum );
    }
    
    public static void part2() throws IOException {

        int sum = 0;
        
        List<Assign> assigns= Files.lines(Paths.get("src/main/resources", "day04", "input1.txt"))
                .filter( Predicate.not(String::isBlank) )
                .flatMap( line -> Arrays.stream(line.split(",")) )
                .map( part -> {
                    // System.out.println(part);
                    String[] p = part.split("-");
                    return new Assign( Integer.parseInt(p[0]), Integer.parseInt(p[1]) );
                })
                .collect( Collectors.toList() );
                /* */
        
        for (int i = 0; i < assigns.size(); i+=2) {
            Assign a = assigns.get(i);
            Assign b = assigns.get(i+1);
            if ( 
                ( b.from() >= a.from() && b.from() <= a.to() ) || 
                ( b.to() >= a.from() && b.to() <= a.to() ) ||
                ( a.from() >= b.from() && a.from() <= b.to() ) || 
                ( a.to()   >= b.from() && a.to()   <= b.to() )
            ) {
                System.out.println("["+i+"] " + a + " overlap " + b);
                sum++; 
            }
        }
        
        // 919
        System.out.println( sum );
    }
    
    
}

record Assign (int from, int to) {
    public String toString() {
        return from+"-"+to;
    }
}