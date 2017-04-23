/**
 * This class contains the main method for the task modifying r parameter of KT
 * project. Also, a method called cross-validation is adopted to split the train.txt
 * document into  training set and testing set. The results of the all the nine 
 * groups will be stored in a single txt file. And the precision and recall will be 
 * given at the end of the file
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Entrance {

	public static void main(String[] args) throws IOException {

		final int INFINITE = -999999;
		final int INSERTION_COST = -1;
		final int DELETION_COST = -1;
		final int MATCH_COST_TUNE_R = 1;
		final int MATCH_COST = 3;
		final int REPLACEMENT_COST = -1;
		final int TRAIN_SET_SIZE = 11944;
		final int TEST_SET_SIZE = 1493;

		BufferedReader testFile = new BufferedReader(new InputStreamReader(new FileInputStream("train.txt")));
		BufferedReader dictFile = new BufferedReader(new InputStreamReader(new FileInputStream("names.txt")));

		// Initialization
		String data = null, allPersian = "", allLatin = "";

		// Read the train.txt once a line, split the Persian and Latin
		// names and store them in two different String arrays
		while ((data = testFile.readLine()) != null) {
			allPersian += (data.split("\t+")[0].toLowerCase() + " ");
			allLatin += (data.split("\t")[1] + " ");
		}
		String[] Persian = allPersian.split(" ");
		String[] Latin = allLatin.split(" ");

		// Read the names.txt once a line, store all the entries of the
		// dictionary in a String array
		String allDict = "";
		while ((data = dictFile.readLine()) != null) {
			allDict += (data + " ");
		}
		String[] dict = allDict.split(" ");

		testFile.close();
		dictFile.close();

		String filename = "result_modify_r_GED_only_CroVal.txt";
		File file = new File(filename);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fwGED = new FileWriter(file, true);
		
		int totalPrediction = 0, correctPrediction = 0;
		
		for (int group = 1; group < 10; group++) {

			int currentIndex = 0;
			String[] trainPersianGroup = new String[TRAIN_SET_SIZE];
			String[] trainLatinGroup = new String[TRAIN_SET_SIZE];
			String[] testPersianGroup = new String[TEST_SET_SIZE];
			String[] testLatinGroup = new String[TEST_SET_SIZE];


			int indexTest = 0, indexTrain = 0;

			for (int i = 0; i < 13437; i++) {
				if ((i >= (group - 1) * TEST_SET_SIZE) && (i < group * TEST_SET_SIZE)) {
					testPersianGroup[indexTest] = Persian[i];
					testLatinGroup[indexTest] = Latin[i];
					indexTest++;
				} else {
					trainPersianGroup[indexTrain] = Persian[i];
					trainLatinGroup[indexTrain] = Latin[i];
					indexTrain++;
				}
			}

			int[][] covertNum = tuningR.tuneR(trainPersianGroup, trainLatinGroup, INSERTION_COST, DELETION_COST, MATCH_COST_TUNE_R, REPLACEMENT_COST);
			double[][] replaceCost = new double[27][26];
			for (int i = 0; i < 27; i++) {
				int rowSum = 0;
				for (int j = 0; j < 26; j++) {
					rowSum += covertNum[i][j];
				}

				for (int j = 0; j < 26; j++) {

					replaceCost[i][j] = covertNum[i][j] == 0 ? -5 : 3 * (double) covertNum[i][j] / rowSum;

				}
			}


			for (String persian : testPersianGroup) {

				// For a Persian name, find the optimal global edit distance, which means the
				// maximum distance in this situation
				double maxGED = INFINITE;
				String allPredict = persian.toUpperCase() + " ";
				for (String latin : dict) {
					double currentGED = gloEditDist.calcGloEditDist(persian, latin, INSERTION_COST, DELETION_COST,
							MATCH_COST, replaceCost);
					if (maxGED < currentGED) {
						maxGED = currentGED;
					}
				}

				// Go through the whole dictionary again, pick all the Latin
				// names with the optimal global edit distance
				for (String latin : dict) {
					if (maxGED == gloEditDist.calcGloEditDist(persian, latin, INSERTION_COST, DELETION_COST, MATCH_COST,
							replaceCost)) {
						allPredict += (latin + " ");
						totalPrediction++;
						if (latin.equals(testLatinGroup[currentIndex])) {
							correctPrediction++;
						}
					}
				}

				allPredict += "\n";
				currentIndex++;
				fwGED.write(allPredict);

			}
		}
		
		fwGED.write("The number of correct Predictions is " + correctPrediction + "\n");
		fwGED.write("The number of total Predictions is " + totalPrediction + "\n");
		fwGED.write("The number of names in test dataset is " + Persian.length + "\n");
		fwGED.write("The precision is " + (double) correctPrediction / totalPrediction + "\n");
		fwGED.write("The recall is " + (double) correctPrediction / Persian.length + "\n");

		fwGED.close();

	}

}
