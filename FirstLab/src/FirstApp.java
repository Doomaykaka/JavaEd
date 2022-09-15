
public class FirstApp {

	public static void main(String[] args) {
		System.out.println("Time to boom :");
		if(counting(args)==10) {
			System.out.println("Check parametrs");
		}
		System.out.println("Boooooooom");
	}
	
	public static int counting(String[] data) {
		int counter = 10;
		if(data.length>0) {
			counter=Integer.parseInt(data[0]);
		}
		for(int i=0;i<counter;i++) {
			System.out.println("Seconds to boom - "+(counter-i));
		}
		return counter;
	}

}
