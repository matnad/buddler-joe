package ch.unibas.cs.p9.fs19.example;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

/**
 * An example test class.
 * Checks the output of the {@link HelloWorld} class and makes sure it contains "Hello World"
 *
 * @author CS108
 */
public class HelloWorldTest {
  
  
  private ByteArrayOutputStream outStream = new ByteArrayOutputStream();

  private PrintStream outBackup;
  private PrintStream errBackup;
  
  @Before
  public void redirectStdOutStdErr(){
    outBackup = System.out;
    System.setOut(new PrintStream(outStream));
  }
  
  @After
  public void reestablishStdOutStdErr(){
    System.setOut(outBackup);
  }
  
  @Test
  public void testMain(){
    HelloWorld.main(new String[0]);
    String toTest = outStream.toString();
    toTest = removeNewline(toTest);
    assertTrue(toTest.contains("Hello World"));
  }
  
  private static String removeNewline(String str){
    return str.replace("\n", "").replace("\r", "");
  }
}