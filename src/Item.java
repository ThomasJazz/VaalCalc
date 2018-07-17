import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class Item {
	private ArrayList<Implicit> potRolls;
	private ArrayList<String> tags; // the tags that apply to the given type of weapon
	private String baseType = "";
	private int poolSize = 0;
	private int ilvl;

	Item(String baseType, int ilvl) {
		tags = new ArrayList<>();
		potRolls = new ArrayList<>();
		this.ilvl = ilvl;
		baseType.toLowerCase();
		this.baseType = baseType;
		setTags(baseType);

	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public ArrayList<Implicit> getPotRolls() {
		return potRolls;
	}

	private ArrayList<String> setTags(String baseType) {
        /*
        if cases for each item type
        */
        baseType = baseType.toLowerCase();
		if (baseType.equals("amulet")) {
			tags.addAll(Arrays.asList("amulet", "default"));
		} else if (baseType.equals("body armour")) {
			tags.addAll(Arrays.asList("body_armour", "default"));
		} else if (baseType.equals("boots")) {
			tags.addAll(Arrays.asList("boots", "default"));
		} else if (baseType.equals("bow")) {
			tags.addAll(Arrays.asList("bow", "two_hand_weapon", "weapon", "default"));
		} else if (baseType.equals("belt")) {
			tags.addAll(Arrays.asList("belt", "default"));
		} else if (baseType.equals("claw")) {
			tags.addAll(Arrays.asList("claw", "weapon", "one_hand_weapon", "default"));
		} else if (baseType.equals("dagger")) {
			tags.addAll(Arrays.asList("dagger", "weapon", "one_hand_weapon", "default"));
		} else if (baseType.equals("fishing rod")) {
			tags.add("fishing_rod");
		} else if (baseType.equals("gloves")) {
			tags.addAll(Arrays.asList("gloves", "default"));
		} else if (baseType.equals("helmet")) {
			tags.addAll(Arrays.asList("helmet", "default"));
		} else if (baseType.equals("shield")) {
			tags.addAll(Arrays.asList("shield", "default"));
		} else if (baseType.equals("one hand axe")) {
			tags.addAll(Arrays.asList("axe", "weapon", "one_hand_weapon", "default"));
		} else if (baseType.equals("one hand mace")) {
			tags.addAll(Arrays.asList("mace", "weapon", "one_hand_weapon", "default"));
		} else if (baseType.equals("one hand sword")) {
			tags.addAll(Arrays.asList("sword", "weapon", "one_hand_weapon", "default"));
		} else if (baseType.equals("jewel")) {
			tags.add("jewel");
		} else if (baseType.equals("quiver")) {
			tags.addAll(Arrays.asList("quiver", "default"));
		} else if (baseType.equals("ring")) {
			tags.addAll(Arrays.asList("ring", "default"));
		} else if (baseType.equals("wand")) {
			tags.addAll(Arrays.asList("wand", "weapon", "one_hand_weapon", "default"));
		} else if (baseType.equals("sceptre")) {
			tags.addAll(Arrays.asList("sceptre", "weapon", "one_hand_weapon", "no_attack_mods", "default"));
		} else if (baseType.equals("staff")) {
			tags.addAll(Arrays.asList("staff", "weapon", "two_hand_weapon", "default"));
		} else if (baseType.equals("two hand axe")) {
			tags.addAll(Arrays.asList("axe", "weapon", "two_hand_weapon", "default"));
		} else if (baseType.equals("two hand mace")) {
			tags.addAll(Arrays.asList("mace", "weapon", "two_hand_weapon", "default"));
		} else if (baseType.equals("two hand sword")) {
			tags.addAll(Arrays.asList("sword", "weapon", "two_hand_weapon", "default"));
		} else {
			System.out.println("Input Error");
		}

		return tags;
	}

	public void addRoll(String name, String effect, int weight) {
		potRolls.add(new Implicit(name, effect, weight));
		poolSize += weight;
	}

	/**
	 * This method checks to see if the incoming tag is contained in our pre-defined array of
	 * item tags. If it IS, we return true, if it is NOT, we return false.
	 */
	public boolean contains(String inputTag) {
		for (int i = 0; i < tags.size(); i++) {
			if (inputTag.equals(tags.get(i)))
				return true; //if the incoming ID matches ANY of our classes ID's, we return true
		}
		return false; // if we get to the end of our array without any matches
	}
	public double getChance(String name) {
		for (Implicit curr : potRolls) {
			if (curr.equals(name))
				return (double)curr.getWeight()/(double)poolSize;
		}
		return 0;
	}

	public String toString() {
		String output = "";
		for (int i = 0; i < potRolls.size(); i++) {
			DecimalFormat df = new DecimalFormat("##.###%");
			double percent = (potRolls.get(i).getWeight()/(double)poolSize)*(double)1/6; // converting the weighted values to a percent
			output += potRolls.get(i).toString() + "\n\tChance: " + df.format(percent) + "\n\tWeight: " + potRolls.get(i).getWeight();
			output += "\n";
		}
		return output;
	}
}