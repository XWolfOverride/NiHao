package nihao.context;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map.Entry;

import nihao.NiHao;
import nihao.NiHaoException;
import nihao.Page;
import nihao.context.StructureItemValue.Type;
import nihao.db.Changeset;
import nihao.db.Query;
import nihao.tokenizer.QToken;
import nihao.tokenizer.QTokenType;
import nihao.tokenizer.QTokenizer;
import nihao.util.Conversor;
import nihao.util.QDB;
import nihao.util.Resources;

public abstract class Context {
	private static final String[] CONTEXTPARSE_ELEMENTS = { "object", "val", "map", "list", "array", "jndi", "provider", "include", "query", "page", "workset", "changeset" };

	ArrayList<StructureItem> structure = new ArrayList<StructureItem>();
	ArrayList<StructureItem> fullstructure = new ArrayList<StructureItem>();
	private HashMap<String, Object> singletons;
	private HashMap<String, HashMap<String, Query>> queries = new HashMap<String, HashMap<String, Query>>();
	private HashMap<String, Page> pages = new HashMap<String, Page>();
	private ArrayList<Page> regexPages = new ArrayList<Page>();
	private StructureItemNull itemNull = new StructureItemNull(this);
	private ArrayList<Changeset> changesets = new ArrayList<Changeset>();

	/**
	 * Realiza el parsing de la estructura de contexto
	 * 
	 * @param s
	 */
	protected void parse(String s) {
		QTokenizer tkn = new QTokenizer(s);
		StructureItem i;
		while ((i = readNextElement(tkn)) != null) {
			if (i.id == null)
				throw new ContextParseException("Can't have anonymous element on root for " + i.getClass().getName());
			StructureItem existant = getStructure(i.id);
			boolean duplicate = existant != null;
			if (duplicate) {
				if (i instanceof StructureItemQuery && existant instanceof StructureItemQuery) {
					StructureItemQuery iq = (StructureItemQuery) i;
					StructureItemQuery iq2 = (StructureItemQuery) existant;
					if (NiHao.equals(iq.engine, iq2.engine))
						throw new ContextParseException("Duplicate query name " + i.id + " for engine " + Conversor.nvl(iq.engine, "any"));
					else
						duplicate = false;
				} else if (i instanceof StructureItemChangeset && existant instanceof StructureItemChangeset) {
					StructureItemChangeset iq = (StructureItemChangeset) i;
					StructureItemChangeset iq2 = (StructureItemChangeset) existant;
					if (NiHao.equals(iq.engine, iq2.engine))
						throw new ContextParseException("Duplicate changeset name " + i.id + " for engine " + Conversor.nvl(iq.engine, "any"));
					else
						duplicate = false;
				}
			}
			if (duplicate)
				throw new ContextParseException("Duplicate element name " + i.id);
			structure.add(i);
		}
	}

	private int search(String element, String[] range) {
		for (int i = 0; i < range.length; i++)
			if (element.equals(range[i]))
				return i;
		return -1;
	}

	private StructureItem readNextElement(QTokenizer tkn) {
		QToken t = tkn.next();
		if (t == null)
			return null;
		if ("multi".equalsIgnoreCase(t.getValue())) {
			StructureItem i = readNextElement(tkn);
			i.singleton = false;
			return i;
		}
		if ("null".equals(t.getValue()))
			return itemNull;
		if (t.getType() == QTokenType.LITERAL)
			return new StructureItemValue(this, null, t.getValue(), Type.STRING);
		if (t.getType() == QTokenType.NUMBER)
			return new StructureItemValue(this, null, t.getValue(), Type.NUMBER);
		if ("@".equals(t.getValue()))
			return new StructureItemReference(this, null, tkn.next().getValue());
		if ("true".equalsIgnoreCase(t.getValue()))
			return new StructureItemValue(this, null, "true", Type.BOOL);
		if ("false".equalsIgnoreCase(t.getValue()))
			return new StructureItemValue(this, null, "false", Type.BOOL);
		if (t.getType() != QTokenType.WORD)
			throw new RuntimeException("Error de configuración de contexto: " + t.getValue());
		String etype = t.getValue().toLowerCase();
		switch (search(etype, CONTEXTPARSE_ELEMENTS)) {
		case 0:
			return readObject(tkn);
		case 1:
			return readValue(tkn);
		case 2:
			return readMap(tkn);
		case 3:
			return readList(tkn);
		case 4:
			return readArray(tkn);
		case 5:
			return readJndi(tkn);
		case 6:
			return readProvider(tkn);
		case 7: {
			readInclude(tkn);
			return readNextElement(tkn);
		}
		case 8:
			return readQuery(tkn);
		case 9:
			return readPage(tkn);
		case 10:
			return readWorkset(tkn);
		case 11:
			return readChangeset(tkn);
		default:
			throw new ContextParseException("Unknown type " + etype);
		}
	}

