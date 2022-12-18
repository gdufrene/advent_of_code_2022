package advent2022;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


public class Day17 implements AdventDay {
    
    
    public static void main(String[] args) throws Exception {
        new Day17()
            .load( Filename.DATA.filename() )
            .part2();
    }
    
    String commands;
    int commandSize;
    PlayGround playGround = new PlayGround();
    
    
    public Day17 load(String f) throws IOException {
        commands = Files.readAllLines( dayFile(f) ).get(0);
        commandSize = commands.length();
        return this;
    }

    public Object part1() throws IOException {
        int todo = 2022;
        int commandIndex = 0;
        
        playGround.nextBlock();
        while ( todo > 0 ) {
            char c = commands.charAt(commandIndex);
            boolean moved = false;
            if ( c == '<' ) moved = playGround.moveLeft();
            if ( c == '>' ) moved = playGround.moveRight();
            if ( !playGround.moveDown() ) {
                playGround.addBlockInCave();
                todo--;
                playGround.nextBlock();
            }
            commandIndex++;
            if ( commandIndex >= commands.length() ) commandIndex = 0;
        }
        
        long solution = playGround.totalHeight();
        System.out.println();
        // 3202
        System.out.println("PART1 ====> " + solution);
        return solution;
    }
    
    private record Cycle(long height, long fallen, int commandIndex) {
        public String toString() {
            return "height +"+height+", stopped +"+fallen;
        }
    }
    
    public void part2() throws IOException {
        long todo = 1000000000000L;
        
        Cycle first = nextCycle(0, todo);
        System.out.println(first);
        todo -= first.fallen;
        
        Cycle then = nextCycle(first.fallen, todo);
        System.out.println(then);
        long totalCycle = (1000000000000L - first.fallen) / then.fallen;
        long totalHeight = totalCycle * then.height + first.height;
        todo -= totalCycle * then.fallen;
        
        Cycle last = nextCycle(first.fallen + then.fallen, todo);
        System.out.println(last);
        totalHeight += last.height; 
        todo -= last.fallen;

        System.out.println( todo );
        
        System.out.println();
        // 1591977077383 false
        // 1591977077358 false
        // 1591977077352  YES
        System.out.println("PART2 ====> " + totalHeight);
        // return solution;
    }
    
    public Cycle nextCycle(long fromStopped, long max) {
        long stopped = 0L;
        long beginHeight = 0;
        long heightBeforeCycle = 0;
        long stopBeforeCycle = 0;
        int commandIndex = -1;
        
        playGround = new PlayGround();
        playGround.nextBlock();
        
        HashSet<Integer> cycles = new HashSet<Integer>(); 
        
        long stopAt = fromStopped + max;
        while ( stopped < stopAt ) {
            
            commandIndex++;
            if ( commandIndex >= commandSize ) {
                commandIndex = 0;
            }
            
            char c = commands.charAt(commandIndex);
            if ( c == '<' ) playGround.moveLeft();
            if ( c == '>' ) playGround.moveRight();
            if ( !playGround.moveDown() ) {
                stopped++;
                playGround.addBlockInCave();
                playGround.nextBlock();
                if ( cycles.contains(commandIndex) ) {
                    break;
                }
                heightBeforeCycle = playGround.totalHeight();
                stopBeforeCycle = stopped;
                if ( stopped == fromStopped )
                    beginHeight = heightBeforeCycle;
                if ( stopped > fromStopped )
                    cycles.add(commandIndex);
                if ( stopped % 10000  == 0) System.out.println(". " + stopped);
                if ( commandIndex == 0 ) { break; }
            }

        }
        
        long height = heightBeforeCycle - beginHeight;
        long fallen = stopBeforeCycle - fromStopped;
        return new Cycle(height, fallen, commandIndex);
    }
    
    
    static int MAX_HEIGHT = 4;
    public static enum RockType {
        A_HORZBAR("####"),
        B_CROSS(" # ,###, # "),
        C_CORNER("  #,  #,###"),
        D_VERTBAR("#,#,#,#"),
        E_SQUARE("##,##");
        
        byte[] colisions = new byte[MAX_HEIGHT*MAX_HEIGHT];
        int width;
        int height;
        
        RockType(String block) {
            List<String> lines =Arrays.asList( block.split(",") );
            height = lines.size();
            int y = MAX_HEIGHT - height;
            for(String line : lines) {
                int x = 0;
                if ( line.length() > width ) width = line.length();
                for( byte b : line.getBytes() ) {
                    colisions[y*MAX_HEIGHT+x] = b == ' ' ? 0 : b;
                    x++;
                }
                y++;
            }
        }
        
        public RockType next() {
            int i = ( ordinal() + 1 ) % values().length;
            return values()[i];
        }
        
    }
    
