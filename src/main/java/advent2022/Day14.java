package advent2022;

import static java.lang.Integer.parseInt;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class Day14 implements AdventDay {
    
    final int factor = 3;
    final int drawEvery = 1;
    
    
    
    public static void main(String[] args) throws Exception {
        new Day14()
            .load( Filename.DATA.filename() )
            .part1();
    }
    
    List<String> lines;
    Point max, min;
    
    char[][] cave;
     
    private Day14 load(String f) throws IOException {
        lines = Files.readAllLines( dayFile(f) );
        
        max = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
        min = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        
        
        lines.stream()
            .flatMap( line -> Stream.of( line.split(" -> ") ) )
            .map( line -> line.split(",") )
            .map( arr -> new Point(parseInt(arr[0]), parseInt(arr[1])) )
            .forEach( p -> {
               if  (p.x < min.x) min.x = p.x;
               if  (p.y < min.y) min.y = p.y;
               
               if  (p.x > max.x) max.x = p.x;
               if  (p.y > max.y) max.y = p.y;
            });
        
        /* ----- Only for part 2 ... *
        max.y += 2;
        min.x = 500 - max.y - 1;
        max.x = 500 + max.y + 1;
        String added = min.x+","+(max.y)+" -> "+max.x+","+(max.y);
        System.out.println(added);
        lines.add( added );
        /* ----- */
        
        min.y = 0;
        
        int height = max.y - min.y + 1;
        cave = new char[ height ][];
        
        int width = max.x - min.x + 1;
        for (int i = 0; i < cave.length; i++) {
            cave[i] = new char[width];
        }
        
        for ( String line : lines ) {
            addLine(line);
        }
        
        System.out.println( min );
        System.out.println( max );        
        
        return this;
    }
    
    private void addLine(String line) {
        Point prev = null;
        for ( String pStr : line.split(" -> ") ) {
            String[] arr = pStr.split(",");
            Point p = new Point(parseInt(arr[0]) - min.x, parseInt(arr[1]) - min.y);
            Point np = new Point(p);
            if ( prev == null ) { prev = p; continue; }
            cave[p.y][p.x] = '#';
            while( !p.equals(prev) ) {
                if ( p.y != prev.y && p.y > prev.y ) p.y--;
                else if ( p.y != prev.y && p.y < prev.y ) p.y++;
                else if ( p.x != prev.x && p.x > prev.x ) p.x--;
                else if ( p.x != prev.x && p.x < prev.x ) p.x++;
                // System.out.println(p);
                cave[p.y][p.x] = '#';
            }
            prev = np;
        }
    }

    public void part1() throws IOException {
        /* */
        int sum = 0;
        while ( dropSand(500) ) {
            sum++;
            // System.out.println("===== "+sum+" =====");
            show();
        }
        show();
        System.out.println("PART1 =====> "+sum);
        /* */
    }
    
    
    JFrame frame;
    boolean init = true;
    JComponent caveComp;
    ReentrantLock lock = new ReentrantLock();
    Condition creation = lock.newCondition();
    Condition drawing = lock.newCondition();
    
    
    int loop = 0;
    public void show() {
        
        if ( init ) {
            init = false;
            
            SwingUtilities.invokeLater( () -> {
                frame = new JFrame("Sand Cave");
                
                caveComp = new JComponent() {
                    
                    
                    int width = (max.x - min.x + 1) * factor;
                    int height = (max.y - min.y + 1) * factor;
                    
                    public void paint(Graphics g) {
                        // System.out.println("PAINT");
                        // super.paint(g);
                        g.clearRect(0, 0, width, height);
                        
                        for (int y = 0; y < cave.length; y++) {
                            for (int x = 0; x < cave[y].length; x++) {
                                // System.out.print( cave[y][x] == 0 ? ' ' : cave[y][x] );
                                Color c = Color.WHITE;
                                if ( cave[y][x] == '#' ) c = Color.BLACK;
                                if ( cave[y][x] == 'o' ) c = Color.RED;
                                g.setColor(c);
                                g.fillRect(x*factor, y*factor, x*factor+factor-1, y*factor+factor-1);
                            }
                            // System.out.println();
                        }
                        
                        lock.lock();
                        drawing.signal();
                        lock.unlock();
                    }
                    public Dimension getPreferredSize()
                    {
                        return new Dimension(width, height);
                    }
                };
                
                //frame.setLayout( null );
                frame.getContentPane().setLayout( new FlowLayout() );
                frame.getContentPane().add( caveComp );
                frame.pack();
                frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
                
                lock.lock();
                creation.signal();
                lock.unlock();
                
                frame.setVisible(true);

            });
            
            
            System.out.println("Waiting creation ...");
            lock.lock();
            // first draw waits for JFrame creation.
            try {                    
                creation.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.unlock();
        }
        
        
        loop++;
        if ( drawEvery > 1 )
            if ( loop % drawEvery != 0 ) return;
        
        
        // System.out.println("Waiting drawing ...");
        lock.lock();
        try {                
            SwingUtilities.invokeAndWait( () -> caveComp.repaint() );
            // draw waits for paint operation
            drawing.await();
            Thread.sleep(25);
        } catch (Exception e) {
            e.printStackTrace();
        }
        lock.unlock();
        
        
    }
    
    private boolean dropSand(int col) {
        int y = -1;
        int x = col - min.x;
        // Point s = new Point(x,y);
        while( true ) {
            y++;
            if ( y >= cave.length ) return false;
            if ( cave[y][x] == 0 ) continue;
            
            // Cave entrance blocked.
            if ( y == 0 ) return false;
            
            int tx = x - 1;
            if ( tx < 0 ) return false;
            if ( cave[y][tx] == 0 ) { x = tx; continue; }
            
            tx = x + 1;
            if ( tx >= cave[0].length ) return false;
            if ( cave[y][tx] == 0 ) { x = tx; continue; }
            
            break;
        }

        cave[y-1][x] = 'o';
        return true;

    }
    
    public void part2() throws IOException {
        
        /* */
        int sum = 0;
        while ( dropSand(500) ) {
            sum++;
            // System.out.println("===== "+sum+" =====");
            // show();
        }
        show();
        System.out.println("PART2 =====> "+sum);
        /* */
    }
    
    

    
    
}

