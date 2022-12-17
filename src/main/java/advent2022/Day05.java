package advent2022;

import static java.lang.Integer.parseInt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class Day05 {
    
    static enum CRANEMOVER { MODEL_9000, MODEL_9001 };
    
    public static void main(String[] args) throws Exception {
        part2();
    }
    
    public static void part1() throws IOException {
        moveCranes("input1.txt", CRANEMOVER.MODEL_9000);
    }
    
    public static void part2() throws IOException {
        moveCranes("input1.txt", CRANEMOVER.MODEL_9001);
    }
    
    public static void moveCranes(String filename, CRANEMOVER cranemover) throws IOException {
        int sum = 0;
        
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources", "day05", filename));
        System.out.println("length: " + lines.size());
        
        
        int len = (lines.get(0).length() + 1) / 4;
        System.out.println(len);
        
        boolean fillStacks = true;
        
        String[] stacks = new String[len];
        for (int i = 0; i < len; i++) {
            stacks[i] = "";
        }
        int nb = -1;
        while(fillStacks) {
            nb++;
            System.out.println(nb);
            String line = lines.get(nb);
            System.out.println(line);
            
            for (int i = 0; i < len; i++) {
                int x = 1 + i*4;
                char c = line.charAt(x);
                if ( c == '1' ) { fillStacks=false; break; }
                if ( c == ' ' ) continue;
                stacks[i] += c;
            }
        }
        nb += 2;
        
        for (int i = 0; i < stacks.length; i++) {
            stacks[i] = inverse(stacks[i]);
            System.out.println("["+i+"] " + stacks[i]);
        }
        System.out.println("---");
            
        while(nb < lines.size()) {
            String line = lines.get(nb++);
            String[] parts = line.split(" ");
            int move = parseInt( parts[1] );
            int from = parseInt( parts[3] ) - 1;
            int to = parseInt( parts[5] ) - 1;
            System.out.println(move + " " + from + " " + to);
            
            
            int j = stacks[from].length() - move ;
            byte[] removed = (j == 0 ? stacks[from] : stacks[from].substring(j)).getBytes();
            if (cranemover != CRANEMOVER.MODEL_9001) inverse(removed);
            stacks[to] +=  new String( removed );
            if ( j == 0 ) stacks[from] = "";
            else stacks[from] = stacks[from].substring(0, j);
        
            for (int i = 0; i < stacks.length; i++) {
                System.out.println("["+i+"] " + stacks[i]);
            }
            System.out.println("---");
        }
        
        System.out.print("Reply ---> ");
        for (int i = 0; i < stacks.length; i++) {
            if ( stacks[i].isBlank() ) System.out.print(' ');
            else System.out.print( stacks[i].charAt(stacks[i].length()-1) );
        }
        System.out.println();
        
    }
    
    static void inverse(byte[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            byte temp = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = temp;
        }
    }
    
    static String inverse(String array) {
        byte[] arr = array.getBytes();
        inverse(arr);
        return new String( arr );
    }
    

    
    
}

