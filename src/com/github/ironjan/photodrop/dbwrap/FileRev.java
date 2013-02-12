package com.github.ironjan.photodrop.dbwrap;

import java.util.Locale;

public class FileRev {
	public long _id;
	public String file;
	public String rev;


	public boolean isDirty(String remoteRev) {
		return !(remoteRev.equals(this.rev));
	}

	@Override
	public String toString() {
		String string = "FileRev [_id=%d, file=%s, rev=%s]"; //$NON-NLS-1$
		return String.format(Locale.GERMAN, string, Long.valueOf(_id), file, rev);
	}

}
