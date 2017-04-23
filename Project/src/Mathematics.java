/**This class provides a number of static methods in dealing with some
 * mathematical problems
 * 
 *
 * @author Yankun Qiu <yankunq@student.unimelb.edu.au>
 * 
 */
public class Mathematics {
	/**This method is used to calculate the number of different
	 * cases if we want to choose n out of m
	 * 
	 * @param n
	 * @param m
	 * @return the number of different cases
	 */
	public static int nOutOfM(int n,int m){
		int res = 1,div = 1;
		for(int i=1;i<=n;i++){
			res = res*m;
			m--;
			div = div*i;
		}
		return res/div;
	}
	/**This method is used to calculate how many 1s are there
	 * in the binary format of the integer n
	 * 
	 * @param n
	 * @return the number of 1s in the binary format of integer
	 * n
	 */
	public static int oneInBinary(int n){
		int count = 0;
		while(n!=0){
			if(n % 2 == 1){
				count++;
			}
			n = n/2;
		}
		return count;
	}
	/**This method is used to convert a decimal number to its
	 * binary format, whose length is the variable length
	 * 
	 * @param n
	 * @param length
	 * @return the binary format of the decimal number
	 */
	public static int[] deciToBinary(int n,int length){
		int[] result = new int[length];
		for(int i = 0;i <length ;i++){
			result[i] = n % 2;
			n = n/2;
		}
		return result;
	}
	/**This method is used to sort given five cards according
	 * to there ranks, and return the results as an array of
	 * integers
	 * 
	 * @param an array of String indicates the five cards
	 * @return a sorted array
	 */
	public static int[] sort(String[] s){
		int[] result = new int[5];
		for(int i = 0;i < 5;i++){
			result[i] = Cards.trueValue(s[i].charAt(0));
		}
		for(int i = 3;i >= 0;i--){
			for(int j = 0;j <= i;j++){
				if(result[j] > result[j+1]){
					int temp = result[j];
					result[j] = result[j+1];
					result[j+1] = temp;
				}
			}
		}
		return result;
	}
}
