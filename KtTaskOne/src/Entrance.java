import java.io.*;
/**
 * This is the class for the task one of the KT project. There is only
 * one main method in this class. The main method can generate two txt
 * files, with the results of running global edit distance and soundex
 * algorithms
 *
 */

public class Entrance {

	public static void main(String[] args) throws IOException {

		//Define some constant numbers
		final int INFINITE = -999999;
		final int INSERTION_COST = -1;
		final int DELETION_COST = -1;
		final int REPLACEMENT_COST = -1;
		final int MATCH_COST = 3;		
		
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
		
		File fileGED = new File("result_GED_Only.txt");
		if (!fileGED.exists()) {
			fileGED.createNewFile();
		}
		FileWriter fwGED = new FileWriter(fileGED, true);
		
		//Define some counters to calculate the precision and recall
		int currentIndex = 0, totalPrediction = 0, correctPrediction = 0;
		for (String persian : Persian) {
			
			//For a Persian name, find out the optimal global edit 
			//distance, which means the maximum distance in this 
			//situation
			int maxGED = INFINITE;
			String allPredict = persian.toUpperCase() + " ";
			for (String latin : dict) {
				int currentGED = gloEditDist.calcGloEditDist(persian, latin, INSERTION_COST, 
						                     DELETION_COST, MATCH_COST, REPLACEMENT_COST);
				if (maxGED < currentGED) {
					maxGED = currentGED;
				}
			}
			
			//Go through the whole dictionary again, pick all the Latin names
			//with the optimal global edit distance
			for (String latin : dict) {
				if (maxGED == gloEditDist.calcGloEditDist(persian, latin, INSERTION_COST, 
						                     DELETION_COST, MATCH_COST, REPLACEMENT_COST)){
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
		fwGED.write("The number of names in testing set is " + Persian.length + "\n");
		fwGED.write("The precision is " + (double)correctPrediction / totalPrediction + "\n");
		fwGED.write("The recall is " + (double)correctPrediction / Persian.length + "\n");
		
		fwGED.close();
		
		
		File fileSoundex = new File("result_Soundex_Only.txt");
		if (!fileSoundex.exists()) {
			fileSoundex.createNewFile();
		}
		FileWriter fwSoundex = new FileWriter(fileSoundex, true);
		
		//initialize the counters
		currentIndex = 0; 
		totalPrediction = 0; 
		correctPrediction = 0;
		for (String persian : Persian) {
			
			//Given a Persian name, for each Latin name, transfer them into soundex
			//representations, calculate the global edit distance between their 
			//soundex representations and record the optimal distance
			int maxSoundexGED = INFINITE;
			String persianSoundex = Soundex.soundex(persian);
			String allPredict = persian.toUpperCase() + " ";
			for (String latin : dict) {
				String latinSoundex = Soundex.soundex(latin);
				int currentSoundexGED = gloEditDist.calcGloEditDist(persianSoundex, latinSoundex, INSERTION_COST, 
						                     DELETION_COST, MATCH_COST, REPLACEMENT_COST);
				if (maxSoundexGED < currentSoundexGED) {
					maxSoundexGED = currentSoundexGED;
				}
			}
			
			//Go through the whole dictionary again, pick all the Latin names
			//with the optimal global edit distance between its and the Persian
			//name's soundex representations
			for (String latin : dict) {
				String latinSoundex = Soundex.soundex(latin);
				if (maxSoundexGED == gloEditDist.calcGloEditDist(persianSoundex, latinSoundex, INSERTION_COST, 
						                     DELETION_COST, MATCH_COST, REPLACEMENT_COST)){
					allPredict += (latin + " ");
					totalPrediction ++;
					if (latin.equals(Latin[currentIndex])) {
						correctPrediction ++;
					}
				}
			}
			
			allPredict += "\n";
			currentIndex ++;
			fwSoundex.write(allPredict);
			
		}
		
		fwSoundex.write("The number of correct Predictions is " + correctPrediction + "\n");
		fwSoundex.write("The number of total Predictions is " + totalPrediction + "\n");
		fwSoundex.write("The number of names in testing set is " + Persian.length + "\n");
		fwSoundex.write("The precision is " + (double)correctPrediction / totalPrediction + "\n");
		fwSoundex.write("The recall is " + (double)correctPrediction / Persian.length + "\n");
		
		fwSoundex.close();	
	}
}
