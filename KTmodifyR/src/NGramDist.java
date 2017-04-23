public class NGramDist {
	static public int calNGramDistance(String persian,String latin,int n){
		String hashPersian = "#" + persian + "#";
		String hashLatin = "#" + latin + "#";
		String[] groupPersian = new String[hashPersian.length() - n + 1];
		String[] groupLatin = new String[hashLatin.length() - n + 1];
		for (int i = 0; i <= hashPersian.length() - n; i++){
			groupPersian[i] = hashPersian.substring(i,i+n);
		}
		for (int i = 0; i <= hashLatin.length() - n; i++){
			groupLatin[i] = hashLatin.substring(i, i+n);
		}
		
		int intersection = 0;
		for (int i = 0; i < groupPersian.length; i++){
			boolean ifBreak = false;
			for (int j = 0; j < groupLatin.length; j++){
				if (groupPersian[i].equalsIgnoreCase(groupLatin[j])){
					groupLatin[j] = groupLatin[j] + "#";
					ifBreak = true;
					break;
				}
			}
			if (ifBreak){
				intersection ++;
			}
		}
		return groupPersian.length + groupLatin.length - 2 * intersection;
	}
}
