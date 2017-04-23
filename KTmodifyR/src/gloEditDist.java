
public class gloEditDist {
	
	static public double minOfThree(double d,double e,double c){
		if (d > e){
			return Math.max(d, c);
		}
		return Math.max(c, e);
	}
	
	static public int minOfThree(int d,int e,int c){
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
	
	static public int ifMatch(char c1,char c2,int paraM,int paraR){
		if(c1 == c2)
			return paraM;
		return paraR;
	}

	static public double calcGloEditDist(String persian,String latin,int paraI,int paraD,int paraM,double[][] paraR) {
		double[][] distMatrix = new double[persian.length() + 1][latin.length() + 1];
		
		
		
		for (int i = 0; i <= latin.length(); i++)
			distMatrix[0][i] = i * paraI;
		for (int i = 0; i <= persian.length(); i++)
			distMatrix[i][0] = i * paraD;
		for (int i = 1; i <= persian.length(); i++){
			for (int j = 1; j <= latin.length(); j++){
				
				
				distMatrix[i][j] = minOfThree(distMatrix[i-1][j] + paraD,
						                      distMatrix[i][j-1] + paraI,
						                      distMatrix[i-1][j-1] + 
						                      replacement(persian.toLowerCase().charAt(i-1),latin.charAt(j-1),paraM,paraR));
			}
		}
		return distMatrix[persian.length()][latin.length()];
	}
	
	static public int calcSoundexDist(String persian,String latin,int paraI,int paraD,int paraM,int paraR) {
		int[][] distMatrix = new int[persian.length() + 1][latin.length() + 1];
		
		
		
		for (int i = 0; i <= latin.length(); i++)
			distMatrix[0][i] = i * paraI;
		for (int i = 0; i <= persian.length(); i++)
			distMatrix[i][0] = i * paraD;
		for (int i = 1; i <= persian.length(); i++){
			for (int j = 1; j <= latin.length(); j++){
				
				
				distMatrix[i][j] = minOfThree(distMatrix[i-1][j] + paraD,
						                      distMatrix[i][j-1] + paraI,
						                      distMatrix[i-1][j-1] + 
						                      ifMatch(persian.toLowerCase().charAt(i-1),latin.charAt(j-1),paraM,paraR));
			}
		}
		return distMatrix[persian.length()][latin.length()];
	}
}
