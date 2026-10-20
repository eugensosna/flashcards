package edu.znu.sosna.flashcardslearning

import edu.znu.sosna.flashcardslearning.data.parseTags
import org.junit.Assert.assertEquals
import org.junit.Test

class TagParsingTest {
    @Test
    fun parseTags_trimsAndDropsEmptyAndDuplicates() {
        assertEquals(listOf("food", "verbs"), parseTags(" food, ,verbs,food ,"))
    }

    @Test
    fun parseTags_blankInput_isEmpty() {
        assertEquals(emptyList<String>(), parseTags("   "))
    }
}
