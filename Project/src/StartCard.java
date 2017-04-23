/**This class provides a single static method to generate
 * an array of String that contains all the possible 
 * start cards.
 *
 * 
 * 
 * @author Yankun Qiu <yankunq@student.unimelb.edu.au>
 * 
 */
public class StartCard {
	
	static String[] suits = {"C","D","H","S"};
	static String[] ranks = {"A","2","3","4","5","6","7","8","9",
			                 "T","J","Q","K"};
	public static final int NUM_OF_WHOLE_DECK = 52;
	
	
	/**In the class,there are two static string, indicate the suits and the 
	 * rank of the cards. With two for loops, we can get all the combinations
	 * of suits and ranks. According to the rules, the 4-6 cards the player 
	 * get first could not be a start card, so all the cards stored in the 
	 * array exclude should be excluded.
	 * 
	 * 
	 * @param list an array of the 4-6 cards a player get first
	 * @return an array of all the possible start cards
	 */
	public static String[] getStartCard(String[] exclude){
		int count = 0;
		String[] result = new String[NUM_OF_WHOLE_DECK-exclude.length];
		for(String s1:ranks){
			for(String s2:suits){
				String s = s1 + s2;
				boolean flag = true;
				for(String s3: exclude){
					//check whether the card should be excluded or not
					if(s.equalsIgnoreCase(s3) == true){
						flag = false;
						break;
					}
				}
				if(flag){
					result[count] = s;
					count++;
				}
			}
		}
		return result;
	}
}