    public static class PlayGround {
        static int WIDTH = 7;
        static String EMPTY_LINE = ".......";
        List<String> rows = new ArrayList<>(16);
        
        int fallX, fallY;
        RockType fallingRock;
        RockType nextFallingRock = RockType.A_HORZBAR;
        long truncated = 0;
        
        public void nextBlock() {
            nextBlock(nextFallingRock);
        }
        
        public long totalHeight() {
            return rows.size() + truncated;
        }

        public void nextBlock(RockType rockType) {
            fallingRock = rockType;
            nextFallingRock = fallingRock.next(); // default next rockType
            
            fallY = rows.size() + 3;
            fallX = 2;
        }
        
        public PlayGround() {
        }
        
        public boolean moveRight() {
            if ( fallingRock == null ) return false;
            if ( fallX + fallingRock.width >= WIDTH ) return false;
            if ( collideCaveBlock(fallX+fallingRock.width, fallingRock.width-1) ) return false;
            
            fallX++;
            return true;
        }
        
        private boolean collideCaveBlock(int caveX, int blockX) {
            int caveHeight = rows.size();
            for ( int dy = 0; dy < fallingRock.height; dy++ ) {
                int y = fallY + dy;
                if ( y >= caveHeight ) continue;
                int rockTest = fallingRock.colisions.length - (MAX_HEIGHT * (dy + 1))
                        + blockX;
                byte b = 0;
                int caveTest = caveX;
                while ( (b = fallingRock.colisions[rockTest]) == 0 ) {
                    if ( caveX < fallX ) { rockTest++; caveTest++; }
                    else { rockTest--; caveTest--; }
                }
                if ( rows.get(y).charAt(caveTest) == '#' 
                    && fallingRock.colisions[rockTest] == '#' ) return true;
            }
            return false;
        }

        public boolean moveLeft() {
            if ( fallingRock == null ) return false;
            if ( fallX == 0 ) return false;
            if ( collideCaveBlock(fallX-1, 0) ) return false;
            fallX--;
            return true;
        }
        
        public boolean moveDown() {
            if ( fallingRock == null ) return false;
            if (fallY == 0) return false;
            if (fallY-1 < rows.size()) { 
                for (int dx = 0; dx < fallingRock.width; dx++) {
                    int rockTest = fallingRock.colisions.length - MAX_HEIGHT + dx;
                    int caveTest = fallY - 1;
                    while ( fallingRock.colisions[rockTest] == 0 ) {
                        rockTest -= MAX_HEIGHT; caveTest++;
                    }
                    if ( caveTest >= rows.size() ) continue;
                    if ( rows.get(caveTest).charAt(fallX+dx) == '#' 
                            && fallingRock.colisions[rockTest] == '#' ) return false;
                }
            }
            fallY--;
            return true;
        }
        
        int lastLeft = -1;
        int lastRight = -1;
        public void addBlockInCave() {
            if ( fallingRock == null ) return;
            for (int dy = 0; dy < fallingRock.height; dy++) {
                int caveY = fallY+dy;
                if ( caveY >= rows.size() ) rows.add(EMPTY_LINE);
                byte[] rowBytes = rows.get(fallY+dy).getBytes();
                int rockY = (MAX_HEIGHT - dy - 1) * MAX_HEIGHT;
                for(int dx = 0; dx < fallingRock.width; dx++) {
                    rowBytes[fallX+dx] = rowBytes[fallX+dx] == '#' ? (byte) '#' : 
                        (fallingRock.colisions[rockY+dx] == 0 ? (byte) '.' : fallingRock.colisions[rockY+dx] );
                }
                rows.set(caveY, new String(rowBytes));
            }
            fallingRock = null;

            if ( rows.size() < 10000 ) return;
            
            truncated++;
            rows.remove(0);
            
        }
        
        public void show() {
            
            int dispY = fallingRock == null ? 
                    rows.size()-1 : Math.max(rows.size()-1, fallY+fallingRock.height-1);
            
            for (; dispY >= 0; dispY--) {
                byte[] caveRow = dispY >= rows.size() ? EMPTY_LINE.getBytes() : rows.get(dispY).getBytes();
                if ( fallingRock != null  && dispY >= fallY && dispY < fallY + fallingRock.height ) {
                    int rockY = (fallingRock.colisions.length - MAX_HEIGHT) - (dispY - fallY) * MAX_HEIGHT;
                    for(int dx = 0; dx < fallingRock.width; dx++) {
                        caveRow[dx+fallX] = 
                                fallingRock.colisions[rockY+dx] == '#' ? 
                                        (byte) '@' : caveRow[dx+fallX]; 
                    }
                }
                
                String row = new String( caveRow );
                System.out.println("|"+row+"|");
            }
            System.out.println("+-------+");
        }
        
    }

    
    
}

