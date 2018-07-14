public class Implicit {
	private String name;
	private String effect;
	private int weight;

	Implicit(String name, String effect, int weight){
		this.name = name;
		this.effect = effect;
		this.weight = weight;
	}

	public String toString(){
		return effect;
	}
	public int getWeight(){
		return weight;
	}
	public boolean equals(String input){
		if (input.equals(effect))
			return true;
		else
			return false;
	}
}