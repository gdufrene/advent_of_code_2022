package advent2022.day17;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import advent2022.Day17;
import advent2022.Day17.PlayGround;
import advent2022.Day17.RockType;

public class Day17Tests {
    
    @Test
    public void testSamplePart1() throws Exception {
        int sol = (Integer) new Day17()
            .load( "sample.txt" )
            .part1();
        assertEquals(3068, sol);
    }
    
    @Test
    public void testColision() {
        PlayGround pg = new PlayGround();
        pg.nextBlock( RockType.B_CROSS );
        pg.moveLeft();
        while ( pg.moveDown() );
        pg.addBlockInCave();
        pg.nextBlock( RockType.C_CORNER );
        pg.moveRight();
        pg.moveRight();
        for (int i = 0; i < 5; i++) {
            assertTrue( pg.moveDown() );
        }
        pg.show();
        assertFalse( pg.moveLeft() );
        assertTrue( pg.moveDown() );
        assertTrue( pg.moveLeft() );
        assertFalse( pg.moveLeft() );
        assertFalse( pg.moveDown() );
        pg.addBlockInCave();
        pg.show();
    }

    @Test
    public void testColision_CROSS_BAR() {
        PlayGround pg = new PlayGround();
        pg.nextBlock( RockType.A_HORZBAR );
        pg.moveLeft();
        while ( pg.moveDown() );
        pg.addBlockInCave();
        pg.nextBlock( RockType.B_CROSS );
        while ( pg.moveRight() );
        for (int i = 0; i < 5; i++) {
            pg.moveDown();
        }
        pg.show();
        assertFalse( pg.moveLeft() );
    }
    
    @Test
    public void testColision_CROSS_BAR_Right() {
        PlayGround pg = new PlayGround();
        pg.nextBlock( RockType.A_HORZBAR );
        while ( pg.moveDown() );
        pg.addBlockInCave();
        pg.nextBlock( RockType.B_CROSS );
        while ( pg.moveLeft() );
        for (int i = 0; i < 5; i++) {
            pg.moveDown();
        }
        pg.show();
        assertFalse( pg.moveRight() );
    }

}
