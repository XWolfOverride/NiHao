package nihao.util.cursor;

import java.util.Iterator;

public class IteratorCursor extends Cursor {

	private Iterator<?> i;

	public IteratorCursor(Iterator<?> i) {
		this.i = i;
	}

	@Override
	public boolean canGoFirst() {
		return false;
	}

	@Override
	public boolean canGetLength() {
		return false;
	}

	@Override
	public Object next() {
		return i.next();
	}

	@Override
	public boolean hasMoreElements() {
		return i.hasNext();
	}

	@Override
	public void goFirst() {
	}

	@Override
	public int getLength() {
		return -1;
	}
}
