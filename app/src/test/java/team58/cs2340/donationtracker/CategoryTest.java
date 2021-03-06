package team58.cs2340.donationtracker;

import org.junit.Test;
import team58.cs2340.donationtracker.models.Category;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

/**
 * Tests functionality of fromString in Category class
 * @author Akshay Karthik
 */
public class CategoryTest {

    /**
     * Tests null input to fromString
     */
    @Test
    public void testNull() {
        assertNull("Null string fails", Category.Companion.fromString(null));
    }

    /**
     * Tests invalid input to fromString
     */
    @Test
    public void testInvalid() {
        assertNull("Invalid string fails", Category.Companion.fromString("potato"));
    }

    /**
     * Tests Appliances input to fromString
     */
    @Test
    public void testAppliances() {
        assertEquals("Correct output for Appliances",
                Category.APPLIANCES, Category.Companion.fromString("Appliances"));
    }

    /**
     * Tests Baby input to fromString
     */
    @Test
    public void testBaby() {
        assertEquals("Correct output for Baby",Category.BABY, Category.Companion.fromString("Baby"));
    }

    /**
     * Tests Bags and Accessories input to fromString
     */
    @Test
    public void testBagsAndAccessories() {
        assertEquals("Correct output for Bags and Accessories",
                Category.BAGSANDACCESSORIES, Category.Companion.fromString("Bags and Accessories"));
    }

    /**
     * Tests Books and Music input to fromString
     */
    @Test
    public void testBooksAndMusic() {
        assertEquals("Correct output for Books and Music",
                Category.BOOKSANDMUSIC, Category.Companion.fromString("Books and Music"));
    }

    /**
     * Tests Clothing input to fromString
     */
    @Test
    public void testClothing() {
        assertEquals("Correct output for Clothing",
                Category.CLOTHING, Category.Companion.fromString("Clothing"));
    }

    /**
     * Tests Electronics input to fromString
     */
    @Test
    public void testElectronics() {
        assertEquals("Empty string fails",Category.ELECTRONICS, Category.Companion.fromString("Electronics"));
    }

    /**
     * Tests Food input to fromString
     */
    @Test
    public void testFood() {
        assertEquals("Correct output for Food",Category.FOOD, Category.Companion.fromString("Food"));
    }

    /**
     * Tests Furniture input to fromString
     */
    @Test
    public void testFurniture() {
        assertEquals("Correct output for Furniture",
                Category.FURNITURE, Category.Companion.fromString("Furniture"));
    }

    /**
     * Tests Movies and Games input to fromString
     */
    @Test
    public void testMoviesAndGames() {
        assertEquals("Correct output for Movies and Games",
                Category.MOVIESANDGAMES, Category.Companion.fromString("Movies and Games"));
    }

    /**
     * Tests Sports and Outdoors input to fromString
     */
    @Test
    public void testSportsAndOutdoors() {
        assertEquals("Correct output for Sports and Outdoors",
                Category.SPORTSANDOUTDOORS, Category.Companion.fromString("Sports and Outdoors"));
    }

    /**
     * Tests Toys input to fromString
     */
    @Test
    public void testToys() {
        assertEquals("Correct output for Toys",
                Category.TOYS, Category.Companion.fromString("Toys"));
    }

}