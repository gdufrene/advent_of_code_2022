package advent2022;

import static java.lang.Integer.parseInt;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;


public class Day18 implements AdventDay {
    
    
    public static void main(String[] args) throws Exception {
        new Day18()
            .load( Filename.DATA.filename() )
            .part2();
    }
    List<Cube> cubes;
    
    
    private Day18 load(String f) throws IOException {
        cubes = Files.lines( dayFile(f) )
                .map(Cube::fromString)
                .toList();
        return this;
    }
    
    List<Cube> equal = new LinkedList<>();
    List<Cube> great = Collections.emptyList();

    public Object part1() throws IOException {
        // cubes.forEach( System.out::println);
        int sol = externalSurface();
        
        System.out.println();
        System.out.println( "PART1 =====> " + sol );
        
        return sol;
    }
    
    private int externalSurface() {
        Node<Cube> root = null;
        for(Cube cube : cubes) {
            if ( root == null ) {
                root = new Node<>(cube);
                continue;
            }
            root.add(cube);
        }
        
        root.forEach( (Cube cube) -> {
            int s = cube.sum();
            if ( equal.isEmpty() ) { equal.add(cube); return; }
            int a = equal.get(0).sum();
            if ( a == s )  { equal.add(cube); return; }
            joins(equal, great);
            great = a-s == 1 ? equal : Collections.emptyList();
            equal = new LinkedList<>();
            equal.add(cube);
        });
        if ( !great.isEmpty() )
            joins(equal, great);
        
        AtomicInteger sol = new AtomicInteger();
        root.forEach( (Cube cube) -> sol.addAndGet(cube.exposed) );
        
        return sol.get();
    }
    
    private void joins(List<Cube> l1, List<Cube> l2) {
        for (Cube c1 : l1) 
            for (Cube c2 : l2)
                c1.tryConnect(c2);
    }
    
    private void show(List<Cube> l1) {
        System.out.println();
        l1.forEach( System.out::println );
    }
    
    
    int groupId;
    public Object part2() throws IOException {
        // to connect cubes
        externalSurface();
        
        List<Group> groups = new ArrayList<>();
        for (Cube c1 : cubes) {
            if (c1.groupId != 0) continue;
            int id = ++groupId;
            groups.add( assignGroup(c1, id) );
        }
        
        System.out.println( "Groups -> " + groups.size() );
        LinkedList<Cube> alones = new LinkedList<>();
        groups.forEach( g -> {
            System.out.println(g.groupId+": " + g.volume()+ " / "+g.cubes.size());
            if ( g.cubes.size() == 1 ) 
                alones.add(g.cubes.first());
        } );
        System.out.println();
        
        Group g1 = groups.get(0);
        
        Cube out = new Cube();
        out.x = g1.max[0];
        out.y = g1.max[1];
        out.z = g1.max[2];
        System.out.println( "Start at : " + out );

        
        LinkedList<Cube> toVisit = new LinkedList<>();
        toVisit.add(out);
        HashSet<Cube> visited = new HashSet<>();
        
        int exposed = 0;
        int touched = 0;
        while(!toVisit.isEmpty()) {
            Cube c1 = toVisit.removeFirst();
            if ( visited.contains(c1) ) continue;
            
            // count touching from next group
            for ( Cube c2 : g1.groupLevel(c1.sum()-1) ) {
                if ( c1.touching(c2) ) 
                    exposed++;
            }
            for ( Cube c2 : g1.groupLevel(c1.sum()+1) ) {
                if ( c1.touching(c2) ) 
                    exposed++;
            }
            
            if (alones.contains(c1)) {
                exposed += 6;
                System.out.println(" touch lonely : " + c1);
                alones.remove(c1);
            }
            
            // add to visited ?
            visited.add(c1);
            
            // add Down, Left, Back (-1) toVisit,
            for (Cube c2 : c1.nextLowers()) {
                // if still inside group volume
                if ( visited.contains(c2) ) continue;
                if (g1.overEdges(c2)) continue;
                if (g1.contains(c2)) {
                    touched++;
                    continue;
                }
                // if possible (not an existing cube)
                toVisit.add(c2);
            }
        }
        
        Cube c = null;
        while( !alones.isEmpty() && (c = alones.removeFirst()) != null ) {
            if (g1.overEdges(c)) {
                exposed += 6;
                System.out.println("  lonely ouside G1 : " + c);
            } else {
                System.out.println("  lonely inside G1 : " + c);
            }
        }
        
        System.out.println("visited: " + visited.size());
        System.out.println("alones: " + alones.size());
        System.out.println("touched: " + touched);
        
        // 2442 too low
        System.out.println( "PART2 =====> " + exposed );
        
        return exposed;
    }
    
