/**This class provides two methods which are used to covert the 
 * ranks of the cards into integers. Because the ways we convert 
 * the ranks are different when calculating the scores for "Fifteen" 
 * and "Runs", so we need two methods here
 * 
 * 
 * 
 *
 */
public class Cards {

	public static final int error = -1;

	/**This method is used to obtain the face value of a specific card
	 * which is used in calculating the score of "Fifteen"
	 * 
	 * @param the rank of the card
	 * @return the face value of the card
	 */
	
	public static int faceValue(char c){
		if(c == 'A'){
			return 1;
		}else if(c > '1' && c <='9'){
			return c-'1'+1;
		}else if(c == 'T' || c == 'J' || c == 'Q' || c == 'K'){
			return 10;
		}else{
			return error;
		}
	}
	/**This method is used to obtain the true value of a specific card
	 * which is used in calculating the score of "Runs"
	 * 
	 * @param the rank of the card
	 * @return the true value of the card
	 */
	public static int trueValue(char c){
		if(c == 'A'){
			return 1;
		}else if(c > '1' && c <= '9'){
			return c-'1'+1;
		}else if(c == 'T'){
			return 10;
		}else if(c == 'J'){
			return 11;
		}else if(c == 'Q'){
			return 12;
		}else if(c == 'K'){
			return 13;
		}else{
			return error;
		}
	}
}
