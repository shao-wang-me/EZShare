/**
 * This class is used to calculate the global edit distance between two 
 * given strings
 * 
 *
 */
public class gloEditDist {
	/*
	 * Return the maximum integer among three given integers
	 */
	static public int maxOfThree(int d,int e,int c){
		if (d > e){
			return Math.max(d, c);
		}
		return Math.max(c, e);
	}

	/*
	 * Comparing two characters, return paraM if they are the same,
	 * return paraR otherwise
	 */
	static public int ifMatch(char c1,char c2,int paraM,int paraR){
		if(c1 == c2)
			return paraM;
		return paraR;
	}

	/*
	 * Given two strings and the parameters, this function is used to calculate
	 * the global edit distance between these two strings
	 */
	static public int calcGloEditDist(String persian,String latin,int paraI,int paraD,int paraM,int paraR) {	
		
		int[][] distMatrix = new int[persian.length() + 1][latin.length() + 1];
		
		
		
		for (int i = 0; i <= latin.length(); i++)
			distMatrix[0][i] = i * paraI;
		for (int i = 0; i <= persian.length(); i++)
			distMatrix[i][0] = i * paraD;
		for (int i = 1; i <= persian.length(); i++){
			for (int j = 1; j <= latin.length(); j++){
				
				
				distMatrix[i][j] = maxOfThree(distMatrix[i-1][j] + paraD,
						                      distMatrix[i][j-1] + paraI,
						                      distMatrix[i-1][j-1] + 
						                      ifMatch(persian.charAt(i - 1),latin.charAt(j - 1),paraM,paraR));
			}
		}
		return distMatrix[persian.length()][latin.length()];
	}
}
