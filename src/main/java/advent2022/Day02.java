package advent2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Day02 {
    public static void main(String[] args) throws Exception {
        part2();
    }
    
    public static void part2() throws IOException {

        int sum = 0;
        int ln = 0;
        
        var lst = Arrays.asList("Rock", "Paper", "Scissors");
        
        
        for (String line : Files.readAllLines(Paths.get("src/main/resources", "day02", "input1.txt"))) {
        //for ( String line : Files.readAllLines(Paths.get("src/main/resources", "day02", "sample.txt")) ) {
            ln++;
            int you = line.charAt(0) - 'A';
            int todo = line.charAt(2) - 'X';
            int me;
            
            /**/
            if ( todo == 0 ) {
                me = (you + 2) % 3;
            } else if (todo == 1){
                me = you;
            } else {
                me = (you + 1) % 3;
            }
            sum += me+1 + todo*3;
            System.out.format("You:%s Todo:%s Me:%s Sum:%d\n", lst.get(you), Arrays.asList("LOSE", "DRAW", "WIN").get(todo), lst.get(me), sum);
            /**/
            
        }
        System.out.println("line => " + ln);
        System.out.println("sum => " + sum);
 
    }

    public static void part1() throws IOException {

        int sum = 0;
        int ln = 0;
        
        for (String line : Files.readAllLines(Paths.get("src/main/resources", "day02", "input1.txt"))) {
        //for ( String line : Files.readAllLines(Paths.get("src/main/resources", "day02", "sample.txt")) ) {
            ln++;
            int you = line.charAt(0) - 'A';
            int me = line.charAt(2) - 'X';

            if ( me < 0 || me > 2 || you < 0 || you > 2 ) {
                throw new IOException("Error input : " + me + " <> " + you);
            }
            
            /**/
            sum += me+1;
            if ( Arrays.asList("A X", "B Y", "C Z").contains(line)) sum += 3;
            if ( Arrays.asList("A Y", "B Z", "C X").contains(line)) sum += 6;
            if ( Arrays.asList("A Z", "B X", "C Y").contains(line)) sum += 0;
            /**/
            
        }
        System.out.println("line => " + ln);
        System.out.println("sum => " + sum);
 
    }
}
