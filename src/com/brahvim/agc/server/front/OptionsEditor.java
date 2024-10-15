package com.brahvim.agc.server.front;

import java.util.IdentityHashMap;

public enum OptionsEditor {

	ADD(),
	REMOVE(),
	/* */ ;

	// region Class stuff.
	public final String LABEL;
	public final String TOOLTIP;

	public static final OptionsEditor[] ORDER_UI = new OptionsEditor[] { // NOSONAR! Speed!

			ADD,
			REMOVE,

	};

	private static final IdentityHashMap<String, OptionsEditor> LABEL_ENUM_MAP = new IdentityHashMap<>();

	static {
		// Could be faster in order (in memory access terms!), right?!:
		for (final var o : OptionsEditor.ORDER_UI)
			OptionsEditor.LABEL_ENUM_MAP.put(o.LABEL, o);
	}

	private OptionsEditor() {
		final String myName = this.name();
		this.LABEL = App.STRINGS.getString("ListEditorOptions", myName);
		this.TOOLTIP = App.STRINGS.getString("TooltipsEditorListOptions", myName);
	}

	public static OptionsEditor valueOfLabel(final String p_label) {
		return OptionsEditor.LABEL_ENUM_MAP.get(p_label);
	}
	// endregion

}
