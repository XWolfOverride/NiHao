package nihao.util.cursor;

import java.util.Iterator;
import java.util.List;

import nihao.NiHaoException;

public abstract class Cursor {
	public static Cursor getCursor(Object o) {
		if (o == null)
			return new ArrayCursor(new Object[0]);
		if (o.getClass().isArray())
			return new ArrayCursor(o);
		if (o instanceof List)
			return new ListCursor((List<?>) o);
		if (o instanceof Iterator)
			return new IteratorCursor((Iterator<?>) o);
		if (o instanceof Iterable)
			return new IteratorCursor(((Iterable<?>) o).iterator());
		throw new NiHaoException("Cursor unknown type " + o.getClass().getName());
	}
	
	public abstract boolean canGoFirst();
	public abstract boolean canGetLength();

	public abstract Object next();
	public abstract boolean hasMoreElements();

	public abstract void goFirst();
	public abstract int getLength();
}
