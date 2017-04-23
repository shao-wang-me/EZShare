
public class Soundex {
	public static String soundex(String s) { 
        char[] ch = s.toCharArray();

        for (int i = 1; i < ch.length; i++) {
            switch (ch[i]) {

                case 'b':
                case 'p':
                case 'f':
                case 'v':
                    ch[i] = '1';
                    break;

                case 'c':
                case 'g':
                case 'j':
                case 'k':
                case 'q':
                case 's':
                case 'x':
                case 'z':
                    ch[i] = '2';
                    break;

                case 'd':
                case 't':
                    ch[i] = '3';
                    break;

                case 'l':
                    ch[i] = '4';
                    break;

                case 'm':
                case 'n':
                    ch[i] = '5';
                    break;

                case 'r':
                    ch[i] = '6';
                    break;

                default:
                    ch[i] = '0';
                    break;
            }
        }

        String output = "" + ch[0];
        for (int i = 1; i < ch.length; i++){
            if (ch[i] != ch[i-1] && ch[i] != '0'){
                output += ch[i];
            }
        }
        output = output + "0000";
        return output.substring(0, 4);
    }
}
