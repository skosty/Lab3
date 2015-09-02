import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

public class Encoder {

  public static void main(String[] args) throws IOException{
    System.setOut(new PrintStream(new File("log")));
    File freqFile = new File(args[0]);
    int k = Integer.parseInt(args[1]);
    int charLength = 1;
    if(args.length >= 3) charLength = Integer.parseInt(args[2]);
    Map<String,Double> Alphabet = null;
    
    // Create alphabet from freqFile
    if(args.length < 4 ){ // 1) freqFile, 2) k, 3) j
      Alphabet = generateAlphabetFromFreqFile(freqFile);
    }
    // Create Test File 
   File testFile = createTestFile(Alphabet, k);
    // Generate Compund Alphabet
    Alphabet = generateCompoundAlphabet(Alphabet, charLength, Alphabet, 0);
  
    
    HuffmanEncoder hfe = new HuffmanEncoder(Alphabet);
    hfe.encodeFile(testFile, new File("testText.enc"), charLength);
    hfe.decodeFile(new File("testText.enc"), new File("testText.dec"));
    hfe.printEncoding();
    // Create ouput
    int keycounter = 0;
    double sum = 0;
    for(String str : Alphabet.keySet()){
      keycounter++;
      sum += Alphabet.get(str);
    }
    System.out.println("Total Number of Compound Characters: " + keycounter);
    System.out.println("Sum of Probabilities: " + sum);
    System.out.println("Entropy\tEfficiency\t%Difference");
    double Entropy = getEntropy(Alphabet);
    double Efficiency = hfe.getActualEfficiency();
    System.out.println(Entropy + "\t" + Efficiency + "\t\t" + getPercentDifference(Entropy,Efficiency));
    
    // TODO code application logic here
  } // end Main

public static double getPercentDifference(double a, double b){
  double pd = round(100*(b - a)/a,2);
  return pd;
}
  
public static String[] getAlphabetSegment(int length){
  
  String[] ref = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
  String[] a = Arrays.copyOfRange(ref, 0, length);
  
  return a;
}  // end getAlphabet 
  
public static Map<String, Double> generateAlphabetFromFreqFile(File freqFile){
  Map<String, Double> Alphabet = new HashMap<>();
  // Get Character Arrays a and pArray
    
    double[] pArray = dlmread(freqFile, "\t\n \r");
    double sum = 0;
    for(int i = 0; i < pArray.length; i++){
      sum+= pArray[i];
    }
    pArray = arrayDivide(pArray, sum);
    String[] a = getAlphabetSegment(pArray.length);
    for(int i = 0; i < a.length; i++){
      Alphabet.put(a[i], pArray[i]);
    }
    return Alphabet;
}

public static double getEntropy(Map<String,Double> Alphabet){
  double S = 0;
  double denom = Math.log(2);
  for(String str : Alphabet.keySet()){
    double p = Alphabet.get(str);
    S -= p*Math.log(p)/denom;
  }
  S = round(S, 3);
  return S;
}

public static Map<String, Double> generateCompoundAlphabet(Map<String,Double> base, int cL, Map<String,Double> cumA, int itC){
  itC++;
  if(itC >= cL) return cumA;
  
  // Create copy map
  Map<String,Double> tmpCumMap = new HashMap<>();       
  for(String K : cumA.keySet()){
    for(String C : base.keySet()){
      tmpCumMap.put(K + C, cumA.get(K) * base.get(C));
    } // inner (C) loop
  } // outer (K) loop

  
  
  Map<String,Double> newCumMap = generateCompoundAlphabet(base, cL, tmpCumMap, itC);
  return newCumMap;
  
} // end generateCompoundAlphabet

public static File createTestFile(Map<String,Double> Alphabet, int length){
// Create arrays CharacterArray and ProbabilityArray
  String[] CharacterArray = new String[Alphabet.keySet().size()];
  double[] ProbabilityArray = new double[Alphabet.keySet().size()];
  int counter = 0;
  for(String str : Alphabet.keySet()){
    CharacterArray[counter] = str;
    ProbabilityArray[counter] = Alphabet.get(str);
    counter++;
  }
  
  
// Works!!!!!
File testFile = new File("testText");
double[] cCounts = new double[CharacterArray.length];
// Create Cumulative Probability Array
double[] cumProbabiilityArray = new double[ProbabilityArray.length];
double cumP = 0;
for(int i = 0 ; i < ProbabilityArray.length; i++){
  cumP += ProbabilityArray[i];
  cumProbabiilityArray[i] = cumP;
}

// Generate Random Numbers, get Strings, and Save to File
Random random = new Random();

String character = null;
try{
  PrintWriter writer = new PrintWriter(new FileOutputStream(testFile));
for(int i = 0; i < length; i++){
  double r = random.nextDouble();
  for(int j = 0; j < cumProbabiilityArray.length; j++){
    
    if(cumProbabiilityArray[j] > r){
      character = CharacterArray[j];
      cCounts[j]++;
      break;
    }
  } // end inner for loop
  
  
      
      
    writer.write(character);
      
  
    
  
} // for loop 
writer.close();
} // end try 
catch(FileNotFoundException e){
  
     } 

return testFile;
} // createTestFile


public static double round(double num, int digit){
		
	double xpon = Math.pow(10, (double) digit);
	return Math.round(xpon*num)/xpon; 
		
}



public static double[] dlmread(File file, String dlm){

		// Create array list 	
		ArrayList<Double> alist = new ArrayList<Double>(); 

		// Create reader 
		try{
			BufferedReader fr = new BufferedReader(new FileReader(file)); 
			String line = null; 
			while((line = fr.readLine()) != null){
				// Create string tokenizer 
				StringTokenizer st = new StringTokenizer(line, dlm); 
				// Extract tokens 
				while(st.hasMoreTokens()){ alist.add( Double.parseDouble(st.nextToken())   ); } // end inner while 
			} // end outer while 

			// Close stream 
			fr.close();
		}catch(FileNotFoundException f){
			System.out.println("FileNotFoundException: Could not open " + file.getAbsolutePath() + " for reading." );
		}catch(IOException e){
			System.out.println("IOException: Could not open " + file.getAbsolutePath() + " for reading." );
		}
		
		// Convert alist to double[]
		double[] ret = new double[alist.size()];
		int index = 0; 
		for(Double D : alist){
			ret[index] = D.doubleValue(); 
			index++; 
		} // end for loop 

		return ret; 

	
	} // end dlmread 


public static double[] arrayDivide(double[] a, double b){

	double[] c = new double[a.length];
	for(int i = 0; i < a.length; i++){
		c[i] = a[i]/b; 
	}
	return c; 
} 

}
