package advent2022;

import static java.lang.Integer.parseInt;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
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
    
    
    public static void main(String[] args) throws Exception {
        new Day16()
            .load( Filename.SAMPLE.filename() )
            .part1();
    }
    
    List<Valve> valves;
    HashMap<String, Valve> byName;
    Valve AA;
    
    private Day16 load(String f) throws IOException {
        byName = new HashMap<>();
        valves = Files.lines( dayFile(f) )
            .map(Valve::fromString)
            .map( v -> {
                byName.put(v.name, v);
                return v;
            })
            .toList();
        AA = byName.get("AA");
        return this;
    }

    public void part1() throws IOException {
        valves
            .stream()
            .map( Valve::details )
            .forEach( System.out::println );
        // System.out.println(AA);
        
        List<Valve> toOpen = valves.stream()
            .filter( v -> v.flow > 0 )
            .collect( Collectors.toList() );
        
        
        valves.forEach( v -> v.maxFlow = 0 );
        toOpen.forEach( v -> flow(v, State.MAX_TIME) );
        
        Valve current = AA;
        int totalFlow = 0;
        int timeLeft = State.MAX_TIME;
        outter:
        while ( timeLeft > 0 ) {
            
            Integer max = current.maxFlow;
            current.maxFlow = 0;
            // Valve next = current;
            do {
                Valve next = current.tunnels.stream()
                    .map( byName::get )
                    .sorted( Comparator.comparingInt((Valve v) -> v.maxFlow).reversed() )
                    .map( v -> {
                        System.out.println("  "+v+" > "+v.maxFlow);
                        return v;
                    })
                    .toList()
                    .get(0);
                
                
                if ( next.maxFlow == 0 ) break outter;
                if ( next.maxFlow <= max ) break;

                timeLeft--;

                
                System.out.println("  Move to " + next + " ("+next.maxFlow+")");
                current = next;
            } while(timeLeft > 0);
            
            if (!toOpen.contains(current)) {
                System.out.println("no open ?");
                break;
            }
            else {
                timeLeft--;
                int addedFlow = timeLeft * current.flow;
                System.out.println("Opened " + current + " (+"+addedFlow+")");
                toOpen.remove(current);
                totalFlow += addedFlow;
            }
            
            valves.forEach( v -> v.maxFlow = 0 );
            final int _timeLeft = timeLeft;
            toOpen.forEach( v -> flow(v, _timeLeft) );
        }
        
        
        System.out.println();
        System.out.println("PART1 ====> " + totalFlow);

    }
    
    
    private void flow(Valve from, int timeLeft) {
        
        Set<Valve> toVisit = new HashSet<>( );
        Set<Valve> visited = new HashSet<>( );
        int sum = from.flow * timeLeft;
        toVisit.add(from);
        mins.put(from.name, 0);
        
        while ( !toVisit.isEmpty() ) {
            sum -= from.flow;
            if ( sum <= 0 ) {
                return;
            }
            final int _sum = sum;
            toVisit.forEach( v -> v.maxFlow += _sum );
            visited.addAll(toVisit);
            toVisit = toVisit.stream()
                .flatMap( v -> v.tunnels.stream() )
                .map( byName::get )
                .filter( Predicate.not(visited::contains) )
                .collect( Collectors.toSet() ); 
        }
        
    }
    
    private State bestCandidate(State state, List<Valve> toOpen) {
        int bestFlow = 0;
        State candidate = state.copy();
        for ( Valve goHere : toOpen ) {
            int timeLeft = state.timeLeft 
                    - getTimeTo( state.location, goHere, state.timeLeft )  // time to move
                    - 1; // time to open
            if (timeLeft < 0) continue;
            int flowToAdd = timeLeft * goHere.flow;
            if ( flowToAdd > bestFlow ) {
                bestFlow = flowToAdd;
                candidate.location = goHere;
                candidate.totalFlow = state.totalFlow + flowToAdd;
                candidate.timeLeft = timeLeft;
            }
        }
        return bestFlow == 0 ? null : candidate;
    }
    
    
    /**
     * Works for sample, but too complex for real DATA. Complexity O(n!) (= 87,178,291,200 permutations)
     */
    private int bestAllCandidate(List<Valve> toOpen) {
        int permutationI = 0;
        int bestFlow = 0;
        List<Valve> bestList = null;
        for(List<Valve> perm : AdventDay.permutations(toOpen)) {
            permutationI++;
            if (permutationI % 100 == 0)
                System.out.println( "--- permutation #" + permutationI +" ---" );
            // System.out.println( "  | " +perm.stream().map(v -> v.name).toList());
            int flow = 0;
            int timeLeft = State.MAX_TIME;
            Valve from = AA;
            for ( Valve goHere : perm ) {
                timeLeft = timeLeft 
                        - getTimeTo( from, goHere, timeLeft )  // time to move
                        - 1; // time to open
                if (timeLeft < 0) break;
                flow += timeLeft * goHere.flow;
                from = goHere;
            }
            if ( flow > bestFlow ) {
                bestFlow = flow;
                System.out.println("  * Best flow -> " + bestFlow );
                bestList = new LinkedList<>( perm );
                
            }
        }
        System.out.println("[[ Best flow : " + bestFlow);
        System.out.println("[[ List      : " + bestList.stream().map(v -> v.name).toList());
        return bestFlow;
    }

    Map<String, Integer> mins = new HashMap<>();
    private int getTimeTo(Valve from, Valve to, int timeLeft) {
        mins = new HashMap<>();
        
        Set<Valve> toVisit = new HashSet<>( );
        Set<Valve> visited = new HashSet<>( );
        
        int sum = 0;
        toVisit.add(from);
        mins.put(from.name, 0);
        
        while ( !toVisit.isEmpty() ) {
            Valve current = toVisit.iterator().next();
            toVisit.remove(current);
            sum = mins.get(current.name) + 1;
            visited.add(current);
            if ( sum > timeLeft ) continue;
            for(String valveName : current.tunnels) {
                Valve valve = byName.get(valveName);
                final int _sum = sum;
                mins.compute( valve.name, (k,v) -> {
                    if ( v == null ) return _sum;
                    return Math.min(v, _sum);
                });
                if (!visited.contains(valve)) toVisit.add(valve);
            }
        }
        
        /*
        System.out.println();
        mins.forEach( (k,v) -> {
            System.out.println(k+": "+v);
        });
        System.out.println();
        */
        
        return Optional.ofNullable( mins.get(to.name) )
                .orElse( Integer.MAX_VALUE );
    }

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

