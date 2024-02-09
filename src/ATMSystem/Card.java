package ATMSystem;
import java.util.Random;


class Card {
    static String pin;
    public static String CreateCardNumber(){
    	String cardNumber;
    	Random random = new Random();
	    String numbers = "0123456789";
	    StringBuilder sb = new StringBuilder();
	    int length=16;
	    for(int i = 0; i < length; i++) {
	    	if (i > 0 && i % 4 == 0) {
                sb.append('-');
            }
	    	int index = random.nextInt(numbers.length());
	        char randomChar = numbers.charAt(index);
	        sb.append(randomChar);
	      }
	    cardNumber = sb.toString();
	    return cardNumber;	
    }
}
