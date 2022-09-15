import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Formatter;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {

	public static void main(String[] args) {
		//BufferedReader
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String bufstr="";
			String result="";
		
			System.out.println("Input strings , exit - end");
		
			while(!bufstr.equals("exit")) {
				bufstr=br.readLine();
				result+=bufstr+" ";
			}
			
			System.out.println("Result : "+result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Scanner
		
		Scanner scr = new Scanner(System.in);
		
		scr.useDelimiter(",");
		
		System.out.println("Input strings , ',' char delimit next and previos elements , exit - end");
		
		String buf = "";
		
		while(scr.hasNext()) {
			buf=(String)scr.next();
			System.out.println(buf);
			if(scr.findInLine("exit")!=null) {
				break;
			}	
		}
		
		scr.close();
		
		//String
		
		char[] cha = {'H','E','N','L','L','O'};
		String chrs = new String(cha,0,5);
		int num = 4;
		System.out.println(chrs+" "+chrs.length()+" "+Integer.toString(num));
		
		System.out.println(chrs.charAt(0));
		System.out.println(chrs.indexOf("NL"));
		System.out.println(chrs.matches("LO"));
		
		//StringTokenizer
		
		String test = "I'm eating ' every day";
		StringTokenizer st = new StringTokenizer(test,"'");
		
		System.out.println(st.countTokens());
		
		//Formatter
		
		Formatter fmt = new Formatter();
		fmt.format("Форматировать очень просто с помощью Java %d", 10);
		System.out.println(fmt.toString());
	}

}
