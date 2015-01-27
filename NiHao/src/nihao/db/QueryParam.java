package nihao.db;

import java.util.ArrayList;

import nihao.context.ContextParseException;
import nihao.tokenizer.QToken;
import nihao.tokenizer.QTokenType;
import nihao.tokenizer.QTokenizer;

public class QueryParam {
	private IQueryParamElement[] element;

	public static QueryParam[] parse(String[] queryparams) {
		QueryParam[] result = new QueryParam[queryparams.length];
		for (int i = 0; i < result.length; i++)
			result[i] = parse(queryparams[i]);
		return result;
	}

	public static QueryParam parse(String queryparam) {
		queryparam = queryparam.trim();
		QueryParam result = new QueryParam();
		if (queryparam.length() == 0)
			return result;
		ArrayList<IQueryParamElement> elements = new ArrayList<IQueryParamElement>();
		QTokenizer tkn = new QTokenizer(queryparam);
		QToken t;
		while ((t = tkn.next()) != null) {
			if ("[".equals(t.getValue())) {
				t = tkn.next();
				if (t == null)
					throw new ContextParseException("Query parameter format error: '" + queryparam + "'");
				if (t.getType() != QTokenType.NUMBER)
					throw new ContextParseException("Query parameter format error: '" + queryparam + "'");
				elements.add(new QueryParamElementArray(Integer.parseInt(t.getValue())));
				t = tkn.next();
				if (t == null)
					throw new ContextParseException("Query parameter format error: '" + queryparam + "'");
				if (!"]".equals(t.getValue()))
					throw new ContextParseException("Query parameter format error: '" + queryparam + "'");
			} else {
				if (t.getType() != QTokenType.WORD)
					throw new ContextParseException("Query parameter format error: '" + queryparam + "'");
				elements.add(new QueryParamElementNode(t.getValue()));
			}
			t = tkn.next();
			if (t != null)
				if (!".".equals(t.getValue()) && !"[".equals(t.getValue()))
					throw new ContextParseException("Query parameter format error: '" + queryparam + "'");
		}
		result.element = elements.toArray(new IQueryParamElement[elements.size()]);
		return result;
	}

	/**
	 * Evalua el objeto para extraer los datos según la infomración de
	 * parametros
	 * 
	 * @param o
	 *            Object
	 * @return Object
	 */
	public QueryParamValue evaluate(Object o) {
		if (element == null || element.length == 0)
			return new QueryParamValue(null, o);
		QueryParamValue val = null;
		for (IQueryParamElement p : element) {
			val = p.evaluate(o);
			o = val.value;
		}
		return val;
	}

	@Override
	public String toString() {
		if (element == null)
			return "?";
		StringBuilder sb = new StringBuilder();
		for (IQueryParamElement e : element) {
			if (sb.length() > 0)
				sb.append('.');
			sb.append(e.toString());
		}
		return sb.toString();
	}
}
