package nihao.util.cursor;

import java.util.List;

public class ListCursor extends Cursor {

	private int index = 0;
	private List<?> lst;

	public ListCursor(List<?> lst) {
		this.lst = lst;
	}

	@Override
	public boolean canGoFirst() {
		return true;
	}

	@Override
	public boolean canGetLength() {
		return true;
	}

	@Override
	public Object next() {
		return lst.get(index++);
	}

	@Override
	public boolean hasMoreElements() {
		return index < lst.size();
	}

	@Override
	public void goFirst() {
		index = 0;
	}

	@Override
	public int getLength() {
		return lst.size();
	}
}
