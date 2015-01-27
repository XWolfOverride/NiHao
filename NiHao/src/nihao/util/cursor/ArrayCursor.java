package nihao.util.cursor;

import java.lang.reflect.Array;

public class ArrayCursor extends Cursor {
	
	private int index = 0;
	private Object array;
	private int length;

	public ArrayCursor(Object array) {
		this.array = array;
		length = Array.getLength(array);
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
		return Array.get(array, index++);
	}

	@Override
	public boolean hasMoreElements() {
		return index < length;
	}

	@Override
	public void goFirst() {
		index = 0;
	}

	@Override
	public int getLength() {
		return length;
	}
}
