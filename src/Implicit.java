public class Implicit {
	private String name;
	private String effect;
	private int weight;
	private int ilvl;

	Implicit(String name, String effect, int weight, int ilvl) {
		this.name = name;
		this.effect = effect;
		this.weight = weight;
		this.ilvl = ilvl;
	}

	public String getEffect() {
		return effect;
	}

	public String getName() {
		return name;
	}

	public int getWeight() {
		return weight;
	}

	public int getIlvl() {
		return ilvl;
	}

	public boolean equals(String input) {
		if (input.equals(effect))
			return true;
		else
			return false;
	}

}