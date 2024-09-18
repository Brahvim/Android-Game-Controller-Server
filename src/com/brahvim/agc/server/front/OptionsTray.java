package com.brahvim.agc.server.front;

import java.util.IdentityHashMap;

public enum OptionsTray {

	ADD(),
	HOME(),
	CLOSE(),
	REMOVE(),
	PROFILES(),

	/* */ ;

	// region Class stuff.
	public final String LABEL;
	public final String TOOLTIP;

	public static final OptionsTray[] ORDER_UI = new OptionsTray[] { // NOSONAR! Speed!

			// ADD,
			HOME,
			CLOSE,
			PROFILES,

	};

	private static final IdentityHashMap<String, OptionsTray> LABEL_ENUM_MAP = new IdentityHashMap<>();

	static {
		// Could be faster in order (in memory access terms!), right?!:
		for (final var o : OptionsTray.ORDER_UI)
			OptionsTray.LABEL_ENUM_MAP.put(o.LABEL, o);
	}

	private OptionsTray() {
		final String myName = this.name();
		this.LABEL = App.STRINGS.getString("ListTrayOptions", myName);
		this.TOOLTIP = App.STRINGS.getString("TooltipsTrayListOptions", myName);
	}

	public static OptionsTray valueOfLabel(final String p_label) {
		return OptionsTray.LABEL_ENUM_MAP.get(p_label);
	}
	// endregion

}
