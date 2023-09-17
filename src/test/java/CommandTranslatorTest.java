import org.junit.Assert;
import org.junit.Test;
import ui.console.CommandTranslator;

import java.util.Arrays;

public class CommandTranslatorTest {



    @Test
    public void Test() {

        String string = "hello   day tell 'tell2' it  \"  i s\" kar 'work   ' ";
        String[] actualResult = CommandTranslator.translateCommandline(string);
        String[] expectedResult ={"hello","day","tell","tell2","it","  i s","kar","work   "};
     Assert.assertEquals(Arrays.asList(expectedResult) , Arrays.asList(actualResult));

    }
}
