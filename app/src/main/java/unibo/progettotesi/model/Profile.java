package unibo.progettotesi.model;

public class Profile {
	private Place start;
	private Place end;
	private String name;

	public Profile(Place start, Place end, String name) {
		this.start = start;
		this.end = end;
		this.name = name;
	}

	public Place getStart() {
		return start;
	}

	public void setStart(Place start) {
		this.start = start;
	}

	public Place getEnd() {
		return end;
	}

	public void setEnd(Place end) {
		this.end = end;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
