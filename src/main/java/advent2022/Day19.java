package advent2022;

import static java.lang.Integer.parseInt;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Day19 implements AdventDay {
    
    
    public static void main(String[] args) throws Exception {
        new Day19()
            .load( Filename.DATA.filename() )
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
        
        //int cost[] = new int[] { 2, 4, 4, 15, 2, 20 };
        int i = 0;
        int sum = 0;
        for( Blueprint bp : blueprints ) {
            i++;
            int geode = maxGeode(bp);
            int qualityLevel = i * geode;
            sum += qualityLevel;
            System.out.format( "%d x %d = %d\n", i, geode, qualityLevel );
        }
        System.out.println( "PART1 ===> " + sum );
        
        // 2193
        
    }
    
    int maxGeode(Blueprint bp) {
        Blueprint maxBp = null;
        int maxValue = 0;
        int[] maxBuys = null;
        for (int i = 25; i >= 0; i--) {
            for (int j = 25; j >= 0; j--) {
                for (int k = 25; k >= 0; k--) {                    
                    int buys[] = new int[] {i, j, k, 25}; 
                    Blueprint bp1 = bp.copy();
                    geode(bp1, buys);
                    // System.out.println( Arrays.toString(bp1.stock) );
                    if ( bp1.stock[OBSIDIAN] >= maxValue ) {
                        maxBp = bp1;
                        maxValue = bp1.stock[OBSIDIAN];
                        maxBuys = buys;
                    }
                }
            }
        }
        
        // int[] maxBuys = null;
        maxValue = 0;
        for (int i = 25; i >= 0; i--) {
            for (int j = 25; j >= 0; j--) {
                int buys[] = new int[] {maxBuys[0], maxBuys[1], maxBuys[2], j}; 
                Blueprint bp1 = bp.copy();
                geode(bp1, buys);
                // System.out.println( Arrays.toString(bp1.stock) );
                if ( bp1.stock[GEODE] >= maxValue ) {
                    maxBp = bp1;
                    maxValue = bp1.stock[GEODE];
                    maxBuys = buys;
                }
            }
        }
        
//        System.out.println("-- To Geode --");
//        System.out.println( Arrays.toString(maxBp.stock) );
        // System.out.println( Arrays.toString(maxBuys) );
        return maxBp.stock[GEODE];
    }
    
    int geode(Blueprint bp, int buyAt[]) {
        
        int cost[]  = bp.costs; //new int[] { 4, 2, 3, 14, 2, 7 };
        int stock[] = bp.stock; // int[] { 0, 0, 0, 0 }; 
        int prod[]  = bp.prod; //  int[] { 1, 0, 0, 0 }; 
        
        boolean needOre = false;
        for (int i = 0; i < 24; i++) {
            
            int buy = -1;
            
            int buyOreAt = buyAt[ORE];
            int buyClayAt = buyAt[CLAY];
            int buyObsAt = buyAt[OBSIDIAN];
            int buyGeodeAt = buyAt[GEODE];
            
            if ( i >= buyGeodeAt && stock[OBSIDIAN] >= cost[5] ) {
                if ( stock[ORE] >= cost[4] ) buy = GEODE;
                // else needOre = true;
            }
            else if ( i >= buyObsAt && stock[CLAY] >= cost[3]) {
                if ( stock[ORE] >= cost[2] ) buy = OBSIDIAN;
                // else needOre = true;
            }
            else if ( !needOre && i >= buyClayAt && stock[ORE] >= cost[1] ) buy = CLAY;
            else if ( !needOre && i >= buyOreAt && stock[ORE] >= cost[0] ) buy = ORE;
            
            if ( buy >= 0 ) { 
                if ( buy == ORE ) { stock[ORE] -= cost[0]; }
                else if ( buy == CLAY ) { stock[ORE] -= cost[1]; }
                else if ( buy == OBSIDIAN ) { stock[ORE] -= cost[2]; stock[CLAY] -= cost[3]; }
                else if ( buy == GEODE ) { stock[ORE] -= cost[4]; stock[OBSIDIAN] -= cost[5]; }
            }
            
            for (int j = 0; j < prod.length; j++) {
                stock[j] += prod[j];
            }
            
            if (buy >= 0) prod[buy]++;
        }
        
        /*
        System.out.format("[%02d]: Ore:%3d Clay:%3d Obs:%3d Geo:%3d\n", 24, 
                stock[ORE], stock[CLAY], stock[OBSIDIAN], stock[GEODE] );
                */
        
        return stock[GEODE];
    }
    
    public void part2() throws IOException {
        // 7200
    }
    
    
    public final static int
        NONE     = -1,
        ORE      = 0,
        CLAY     = 1,
        OBSIDIAN = 2,
        GEODE    = 3;
    
    public static class Task {
        int[] needs;
        int atTime;
        int toBuild;
        public Task(int toBuild) {
            this.toBuild = toBuild;
        }
    }
    
    public static class Blueprint {
        int[] costs;
        int[] prod  = new int[4];
        int[] stock = new int[4];
        int buy = -1;
        int time;
        public Blueprint(int[] costs) {
            this.costs = costs;
            prod[ORE] = 1;
        }
        public void apply(Task t) {
            // TODO Auto-generated method stub
            
        }
        public Optional<Task> requirement(Task t) {
            int requirement = switch (t.toBuild) {
                case ORE -> stock[ORE] >= costs[0] ? NONE : ORE;
                case CLAY -> stock[ORE] >= costs[1] ? NONE : ORE;
                case OBSIDIAN -> {
                    if (stock[ORE] < costs[2]) yield ORE;
                    if (stock[CLAY] < costs[3]) yield CLAY;
                    yield NONE;
                }
                case GEODE -> { 
                    if (stock[ORE] < costs[4]) yield ORE;
                    if (stock[OBSIDIAN] < costs[5]) yield OBSIDIAN;
                    yield NONE;
                }
                default -> NONE;
            };
            if ( requirement == NONE ) return Optional.empty();
            
            return null;
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
            p.prod = Arrays.copyOf(prod, 4);
            return p;
        }
        void produce() {
            for (int i = 0; i < prod.length; i++)
                stock[i] += prod[i];
            if ( buy >= 0 ) {
                prod[buy]++;
                buy = 0;
            }
            time++;
        }
    }
    
    
}

