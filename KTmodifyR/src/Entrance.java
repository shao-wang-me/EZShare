import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Entrance {

	public static void main(String[] args) throws IOException {

		
		final int INFINITE = -999999;
		
		
		int[][] covertNum = tuningR.tuneR(-1,-1,1,-1);
		double[][] replaceCost = new double[27][26];
		for (int i = 0; i < 27; i++){
			int rowSum = 0;
			for (int j = 0; j < 26; j++){
				rowSum += covertNum[i][j];
			}
			
			for (int j = 0; j < 26; j++){
				
			    replaceCost[i][j] = covertNum[i][j] == 0? -5: 3 * (double)covertNum[i][j] / rowSum;
				
			}
		}
		
		
		

		
		
		final int INSERTION_COST = -1;
		final int DELETION_COST = -1;
		final int MATCH_COST = 3;
		
		
		for (int a = 0; a < 27; a++){
			for (int j = 0; j < 26; j++){
				System.out.print(replaceCost[a][j] + "   ");
			}
			System.out.println();
		}
		
		
		
		
		BufferedReader testFile = new BufferedReader(new InputStreamReader(new FileInputStream("train.txt")));
		BufferedReader dictFile = new BufferedReader(new InputStreamReader(new FileInputStream("names.txt")));

		// Initialization
		String data = null, allPersian = "", allLatin = "";

		//Read the train.txt once a line, split the Persian and Latin
		//names and store them in two different String arrays
		while ((data = testFile.readLine()) != null) {
			allPersian += (data.split("\t+")[0].toLowerCase() + " ");
			allLatin += (data.split("\t")[1] + " ");
		}
		String[] Persian = allPersian.split(" ");
		String[] Latin = allLatin.split(" ");

		//Read the names.txt once a line, store all the entries of the 
		//dictionary in a String array
		String allDict = "";
		while((data = dictFile.readLine()) != null) {
			allDict += (data + " "); 
		}
		String[] dict = allDict.split(" ");
				
		testFile.close();
		dictFile.close();
		
		
		
		
		File file = new File("result_modify_r_GED_only.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fwGED = new FileWriter(file, true);

        int currentIndex = 0, totalPrediction = 0, correctPrediction = 0;
		
		for (String persian : Persian) {
			
			//For a Persian name, find the Latin name in the dictionary 
			//with the optimal global edit distance, which means the maximum
			//distance in this situation
			double maxGED = INFINITE;
			String allPredict = persian.toUpperCase() + " ";
			for (String latin : dict) {
				double currentGED = gloEditDist.calcGloEditDist(persian, latin, INSERTION_COST, 
						                     DELETION_COST, MATCH_COST, replaceCost);
				if (maxGED < currentGED) {
					maxGED = currentGED;
				}
			}
			
			//Go through the whole dictionary again, pick all the Latin names
			//with the optimal global edit distance
			for (String latin : dict) {
				if (maxGED == gloEditDist.calcGloEditDist(persian, latin, INSERTION_COST, 
						                     DELETION_COST, MATCH_COST, replaceCost)){
					allPredict += (latin + " ");
					totalPrediction ++;
					if (latin.equals(Latin[currentIndex])) {
						correctPrediction ++;
					}
				}
			}
			
			allPredict += "\n";
			currentIndex ++;
			fwGED.write(allPredict);
			
		}
		
		
		fwGED.write("The number of correct Predictions is " + correctPrediction + "\n");
		fwGED.write("The number of total Predictions is " + totalPrediction + "\n");
		fwGED.write("The number of names in dictionary is " + Persian.length + "\n");
		fwGED.write("The precision is " + (double)correctPrediction / totalPrediction + "\n");
		fwGED.write("The recall is " + (double)correctPrediction / Persian.length + "\n");
		
		fwGED.close();
	
		
		
	}
	
	

}
