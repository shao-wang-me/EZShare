/**COMP90041 Lab 2 Question 1
 * 
 * @author Yankun Qiu
 *
 */
import java.util.Scanner;

public class Lab2_1 {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String in = sc.nextLine();
		System.out.println(in.length());
		String[] words = in.split(" ");
		System.out.println(words[0]);
		for(int i = 1;i < words.length; i++){
			System.out.print(words[i]);
			if(i != words.length - 1){
				System.out.print(" ");
			}
		}
		System.out.println();
		sc.close();
	}

}
