package nihao.context;

import java.util.ArrayList;

import nihao.Page;
import nihao.types.PageSecuityType;

/**
 * Una definición de página
 * @author XWolf Override
 *
 */
public class StructureItemPage extends StructureItem {
	String url;
	private ArrayList<String> groupsA = new ArrayList<String>();
	private ArrayList<String> groupsD = new ArrayList<String>();
	private String[] grA, grD;
	private String workset;
	private PageSecuityType security = PageSecuityType.BYGROUP;
	boolean regex;

	// private StructureItem worksetStruct;

	public StructureItemPage(Context owner, String id) {
		super(owner, id);
		url = id;
	}

	@Override
	public Object get() {
		Page result = new Page(url, security);
		// if (workset != null)
		// if (worksetStruct == null)
		// result.setWorksetStructure(worksetStruct);
		// else
		// result.setWorksetSingleton(owner.get(workset));
		result.setGroupAllow(grA);
		result.setGroupDeny(grD);
		result.setWorkset(workset);
		result.setRegex(regex);
		return result;
	}

	@Override
	public Class<?> getStructureClass() {
		return Page.class;
	}

	@Override
	public boolean canPreinstantiate() {
		return true;
	}

	@Override
	public void commit() {
		if (groupsA.size() > 0)
			grA = groupsA.toArray(new String[groupsA.size()]);
		if (groupsD.size() > 0)
			grD = groupsD.toArray(new String[groupsD.size()]);
		groupsA = null;
		groupsD = null;
		// if (workset != null) {
		// worksetStruct = owner.getStructure(workset);
		// if (worksetStruct == null)
		// throw new ContextParseException("Page '" + url + "' workset '" +
		// workset + "' not found");
		// if (!(worksetStruct instanceof StructureItemWorkset) &&
		// !(worksetStruct instanceof StructureItemObject))
		// throw new ContextParseException("Page '" + url + "' workset '" +
		// workset + "' not defined as a workset or object");
		// if (worksetStruct.isSingleton())
		// worksetStruct = null;
		// }
	}

	@Override
	public String toString() {
		return "page " + url;
	}

	public void addGroup(String name, boolean allow) {
		if (allow)
			groupsA.add(name.substring(1));
		else
			groupsD.add(name);
	}

	public void setWorkset(String workset) {
		this.workset = workset;
	}

	public void setPublicPage() {
		this.security = PageSecuityType.PUBLIC;
	}

	public void setPrivatePage() {
		this.security = PageSecuityType.PRIVATE;
	}
	
	/**
	 * Treat URL as a regex syntax
	 * @return
	 */
	public boolean isRegex(){
		return regex;
	}
}
