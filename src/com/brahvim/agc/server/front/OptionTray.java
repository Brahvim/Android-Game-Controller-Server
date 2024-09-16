package com.brahvim.agc.server.front;

import java.util.IdentityHashMap;

public enum OptionTray {

	ADD(),
	HOME(),
	CLOSE(),
	REMOVE(),
	LAYOUT(),

	/* */ ;

	// region Class stuff.
	public final String LABEL;
	public final String TOOLTIP;

	private static final IdentityHashMap<String, OptionTray> LABEL_ENUM_MAP = new IdentityHashMap<>();
	private static final OptionTray[] ORDER_UI = new OptionTray[] {

			// ADD,
			HOME,
			CLOSE,
			LAYOUT,

	};

	static {
		// Could be faster in order (in memory access terms!), right?!:
		for (final var o : OptionTray.valuesOrdered())
			OptionTray.LABEL_ENUM_MAP.put(o.LABEL, o);
	}

	private OptionTray() {
		final String myName = this.name();
		this.LABEL = App.STRINGS.getString("TrayList", myName);
		this.TOOLTIP = App.STRINGS.getString("Tooltips", myName);
	}

	public static OptionTray[] valuesOrdered() {
		return OptionTray.ORDER_UI;
	}

	public static OptionTray valueOfLabel(final String p_label) {
		return OptionTray.LABEL_ENUM_MAP.get(p_label);
	}
	// endregion

}
