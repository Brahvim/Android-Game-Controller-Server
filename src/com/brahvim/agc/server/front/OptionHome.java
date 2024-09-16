package com.brahvim.agc.server.front;

import java.util.IdentityHashMap;

public enum OptionHome {

	ADD(),
	STOP(),
	REMOVE(),
	LAYOUT(),
	CONTROLS(),

	/* */ ;

	// region Class stuff.
	public final String LABEL;
	public final String TOOLTIP;

	private static final IdentityHashMap<String, OptionHome> LABEL_ENUM_MAP = new IdentityHashMap<>();
	private static final OptionHome[] ORDER_UI = new OptionHome[] {

			ADD,
			STOP,
			REMOVE,
			LAYOUT,
			CONTROLS,

	};

	static {
		// Could be faster in order (in memory access terms!), right?!:
		for (final var o : OptionHome.valuesOrdered())
			OptionHome.LABEL_ENUM_MAP.put(o.LABEL, o);
	}

	private OptionHome() {
		final String myName = this.name();
		this.TOOLTIP = App.STRINGS.getString("Tooltips", myName);
		this.LABEL = App.STRINGS.getString("OptionsList", myName);
	}

	public static OptionHome[] valuesOrdered() {
		return OptionHome.ORDER_UI;
	}

	public static OptionHome valueOfLabel(final String p_label) {
		return OptionHome.LABEL_ENUM_MAP.get(p_label);
	}
	// endregion

}
