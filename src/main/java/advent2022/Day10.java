package advent2022;

import static java.lang.Integer.parseInt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public class Day10 implements AdventDay {
    
    
    public static void main(String[] args) throws Exception {
        new Day10()
            .load( Filename.DATA.filename() )
            .part2();
    }
    
    List<Instruction> lines;
    
    
    private Day10 load(String filename) throws IOException {
        Path p = dayFile(filename);
        lines = Files.lines( p )
            .map( line -> {
                String part[] = line.split(" ");
                int val = -1;
                if (part.length > 1) val = parseInt(part[1]);
                return new Instruction(
                        InstName.valueOf(part[0].toUpperCase()), 
                        val);
            })
            .collect(Collectors.toList());
        return this;
    }

    public void part1() throws IOException {
        int register = 1;
        int cycle = 1;
        long strength = 0;
        int nextSum = 20;
        
        List<Integer> cycles = new LinkedList<>();
        List<Integer> registers = new LinkedList<>();
        
        for(Instruction i : lines) {
            cycle = cycle + i.inst().cycle;
            int toAdd = register;
            switch (i.inst()) {
                case NOOP: break; 
                case ADDX: register += i.val();
            }
            if (cycle==nextSum) toAdd = register;
            
            cycles.add(cycle);
            registers.add(register);
            
            if (cycle>=nextSum) {
                strength += nextSum * toAdd;

                System.out.println(cycles);
                System.out.println(registers);
                cycles = new LinkedList<>();
                registers = new LinkedList<>();

                System.out.format(
                    "During the %dth cycle, register X has the value %d, so the signal strength is %d, total is %d\n",
                    nextSum, toAdd, (nextSum * toAdd), strength  
                );
                nextSum += 40;
            }
        }
        System.out.format("register at end %d, after cycle %d\n", register, cycle);
        
        System.out.println("PART1 =====> " + strength);
    }
    
    public void part2() throws IOException {
        int register = 1;
        int cycle = 0;
        
        System.out.println("PART2 =====> ");
        System.out.println();

        int beamx = 0;
        int diff = 0;
        for(Instruction i : lines) {
            int endCycle = cycle + i.inst().cycle - 1;
            while(cycle <= endCycle ) {
                diff = register - beamx;
                System.out.print( diff >= -1 && diff <= 1 ? '#' : ' ' );
                if ( cycle == endCycle ) {
                    switch (i.inst()) {
                        case NOOP: break; 
                        case ADDX: register += i.val();
                    }
                }
                cycle++;
                beamx++;
                if (beamx==40) {
                    System.out.println();
                    beamx=0;
                }
            }
        }
        
        System.out.println();
        System.out.println("==== ==== ==== ==== ==== ==== ==== ==== ");
    }
    
    

    
    
}

record Instruction(InstName inst, int val) {}
enum InstName {
    NOOP(1), ADDX(2);
    InstName(int i) { cycle = i; }
    int cycle;
}

