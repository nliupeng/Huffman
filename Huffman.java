// Nelly Liu Peng
// CS 241-01 Data Structures and Algorithms II
// PA3 
// 2-16-15

import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Set;
import java.io.IOException;
import java.io.File;

public class Huffman {

  //**************************//
  //*   Instance variables   *//
  //**************************//
  public PriorityQueue<HuffmanNode> pQueue;       // To retrieve each time the node with the most min. value
  public HashMap<Character, Integer> charFreq;    // A table storing characters and their frequencies
  public HashMap<Character, String> charToCode;   // A table with input chars and their Huffman encoding
  public HashMap<String, Character> codeToChar;   // A table with Huffman codes and their char representations
  String text = "";                               // String with the contents of an input file
  String compressedText = "";                     // Huffman representation of the input text

  //*******************//
  //*   Constructor   *//
  //*******************//
  public Huffman(File file) throws IOException {	  
    // Read data from a file
    text = getText(file);                         // Contents of file into a single string  
 
    // Create HashMap with chars contained in the string and their frequencies
    countCharsAndFreq();

    // Create Huffman nodes for every char stored in HashMap
    createHuffmanNodes();    

    // Build a tree
    HuffmanNode root = buildTree(pQueue.size());  // Build a tree with current char nodes in pQueue
    
    // Build conversion table
    buildTable(root);                             // Prepare HashMaps with char/code representations
  }

  /*******
  * This method reads the contents of a file and stores them into a single string
  * @param file: Input file
  * @return: A single string with contents from input file
  *******/
  public String getText(File file) throws IOException {
    if (!file.exists()) {                         // Exit program if file not found
      System.out.println("Nonexistent file");
      System.exit(0);
    }
    Scanner inputFile = new Scanner(file);        // Read input file
    StringBuilder sb = new StringBuilder();       // String that will contain all chars in file
    while (inputFile.hasNextLine()) {
      sb.append(inputFile.nextLine());
      sb.append("\n");
    }
    inputFile.close();                            // Close the file
    return sb.toString();                         // Return String
  }

  /*******
  * This method creates a table with characters of input text and their frequencies 
  *******/
  private void countCharsAndFreq() {
    charFreq = new HashMap<Character, Integer>(); // Store chars and their freq
    for (int i=0; i<text.length(); i++) {         // Store all chars of string into HashMap
      char character = text.charAt(i);
      if (charFreq.containsKey(character))        // If char already in HashMap, just increase freq      
        charFreq.put(character, charFreq.get(character)+1);
      else                                        // If char not present, add char, freq=1
        charFreq.put(character, 1);
    }
  }

  /*******
  * This method creates the leaf nodes containing characters used in input text and their frequencies
  *******/
  private void createHuffmanNodes() {
    pQueue = new PriorityQueue<HuffmanNode>();
    Set<Character> chars = charFreq.keySet();     // Set of all chars in HashMap
    for (char c : chars) {                        // Iterate over each element in set
      HuffmanNode hn = new HuffmanNode();         // Create nodes for each char, store their freq
      hn.character = c;
      hn.frequency = charFreq.get(c);
      pQueue.add(hn);                             // Add newly created node into priority queue
    }
  }

  /*******
  * This method builds the tree based on the frequency of evert character
  * @param n: Size of pQueue
  * @return: The root of a tree 
  *******/
  private HuffmanNode buildTree(int n) {
    for (int i=1; i<n; i++ ) {
      HuffmanNode hn = new HuffmanNode();       	// Create new HuffmanNode
      hn.left = pQueue.poll();                  	// Poll pQueue's head to make a left node
      hn.right = pQueue.poll();                 	// Poll pQueue's head to make a right node
      hn.frequency = hn.left.frequency + hn.right.frequency;  // New freq = freq of children
      pQueue.add(hn);                           	// Add this new node back to pQueue
    }
    return pQueue.poll();                       	// Return the last node in pQueue (root)
  }
  
  /*******
  * This method builds the table for the compression and decompression
  *******/
  private void buildTable(HuffmanNode root) {
    charToCode = new HashMap<Character, String>();  // Initialize conversion tables
    codeToChar = new HashMap<String, Character>();
    postorder(root, "");      // After this method is executed, both tables above would be filled with codes/chars
  }

  /*******
  * This method is used to create the codes starting at root
  *******/
  private void postorder(HuffmanNode n, String s) {
    if (n == null)
      return;
    postorder(n.left, s+"0");              // Traverse the tree
    postorder(n.right, s+"1");
    if (n.character != '\u0000' ) {        // If node has a valid char, insert its code into our HashMaps
      charToCode.put(n.character, s);
      codeToChar.put(s, n.character);
    }
  }
    
  /*******
  * This method assumes that the tree and dictionary are already built
  * Replaces every char from input text to its Huffman representation
  * @return: The compressed text 
  *******/
  public String compress() {
    StringBuilder sb = new StringBuilder();       // Encoded String to be returned    
    for (int i=0; i<text.length(); i++)           // Replace each char to encoded Huffman code
      sb.append(charToCode.get(text.charAt(i)));  
    compressedText = sb.toString();               // Save the compressed text
    return compressedText;                        // Return encoded message
  }

  /*******
  * This method assumes that the tree and dictionary are already built
  * Replaces every Huffman code with its equivalent character
  * @return: The decompressed text
  *******/
  public String decompress() {
    StringBuilder sb = new StringBuilder();           // The decompressed text
    for (int i=0; i<compressedText.length(); i++) {   // Read through compressed text
      String code = compressedText.charAt(i) + "";    // A char from the text
      while (!codeToChar.containsKey(code))
        code = code + compressedText.charAt(++i);
      sb.append(codeToChar.get(code));
    }
    return sb.toString();
  }
   
  /*******
  * Internal class
  *******/
  class HuffmanNode implements Comparable<HuffmanNode> {     
    public char character;
    public int frequency;
    public HuffmanNode left, right;
      
    public HuffmanNode() { }      

    public HuffmanNode(char character, int frequency) {
      this.character = character;
      this.frequency = frequency;
    }      
    public String toString() {
      return character + " " + frequency;
    }      
    public int compareTo(HuffmanNode that) {
      return this.frequency - that.frequency;
    }
  }

}


