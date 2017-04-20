package projecttester;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

public class RawData {
	static ArrayList<String> values = new ArrayList<String>();
	static ArrayList<String> duplicate = new ArrayList<String>();

	startsocket sc = new startsocket();

	public void startFetch() {
				values = sc.startsocketwiki();
				fetchEdits(values);
	}

	public void fetchEdits(ArrayList<String> lst) {

		int counter = 0;
		for (int i = 0; i < lst.size(); i++) {
			final String exet = lst.get(i);
			duplicate.add(exet);
			boolean justOnce = moreThanOnce(duplicate, exet);
			if(!justOnce){
			try {
				BufferedWriter bw = new BufferedWriter(
						new FileWriter("/Users/poonamgohil/Desktop/Relevance.txt", true));

				String content = "" + exet + "\n";
				bw.write(content);
				bw.close();
				if (i == lst.size() - 1) {
					startFetch();
				}
				counter++; 
				if(counter == 5000){
					System.exit(0);
				}
			} catch (Exception e) {

				System.out.println(e.toString());
				}
			}
		}

	}

	public static boolean moreThanOnce(ArrayList<String> list, String searched) 
	{
	    int numCount = 0;

	    for (String thisString : list) {
	        if (thisString == searched) 
	        	numCount++;
	    }

	    return numCount > 1;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RawData data = new RawData();
		data.startFetch();

	}
}
