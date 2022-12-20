package advent2022;

import static java.lang.Integer.parseInt;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Day16 implements AdventDay {
    
    private static Pattern pattern = Pattern.compile( 
            "Valve ([A-Z]*) has flow rate=(\\d+); tunnel[s]? lead[s]? to valve[s]? (.*)"
    );
    
    
    public static void main(String[] args) throws Exception {
        new Day16()
            .load( Filename.SAMPLE.filename() )
            .part1();
    }
    
    List<String> names = new ArrayList<>();
    HashMap<String, Integer> flows  = new HashMap<>();
    HashMap<String, List<String>> conn  = new HashMap<>();
    int[][] grid;
    Valve AA;
    
    private Day16 load(String f) throws IOException {
        Files.lines( dayFile(f) )
            .map( pattern::matcher )
            .filter( Matcher::matches )
            .map( m -> {
                names.add( m.group(1) );
                return m;
            })
            .map( m -> {
                flows.put( m.group(1), parseInt(m.group(2)));
                return m;
            })
            .forEach( m -> {
                conn.put(
                    m.group(1), 
                    Stream.of( m.group(3).split(",") )
                        .map( String::trim )
                        .toList() );
            });
        
        int len = names.size();
        grid = new int[len][len];
        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid.length; j++) 
                grid[i][j] = 9999;

        for (int i = 0; i < names.size(); i++) {
            List<String> tunnels = conn.get( names.get(i) );
            for (String tunnel : tunnels ) {
                int j = names.indexOf(tunnel);
                if ( j < 0 ) System.out.println( tunnel );
                grid[i][j] = 1;
                // System.out.println( i + " -> " + j + " :: " + grid[i][j] );
            }
        }
        
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                int min = 9999;
                if ( i == j ) grid[i][j] = 0;
                for (int k = 0; k < grid.length; k++) {
                    int l = grid[i][k] + grid[k][j];
                    if ( l < min ) min = l;
                }
                grid[i][j] = min;
                // System.out.println( names.get(i) + " -> " + names.get(j) + " :: " + grid[i][j] );
            }
        }
        
        return this;
    }

    public void part1() throws IOException {
        
        // names.forEach(System.out::println);
        
        ArrayList<String> backTrace = new ArrayList<>(); 
        int totalFlow = dfs( names.indexOf("AA"), 
            flows.keySet().stream().filter(k -> flows.get(k) > 0 ).toList(),
            30,
            backTrace
        );
        
        System.out.println(backTrace);
        
        // 1952 xxx
        System.out.println();
        System.out.println("PART1 ====> " + totalFlow);
        
        System.out.println();
        System.out.println("hitcache => "+hitCache);
    }
    
    long hitCache = 0;
    HashMap<Entry, Integer> dfsMem = new HashMap<>();
    String pad = "  ";
    private int dfs(int idx, List<String> lst, int time, List<String> backTrace) {
        Entry e = new Entry(idx, lst, time);
        /* */
        if ( dfsMem.get(e) != null ) {
            hitCache++;
            return dfsMem.get(e);
        }
        /* */
        int res = 0;
        if ( time > 0 ) {
            String nextMax = "";
            // pad = pad + "  ";
            List<String> subList = Collections.emptyList();
            List<String> maxList = Collections.emptyList();
            backTrace.add(names.get(idx));
            for (String r : lst) {
                int j = names.indexOf(r);
                int timeLeft = (time - grid[idx][j] - 1);
                if ( timeLeft <= 0 ) continue;
                List<String> rest = new ArrayList<>(lst);
                rest.remove(r);
                subList = new ArrayList<>();
                int flow = flows.get(r) * timeLeft;
                if ( !rest.isEmpty() ) flow += dfs( names.indexOf(r), rest, timeLeft, subList);
                if ( flow > res ) {
                    maxList = subList;
                    res = flow;
                    nextMax = r;
                }
            }
            if ( res > 0 && !nextMax.isEmpty() ) {
                backTrace.addAll(maxList);
                // System.out.println(pad + "Next " + nextMax + " at " + (time - grid[idx][names.indexOf(nextMax)]) + " for " + res );
            }
            // pad = pad.substring(2);
        }
        dfsMem.put(e, res);
        return res;
    }

    static record Entry(int idx, List<String> from, int time) {} 
    

    public void part2() throws IOException {
        
    }
    
    
    public static class State {
        static int MAX_TIME = 30;
        
        int timeLeft = MAX_TIME;
        int totalFlow = 0;
        Valve location;
        public State(Valve location) {
            this.location = location;
        }
        State copy() {
            State s = new State(location);
            s.timeLeft = timeLeft;
            s.totalFlow = totalFlow;
            return s;
        }
    }
    
    public static class Valve {
        private static final Pattern valvePattern = Pattern.compile(
            "Valve ([A-Z]*) has flow rate=(\\d+); tunnel[s]? lead[s]? to valve[s]? (.*)"
        );

        String name;
        int flow;
        List<String> tunnels;
        
        int maxFlow;
        
        public static Valve fromString(String str) {
            Matcher m = valvePattern.matcher(str);
            if ( !m.matches() ) throw new RuntimeException("Error in input " + str);
            Valve v = new Valve();
            v.name=m.group(1);
            v.flow = parseInt(m.group(2));
            v.tunnels = Stream.of( m.group(3).split(",") )
                    .map( String::trim )
                    .toList();
            return v;
        }
        
        @Override
        public String toString() {
            return name;
                    //String.format("[%s] .: %03d :.  >> %s", name, flow, next);
        }
        
        public String details() {
            return String.format("[%s] .: %03d :.  >> %s", name, flow, tunnels);
        }
        
    }
    
    
}

