package parsop.util;

public class Pair<A, B> {
	
	public A first;
	public B second;
	
	public Pair(A first, B second) {
		this.first = first;
		this.second = second;
	}
	
	@SuppressWarnings("unchecked")
	public boolean equals(Object other) {
		Pair<A,B> otherPair;
		if (other instanceof Pair)
			 otherPair = (Pair<A,B>) other;
		else
			return false;
		return this.first.equals(otherPair.first) && this.second.equals(otherPair.second);
	}
	
	public int hashCode() {
		return first.hashCode() ^ second.hashCode();
	}
}
