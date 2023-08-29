import java.util.*;
import java.io.*;

public class farm{

	/* Normally, it is a bad idea to have the main function
	 *   throw an Exception.  But doing this eliminates the 
	 *   need for try-catch blocks around file I/O operations
	 **/
	public static void main(String[] args) throws Exception{

		int days, n;			// input variables
		int season = 1;
		BufferedReader br;
		StringTokenizer tok;		// parses input
		Crop[] crops;
		String divider = "+---+----------+----------+--------+--------+";
		String planted, harvested;
		int expense_today, income_today, expense_total, income_total;

		/* create the buffered reader to be used for reading
		 *   in the input file
		 **/
		br  = new BufferedReader(new FileReader("farm.in"));

		/* we'll break out of this loop when days equals zero
		 **/
		while(true){

			/* read in the number of days for the current season
			 * break if it equals zero
			 **/
			days = Integer.parseInt(br.readLine().trim());
			if(days==0) break;

			/* read in the n value
			 * the trim() function is called here to remove all
			 *   leading and trailing spaces
			 * create the crops array of size p
			 **/
			n = Integer.parseInt(br.readLine().trim());
			crops = new Crop[n];

			/* read in the line consisting of the crop information
			 * create the new Crop by calling the Crop constructor
			 **/
			for(int i = 0; i < n; i++){
				crops[i] = new Crop(br.readLine());
			}

			/* print out the table header information
			 **/
			System.out.println("Season " + season);
			System.out.println(divider);
			System.out.println("|Day|  Plant   | Harvest  |Expenses| Income |");
			System.out.println(divider);

			/* initialize the season's expense and income to zero
			 **/
			expense_total = income_total = 0;

			for(int i = 1; i <= days; i++){

				expense_today = income_today = 0;

				/* figure out what plants can be harvested
				 **/
				harvested = "";
				for(int j = 0; j < n; j++){
					crops[j].harvested_today = false;
					if(crops[j].already_harvested){
						if(crops[j].days_planted == crops[j].regrow_days){
							harvested += crops[j].first_upper;
							crops[j].harvested_today = true;
							income_today += crops[j].income;
						}
					}else{
						if(crops[j].days_planted == crops[j].num_days){
							harvested += crops[j].first_upper;
							crops[j].already_harvested = true;
							crops[j].harvested_today = true;
							income_today += crops[j].income;
						}
					}
				}

				planted = "";
				for(int j = 0; j < n; j++){
					if(!crops[j].initially_planted && crops[j].num_days + i <= days){
						planted += crops[j].first_upper;
						crops[j].initially_planted = true;
						crops[j].days_planted = 0;
						expense_today += crops[j].expense;		
					}else if(crops[j].already_harvested &&
					         crops[j].single_harvest && 
						 crops[j].days_planted == crops[j].regrow_days &&
						 crops[j].regrow_days + i <= days){
						planted += crops[j].first_upper;
						crops[j].days_planted = 0;
						expense_today += crops[j].expense;
					}
					if(crops[j].harvested_today){
						crops[j].days_planted = 0;
					}
						
				}

				for(int j = 0; j < n; j++){
					crops[j].days_planted++;
				}

				if(!(planted.equals("") && harvested.equals(""))){
					System.out.print("|" + leftPad(i, 3) + "|");
					System.out.print(rightPad(planted, 10) + "|");
					System.out.print(rightPad(harvested, 10) + "|");
					System.out.print(leftPad(expense_today + "G", 8) + "|");
					System.out.print(leftPad(income_today + "G", 8) + "|");
					System.out.println();
				}

				income_total += income_today;
				expense_total += expense_today;

			}

			System.out.println(divider);
			System.out.print("|ALL|          |          |");
			System.out.print(leftPad(expense_total + "G", 8) + "|");
			System.out.print(leftPad(income_total + "G", 8) + "|");
			System.out.println();
			System.out.println(divider);
			System.out.println("Ali will make " + (income_total-expense_total) + "G.");
			System.out.println();

			season++;

		}

	}

	private static String leftPad(String str, int padding){

		while(str.length() < padding) str = " " + str;

		return str;

	}

	private static String rightPad(String str, int padding){

		while(str.length() < padding) str += " ";

		return str;

	}

	private static String leftPad(int n, int padding){

		String str = n+"";

		while(str.length() < padding) str = " " + str;

		return str;

	}

	private static String rightPad(int n, int padding){

		String str = n+"";

		while(str.length() < padding) str += " ";

		return str;

	}

	private static class Crop{

		String name;
		boolean single_harvest;
		int num_patches, cost_bag, market_price, num_days, regrow_days;
		String first_upper;
		int expense, income;
		boolean initially_planted, already_harvested, harvested_today;
		int days_planted;

		public Crop(String str){

			StringTokenizer tok = new StringTokenizer(str);

			this.name = tok.nextToken();
			this.single_harvest = tok.nextToken().toUpperCase().equals("S");
			this.num_patches = Integer.parseInt(tok.nextToken());
			this.cost_bag = Integer.parseInt(tok.nextToken());
			this.market_price = Integer.parseInt(tok.nextToken());
			this.num_days = Integer.parseInt(tok.nextToken());
			if(!single_harvest)
				this.regrow_days = Integer.parseInt(tok.nextToken());
			else
				this.regrow_days = this.num_days;

			for(int i = 0; i < this.name.length(); i++){
				if(this.name.charAt(i) >= 'A' && this.name.charAt(i) <= 'Z'){
					this.first_upper = "" + this.name.charAt(i);
					break;
				}
			}

			this.income = this.num_patches * this.market_price * 9;
			this.expense = this.num_patches * this.cost_bag;
			this.initially_planted = false;
			this.already_harvested = false;
			this.days_planted = 0;

		}

	}

}