package com.brahvim.agc.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public final class StringTable {

	private final HashMap<String, HashMap<String, String>> table = new HashMap<>();

	private File file;

	public StringTable(final File p_file) throws FileNotFoundException {
		if (p_file == null)
			throw new NullPointerException("Path passed to `StringTable::StringTable(File)` was `null`.");

		if (!p_file.exists())
			throw new FileNotFoundException(String.format(

					"File `%s` passed to `StringTable::StringTable(File)` does not exist.",
					p_file.getName()

			));

		this.file = p_file;
		this.refresh();
	}

	public StringTable(final String p_fileName) throws FileNotFoundException {
		this(new File(p_fileName));
	}

	/**
	 * Gets a string given the name of an <i>INI section</i> and <i>INI property</i>
	 * in the form to grab them off of.
	 *
	 * If the given section and/or property do not exist, an empty string is
	 * returned.
	 */
	public String getString(final String p_section, final String p_property) {
		final String toRet;

		synchronized (this.table) {
			final HashMap<String, String> section = this.table.get(p_section);

			synchronized (section) {
				toRet = section.get(p_property);
			}
		}

		if (toRet == null) {
			System.err.printf("String table property `%s` not found!%n", p_section);
			return "";
		}

		return toRet;
	}

	/** ...Changes the underlying file! */
	public void changeFile(final File p_file) {
		this.file = p_file;
	}

	/** Re-reads the table off of the file. */
	public void refresh() {
		synchronized (this.table) {
			this.table.clear();
			final String fileName = this.file.getName();

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
										fileName,
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
				System.out.println("Failed to read string table file!");
				e.printStackTrace();
			}
		}
	}

}