    private Group assignGroup(Cube c, int groupId) {
        Group g = new Group(groupId);
        Set<Cube> visited = new HashSet<>();
        LinkedList<Cube> toVisit = new LinkedList<>();
        toVisit.add(c);
        while(!toVisit.isEmpty()) {
            Cube c2 = toVisit.removeFirst();
            if ( visited.contains(c2) ) continue;
            g.add(c2);
            visited.add(c2);
            toVisit.addAll(c2.connected);
        }
        return g;
    }
    
    
    public static class Cube implements Comparable<Cube> {
        static private int counter = 0;
        int x, y, z;
        int idx = 0;
        int exposed = 6;
        int groupId;
        List<Cube> connected = new ArrayList<>(3);
        public static Cube fromString(String str) {
            Cube c = new Cube();
            String part[] = str.split(",");
            c.idx = ++counter;
            c.x = parseInt(part[0]);
            c.y = parseInt(part[1]);
            c.z = parseInt(part[2]);
            return c;
        }
        public Cube[] nextLowers() {
            Cube c1 = copy(); c1.x--;
            Cube c2 = copy(); c2.y--;
            Cube c3 = copy(); c3.z--;
            
            Cube c4 = copy(); c4.x++;
            Cube c5 = copy(); c5.y++;
            Cube c6 = copy(); c6.z++;
            return new Cube[] {c1, c2, c3, c4, c5, c6};
        }
        public boolean touching(Cube c2) {
            int dx = Math.abs( x - c2.x );
            int dy = Math.abs( y - c2.y );
            int dz = Math.abs( z - c2.z );
            return dx+dy+dz == 1 ;
        }
        public Cube copy() {
            Cube o = new Cube();
            o.x = x;
            o.y = y;
            o.z = z;
            return o;
        }
        public void tryConnect(Cube c2) {
            if ( touching(c2) ) {
                exposed--;
                c2.exposed--;
                connected.add(c2);
                c2.connected.add(this);
            }
        }
        @Override public String toString() {
            return String.format("[%3d] %2d, %2d, %2d", idx, x, y, z);
        }
        @Override public int compareTo(Cube o) {
            return sum() - o.sum();
        }
        public int sum() {
            return x+y+z;
        }
        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Cube other = (Cube) obj;
            return x == other.x && y == other.y && z == other.z;
        }
    }
    
    
    public static class Node<T extends Comparable<T>> {
        T value;
        Node<T> left, right;
        Node( T value ) {
            this.value = value;
        }

        public void forEach(Consumer<T> todo) {
            if (left != null) left.forEach(todo);
            todo.accept(value);
            if (right != null) right.forEach(todo);
        }
        
        public int size() {
            AtomicInteger size = new AtomicInteger();
            forEach( c -> size.incrementAndGet() );
            return size.get();
        }
        
        public T first() {
            T res = value;
            Node<T> next = this;
            while ( next.left != null ) {
                res = next.value;
                next = next.left;
            }
            return res;
        }

        public void add(T val) {
            int diff = value.compareTo(val);
            if (diff < 0) {
                if ( left == null ) left = new Node<>(val);
                else left.add(val);
                return;
            } 
            if ( diff > 0 ) {
                if ( right == null ) right = new Node<>(val);
                else right.add(val);
                return;
            }
            if ( left == null ) left = new Node<>(val);
            else if ( right == null ) right = new Node<>(val);
            else left.add(val);
        }
    }
    
    public static class Group {
        Node<Cube> cubes; // root 
        
        int[] min = new int[3]; // xmin ymin zmin
        int[] max = new int[3]; // xmax ymax zmax
        int groupId;
        public Group(int id) {
            groupId = id;
            for (int i = 0; i < max.length; i++) {
                min[i] = Integer.MAX_VALUE;
                max[i] = Integer.MIN_VALUE;
            }
        }
        public boolean contains(Cube c2) {
            for ( Cube c : groupLevel(c2.sum())) {
                if (c.equals(c2)) return true;
            }
            return false;
        }
        public boolean overEdges(Cube c2) {
            if ( c2.x > max[0] + 1 ) return true;
            if ( c2.y > max[1] + 1 ) return true;
            if ( c2.z > max[2] + 1 ) return true;
            
            if ( c2.x < min[0] - 2 ) return true;
            if ( c2.y < min[1] - 2 ) return true;
            if ( c2.z < min[2] - 2 ) return true;
            
            return false;
        }
        public int volume() {
            return (
                (max[0] - min[0]) *
                (max[1] - min[1]) *
                (max[2] - min[2])
            );
        }
        public void add(Cube c) {
            if ( cubes == null) cubes = new Node<>(c);
            else cubes.add(c);
            
            c.groupId = groupId; 
            
            if ( c.x < min[0] ) min[0] = c.x;
            if ( c.y < min[1] ) min[1] = c.y;
            if ( c.z < min[2] ) min[2] = c.z;
            
            if ( c.x+1 > max[0] ) max[0] = c.x+1;
            if ( c.y+1 > max[1] ) max[1] = c.y+1;
            if ( c.z+1 > max[2] ) max[2] = c.z+1;
        }
        
        HashMap<Integer, List<Cube>> byLevel = new HashMap<>(); 
        
        public List<Cube> groupLevel(int level) {
            
            if ( byLevel.get(level) != null ) return byLevel.get(level);
            
            AtomicReference<List<Cube>> res = new AtomicReference<>( Collections.emptyList() );
            new GroupWalk()
                .eachEqual(
                    lst -> {
                        if (lst.isEmpty()) return;
                        if (lst.get(0).sum() == level) res.set(lst);
                    }
                );
            
            byLevel.put(level, res.get());
            
            return res.get();
        }
        
        private class GroupWalk {
            List<Cube> equal = new LinkedList<>();

            void eachEqual( Consumer<List<Cube>> fn ) {
                cubes.forEach( (Cube cube) -> {
                    int s = cube.sum();
                    if ( equal.isEmpty() ) { equal.add(cube); return; }
                    int a = equal.get(0).sum();
                    if ( a == s )  { equal.add(cube); return; }
                    fn.accept(equal);
                    equal = new LinkedList<>();
                });
                
            }
        }
    }
    
}

