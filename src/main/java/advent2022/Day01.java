package advent2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Day01 {
    
    public static void main(String[] args) throws IOException {
        part2();
    }
    
    public static void part1() throws IOException {
        
        int max = Integer.MIN_VALUE;
        int sum = 0;
         for ( String line : Files.readAllLines(Paths.get("src/main/resources", "day01", "input1.txt")) ) {
        
            if (line.isBlank()) {
                if (sum > max ) max = sum;
                System.out.println( sum );
                sum = 0;
            } else {
                sum += Integer.parseInt(line);
            }
        }
        if (sum > max ) max = sum;
        
        System.out.println( "max = " + max );
            
        
    }
    
    public static void part2() throws IOException {
        
        List<Integer> sums = new LinkedList<>();
        int sum = 0;
        for ( String line : Files.readAllLines(Paths.get("src/main/resources", "day01", "input1.txt")) ) {
            if (line.isBlank()) {
                sums.add(sum);
                System.out.println( sum );
                sum = 0;
            } else {
                sum += Integer.parseInt(line);
            }
        }
        if ( sum > 0 ) sums.add(sum);
        int total = 0;
        
        total = 
                sums.stream().sorted(Comparator.reverseOrder()).limit(3)
        //        .forEach( System.out::println );
                .mapToInt( Integer::intValue ).sum();
        
        System.out.println( "max 3 = " + (total) );
            
        
    }
}
