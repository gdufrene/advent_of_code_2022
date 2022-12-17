package advent2022;

import static java.lang.Integer.parseInt;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;


public class Day13 implements AdventDay {
    
    
    public static void main(String[] args) throws Exception {
        /* */
        new Day13()
            .load( Filename.DATA.filename() )
            .part2();
            /* */
        /* *
        Packet p = Packet.fromString("[[],[]]");
        System.out.println(p);
        /* */
    }
    
    List<Packet> lines;
    
    
    private Day13 load(String f) throws IOException {
        lines = Files.lines( dayFile(f) )
                .filter( Predicate.not(String::isBlank) )
                .map( Packet::fromString )
                .collect( ArrayList::new, ArrayList::add, ArrayList::addAll );
        return this;
    }

    public void part1() throws IOException {
        int index = 1;
        int sum = 0;
        for (int i = 0; i < lines.size(); i+=2) {
            Packet left = lines.get(i);
            Packet right = lines.get(i+1);
            
            int compare = left.compareTo(right);
            System.out.println( left + " vs " + right + "  ==> " + compare );
            
            if ( compare < 0 ) sum += index;
            
            index++;
        }
        
        System.out.println("PART1 ====> " + sum);
    }
    
    public void part2() throws IOException {
        
        Packet d1 = Packet.fromString("[[2]]");
        Packet d2 = Packet.fromString("[[6]]");
        
        lines.add( d1 );
        lines.add( d2 );
        
        Collections.sort(lines);
        
        int key = 1;
        for (int i = 0; i < lines.size(); i++) {
            if ( lines.get(i) == d1 || lines.get(i) == d2 ) key *= (i+1);
        }
        
        System.out.println("PART2 =====> " + key);
        
    }
    
    static class Packet implements Comparable<Packet> {
        List<Packet> elements = new ArrayList<>();
        Integer value;
        private int end;
        
        @Override
        public int compareTo(Packet o) {
            
            Packet right = (Packet) o;
            
            if ( value != null && right.value != null ) 
                return value - right.value;
            
            if ( value == null && right.value == null ) {
                int len = elements.size();
                int len2 = right.elements.size();
                /*
                if ( len > right.elements.size() ) {
                    return -1;
                }
                */
                int maxLen = Math.max(len, len2);
                for (int i = 0; i < maxLen; i++) {
                    if ( i >= len ) return -1;
                    if ( i >= len2 ) return 1;
                    int compareElements = elements.get(i).compareTo( right.elements.get(i) );
                    if ( compareElements != 0 ) return compareElements;
                }
                return 0;
            }
            
            Packet left = value == null ? this : Packet.asElements(this);
            right = right.value == null ? right : Packet.asElements(right);
            
            return left.compareTo(right);
        }
        
        
        private static Packet asElements(Packet value) {
            Packet p = new Packet();
            p.elements.add( value );
            return p;
        }


        static Packet EMPTY = new Packet() {
            @Override
            public String toString() {
                return "";
            }
        };
        
        static int rec = 0;
        public static Packet fromString(String str) {
            Packet p = new Packet();
            
            int c = str.charAt(0);
            
            if ( c == ']' ) return EMPTY;
            
            if ( c != '[' ) {
                int end = str.indexOf(']');
                int end2 = str.indexOf(',');
                if ( end < 0 || (end2>0 && end2<end) ) end = end2;
                if ( end < 0 ) end = str.length();
                p.value = parseInt(str.substring(0, end));
                p.end = end;
                return p;
            }
            
            int open = 0;
            int i = 0;
            for (; (c != ']' || open > 0) && c > 0; c = str.isBlank() ? -1 : str.charAt(0)) {

                str = str.substring(1);
                p.end++;

                if ( c == ']' ) {
                    open--;
                    if ( open == 0 ) return p;
                }

                if ( c == '[' ) open++;
                
                Packet sub = fromString(str);
                p.elements.add( sub );
                str = str.substring(sub.end);
                p.end += sub.end;

            }

            return p;
        }
        
        @Override
        public String toString() {
            if (value != null) return Integer.toString(value);
            
            StringBuilder stb = new StringBuilder("[");
            int len = elements.size();
            for (int i = 0; i < len; i++) {
                if ( i>0 ) stb.append(",");
                stb.append(elements.get(i).toString());
            }
            stb.append("]");
            return stb.toString();
        }
        
    }

    
    
}

