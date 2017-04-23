/**This class provides some static methods, each corresponds to
 * one of the rules of the game.
 * 
 * 
 * @author Yankun Qiu <yankunq@student.unimelb.edu.au>
 *
 */
public class Rules {

	/**This method compute the score according to the rule
	 * Fifteen, five cards are passed to the method as parameters
	 * including four hand cards and one start card. The method 
	 * will try all the possible combinations and check whether 
	 * the sum of the ranks equals to fifteen or not. And the 
	 * total score will be returned. 
	 * 
	 * @param an array of the hand cards
	 * @param a string that indicates the start card
	 * @return an integer that equals to the score according to
	 *         the rule "Fifteen"
	 */
	public static final int TOTAL_CARD_NUM = 5;
	public static final int HAND_CARD_NUM = 4;
	public static final int LEAST_RUN_LEN = 3;
	public static final int FIFTEEN =15;
	public static int scoFifteen(String[] hand,String sta){
		String[] st = new String[TOTAL_CARD_NUM];
		int total = 0;
		for(int i = 0;i < HAND_CARD_NUM;i++){
			st[i] = hand[i];
		}
		st[4] = sta;
		for(int i = 1;i < 32;i++){
			/**
			 * The for loop ends at 31 
			 * because its binary format of 31 is 11111
			 */
			int sum = 0;
			/**
			 * The method deciToBinary is used to 
			 * convert a decimal number into its binary format
			 */
			int[] bin = Mathematics.deciToBinary(i,TOTAL_CARD_NUM);
			for(int j = 0;j < TOTAL_CARD_NUM;j++){
				if(bin[j] == 1){
				    sum += Cards.faceValue(st[j].charAt(0));
				}
			}
			/**
			 * when we find a combination whose sum 
			 * is 15, we plus one to the variable total
			 */
			if(sum == FIFTEEN){
				total++;
			}
		}
		return 2*total;
	}
	
	/**This method compute the score according to the rule
	 * Fifteen, five cards are passed to the method as parameters
	 * including four hand cards and one start card. The method will
	 * check each pairs whether their ranks are the same or not using
	 * two for loops
	 * 
	 * @param an array of the hand cards
	 * @param a string that indicates the start card
	 * @return the score it gets according to the rule "Pairs"
	 */
	public static int scoPairs(String[] hand,String sta){
		int score = 0;
		for(int i = 0;i < HAND_CARD_NUM-1;i++){
			for(int j = i+1;j < HAND_CARD_NUM;j++){
				//check how many pairs are there among the four hand cards
				if (hand[i].charAt(0) == hand[j].charAt(0)){
					score += 2;
				}
			}
			//check how many hand cards can be pairs with the start card
			if(hand[i].charAt(0) == sta.charAt(0)){
				score += 2;
			}
		}
		if(hand[3].charAt(0) == sta.charAt(0)){
			score += 2;
		}
		return score;
	}
	
	/**This method is used to compute the score according to the rule
	 * Runs. The method will first sort the five cards according to 
	 * their ranks. Then it goes through the sorted list and record the 
	 * information of the cards that have consecutive ranks. And the 
	 * score will be returned as a single integer.
	 * 
	 * @param hand
	 * @param sta
	 * @return the score of the cards get according to the rules "Run"
	 */
	public static int scoRuns(String[] hand,String sta){
		String[] s = new String[TOTAL_CARD_NUM];
		//put the four hand cards and the start card in an array size of five
		for(int i = 0;i < HAND_CARD_NUM;i++){
			s[i] = hand[i];
		}
		s[4] = sta;
		//sort the array according to their ranks
		int[] card = Mathematics.sort(s);
		//same is to store how many numbers which are the same we have now
		//length is to store the current length of the runs we have
		//num is to store the number of current runs
		int same = 1,length = 1,num = 1;
		for(int i = 1;i < TOTAL_CARD_NUM;i++){
			if(card[i] - card[i-1] == 0){
				//when we find two numbers and they are the same
				//if it is the last element, update the num
				same += 1;
				if(i == TOTAL_CARD_NUM-1){
					num = num*same;
				}
			}else if(card[i] - card[i-1] == 1){
				/**
				 * when we find two consecutive numbers,
				 * update the length,num and same
				 */
				length ++;
				num = num*same;
				same = 1;
			}else{
				/**
				 * when we find two numbers which are 
				 * neither the same nor consecutive
				 */
				if(length >= LEAST_RUN_LEN){
					/**
					 * if the current length of the run is greater than 2,
					 * there is no need to continue
					 * we can just update the num and break the for loop
					 */
					num = num*same;
					break;
				}else{
					/**
					 * if the current length of the run less than or equal 
					 * to 2, update the variables and continue
					 */
					length = 1;
					num = 1;
					same = 1;
				}
			}
		}
		if(length >= LEAST_RUN_LEN){
		   /**
		    * if the length of the runs is greater than 2, 
		    * calculate the score and return
		    */
		   return length*num;
		}else{
			return 0;
		}
	}
	
	/**This method is used to calculate the score the cards get
	 * according to the rule "Flushes". To achieve this, we first 
	 * check whether the four hand cards' suits are the same,then 
	 * we check the suit of the start card.
	 * 
	 * @param an array of String indicates the hand cards
	 * @param a String indicates the start card
	 * @return the score of the cards get according to the rules "Flush"
	 */
	public static int scoFlushes(String[] hand,String sta){
		boolean flag = true;
		for(int i = 0;i < HAND_CARD_NUM-1;i++){
			//check whether the four hand cards are in the same suits or not
			if(hand[i].charAt(1) != hand[i+1].charAt(1)){
				flag = false;
	     		break;
			}
		}
		if(flag == true){
			//check whether the suit of the start card are also the same
			if(sta.charAt(1) == hand[0].charAt(1)){
				return 5;
			}else{
				return 4;
			}
		}else{
			return 0;
		}
	}
	
	/**This method is used to calculate the score the cards get
	 * according to the rule "One for his nob". To achieve this, 
	 * we first check whether there is a card whose rank is 'J',
	 * then we check whether its suit is the same as the start 
	 * card or not
	 * 
	 * @param an array of String indicates the hand cards
	 * @param a String indicates the start card
	 * @return the score of the cards get according to the rules "One
	 * for his nob"
	 */
	public static int scoOneForNob(String[] hand,String sta){
		boolean flag = false;
		for(String s:hand){
			/**
			 * check whether there is a hand card whose rank is 
			 * 'J' and its suit is the same with the start card
			 */
			if(s.charAt(1) == sta.charAt(1) && s.charAt(0) == 'J'){
				flag = true;
				break;
			}
		}
		if(flag == true){
			return 1;
		}else{
			return 0;
		}
	}    
}
