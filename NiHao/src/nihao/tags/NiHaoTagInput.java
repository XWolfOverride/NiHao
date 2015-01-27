package nihao.tags;

import nihao.tags.util.TagBuilder;

public class NiHaoTagInput extends NiHaoTag {
	private static final long serialVersionUID = 1L;

	@Override
	protected void setValue(TagBuilder tb) {
		tb.setAttr("value", getDisplayValue());
	}
}
