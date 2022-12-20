package advent2022;

import static java.lang.Integer.parseInt;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;


public class Day16 implements AdventDay {
    
    private static Pattern pattern = Pattern.compile( 
        "Valve ([A-Z]*) has flow rate=(\\d+); tunnel[s]? lead[s]? to valve[s]? (.*)"
    );
    
    
    public static void main(String[] args) throws Exception {
        new Day16()
            .load( Filename.DATA.filename() )
            .part1();
    }
    
    List<String> names = new ArrayList<>();
    HashMap<String, Integer> flows = new HashMap<>();
    HashMap<String, List<String>> conn = new HashMap<>();
    LinkedList<String> toOpens = new LinkedList<>();
    int[][] grid;
    Valve AA;
    
    private Day16 load(String f) throws IOException {
        Files.lines( dayFile(f) )
            .map( pattern::matcher )
            .filter( Matcher::matches )
            .forEach( m -> {
                String name = m.group(1);
                names.add( name );
                int flow = parseInt(m.group(2));
                flows.put( name, flow);
                if (flow > 0) {
                    toOpens.add(name);
                }
                conn.put( name, 
                    Stream.of( m.group(3).split(",") )
                        .map( String::trim )
                        .toList() );
            });
        
        int len = names.size();
        grid = new int[len][len];
        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid.length; j++) 
                grid[i][j] = 9999;
        
        List<String> computeDist = new LinkedList<>( toOpens );
        computeDist.add("AA");
        for( String node : computeDist ) {
            LinkedList<String> visited = new LinkedList<>();
            LinkedList<String> toVisit = new LinkedList<>();
            HashMap<String, Integer> mins = new HashMap<>();
            mins.put(node, 0);
            toVisit.add(node);
            while(!toVisit.isEmpty()) {
                String n = toVisit.removeFirst();
                final int _sum = mins.get(n) + 1;
                visited.add(n);
                conn.get(n)
                    .forEach( k -> {
                        mins.compute( k, (k1, v) -> {
                            if ( v == null ) return _sum;
                            return Math.min(v, _sum);
                        });
                        if (!visited.contains(k)) toVisit.add(k);
                    });
            }
            mins.forEach( (k, v) -> {
                grid[names.indexOf(node)][names.indexOf(k)] = v;
            });
        }
        return this;
    }

    public void part1() throws IOException {
        
        // names.forEach(System.out::println);
        
        ArrayList<String> backTrace = new ArrayList<>(); 
        int totalFlow = dfs( names.indexOf("AA"), 
            toOpens,
            30,
            backTrace
        );
        
        System.out.println(backTrace);
        
        System.out.println();
        System.out.println("hitcache => "+hitCache);
        
        // 1952 xxx
        // 2087 is OK !!
        System.out.println();
        System.out.println("PART1 ====> " + totalFlow);
        
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

