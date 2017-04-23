import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class tuningR {
	static public int[][] tuneR(int paraI,int paraD,int paraM,int paraR) throws IOException{
		
		//A two dimension matrix is defined to count the number of conversion
		int[][] coversionNum = new int[27][26];
		BufferedReader trainFile = new BufferedReader(new InputStreamReader(new FileInputStream("train.txt")));
		String data = null, persian = "",latin = "";

		//Read the data from train.txt and stored the Persian and Latin names in two arrays
		while ((data = trainFile.readLine()) != null) {
			persian += (data.split("\t+")[0].toLowerCase() + " ");
			latin += (data.split("\t")[1].toLowerCase() + " ");
		}
		trainFile.close();
		
		//The Persian name in train.txt
		String[] Persian = persian.split(" ");
		//The corresponding Latin name in train.txt
		String[] Latin = latin.split(" ");
		

		for (int i = 0; i < Persian.length; i++){
			//Calculate the local edit distance of each pair of Persian and Latin names
			//and return the distance matrix
			int[][] distMatrix = localEditDistance.calcLocEditDist(Persian[i],Latin[i], paraI, paraD, paraM, paraR);
			
			//Search the whole matrix
			int indexI = 0;
			for (int[] row: distMatrix){
				int indexJ = 0;
				for (int element: row){
					//If both characters are not the first character in the Persian and Latin names
					//AND the intersection of these two characters is one AND these two characters
					//are the same, then we say, the characters before these two may be a potential
					//replacement
					if (indexI > 1 && indexJ > 1 &&
							distMatrix[indexI][indexJ] == 1 && 
							Persian[i].charAt(indexI-1) == Latin[i].charAt(indexJ-1)){
						//Get the two previous characters in Persian and Latin names
						char preCharacterPersian = Persian[i].charAt(indexI - 2);
						char preCharacterLatin = Latin[i].charAt(indexJ - 2);
						
						if(preCharacterPersian == '\''){
							coversionNum[26][preCharacterLatin - 'a']++;
						} else {
						    coversionNum[preCharacterPersian - 'a'][preCharacterLatin - 'a']++;
						}
					//If both characters are in the Persian and Latin names, which means the indexes 
					//should be greater than one AND two previous characters can match but they are 
					//different, then we say, these two characters are also a potential replacement
					} else if ((indexI > 0 && indexJ > 0) && 
							distMatrix[indexI][indexJ] == distMatrix[indexI - 1][indexJ - 1] - 1){
						//Get these two characters in Persian and Latin names
						char characterPersian = Persian[i].charAt(indexI - 1);
						char characterLatin = Latin[i].charAt(indexJ - 1);
						if(characterPersian == '\''){
							coversionNum[26][characterLatin - 'a']++;
						} else {
					        coversionNum[characterPersian - 'a'][characterLatin - 'a']++;
						}
					}
					indexJ ++;
				}
				indexI ++;
			}
		}
		return coversionNum;
	}
}
