package org.mve.leopard;

import org.mve.leopard.connection.HTTPConnection;
import org.mve.leopard.http.HTTPConnectionResponder;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Leopardcat
{
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("HH:mm:ss:SSS");
	private static int count = 1;
	public final Object L = new Object();
	public static final File ROOT = new File("resource").getAbsoluteFile();
	private final ServerSocket server;
	public final Queue<HTTPConnection> connection = new ConcurrentLinkedQueue<>();
	private boolean running = false;
	private final ExecutorService service = new ThreadPoolExecutor(10, 20, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new LeopardcatFactory(Leopardcat.count++));
	private int activity = 0;
	private int waiting = 0;
	private final int ID;

	public Leopardcat(int port) throws IOException
	{
		this.ID = count++;
		this.server = new ServerSocket(port);
		this.execute(new HTTPConnectionResponder(this));
	}

	public int ID()
	{
		return this.ID;
	}

	public void start()
	{
		this.running = true;
		while (!this.server.isClosed())
		{
			try
			{
				Socket socket = this.server.accept();
				HTTPConnection connection = new HTTPConnection(socket);
				System.out.println(Leopardcat.prefix() + " Connecting to " + connection.address());
				this.connection.offer(connection);
				if (this.waiting == 0)
				{
					this.execute(new HTTPConnectionResponder(this));
				}
				else
				{
					this.notifying();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		this.running = false;
	}

	public boolean running()
	{
		return this.running;
	}

	public void stop()
	{
		this.running = false;
		this.service.shutdown();
		synchronized (this.L)
		{
			this.L.notifyAll();
		}

		try
		{
			this.server.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void execute(Runnable runnable)
	{
		this.service.execute(() ->
		{
			runnable.run();
			Leopardcat.this.activity--;
		});
		this.activity++;
	}

	public void waiting() throws InterruptedException
	{
		synchronized (this.L)
		{
			this.waiting++;
			this.L.wait();
		}
	}

	public void notifying()
	{
		synchronized (this.L)
		{
			this.L.notify();
			this.waiting--;
		}
	}

	public static String prefix()
	{
		return "[" + Leopardcat.FORMAT.format(new Date()) + " " + Thread.currentThread().getName() + "]";
	}

	public static void main(String[] args)
	{
		Thread t0 = Thread.currentThread();
		Thread t1 = new Thread(() ->
		{
			try
			{
				Leopardcat ins = new Leopardcat(80);
				ins.start();
			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}
		});
		Thread t2 = new Thread(() ->
		{
			try
			{
				Leopardcat ins = new Leopardcat(443);
				ins.start();
			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}
		});
		t0.setName(LeopardcatFactory.name(0, 0, t0.getId()));
		t1.setName(LeopardcatFactory.name(0, 0, t1.getId()));
		t2.setName(LeopardcatFactory.name(0, 0, t2.getId()));
		t1.start();
//		t2.start(); // HTTPs unsupported
	}
}
