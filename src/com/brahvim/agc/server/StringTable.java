package com.brahvim.agc.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public final class StringTable {

	private final HashMap<String, HashMap<String, String>> table = new HashMap<>();

	private String fileName;
	private File file;

	public StringTable(final File p_file) throws FileNotFoundException {
		if (p_file == null)
			throw new NullPointerException("Path passed to `StringTable::StringTable(File)` was `null`.");

		if (!p_file.exists())
			throw new FileNotFoundException(String.format(

					"File `%s` passed to `StringTable::StringTable(File)` does not exist.",
					p_file.getName()

			));

		this.changeFile(p_file);
	}

	public StringTable(final String p_fileName) throws FileNotFoundException {
		this(new File(p_fileName));
	}

	public static StringTable tryCreating(final String p_fileName) {
		return StringTable.tryCreating(new File(p_fileName));
	}

	public static StringTable tryCreating(final File p_file) {
		try {
			return new StringTable(p_file);
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets a string given the name of an <i>INI section</i> and <i>INI property</i>
	 * in the form to grab them off of, then passes it to `String.format()` with
	 * your arguments.
	 *
	 * If the given section and/or property do not exist, an empty string is
	 * returned.
	 */
	public final String getFormatted(final String p_section, final String p_property, final Object... p_args) {
		return String.format(this.getString(p_section, p_property), p_args);
	}

	/**
	 * Gets a string given the name of an <i>INI section</i> and <i>INI property</i>
	 * in the form to grab them off of.
	 *
	 * If the given section and/or property do not exist, an empty string is
	 * returned.
	 */
	public final String getString(final String p_section, final String p_property) {
		final String property;

		synchronized (this.table) {
			final HashMap<String, String> section = this.table.get(p_section);

			if (section == null) {
				System.err.printf("String table section `%s` not found!%n", p_section);
				return "";
			}

			synchronized (section) {
				property = section.get(p_property);
			}
		}

		if (property == null) {
			System.err.printf("String table property `%s` not found in section `%s`!%n", p_property, p_section);
			return "";
		}

		return property;
	}

	/** ...Changes the underlying file! */
	public final void changeFile(final File p_file) {
		this.file = p_file;
		this.fileName = this.file.getName();

		this.refresh();
	}

	/** Re-reads the table off of the file. */
	public final void refresh() {
		synchronized (this.table) {
			if (this.table.size() != 0) { // A mere `int` is returned.
				this.table.clear(); // WAY more work than that!
				System.gc(); // All those `HashMap`s need collecting!
			}

			// System.out.println("Found string table file!");
			try (BufferedReader reader = new BufferedReader(new FileReader(this.file))) {

				StringBuilder lineParsed;
				int lineLen = 0, lineCount = 0;
				int posEquals = 0, posNewLineDelim = 0;
				String section = null, content = null;

				// Remember that this loop goes through EACH LINE!
				// Not each *character!* :joy::
				for (String line; (line = reader.readLine()) != null;) {
					lineLen = line.length();
					++lineCount;

					// Leave empty lines alone!:
					if (line.isBlank())
						continue;

					// Skipping comments and registering sections,
					// and skip this iteration if they exist:
					switch (line.charAt(0)) {

						case ';': // Semicolons are also comments in INI files, apparently!
						case '#':
							continue;

						case '[':
							try { // NOSONAR!
								section = line.substring(1, line.indexOf(']'));
							} catch (final IndexOutOfBoundsException e) {
								System.err.printf(

										"String table file `%s` missing at line `%d`.%n",
										this.fileName,
										lineCount

								);
							}
							continue;

					}

					// Find where the `=` sign is!:
					posEquals = line.indexOf('=');
					content = line.substring(posEquals + 1, lineLen);

					// Parse out `\n`s!:
					lineParsed = new StringBuilder(content);

					while ((posNewLineDelim = lineParsed.indexOf("\\n")) != -1) {
						// Causes an infinite loop, and I won't be writing `\n` anywhere, anyway:
						// if (parsedContent.charAt(newLineCharPos - 1) == '\\')
						// continue;

						for (int i = 0; i < 2; i++)
							lineParsed.deleteCharAt(posNewLineDelim);
						lineParsed.insert(posNewLineDelim, '\n');
					}

					// if (content.contains("<br>"))
					// content = content.replace("\\\\n", App.NEWLINE);

					final String property = line.substring(0, posEquals).trim();
					HashMap<String, String> sectionMap = this.table.get(section);

					if (sectionMap == null) {
						sectionMap = new HashMap<>();
						this.table.put(section, sectionMap); // Nobody's seen it yet. No sync needed.
					}

					synchronized (sectionMap) {
						sectionMap.put(property, lineParsed.toString());
					}
				}

			} catch (final IOException e) {
				System.err.println("Failed to read string table file!");
				e.printStackTrace();
			}
		}
	}

}