	private StructureItem readJndi(QTokenizer tkn) {
		throw new ContextParseException("Unimplemented");
	}

	private StructureItem readArray(QTokenizer tkn) {
		QToken t = tkn.next();
		String id = t.getValue().toLowerCase();
		if ("of".equals(t.getValue()))
			id = null;
		else if (!"of".equals(tkn.next().getValue()))
			throw new ContextParseException("'of' mistach");
		String className = elementReadClassName(tkn);
		ArrayList<StructureItem> list = new ArrayList<StructureItem>();
		while (!"}".equals(tkn.patrol().getValue())) {
			list.add(readNextElement(tkn));
		}
		tkn.next();
		return new StructureItemArray(this, id, className, list.toArray(new StructureItem[list.size()]));
	}

	private StructureItem readList(QTokenizer tkn) {
		QToken t = tkn.next();
		String id = t.getValue().toLowerCase();
		StructureItemList result;
		if (!"{".equals(t.getValue())) {
			result = new StructureItemList(this, id);
			if (!"{".equals(tkn.next().getValue()))
				throw new ContextParseException("'{' mistach");
		} else
			result = new StructureItemList(this, null);
		while (!"}".equals(tkn.patrol().getValue()))
			result.add(readNextElement(tkn));
		tkn.next();
		return result;
	}

	private StructureItem readMap(QTokenizer tkn) {
		QToken t = tkn.next();
		String id = t.getValue().toLowerCase();
		StructureItemMap result;
		if (!"{".equals(t.getValue())) {
			if (t.getType() != QTokenType.WORD)
				throw new ContextParseException("Identifier error: " + t.getValue());
			result = new StructureItemMap(this, id);
			if (!"{".equals(tkn.next().getValue()))
				throw new ContextParseException("'{' mistach");
		} else
			result = new StructureItemMap(this, null);
		t = tkn.next();
		while (!"}".equals(t.getValue())) {
			if (!"{".equals(t.getValue()))
				throw new ContextParseException("'{' mistach");
			StructureItem k = readNextElement(tkn);
			if (!",".equals(tkn.next().getValue()))
				throw new ContextParseException("',' mistach");
			StructureItem v = readNextElement(tkn);
			if (!"}".equals(tkn.next().getValue()))
				throw new ContextParseException("'}' mistach");
			result.putMap(k, v);
			t = tkn.next();
		}
		return result;
	}

	private StructureItem readObject(QTokenizer tkn) {
		QToken t = tkn.next();
		if (t.getType() != QTokenType.WORD)
			throw new ContextParseException("Identifier error: " + t.getValue());
		String id = t.getValue().toLowerCase();
		StructureItemObject result;
		if (!"class".equals(id)) {
			result = new StructureItemObject(this, id);
			t = tkn.next();
			if (!t.getValue().equals("class"))
				throw new ContextParseException("'class' keyword excepted, found " + t.getValue());
		} else
			result = new StructureItemObject(this, null);
		String className = elementReadClassName(tkn);
		result.setObjectClass(className);
		t = tkn.next();
		if ("(".equals(t.getValue())) {
			do {
				result.addConstructor(readNextElement(tkn));
				t = tkn.next();
				if (")".equals(t.getValue()))
					break;
				else if (!",".equals(t.getValue()))
					throw new ContextParseException("Constructor object separator mistach");
			} while (true);
			t = tkn.next();
		}
		while (!"}".equals(t.getValue())) {
			if (t.getType() != QTokenType.WORD)
				throw new ContextParseException("Unssuported " + t.getValue());
			String paramname = t.getValue();
			t = tkn.next();
			if (!"=".equals(t.getValue()))
				throw new ContextParseException("Equals mistach");
			result.setParam(paramname, readNextElement(tkn));
			t = tkn.next();
			if (!";".equals(t.getValue()))
				throw new ContextParseException("Missing ; for object '" + result.id + "' of class '" + className + "' in field '" + paramname + "' definition");
			t = tkn.next();
		}
		return result;
	}

