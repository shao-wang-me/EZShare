/**
 * This class is used to calculate the global edit distance between
 * two given strings
 * 
 *
 */
public class gloEditDist {
	
	static public double maxOfThree(double d,double e,double c){
		if (d > e){
			return Math.max(d, c);
		}
		return Math.max(c, e);
	}
	

	
	static public double replacement(char c1,char c2,int paraM,double paraR[][]){
		if (c1 == c2)
			return paraM;
		if (c1 == '\'')
			return paraR[26][c2 - 'a'];
		return paraR[c1 - 'a'][c2 - 'a'];
	}
	


	static public double calcGloEditDist(String persian,String latin,int paraI,int paraD,int paraM,double[][] paraR) {
		double[][] distMatrix = new double[persian.length() + 1][latin.length() + 1];
		
		
		
		for (int i = 0; i <= latin.length(); i++)
			distMatrix[0][i] = i * paraI;
		for (int i = 0; i <= persian.length(); i++)
			distMatrix[i][0] = i * paraD;
		for (int i = 1; i <= persian.length(); i++){
			for (int j = 1; j <= latin.length(); j++){
				
				
				distMatrix[i][j] = maxOfThree(distMatrix[i-1][j] + paraD,
						                      distMatrix[i][j-1] + paraI,
						                      distMatrix[i-1][j-1] + 
						                      replacement(persian.toLowerCase().charAt(i-1),latin.charAt(j-1),paraM,paraR));
			}
		}
		return distMatrix[persian.length()][latin.length()];
	}
	

}
