import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class HuffmanEncoder {
  
  Map<String, Double> ProbabilityMap = new HashMap<>(); 
  List<HuffmanTree> CharacterList = new ArrayList<>(); // 
  Map<String, String> EncodingMap = new HashMap<>();
  HuffmanTree RootTree;
  double eff;
  
  public HuffmanEncoder(String[] CharacterArray, double[] ProbabilityArray){
    
    ProbabilityMap = buildProbabilityMap(CharacterArray, ProbabilityArray);
    
    // Build Tree 
    RootTree = buildTree();
    
    // Create EncodingMap
    for(HuffmanTree hft : CharacterList){
      EncodingMap.put(hft.character, reverseString(getCharacterEncoding(hft)));
    } // end hft for-loop
    
    
  } // end constructor
  
  
  public HuffmanEncoder(Map<String, Double> ProbabilityMap){
    
    this.ProbabilityMap = ProbabilityMap;
    
    // Build Tree 
    RootTree = buildTree();
    
    // Create EncodingMap
    for(HuffmanTree hft : CharacterList){
      EncodingMap.put(hft.character, reverseString(getCharacterEncoding(hft)));
    } // end hft for-loop
    
    
  } // end constructor
  
  public static Map<String, Double> buildProbabilityMap(String[] CharacterArray, double[] ProbabilityArray){
    
    Map<String, Double> ProbabilityMap = new ConcurrentHashMap<>();
    // Build ProbabilityMap 
    for(int i = 0; i < CharacterArray.length; i++){
      ProbabilityMap.put(CharacterArray[i], new Double(ProbabilityArray[i]));
    } // end for 
    
    return ProbabilityMap;
  }
  
  public void encodeFile(File in, File out, int charLength){
   double nbits = 0;
   double nsymbols = 0;
    try{
      BufferedReader reader = new BufferedReader(new InputStreamReader( new FileInputStream(in)));
      PrintWriter writer = new PrintWriter(new FileOutputStream(out));
      String line;
      while((line = reader.readLine()) != null ){
        // Iterate through line every charLength characters 
        for(int index = 0; index <= line.length() - charLength; index += charLength){
          String encodedCharacter = EncodingMap.get(line.substring(index, (index + charLength)));
          writer.print(encodedCharacter);
          nbits += encodedCharacter.length();
          nsymbols++;
        }
      } // end while 
      writer.close();
      reader.close();
    } catch(FileNotFoundException e){
  System.out.println("File " + in.getAbsolutePath() + " not found for reading.\nError in HuffmanEncoder.encodeFile");
    } catch(IOException e2){
      System.out.println("Error parsing file " + in.getAbsolutePath() +"\nError in HuffmanEncoder.encodeFile ");
    }// end catch 
    
    this.eff = nbits/nsymbols;
    
  } // end encodeFile 
  
  public double getActualEfficiency(){
    return round(this.eff,3);
  }
  
  
  public void decodeFile(File in, File out){
    // read in character by character 
    // Start at root
    // If letter is 0, go to left node; else go to right
    // If that HFT has no children, read in next character 
    
    try{
      BufferedReader reader = new BufferedReader(new InputStreamReader( new FileInputStream(in)));
      PrintWriter writer = new PrintWriter(new FileOutputStream(out));
      HuffmanTree currentTree = RootTree;
      int c;
      while((c = reader.read()) != -1){
        if(currentTree.character == null){
          if(Character.getNumericValue(c) == Character.getNumericValue('0')) currentTree = currentTree.Left;
          else currentTree = currentTree.Right;
        }
        
        if(currentTree.character != null){
          writer.print(currentTree.character);
          currentTree = RootTree;
        }
//        System.out.print(Character.toChars(c)[0]);
//        if(currentTree.character != null){
//          writer.print(currentTree.character);
//          System.out.println("\t" + currentTree.character );
//          currentTree = RootTree;
//        }
//        else{
//          if(Character.getNumericValue(c) == Character.getNumericValue('0')) currentTree = currentTree.Left;
//          else currentTree = currentTree.Right;
//        }
        
      } // end while 
      writer.close();
    }catch(FileNotFoundException e){
  System.out.println("File " + in.getAbsolutePath() + " not found for reading.\nError in HuffmanEncoder.encodeFile");
    } catch(IOException e2){
      System.out.println("Error parsing file " + in.getAbsolutePath() +"\nError in HuffmanEncoder.encodeFile ");
    }// end catch 
    
  }
  
  public void printEncoding(){
    for(String str : EncodingMap.keySet()){
      System.out.println(str + "( " + ProbabilityMap.get(str) + " ) -> " + EncodingMap.get(str));
    }
  }
  
  
  HuffmanTree buildTree(){
    // returns Root Node (RootTree)
        // Create Q
          List<HuffmanTree> PrioritySet = new ArrayList<>();
          for(String str : ProbabilityMap.keySet()){
            HuffmanTree hft = new HuffmanTree(str, ProbabilityMap.get(str));
            PrioritySet.add(hft);
            CharacterList.add(hft);
          } // end str for loop
          
          
        // Cycle through Q
          while(PrioritySet.size() > 1){
            // Find two least likely elements of Q 
            HuffmanTree hft1 = new HuffmanTree(null, 1), hft2 = new HuffmanTree(null, 1);
            for(HuffmanTree hft : PrioritySet){ // MAY NEED ITERATOR HERE? (ADDRESS REASONS????)
              if(hft.probability <= hft1.probability || hft.probability <= hft2.probability){
                if(hft1.probability < hft2.probability) hft2 = hft; // hft2 is less or equally likely than hft1 (replace hft2)
                else  hft1 = hft;
              } // end outer if 
            } // end hft for-loop
            
            // Create node, add hft1/2 to it, 
            HuffmanTree node = new HuffmanTree(null, hft1.probability + hft2.probability);
            if(hft1.probability <= hft2.probability){
              node.Left = hft1;
              node.Right = hft2;
            }
            else{
              node.Right = hft1;
              node.Left = hft2; 
            } 
            // Add node to PrioritySet, and remove hft1, hft2 
            PrioritySet.add(node);
            hft1.Parent = node;
            hft2.Parent = node;
            PrioritySet.remove(hft1);
            PrioritySet.remove(hft2);
            
            
          } // end while-loop 

          return PrioritySet.get(0);
  } // end buildTree 
  
  String getCharacterEncoding(HuffmanTree hft){
   
    StringBuffer encoding = new StringBuffer();
    if(hft == RootTree) return encoding.toString();
    // If is hft is left node, return 1
    // Else, return 0
       if(hft == hft.Parent.Left) encoding.append("0");
       else encoding.append("1");
       encoding.append(getCharacterEncoding(hft.Parent));
    return encoding.toString();
  } // end getCharacterEncoding
   
  private String reverseString(String str){
    StringBuffer invert = new StringBuffer();
    for(int i = str.length() - 1; i >= 0; i--){
      invert.append(str.charAt(i));
    }
    return invert.toString();
  }
  
      public class HuffmanTree implements Comparator<String>{
        
        // Methods 
          // constructor 
          // comparator 
          
        
        // Fields
        HuffmanTree Left, Right; 
        HuffmanTree Parent;
        double probability;
        String character;
        
        HuffmanTree(String _character, double _probability){
          this.character = _character;
          this.probability = _probability;
        } // end Constructor 

        @Override 
        public int compare(String a, String b){
          // If a is more probable, returns positive
          // If b is more probable, returns negative 
          if(ProbabilityMap.get(a) > ProbabilityMap.get(b)) return 1;
          else if(ProbabilityMap.get(a) > ProbabilityMap.get(b)) return -1;
          else return 0;
        }

//        private HuffmanTree buildTree(){
//          // Create Queue from ProbabilityMap, where each new element is placed according to probability  
//          // While the queue has more than one HuffmanTree object, take two least probable nodes, and create new parent             node with them as children. Add the parent node to the queue and remove the two nodes. 
//          // Return the last node 
//          
//          // Create Q
//          List<HuffmanTree> PrioritySet = new ArrayList<>();
//          for(String str : ProbabilityMap.keySet()){
//            PrioritySet.add(new HuffmanTree(str, ProbabilityMap.get(str)));
//          } // end str for loop
//          
//          // Cycle through Q
//          while(PrioritySet.size() > 1){
//            // Find two least likely elements of Q 
//            HuffmanTree hft1 = new HuffmanTree(null, 1), hft2 = new HuffmanTree(null, 1);
//            for(HuffmanTree hft : PrioritySet){
//              if(hft.probability <= hft1.probability || hft.probability <= hft2.probability){
//                if(hft1.probability < hft2.probability) hft2 = hft;
//                else  hft1 = hft;
//              } // end outer if 
//            } // end hft for-loop
//            
//            // Create new parent node, add to priority set, and remove hft1 and hft2
//            HuffmanTree newTree = new HuffmanTree(null, hft1.probability + hft2.probability);
//            newTree.Children.add(hft1);
//            newTree.Children.add(hft2);
//            PrioritySet.add(newTree);
//            PrioritySet.remove(hft1);
//            PrioritySet.remove(hft2);
//          } // end while 
//            
//          return PrioritySet.get(0);  
//          
//        } // end buildTree 
//        
//        

        
      } // end Huffman Tree Class 
  
  public static double round(double num, int digit){
		
			double xpon = Math.pow(10, (double) digit);
			return Math.round(xpon*num)/xpon; 
		
		}
  
} // end Huffman Encoder Class 
