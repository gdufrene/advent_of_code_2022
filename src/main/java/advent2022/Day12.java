package advent2022;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Day12 implements AdventDay {
    
    
    public static void main(String[] args) throws Exception {
        new Day12()
            .load( Filename.DATA.filename() )
            .part2();
    }
    
    List<String> lines;
    
    Square start;
    Square end;
    Map<Point, Square> squares;
    int width;
    int height;
    
    final static int H_START = 'a' - 1;
    final static int H_END = 'z' + 1;
    
    private Day12 load(String f) throws IOException {
        
        squares = new HashMap<>();
        
        lines = Files.readAllLines( dayFile(f) );
        
        int y = 0;
        width = lines.get(0).length();
        for (String line : this.lines) {
            Square left = null;
            Point up = new Point(0,y-1);
            for (int x = 0; x < width; x++) {
                up.x = x;
                Square s = new Square();
                s.height = line.charAt(x);
                s.pos = new Point(x, y);
                if ( s.height == 'S' ) { 
                    start = s; 
                    s.height = H_START; 
                }
                if ( s.height == 'E' ) { 
                    end = s; 
                    s.height = H_END; 
                }
                if ( left != null ) left.maybeAdd( s );
                if ( y > 0 ) squares.get( up ).maybeAdd(s);
                left = s;
                squares.put(s.pos, s);
            }
            y++;
            height = y;
        }
        
        System.out.println( "Start is at : " + start.pos );
        System.out.println( "End is at : " + end.pos );
        
        return this;
    }

    public void part1() throws IOException {
        
        flow();
        System.out.println( "PART1 ====> " + end.minDist );
        
        // System.out.println("PART1 =====> " + steps);
    }
    
    
    public void flow() {
        List<Square> todo = new ArrayList<>();
        start.minDist = 0;
        todo.add( start );
        
        int dist = 0;
        while(!todo.isEmpty()) {
            
            Square s = todo.remove(0);
            dist = s.minDist + 1;
            
            for (Square next : s.nextAvailables) {
                if ( dist < next.minDist ) {
                    next.minDist = dist;
                    if ( !todo.contains(next) ) todo.add(next);
                }
            }
            
            // showMins();
            // System.out.println("Todo -- " + todo.size());
            //readLine();
            
        }
    }
    
    BufferedReader reader;
    String readLine() {
        if ( reader == null ) reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void showMins() {
        Point at = new Point();
        for (int y = 0; y < height; y++) {
            at.y = y;
            for (int x = 0; x < width; x++) {
                at.x = x;
                Square s = squares.get(at);
                System.out.format("%02d ", s.minDist > 99 ? 99 : s.minDist );
            }
            System.out.println();
        }
    }

    public void part2() throws IOException {
        List<Square> listA = squares.values()
                .stream()
                .filter( s -> s.height == 'a' )
                .toList();
        
        int minDist = Integer.MAX_VALUE;
        
        System.out.println(listA.size() + " starting candidates");
        
        for (Square startCandidate : listA) {
            squares.values().forEach( s -> s.minDist = Integer.MAX_VALUE );
            start = startCandidate;
            flow();
            if ( end.minDist < minDist ) {
                minDist = end.minDist;
            }
        }
        
        System.out.println( "PART2 ====> " + minDist );
    }
    
    
    static class Square {
        Point pos;
        int height;
        List<Square> nextAvailables = new ArrayList<>(4);
        
        int minDist = Integer.MAX_VALUE;
        
        public void maybeAdd(Square s) {
            // boolean isBound = ( height == H_START || height == H_END || s.height == H_START || s.height == H_END );
            int diff = s.height - height;
            if ( diff <= 1  ) nextAvailables.add(s);
            if ( diff >= -1 ) s.nextAvailables.add(this);
        }
    }

    
    
}

