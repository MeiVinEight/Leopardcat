package org.mve.leopard;

import org.mve.text.Hexadecimal;

import java.util.concurrent.ThreadFactory;

public class LeopardcatFactory implements ThreadFactory
{
	private final int ID;
	private int count = 0;

	public LeopardcatFactory(int ID)
	{
		this.ID = ID;
	}

	@Override
	public Thread newThread(Runnable r)
	{
		Thread t = new Thread(r);
		t.setName(name(this.ID, this.count++, t.getId()));
		return t;
	}

	public static String name(int ID, int count, long tid)
	{
		return new String(Hexadecimal.transform(((long) ID << 24) | ((long) count << 16) | tid)).substring(8);
	}
}
