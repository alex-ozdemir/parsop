package parsop.util;

import java.util.List;

public class ListStream<T> {

	List<T> list;
	int nextIndex;
	
	public ListStream(List<T> list) {
		this.nextIndex = 0;
		this.list = list;
	}
	
	public T peek() {
		return this.list.get(this.nextIndex);
	}
	
	public boolean hasNext() {
		return this.nextIndex < this.list.size();
	}
	
	public T next() {
		return this.list.get(this.nextIndex++);
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer(" ->");
		for (int i = nextIndex; i < list.size(); i++)
			result.append("  ").append(list.get(i));
		return result.toString();
	}
}
