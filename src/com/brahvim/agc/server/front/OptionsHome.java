package com.brahvim.agc.server.front;

import java.util.IdentityHashMap;

public enum OptionsHome {

	ADD(),
	STOP(),
	REMOVE(),
	CONTROLS(),
	PROFILES(),

	/* */ ;

	// region Class stuff.
	public final String LABEL;
	public final String TOOLTIP;

	public static final OptionsHome[] ORDER_UI = new OptionsHome[] { // NOSONAR! Speed!

			ADD,
			REMOVE,
			STOP,
			CONTROLS,
			PROFILES,

	};

	private static final IdentityHashMap<String, OptionsHome> LABEL_ENUM_MAP = new IdentityHashMap<>();

	static {
		// Could be faster in order (in memory access terms!), right?!:
		for (final var o : OptionsHome.ORDER_UI)
			OptionsHome.LABEL_ENUM_MAP.put(o.LABEL, o);
	}

	private OptionsHome() {
		final String myName = this.name();
		this.LABEL = App.STRINGS.getString("ListHomeOptions", myName);
		this.TOOLTIP = App.STRINGS.getString("TooltipsHomeListOptions", myName);
	}

	public static OptionsHome valueOfLabel(final String p_label) {
		return OptionsHome.LABEL_ENUM_MAP.get(p_label);
	}
	// endregion

}
