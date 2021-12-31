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
		t.setName(new String(Hexadecimal.transform(((long) this.ID << 24) | (this.count++ << 16) | t.getId())).substring(8));
		return t;
	}
}
