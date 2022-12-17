package advent2022;

import static java.lang.Integer.parseInt;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class Day09 implements AdventDay {
    
    static double maxLen = 2.0;
    
    public static void main(String[] args) throws Exception {
        /* */
        new Day09()
            //.load( Filename.DATA.filename()  )
            //.load( "sample2.txt"  )
            .load( Filename.DATA.filename() )
            .part2();
        /* */
    }
    
    List<Move> lines;
    
    
    private Day09 load(String filename) throws IOException {
        Path p = dayFile(filename);
        lines = Files.lines( p )
            .map( line -> {
                String part[] = line.split(" ");
                Move m = new Move();
                m.dir = Dir.valueOf(part[0]);
                m.value = parseInt(part[1]);
                return m;
            })
            .collect(Collectors.toList());
        return this;
    }

    public void part1() throws IOException {
        Point H = new Point();
        Point T = new Point();
        
        Set<Point> allPos = new HashSet<>();
        allPos.add(new Point(T));
        
        lines.forEach( move -> {
            // System.out.println("Move Head: " + move.dir + " of " + move.value);
            for (int i = 0; i < move.value; i++) {
                move.dir.apply(H);
                // System.out.println("  Head is " + H + ", tail is " + T);
                if ( H.distance(T) >= maxLen ) {
                    move.dir.apply(T);
                    switch (move.dir) {
                    case U:  
                    case D:
                        T.x = H.x; break;
                    case R:  
                    case L:  
                        T.y = H.y; break;
                    }
                    // System.out.println("  moved tail at " + T);
                    allPos.add( new Point(T) );
                }
            }
        });
        // 6563 <--- xxx
        // 6086 <--- xxx
        
        System.out.println("PART1 ===> " + allPos.size());
    }
    
    public void part2() throws IOException {
        Point[] rope = new Point[9];
        for (int i = 0; i < rope.length; i++) {
            rope[i] = new Point();
        }
        Point T = rope[rope.length-1];
        Point H = new Point();
        
        Set<Point> allPos = new HashSet<>();
        allPos.add(new Point(T));
        
        Histo histo = new Histo();
        
        lines.forEach( move -> {
            //System.out.println("Move Head: " + move.dir + " of " + move.value);
            for (int i = 0; i < move.value; i++) {
                move.dir.apply(H);
                Point previous = H;
                for (int j = 0; j < rope.length; previous = rope[j++]) {
                    double dist = previous.distance(rope[j]);
                    // System.out.format("  dist from [%d] to previous : %.2f\n", j+1, dist);
                    if ( dist >= maxLen ) {
                        int dx = signCompare(previous.x, rope[j].x);
                        int dy = signCompare(previous.y, rope[j].y);
                        rope[j].x += dx;
                        rope[j].y += dy;
                    }
                    //previous = rope[j];
                }
                allPos.add( new Point(T) );
                // System.out.println("  Head is " + H + ", tail is " + T);
            }
            // show(H, rope); System.out.println("---");
            histo.entry(move, H, rope);
        });
        
        // histo.show();
        
        // 2595 <--- xxx
        // 2482
        System.out.println("PART2 ===> " + allPos.size());
    }
    
    static class Histo {
        List<HistoEntry> entries = new LinkedList<>();
        public void entry(Move move, Point head, Point[] rope) {
            entries.add(
                new HistoEntry()
                    .move(move)
                    .head(head)
                    .rope(rope)
            );
        }
        public int[] bounds() {
            int[] res = new int[4];
            Consumer<Point> challenge = (Point p) -> {                
                if ( p.x < res[0] ) res[0] = p.x;
                if ( p.x > res[1] ) res[1] = p.x;
                if ( p.y < res[2] ) res[2] = p.y;
                if ( p.y > res[3] ) res[3] = p.y;
            };
            for(HistoEntry entry : entries) {
                challenge.accept(entry.head);
                for(Point p : entry.rope) challenge.accept(p);
            }
            return res;
        }
        public void show() {
            int round = 0;
            for(HistoEntry entry : entries) {
                round++;
                System.out.println("----- round ("+ round + ") -----> " + entry.move);
                Day09.show(entry.head, entry.rope, bounds());
            }
        }
    }
    
    static class HistoEntry {
        Point head;
        Point[] rope;
        Move move;
        public HistoEntry head(Point p) {
            head = new Point(p);
            return this;
        }
        public HistoEntry rope(Point[] rope) {
            this.rope = new Point[rope.length];
            for (int i = 0; i < rope.length; i++) {
                this.rope[i] = new Point(rope[i]);
            }
            return this;
        }
        public HistoEntry move(Move move) {
            this.move = move;
            return this;
        }
    }
    
    private int signCompare(int a, int b) {
        if (a == b) return 0;
        if (a > b) return 1;
        return -1;
    }
    
    private static void show(Point H, Point[] rope) {
        show(H, rope, new int[] {0, 22, 0, 20} );
    }
    
    private static void show(Point H, Point[] rope, int[] bounds) {
        for (int y = bounds[2]; y <= bounds[3]; y++) {
            nextPoint:
            for (int x = bounds[0]; x <= bounds[1]; x++) {
                Point p = new Point(x,y);
                if (H.equals(p)) {
                    System.out.print("H");
                    continue;
                } 

                for (int i = 0; i < rope.length; i++) {
                    if (rope[i].equals(p)) {
                        System.out.print((i+1));
                        continue nextPoint;
                    }
                }

                System.out.print(".");
            }
            System.out.println();
        }
    }
    

    static enum Dir {
        R, U, L, D;
        public void apply(Point p) {
            switch (this) {
            case U:  p.y++; break;
            case D:  p.y--; break;
            case R:  p.x++; break;
            case L:  p.x--; break;
            }
        }
    }

    static class Move {
        Dir dir;
        int value = 1;
        public Move() {}
        @Override
        public String toString() {
            return "Move [" + dir + ", " + value + "]";
        }
        
    }
    
    
}

