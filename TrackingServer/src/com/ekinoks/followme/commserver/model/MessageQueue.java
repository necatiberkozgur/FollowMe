package com.ekinoks.followme.commserver.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class MessageQueue {

	private final LinkedBlockingQueue<Object> messageQueue = new LinkedBlockingQueue<>();

	private Set<Consumer<Object>> listeners = Collections.synchronizedSet(new HashSet<>());

	public MessageQueue() {

		Thread dispatcherThread = new Thread(this::dispatchReceivedObjects);
		dispatcherThread.start();
	}

	public void enqueue(Object message) {

		try {

			messageQueue.put(message);

		} catch (InterruptedException e) {

			e.printStackTrace();
		}
	}

	public Object dequeue() {

		Object message = null;

		try {

			message = messageQueue.take();

		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		return message;
	}

	public void addReceivedMessageListener(Consumer<Object> listener) {

		if (this.listeners.contains(listener) == false) {

			this.listeners.add(listener);
		}
	}

	public void removeReceivedMessageListener(Consumer<Object> listener) {

		if (this.listeners.contains(listener) == true) {

			this.listeners.remove(listener);
		}
	}

	private void dispatchReceivedObjects() {

		while (true) {

			Object message = null;

			try {

				message = messageQueue.take();

			} catch (InterruptedException e) {

				e.printStackTrace();
			}

			for (Consumer<Object> listener : listeners) {

				listener.accept(message);
			}
		}
	}

}
