package advent2022;

import static java.lang.Integer.parseInt;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Day15 implements AdventDay {
    
    
    public static void main(String[] args) throws Exception {
        new Day15()
            .load( Filename.DATA.filename() )
            .part2();
    }
    
    Set<Beacon> beacons = new HashSet<>();
    List<Sensor> sensors = new LinkedList<>();
    
    
    Pattern p = Pattern.compile("Sensor at x=([-]?\\d+), y=([-]?\\d+): closest beacon is at x=([-]?\\d+), y=([-]?\\d+)");

    private Day15 load(String f) throws IOException {
        for(String str : Files.readAllLines(dayFile(f))) {
            addLine(str);
        }
        return this;
    }
    
    private void addLine(String line) {
        Matcher m = p.matcher(line);
        if ( !m.matches() ) {
            System.err.println("Bad pattern for: "+line);
        }
        
        Sensor s = new Sensor();
        s.x = parseInt( m.group(1) );
        s.y = parseInt( m.group(2) );
        
        Beacon b = new Beacon();
        b.x = parseInt( m.group(3) );
        b.y = parseInt( m.group(4) );
        
        s.setNearest( b );
        
        sensors.add(s);
        beacons.add(b);
    }

    public void part1() throws IOException {
        // final int line = 10;
        final int line = 2000000;
        
        List<Zone> mergedZones = zonesAt(line);
        
        System.out.println(":: Final list ::");
        mergedZones.forEach( System.out::println );
        
        long sensorsIn = mergedZones.stream()
            .flatMap( z ->
                sensors.stream()
                    .filter( s -> s.y == line && s.x >= z.from && s.x <= z.to )
            )
            .count();
        System.out.println("sensors in : " + sensorsIn);
        
        long beaconsIn = mergedZones.stream()
            .flatMap( z ->
                beacons.stream()
                    .filter( s -> s.y == line && s.x >= z.from && s.x <= z.to )
            )
            .count();
        System.out.println("beacons in : " + beaconsIn);
        
        int total = mergedZones.stream()
                .mapToInt( Zone::width )
                .sum() - (int) beaconsIn - (int) sensorsIn; 
        
        System.out.println("PART1 ====> " + total );
    }
    
    private void test() {
        addLine("Sensor at x=8, y=7: closest beacon is at x=2, y=10");
        Sensor s = sensors.get(0);
        System.out.println( s.dist );
        System.out.println( s.noBeaconZoneAt(10) );
        
        /*      
         * 0123456
         * | xxxxx
         * |xxxx
         * |
         */
        int dist = new Zone(1, 4).cover( new Zone(2, 6) );
        System.out.println(dist);
        
        /* 0123456789
         * | xxxxx
         * |xxxxxxxxx
         * |
         */
        dist = new Zone(1, 9).cover( new Zone(2, 6) );
        System.out.println(dist);
    }
    
    public void part2() throws IOException {
        int xSignal = 0;
        int ySignal = 0;
        
        Zone search = new Zone(0, 4000000);
        int maxY = 4000000;
        
        for ( int line = 0; line <= maxY; line++ ) {    
            if ( line % 10000 == 0 ) System.out.println("... " + line);

            List<Zone> mergedZones = zonesAt(line); 
            
            // crop zones ...
            for(Zone z : mergedZones) z.crop(search);
            
            // System.out.println(":: Final list ::");
            // mergedZones.forEach( System.out::println );
            
            if ( mergedZones.size() == 1 ) {
                Zone z = mergedZones.get(0);
                if (z.width() >= 4000000) continue;
                if (z.cover(search) == 1) {
                    System.out.println( mergedZones.get(0) );
                    break;
                }
            }
            
            System.out.println("DONE");
            Zone z1 = mergedZones.get(0);
            Zone z2 = mergedZones.get(1);
            
            ySignal = line;
            xSignal = z1.from < z2.from ? z1.to+1 : z2.to+1;
            
            break;
        }
        
        long total = xSignal * 4000000L + ySignal;
        System.out.println("PART2 ====> " + total );
    }
    
    private List<Zone> zonesAt(int line) {
        final int linezz = line;
        List<Zone> zones = sensors.stream()
                .map( sen -> sen.noBeaconZoneAt(linezz) )
                .filter( Optional::isPresent )
                .map( Optional::get )
                .toList();
        
        // zones.forEach( System.out::println );
        
        List<Zone> mergedZones = new ArrayList<>();
        for(Zone fromZone : zones) {
            Zone toAdd = fromZone;
            List<Zone> toRemove = new ArrayList<>();
            for (Zone done : mergedZones) { 
                if ( toAdd.cover(done) > 0 ) {
                    // System.out.println( "Merging "+toAdd+" and "+done);
                    toRemove.add(done);
                    toAdd = toAdd.merge(done);
                    // System.out.println("  --> " + toAdd);
                }
            }
            mergedZones.removeAll(toRemove);
            mergedZones.add(toAdd);
        }
        return mergedZones;
    }
    
    static class Beacon extends Point {
        boolean in(Zone z) {
            return x >= z.from && x <= z.to;
        }
    }
    
    static class Sensor extends Point {
        Beacon nearest;
        int dist;
        
        boolean in(Zone z) {
            return x >= z.from && x <= z.to;
        }

        public void setNearest(Beacon b) {
            dist = Math.abs(x - b.x) + Math.abs(y - b.y);
        }
        
        public Optional<Zone> noBeaconZoneAt(int y) {
            int diffY = Math.abs(this.y - y);
            if (diffY > dist) return Optional.empty();
            int dx = dist - diffY;
            Zone z = new Zone(x-dx, x+dx);
            return Optional.of(z);
        }
    }
    
    static class Zone {
        int from, to;

        public Zone(int from, int to) {
            this.from = from;
            this.to = to;
        }
        
        public Zone merge(Zone z) {
            return new Zone(Math.min(from, z.from), Math.max(to, z.to));        
        }
        
        public void crop(Zone search) {
            if ( to < 0 || from > 4000000) return;
            if (from < search.from || to > search.to ) {
                from = Math.max(search.from, from);
                to = Math.min(search.to, to);
            }
        }

        @Override
        public String toString() {
            return "zone["+from+" <> "+to+"]";
        }
        
        int cover(Zone z) {
            if ( to < z.from || from > z.to ) return 0;
            int dist = Math.min(to, z.to) - Math.min(from, z.from);
            return dist;
        }
        
        int width() {
            return to - from + 1;
        }
    }
    
    
    
}

