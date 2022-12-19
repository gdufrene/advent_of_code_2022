package advent2022;

import static java.lang.Integer.parseInt;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Day19 implements AdventDay {
    
    
    public static void main(String[] args) throws Exception {
        new Day19()
            .load( Filename.SAMPLE.filename() )
            .part1();
    }
    
    List<Blueprint> blueprints;
    
    
    private Day19 load(String f) throws IOException {
        Pattern bp = Pattern.compile(
            "Blueprint \\d+: "
            + "Each ore robot costs (\\d+) ore. "
            + "Each clay robot costs (\\d+) ore. "
            + "Each obsidian robot costs (\\d+) ore and (\\d+) clay. "
            + "Each geode robot costs (\\d+) ore and (\\d+) obsidian."  
        );
        blueprints = Files.lines( dayFile(f) )
            .map( str -> {
                Matcher m = bp.matcher(str);
                if ( !m.matches() ) throw new RuntimeException("Error in pattern macher => " + str);
                int[] costs = new int[6]; 
                for (int i = 0; i < 6; i++) {
                    costs[i] = parseInt(m.group(i+1));
                }
                return new Blueprint(costs);
            })
            .toList();
        return this;
    }

    public void part1() throws IOException {
        
        
        int maxTime = 24;
        
        Blueprint bp = blueprints.get(0);
        LinkedList<Blueprint> backChoices = new LinkedList<>();
        backChoices.add(bp);
        
        while( !backChoices.isEmpty() ) {
            bp = backChoices.removeFirst();
            while(bp.time < maxTime) {
                bp.buyOptions()
                    .stream()
                    .map( opt -> { opt.produce(); return opt;} )
                    .forEach( backChoices::add );
                bp.produce();
            }
            System.out.println("BackChoice size : " + backChoices.size());
            System.out.println( bp.stockString() );
        }
        
    }
    
    public void part2() throws IOException {
        
    }
    
    
    public static int
        ORE      = 0,
        CLAY     = 1,
        OBSIDIAN = 2,
        GEODE    = 3;
    
    public static class Blueprint {
        int[] costs;
        int[] robots = new int[4];
        int[] stock = new int[4];
        int buy = -1;
        int time;
        public Blueprint(int[] costs) {
            this.costs = costs;
            robots[ORE] = 1;
        }
        public String stockString() {
            return String.format("ORE[%2d] CLAY[%2d] OBSID[%2d] GEODE[%2d]", stock[ORE], stock[CLAY], stock[OBSIDIAN], stock[GEODE]);
        }
        public List<Blueprint> buyOptions() {
            List<Blueprint> res = new ArrayList<>(4);
            
            BiConsumer<Integer, Consumer<Blueprint>> withBuyBlueprint = (Integer toBuy, Consumer<Blueprint> todo) -> {
                Blueprint p = copy();
                p.buy = toBuy;
                todo.accept(p);
                res.add(p);
            };
            
            // buy geode robot ?
            if ( stock[ORE] >= costs[4] && stock[OBSIDIAN] >= costs[5] ) {
                withBuyBlueprint.accept(GEODE, p -> {
                    p.stock[ORE] -= costs[4];
                    p.stock[OBSIDIAN] -= costs[5];
                });
            }
            // buy obsidian robot ?
            if ( stock[ORE] >= costs[2] && stock[CLAY] >= costs[3] ) {
                withBuyBlueprint.accept(OBSIDIAN, p -> {
                    p.stock[ORE] -= costs[2];
                    p.stock[CLAY] -= costs[3];
                });
            }
            // buy clay robot ?
            if ( stock[ORE] >= costs[1] ) {
                withBuyBlueprint.accept(CLAY, p -> {
                    p.stock[ORE] -= costs[1];
                });
            }
            // buy ore robot ?
            if ( stock[ORE] >= costs[0] ) {
                withBuyBlueprint.accept(ORE, p -> {                                        
                    p.stock[ORE] -= costs[0];
                });
            }
            
            return res;
        }
        Blueprint copy() {
            Blueprint p = new Blueprint(costs);
            p.time = time;
            p.stock = Arrays.copyOf(stock, 4);
            p.robots = Arrays.copyOf(robots, 4);
            return p;
        }
        void produce() {
            for (int i = 0; i < robots.length; i++)
                stock[i] += robots[i];
            if ( buy >= 0 ) {
                robots[buy]++;
                buy = 0;
            }
            time++;
        }
    }
    
    
}