	private StructureItem readValue(QTokenizer tkn) {
		QToken t = tkn.next();
		if (t == null)
			throw new ContextParseException("Unexpected end of script");
		if (t.getType() == QTokenType.LITERAL)
			return new StructureItemValue(this, null, t.getValue(), Type.STRING);
		if (t.getType() == QTokenType.NUMBER)
			return new StructureItemValue(this, null, t.getValue(), Type.NUMBER);
		if ("true".equalsIgnoreCase(t.getValue()))
			return new StructureItemValue(this, null, "true", Type.BOOL);
		if ("false".equalsIgnoreCase(t.getValue()))
			return new StructureItemValue(this, null, "false", Type.BOOL);
		String id = t.getValue().toLowerCase();
		t = tkn.next();
		if (!"=".equals(t.getValue()))
			throw new ContextParseException("Equals mistach");
		t = tkn.next();
		StructureItemValue result;
		if (t.getType() == QTokenType.LITERAL)
			result = new StructureItemValue(this, id, t.getValue(), Type.STRING);
		else if (t.getType() == QTokenType.NUMBER)
			result = new StructureItemValue(this, id, t.getValue(), Type.NUMBER);
		else if ("true".equalsIgnoreCase(t.getValue()))
			result = new StructureItemValue(this, null, "true", Type.BOOL);
		else if ("false".equalsIgnoreCase(t.getValue()))
			result = new StructureItemValue(this, null, "false", Type.BOOL);
		else
			throw new ContextParseException("Syntax Error");
		if (!";".equals(tkn.next().getValue()))
			throw new ContextParseException("; Mistach");
		return result;
	}

	private StructureItem readProvider(QTokenizer tkn) {
		QToken t = tkn.next();
		if (t == null)
			throw new ContextParseException("Unexpected end of script");
		if (t.getType() != QTokenType.WORD)
			throw new ContextParseException("Identifier type error: " + t.getValue());
		StructureItemProvider result = new StructureItemProvider(this, t.getValue());
		if (!"default".equals(tkn.next().getValue()))
			throw new ContextParseException("'default' mistach");
		t = tkn.next();
		if (t.getType() != QTokenType.WORD)
			throw new ContextParseException("default name type error, can't be: " + t.getValue());
		result.setDefault(t.getValue());
		if (!"{".equals(tkn.next().getValue()))
			throw new ContextParseException("'{' mistach");
		ArrayList<String> list = new ArrayList<String>();
		while (!"}".equals(tkn.patrol().getValue())) {
			t = tkn.next();
			if (t.getType() != QTokenType.WORD)
				throw new ContextParseException("alternative name type error, can't be: " + t.getValue());
			list.add(t.getValue());
		}
		tkn.next(); // } final
		result.setAuxiliar(list.toArray(new String[list.size()]));
		return result;
	}

	private void readInclude(QTokenizer tkn) {
		QToken t = tkn.next();
		if (t.getType() != QTokenType.LITERAL)
			throw new ContextParseException("filename literal mistach");
		String path = t.getValue();
		t = tkn.next();
		if (!";".equals(t.getValue()))
			throw new ContextParseException("Missing ; for include '" + path + "' definition");

		InputStream input = Resources.getResourceAsStream(path);
		if (input == null)
			input = Resources.getResourceAsStream("META-INF/" + path);
		if (input == null)
			throw new NiHaoException("Can't open context file: " + path);
		String includestr = Conversor.readToString(input);
		parse(includestr);
	}

