package advent2022;

import static java.lang.Integer.parseInt;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;


public class Day11 implements AdventDay {
    
    
    public static void main(String[] args) throws Exception {
        new Day11()
            .load( Filename.DATA.filename() )
            .part2();
    }
    
    List<Monkey> allMonkeys = new LinkedList<>();
    
    
    private Day11 load(String f) throws IOException {
        Scanner s = new Scanner( dayFile(f) );
        
        while (s.hasNextLine()) {
            Monkey m = Monkey.read(s);
            allMonkeys.add(m);
            if (s.hasNextLine()) s.nextLine(); // empty
        }
        
        BigInteger mod = BigInteger.ONE;
        for (Monkey m : allMonkeys) {
            mod = mod.multiply(m.divisor);
        }
        System.out.println( "modulus ---> " + mod);
        
        final BigInteger modulus = mod;
        for (Monkey m : allMonkeys) {
            final UnaryOperator<BigInteger> previous = m.operation;
            
            m.operation = (BigInteger i) -> previous.apply(i).mod(modulus); 
                    // (UnaryOperator<BigInteger>) m.operation.andThen( (BigInteger i) -> i.mod(modulus) );
        }
        
        //lines = Files.readAllLines( dayFile(f) );
        return this;
    }

    public void part1() throws IOException {
        BigInteger total = goRounds(20, true);
        System.out.println( "PART1 =====> " + total);
    }
    
    public void part2() throws IOException {
        BigInteger total = goRounds(10000, false);
        System.out.println( "PART2 =====> " + total);
    }
    
    BigInteger goRounds(int nbRounds, boolean withDividedWorry) {
        for (int i = 0; i < nbRounds; i++) {
            int round = (i+1);
            if ( round < 20 ) System.out.println("---- Round " + round);
            else if ( round % 20 == 0 ) System.out.println("---- Round " + round);
            for(Monkey m : allMonkeys) m.processWithOthers( allMonkeys, withDividedWorry );
        }
        
        for(Monkey m : allMonkeys) {
            System.out.println( "Monkey "+m.id+": inspected "+m.inspected+" items");
        }
        System.out.println();
        
        final AtomicReference<BigInteger> total = new AtomicReference<>(BigInteger.ONE);
        allMonkeys.stream()
            .sorted( Comparator.comparing( (Monkey m) -> m.inspected ).reversed() )
            .limit(2)
            .forEach( m -> total.set( total.get().multiply(m.inspected) ) );
        
        return total.get();
    }    
    
}

class Monkey {

    int id = -1;
    List<BigInteger> items = new LinkedList<>();
    UnaryOperator<BigInteger> operation = UnaryOperator.identity();
    BigInteger divisor;
    Predicate<BigInteger> test = (i) -> false;
    int monkeyTrue, monkeyFalse;
    BigInteger inspected = BigInteger.ZERO;

    public static Monkey read(Scanner s) {
        Monkey m = new Monkey();
        
        s.next(); 
        m.id = parseInt(s.next().split(":")[0]);
        s.nextLine();
        
        for (String item : s.nextLine().split(":")[1].split(",") )
            m.items.add( new BigInteger(item.trim()) );
        
        String operation = s.nextLine().split("= old ")[1];
        if ( operation.equals("* old") ) {
            m.operation = (i) -> i.multiply(i) ;
        } else if (operation.charAt(0) == '+') {
            m.operation = (i) -> i.add( new BigInteger(operation.substring(2)) );
        } else if (operation.charAt(0) == '*') {
            m.operation = (i) -> i.multiply( new BigInteger(operation.substring(2)) );
        } else {
            throw new RuntimeException("No such operation: " + operation);
        }
        
        final BigInteger divisor = new BigInteger( s.nextLine().split("by ")[1] );
        m.divisor = divisor;
        m.test = (i) -> i.mod(divisor) == BigInteger.ZERO; 
        
        m.monkeyTrue = parseInt( s.nextLine().split("monkey ")[1] );
        m.monkeyFalse = parseInt( s.nextLine().split("monkey ")[1] );
        
        return m;
    }
    
    BigInteger THREE = new BigInteger("3");
    
    public void processWithOthers(List<Monkey> allMonkeys, boolean divide) {
        //DEBUG// System.out.println("Monkey " + id + ":");
        while (!items.isEmpty()) {
            inspected = inspected.add(BigInteger.ONE);
            BigInteger item = items.remove(0);
          //DEBUG//System.out.println("  inspects item "+item);
            item = operation.apply(item);
          //DEBUG//System.out.println("    worry increased to "+item);
            if ( divide ) item = item.divide(THREE);
          //DEBUG//System.out.println("    worry divided to "+item);
            int nextMonkey = test.test(item) ? monkeyTrue : monkeyFalse;
          //DEBUG//System.out.println("    item thrown to " + nextMonkey );
            allMonkeys.get(nextMonkey).items.add(item);
        }
    }

    @Override
    public String toString() {
        return String.format(
            "Monkey %d:\n" +
            "  items: %s\n" +
            "  if true %d, if false %d",
            id, items, monkeyTrue, monkeyFalse
        );
    }
    
}