
public class HandValue {

	public static void main(String[] args) {
		String[] hand = new String[4];
		String start = args[4];
		int total = 0;
		for(int i=0;i<4;i++){
			hand[i] = args[i];
		}
		total += Rules.scoFifteen(hand, start);
	    total += Rules.scoPairs(hand, start);
	    total += Rules.scoRuns(hand, start);
	    total += Rules.scoFlushes(hand, start);
	    total += Rules.scoOneForNob(hand,start);
	    System.out.println(total);
	}

}
