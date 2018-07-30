import java.util.ArrayList;
import java.io.*;

public class Driver {
	private ArrayList<String> strImplicits;
	private VaalUI ui;
	private Item baseItem;
	private Implicit prefImp;
	private int implicitilvl;

	public static void main(String[] args){
		new Driver().run(args);
	}

	public void run(String[] args) {
		ui = new VaalUI();
		javafx.application.Application.launch(VaalUI.class);
	}
	/**
	 * User data needed for reading the text file:
	 * Item level
	 * Base type
	 */
	public ArrayList<String> analyze(String basetype){
		strImplicits = new ArrayList<>();
		// make a new item based on the user input
		baseItem = new Item(basetype);

		try {
			// File reading
			BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().
					getResourceAsStream("ImplicitList.txt")));

			String line;
			String name = "";
			String effect = "";

			while ((line = reader.readLine()) != null){
				if (line.equals("$")) {
					line = reader.readLine();

					// splitting the line and grabbing data
					String[] info = line.split("\t");
					name = info[0];
					implicitilvl = Integer.parseInt(info[1]);
					effect = info[2];

				} else if (!line.equals(null) && !line.equals("$")){
					String[] tag = line.split(" ");
					int weight = Integer.parseInt(tag[1]);

					if (baseItem.getTags().contains(tag[0]) && weight != 0) {
						baseItem.addRoll(name, effect, weight, implicitilvl);
					}
				} // end else-if
				if (line == null)
					break;
			} // end of while
			/**
			 * Iterate through each line of the text file, looking for the $ delimiter.
			 * After we find the & character we need to check the mod's tags to see if it applies.
			 *
			 * if yes, add the weighting to the int variable that represents the pool of mods
			 * probably would need an arrayList to store and print out the possible variables
			 *
			 */
		} catch (FileNotFoundException e) {
			e.getMessage();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// string representation of all the possible implicits to be used in combo box
		for (Implicit imp: baseItem.getPotRolls())
			strImplicits.add(imp.getEffect());
		return strImplicits;
	}

	public void setEcon(Double initVal, Double corrVal){

	}

	// sets the user's preffered implicit stat so we can compare later.
	public void setPrefImp(String input){
		Implicit temp;
		try {
			// File reading
			BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().
					getResourceAsStream("ImplicitList.txt")));

			String line;
			String name = "";
			String effect = "";
			int ilvl = 0;

			while ((line = reader.readLine()) != null){

				if (line.equals("$")) {
					line = reader.readLine();

					// splitting the line and grabbing data
					String[] info = line.split("\t");

					if (info[2].equals(input)) { // if we found our preferred implicit in the text file
						name = info[0];
						ilvl = Integer.parseInt(info[1]);
						effect = info[2];

						while (!line.equals("$")) { // now we search for the weighting so we can make prefImp
							line = reader.readLine();

							String[] tag = line.split(" ");
							int weight = Integer.parseInt(tag[1]);

							if (baseItem.getTags().contains(tag[0]) && weight != 0) {
								prefImp = new Implicit(name, effect, weight, ilvl);
								return;
							}
						}
					}
				}

				if (line == null)
					break;
			} // end of while
		} catch (FileNotFoundException e) {
			e.getMessage();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Implicit getPrefImp(){
		return prefImp;
	}

	public Item getBaseItem(){
		return baseItem;
	}
}