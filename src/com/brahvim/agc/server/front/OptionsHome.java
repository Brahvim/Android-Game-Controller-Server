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

	private static final IdentityHashMap<String, OptionsHome> LABEL_ENUM_MAP = new IdentityHashMap<>();
	private static final OptionsHome[] ORDER_UI = new OptionsHome[] {

			ADD,
			STOP,
			REMOVE,
			PROFILES,
			CONTROLS,

	};

	static {
		// Could be faster in order (in memory access terms!), right?!:
		for (final var o : OptionsHome.valuesOrdered())
			OptionsHome.LABEL_ENUM_MAP.put(o.LABEL, o);
	}

	private OptionsHome() {
		final String myName = this.name();
		this.TOOLTIP = App.STRINGS.getString("TooltipsHomeListOptions", myName);
		this.LABEL = App.STRINGS.getString("ListHomeOptions", myName);
	}

	public static OptionsHome[] valuesOrdered() {
		return OptionsHome.ORDER_UI;
	}

	public static OptionsHome valueOfLabel(final String p_label) {
		return OptionsHome.LABEL_ENUM_MAP.get(p_label);
	}
	// endregion

}
