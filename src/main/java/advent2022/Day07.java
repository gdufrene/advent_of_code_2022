package advent2022;

import static java.lang.Integer.parseInt;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;



public class Day07 implements AdventDay {
    
    
    public static void main(String[] args) throws Exception {
        new Day07()
            .load( Filename.DATA )
            .part2();
    }
    
    Dir NONE = new Dir("NONE");
    
    class Dir {
        String name;
        long size = 0;
        List<Dir> sub = new LinkedList<>();
        Dir parent = NONE;
        public Dir(String name) {
            this.name = name;
        }
        void add(long size) {
            //System.out.format("  ... Add %d to %s\n", size, name);
            this.size += size;
        }
        Dir parent(Dir dir) {
            this.parent = dir;
            return this;
        }
        String path() {
            Dir current = this;
            List<String> names = new ArrayList<>();
            while(!current.name.equals("/")) {
                names.add( current.name );
                current = current.parent;
            }
            Collections.reverse(names);
            return "/"+String.join("/", names);
        }
    }
    
    List<String> lines;
    Map<String, Dir> map;
    Dir root;
    
    
    private Day07 load(Filename f) throws IOException {
        
        map = new HashMap<>();
        lines = Files.readAllLines( dayFile(f.filename()) );
        
        root = new Dir("/");
        root.parent(root);
        map.put("/", root);
        
        Dir dir = root;
        String path = "/";
        
        // Consumer<Integer> saveSize = s -> map.put(dir, s);
        
        for (String line : lines) {
            if ( line.isBlank() ) continue;
            
            if (line.startsWith("$ cd ")) {
                // map.put(dir, size);
                String to = line.substring(5);
                if ( to.equals("/") ) {
                    dir = root;
                    path = "/";
                }
                else if (to.equals("..")) {
                    dir = dir.parent;
                    path = dir.path();
                }
                else {
                    if ( path.equals("/") ) path += to;
                    else path += "/"+to;
                    
                    final Dir _dir = dir;
                    dir = map.compute(path, (k,v) -> v == null ? new Dir(to).parent(_dir) : v);
                    if ( !_dir.sub.contains(dir) ) _dir.sub.add(dir);
                }
                // size = map.get(dir);
            }
            else if(line.startsWith("dir ")) {
                
            }
            else if(line.startsWith("$ ls")) {
                dir.size = 0;
            }
            else {
                int size = parseInt(line.split(" ")[0]);
                dir.add( size );
            }
        }
        
        return this;
    }
    
    String pad = "";
    long walkSum(Dir dir, BiConsumer<Dir, Long> action) {
        long sum = 0;
        //System.out.println(pad+"- " + dir.name);
        pad += "  ";
        for (Dir d : dir.sub) {
            sum += walkSum(d, action);
        }
        pad = pad.substring(0, pad.length()-2);
        sum += dir.size;
        //System.out.println(pad+":: " + sum);
        action.accept(dir, sum);
        return sum;
    }
    
    
    /* -----------------------------------------------------------------------------
     *                               PART 1
     * ----------------------------------------------------------------------------- */
    

    long sumLower100k = 0;
    
    public void part1() throws IOException {
        map.forEach( (path, dir) ->  System.out.format("- %-30s [%09d]\n", path, dir.size) );
        
        walkSum(root, (dir, size) -> { if (size < 100000) sumLower100k += size;  } );
        System.out.println( "Res ==> " + sumLower100k);
    }
    
    
    /* -----------------------------------------------------------------------------
     *                               PART 2
     * ----------------------------------------------------------------------------- */
    
    long smallestCandidate = Long.MAX_VALUE;
    
    public void part2() throws IOException {
        long totalSize = walkSum(root, (p,d) -> {});
        System.out.println( "Total size => " + totalSize );
        long freeSpace = 70000000 - totalSize;
        System.out.println( "Free space => " + freeSpace );
        long needSpace = 30000000 - freeSpace;
        System.out.println( "Need space => " + needSpace );
        
        walkSum( root, (dir, size) -> {
            if (size > needSpace && size < smallestCandidate) {
                smallestCandidate = size;
                System.out.println( "Candidate " + dir.path() + " with " + smallestCandidate );
            }
        });
        
        System.out.println("===> "+smallestCandidate);
    }
    

}

