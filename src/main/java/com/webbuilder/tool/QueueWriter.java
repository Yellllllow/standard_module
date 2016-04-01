package com.webbuilder.tool;

public class QueueWriter implements java.io.Serializable {
	private static final long serialVersionUID = 2100540580239114561L;
	private StringBuffer buf;
	private int bs;

	public QueueWriter(int bufferSize) {
		buf = new StringBuffer();
		bs = bufferSize;
	}

	public void print(Object o) {
		String s;
		if (o == null)
			s = "null";
		else
			s = o.toString();
		buf.append(s);
		checkSize();
	}

	public void println(Object o) {
		String s;
		if (o == null)
			s = "null";
		else
			s = o.toString();
		buf.append(s);
		buf.append("\n");
		checkSize();
	}

	public String toString() {
		return buf.toString();
	}

	public void clear() {
		buf.delete(0, buf.length());
	}

	private void checkSize() {
		int len = buf.length();
		if (len > bs)
			buf.delete(0, len - bs);
	}
}
