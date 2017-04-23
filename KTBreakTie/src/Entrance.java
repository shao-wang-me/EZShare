import java.io.*;

public class Entrance {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		
		
		//Define some constant numbers
		final int INFINITE = -999999;
		final int INSERTION_COST = -1;
		final int DELETION_COST = -1;
		final int REPLACEMENT_COST = -1;
	    final int MATCH_COST = 1;		
				
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
				
			
				
		File fileSoundex = new File("result_Soundex_And_GED.txt");
		if (!fileSoundex.exists()) {
			fileSoundex.createNewFile();
		}
		FileWriter fwSoundex = new FileWriter(fileSoundex, true);
				
		int currentIndex = 0,totalPrediction = 0,correctPrediction = 0;
				
		for (String persian : Persian) {
					
			//For a Persian name, find the Latin name in the dictionary 
			//with the optimal global edit distance, which means the maximum
			//distance in this situation
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
					
			int maxGED = INFINITE;
			for (String latin : dict) {
				String latinSoundex = Soundex.soundex(latin);
				if (maxSoundexGED == gloEditDist.calcGloEditDist(persianSoundex, latinSoundex, INSERTION_COST, 
						                     DELETION_COST, MATCH_COST, REPLACEMENT_COST)){
							
					int currentGED = gloEditDist.calcGloEditDist(persian, latin, INSERTION_COST, DELETION_COST, MATCH_COST, REPLACEMENT_COST);
					if (currentGED > maxGED) {
						maxGED = currentGED;
					}
							
				}
			}
					
					
			for (String latin : dict) {
				String latinSoundex = Soundex.soundex(latin);
				int SoundexGED = gloEditDist.calcSoundexDist(persianSoundex, latinSoundex, INSERTION_COST, DELETION_COST, MATCH_COST, REPLACEMENT_COST);
				int GED = gloEditDist.calcGloEditDist(persian, latin, INSERTION_COST, DELETION_COST, MATCH_COST, REPLACEMENT_COST);
				if (maxSoundexGED == SoundexGED && maxGED == GED) {
					allPredict += (latin + " ");
					totalPrediction ++;
					if (latin.equals(Latin[currentIndex])) {
						correctPrediction ++;
					}
				}
			}
					
					
			allPredict += "\n";
			currentIndex++;
			fwSoundex.write(allPredict);

		}

		fwSoundex.write("The number of correct Predictions is " + correctPrediction + "\n");
		fwSoundex.write("The number of total Predictions is " + totalPrediction + "\n");
		fwSoundex.write("The number of names in dictionary is " + Persian.length + "\n");
		fwSoundex.write("The precision is " + (double) correctPrediction / totalPrediction + "\n");
		fwSoundex.write("The recall is " + (double) correctPrediction / Persian.length + "\n");

		fwSoundex.close();
				
	}

}
