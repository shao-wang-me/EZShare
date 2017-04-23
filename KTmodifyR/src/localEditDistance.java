
public class localEditDistance {

	final static int INFINITY = 999999;
	
	static public int maxOfFour(int a,int b,int c,int d){
		return Math.max(Math.max(a, b), Math.max(c, d));
	}
	
	static public int ifMatch(char c1,char c2,int paraM,int paraR){
		if (c1 == c2){
			return paraM;
		}
		return paraR;
	}

	static public int[][] calcLocEditDist(String persian,String latin,int paraI,int paraD,int paraM,int paraR) {
		int[][] distMatrix = new int[persian.length() + 1][latin.length() + 1];
		//int minCost = INFINITY;
		for (int i = 0; i <= latin.length(); i++)
			distMatrix[0][i] = 0;
		for (int i = 0; i <= persian.length(); i++)
			distMatrix[i][0] = 0;
		for (int i = 1; i <= persian.length(); i++){
			for (int j = 1; j <= latin.length(); j++){
				distMatrix[i][j] = maxOfFour(0,distMatrix[i-1][j] + paraD,
						                      distMatrix[i][j-1] + paraI,
						                      distMatrix[i-1][j-1] + 
						                      ifMatch(persian.charAt(i-1),latin.charAt(j-1),paraM,paraR));
			}
		}
		return distMatrix;
	}
}