	private StructureItem readQuery(QTokenizer tkn) {
		QToken t = tkn.next();
		if (t == null)
			throw new ContextParseException("Unexpected end of script");
		if (t.getType() != QTokenType.WORD)
			throw new ContextParseException("Invalid query name");
		String name = t.getValue();
		if ("engine".equals(name))
			throw new ContextParseException("Query name mistach");
		if ("type".equals(name))
			throw new ContextParseException("Query name mistach");
		StructureItemQuery result = new StructureItemQuery(this, name);
		t = tkn.next();
		if ("engine".equals(t.getValue())) {
			t = tkn.next();
			if (t.getType() != QTokenType.WORD && t.getType() != QTokenType.LITERAL)
				throw new ContextParseException("engine name type error");
			if ("any".equals(t.getValue()))
				result.engine = null;
			else
				result.engine = t.getValue();
			t = tkn.next();
		}
		if ("type".equals(t.getValue()))
			result.returnTypeName = elementReadClassName(tkn);
		else if (!"{".equals(t.getValue()))
			throw new ContextParseException("{ mistach");
		StringBuilder query = new StringBuilder();
		ArrayList<String> names = new ArrayList<String>();
		while (!tkn.patrol().equals(QTokenType.SYMBOL, "}")) {
			QToken last = t;
			t = tkn.next();
			if (query.length() != 0 && (t.getType() != QTokenType.SYMBOL && last.getType() != QTokenType.SYMBOL))
				query.append(' ');
			if ("#".equals(t.getValue())) {
				query.append('?');
				StringBuilder inner = new StringBuilder();
				while (!"#".equals(tkn.patrol().getValue()))
					inner.append(tkn.next().getValue());
				tkn.next();
				names.add(inner.toString());
			} else {
				if (t.getType() == QTokenType.LITERAL)
					query.append("'");
				query.append(t.getValue());
				if (t.getType() == QTokenType.LITERAL)
					query.append("'");
			}
		}
		tkn.next();
		result.query = query.toString();
		result.names = names.toArray(new String[names.size()]);
		return result;
	}

	private StructureItem readPage(QTokenizer tkn) {
		QToken t = tkn.next();

		if (t == null)
			throw new ContextParseException("Unexpected end of script");
		boolean regex = false;
		if (t.getType() == QTokenType.SYMBOL && "~".equals(t.getValue())) {
			regex = true;
			t = tkn.next();
		}
		if (t.getType() != QTokenType.LITERAL)
			throw new ContextParseException("Invalid page url");
		String name = t.getValue();
		if ("for".equals(name))
			throw new ContextParseException("Page url mistach");
		if ("public".equals(name))
			throw new ContextParseException("Page url mistach");
		if ("private".equals(name))
			throw new ContextParseException("Page url mistach");
		if (!regex && !name.startsWith("/"))
			throw new ContextParseException("Page url must start with \"/\"");
		StructureItemPage result = new StructureItemPage(this, name);
		result.regex = regex;
		t = tkn.next();
		if ("public".equals(t.getValue())) {
			result.setPublicPage();
		} else if ("private".equals(t.getValue())) {
			result.setPrivatePage();
		} else {
			if (!"for".equals(t.getValue()))
				throw new ContextParseException("Page groups definition mistach");
			boolean done = false;
			do {
				t = tkn.next();
				if ("!".equals(t.getValue())) {
					t = tkn.next();
					if (t.getType() != QTokenType.WORD)
						throw new ContextParseException("Page group name error");
					result.addGroup(t.getValue(), false);
				} else {
					if (t.getType() != QTokenType.WORD)
						throw new ContextParseException("Page group name error");
					result.addGroup(t.getValue(), true);
				}
				if (",".equals(tkn.patrol().getValue()))
					tkn.next();
				else
					done = true;
			} while (!done);
		}
		if ("workset".equals(tkn.patrol().getValue())) {
			tkn.next();
			t = tkn.next();
			if (t.getType() != QTokenType.WORD)
				throw new ContextParseException("Workset name error");
			result.setWorkset(t.getValue());
		}
		t = tkn.next();
		if (!";".equals(t.getValue()))
			throw new ContextParseException("Missing ; for page '" + result.url + "' definition");
		return result;
	}

