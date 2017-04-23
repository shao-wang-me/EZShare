/**This class contains the main method of the project
 * 
 * 
 * 
 * @author Yankun Qiu <yankunq@student.unimelb.edu.au>
 *
 */
public class SelectHand {
	public static final int HAND_CARD_NUM = 4;
	public static final int TOTAL_CARD_NUM = 5;
	public static final int NUM_OF_WHOLE_DECK = 52;
	/**In the main method, we receive the possible hand card in command
	 * lines. Then we call the methods in the class Rules to calculate 
	 * the scores of all the possible combinations of hand cards and 
	 * start card. And we record the maximum average scores and output
	 * the optimal method of selecting hand cards
	 * 
	 * @param receiveing the possible hand cards from the command line
	 */
	public static void main(String[] args) {	
		String[][] lines = Combinations.combinations(args);
		String[] startCard = StartCard.getStartCard(args);
		int max_i=0,flag=1;
		float total = 0,max = 0;
		for(int i = 0;i < Mathematics.nOutOfM(HAND_CARD_NUM,args.length);i++){
		    for(String sta:startCard){
			     total += Rules.scoFifteen(lines[i], sta);
			     total += Rules.scoPairs(lines[i], sta);
			     total += Rules.scoRuns(lines[i], sta);
			     total += Rules.scoFlushes(lines[i], sta);
			     total += Rules.scoOneForNob(lines[i], sta);
		    }
		    total = total / (NUM_OF_WHOLE_DECK-args.length);
		    /**
		     * if the current selecting card is the optimal method,
		     * update the information
		     */
		    if(total > max){
			     max_i = i;
			     max = total;
		    }
		}
		for(String s:lines[max_i]){
			System.out.print(s);
			flag++;
			if(flag<TOTAL_CARD_NUM){
				System.out.print(" ");
			}
		}
		System.out.println();
	}
}
