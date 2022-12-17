package advent2022;

import static java.lang.Integer.parseInt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class Day00 implements AdventDay {
    
    
    public static void main(String[] args) throws Exception {
        new Day00()
            .load( Filename.SAMPLE.filename() )
            .part1();
    }
    
    List<String> lines;
    
    
    private Day00 load(String f) throws IOException {
        lines = Files.readAllLines( dayFile(f) );
        return this;
    }

    public void part1() throws IOException {
        
    }
    
    public void part2() throws IOException {
        
    }
    
    

    
    
}