	private StructureItem readWorkset(QTokenizer tkn) {
		QToken t = tkn.next();
		if (t == null)
			throw new ContextParseException("Unexpected end of script");
		if (t.getType() != QTokenType.WORD)
			throw new ContextParseException("Invalid workset name");
		String name = t.getValue();
		if ("class".equals(name))
			throw new ContextParseException("Workset name mistach");
		StructureItemWorkset result = new StructureItemWorkset(this, name);
		t = tkn.next();
		if (!"class".equals(t.getValue()))
			throw new ContextParseException("class mistach");
		String className = elementReadClassName(tkn);
		result.setClassName(className);
		return result;
	}

	private StructureItem readChangeset(QTokenizer tkn) {
		QToken t = tkn.next();
		if (t == null)
			throw new ContextParseException("Unexpected end of script");
		if (t.getType() != QTokenType.LITERAL)
			throw new ContextParseException("Invalid changeset id literal");
		if (t.getValue().length() > 128)
			throw new ContextParseException("Changeset id too long :'" + t.getValue() + "'");
		StructureItemChangeset result = new StructureItemChangeset(this, t.getValue());
		t = tkn.next();
		if ("engine".equals(t.getValue())) {
			t = tkn.next();
			if (t.getType() != QTokenType.WORD && t.getType() != QTokenType.LITERAL)
				throw new ContextParseException("Engine name type error");
			if ("any".equals(t.getValue()))
				result.engine = null;
			else
				result.engine = t.getValue();
			t = tkn.next();
		}
		if (!"author".equals(t.getValue()))
			throw new ContextParseException("Missing author");
		t = tkn.next();
		if (t.getType() != QTokenType.LITERAL)
			throw new ContextParseException("Author name type error");
		result.author = t.getValue();
		t = tkn.next();
		if ("execute".equals(t.getValue())) {
			t = tkn.next();
			try {
				result.executeMode = Changeset.ExecutionMode.valueOf(t.getValue().toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new ContextParseException("Changeset execution mode not supported");
			}
		}
		if (!"{".equals(t.getValue()))
			throw new ContextParseException("{ mistach");
		StringBuilder query = new StringBuilder();
		while (!tkn.patrol().equals(QTokenType.SYMBOL, "}")) {
			QToken last = t;
			t = tkn.next();
			if (t.equals(QTokenType.SYMBOL, ";")) {
				result.addQuery(query.toString());
				query.setLength(0);
			} else {
				if (query.length() != 0 && (t.getType() != QTokenType.SYMBOL && last.getType() != QTokenType.SYMBOL))
					query.append(' ');
				if (t.getType() == QTokenType.LITERAL)
					query.append("'");
				query.append(t.getValue());
				if (t.getType() == QTokenType.LITERAL)
					query.append("'");
			}
		}
		if (query.length() > 0)
			result.addQuery(query.toString());
		tkn.next();
		return result;
	}

	private String elementReadClassName(QTokenizer tkn) {
		String className = "";
		do {
			QToken t = tkn.next();
			if (t.getType() != QTokenType.WORD)
				throw new ContextParseException("Class name error: " + t.getValue());
			className += t.getValue();
			t = tkn.next();
			if (t.getType() != QTokenType.SYMBOL)
				throw new ContextParseException("Class name separator error: " + t.getValue());
			if (".".equals(t.getValue()))
				className += ".";
			else if ("{".equals(t.getValue()) || ";".equals(t.getValue()))
				break;
			else
				throw new ContextParseException("Class name separator error: " + t.getValue());
		} while (true);
		return className;
	}

	public Object get(String name) {
		if (name == null)
			return null;
		name = name.toLowerCase();
		if (singletons.containsKey(name))
			return singletons.get(name);
		for (StructureItem i : structure)
			if (name.equals(i.id)) {
				Object result = i.get();
				if (i.canPreinstantiate()) {
					singletons.put(i.id, result);
					structure.remove(i);
				}
				return result;
			}
		return null;
	}

	/**
	 * Retorna la query con el nombre especificado que mejor concuerde con el
	 * engine
	 * 
	 * @param name
	 * @param engine
	 * @return
	 */
	public Query getQuery(String name, String engine) {
		if (name == null)
			throw new NiHaoException("Query name can't be null");
		name = name.toLowerCase();
		HashMap<String, Query> engines = queries.get(name);
		if (engines == null)
			throw new NiHaoException("Query '" + name + "' not found");
		Query result = engines.get(engine);
		if (result == null && engine != null)
			result = engines.get(null);
		if (result == null)
			result = engines.values().iterator().next();
		return result;
	}

	/**
	 * Returns and discard all changesets
	 * 
	 * @return
	 */
	public ArrayList<Changeset> getChangesets(String engine) {
		QDB q = new QDB();
		HashSet<String> ids = new LinkedHashSet<String>();
		for (Changeset ch : changesets) {
			ids.add(ch.getId());
			q.$(ch.getId()).$(ch.getEngine()).set(ch);
		}
		ArrayList<Changeset> result = new ArrayList<Changeset>();
		for (String id : ids) {
			QDB qchange = q.$(id);
			Changeset ch = qchange.$(engine).get();
			if (ch == null)
				ch = qchange.$(null).get();
			if (ch == null)
				ch = qchange.iterator().next().get();
			result.add(ch);
		}
		return result;
	}

	/**
	 * Return true if any chagneset was defined (also if defined the bootstrap
	 * changeset)
	 * 
	 * @return
	 */
	public boolean haveChangesets() {
		return !changesets.isEmpty();
	}

	/**
	 * Retorna la página diseñada para una URL determinada
	 * 
	 * @param url
	 *            String
	 * @return Page
	 */
	public Page getPage(String url) {
		try {
			Page page = pages.get(url);
			if (page != null)
				return page;
			for (Page p : regexPages)
				if (url.matches(p.getUrl()))
					return p;
		} catch (Throwable t) {
		}
		return null;
	}

	/**
	 * Return the struncture info
	 * 
	 * @param name
	 * @return
	 */
	public StructureItem getStructure(String name) {
		for (StructureItem i : structure)
			if (name.equals(i.id))
				return i;
		return null;
	}

	/**
	 * Retorna true si el contexto no tiene nada definido
	 * 
	 * @return
	 */
	public boolean isEmprty() {
		return structure.size() == 0 && singletons.size() == 0;
	}

	/**
	 * Realiza el commit de sus estructuras y preinstancia singletons
	 */
	public void commit() {
		singletons = new HashMap<String, Object>();
		for (StructureItem i : fullstructure)
			i.commit();
		ArrayList<StructureItem> toKill = new ArrayList<StructureItem>();
		for (StructureItem i : structure)
			if (i instanceof StructureItemQuery) {
				Query q = (Query) i.get();
				HashMap<String, Query> engines = queries.get(q.getId());
				if (engines == null) {
					engines = new HashMap<String, Query>();
					queries.put(q.getId(), engines);
				}
				if (engines.get(q.getEngine()) != null)
					throw new NiHaoException("Query '" + q.getId() + " duplicated for engine " + Conversor.nvl(q.getEngine(), "any"));
				engines.put(q.getEngine(), q);
				toKill.add(i);
			} else if (i instanceof StructureItemPage) {
				Page p = (Page) i.get();
				if (p.isRegex())
					regexPages.add(p);
				else
					pages.put(p.getUrl(), p);
				toKill.add(i);
			} else if (i instanceof StructureItemChangeset) {
				changesets.add((Changeset) i.get());
				toKill.add(i);
			} else if (i.canPreinstantiate()) {
				singletons.put(i.id, i.get());
				toKill.add(i);
			}
		for (StructureItem i : toKill)
			structure.remove(i);
		fullstructure = null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (StructureItem i : structure) {
			sb.append(i.toString());
			sb.append('\n');
		}
		if (singletons != null)
			for (String k : singletons.keySet()) {
				sb.append(k);
				sb.append(" <singleton>\n");
			}
		for (HashMap<String, Query> hm : queries.values())
			for (Entry<String, Query> e : hm.entrySet()) {
				sb.append(e.getValue().getId());
				sb.append(" <query (");
				sb.append(e.getKey() == null ? "any" : e.getKey());
				sb.append(")>\n");
			}
		return sb.toString();
	}
}
