package fab.tests;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WatchTest {
	private final Path location;
	private WatchThread watchThread;
	private int refreshCount;
	private WriterThread writerThread;

	private final class WatchThread extends Thread {
		public boolean shouldRun = true;
		public void run() {
			System.out.println("WatchThread started");
			refresh();
			try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
				WatchKey dirWK = location.register(watchService,
						StandardWatchEventKinds.ENTRY_CREATE,
						StandardWatchEventKinds.ENTRY_DELETE,
						StandardWatchEventKinds.ENTRY_MODIFY);
				int tightRunCount = 0;
				while (shouldRun) {
					WatchKey wk;
					long tic = System.currentTimeMillis();
					try {
						wk = watchService.take();
						System.out.println("Got wk:" + wk);
					} catch (InterruptedException e) {
						long toc = System.currentTimeMillis();
						if (toc - tic < 10) {
							tightRunCount++;
							if (tightRunCount > 100) {
								logErr("WatchThread exiting");
								break; // exit the loop
							}
						} else
							tightRunCount = 0;
						continue; // as wk is not initialised
					}
					tightRunCount = 0;
					// just a paranoid patch
					List<WatchEvent<?>> events = wk.pollEvents();
					int changeCount = 0;
					for (WatchEvent<?> event : events) {
						if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
							changeCount++;
						} else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
							changeCount++;
						} else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
							changeCount++;
						}
					}
					wk.cancel();
					if (changeCount > 0 && shouldRun)
						refresh();
				}
				dirWK.cancel();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
//				removeWatchThread(this);
				System.out.println("WatchThread stopped");
			}
		}
	}

	private static class WriterThread extends Thread {
		private final WatchThread watchThread;
		private final Path location;
		private final List<Path> testFiles = new ArrayList<>();

		public WriterThread(WatchThread watchThread, Path location) {
			super();
			this.watchThread = watchThread;
			this.location = location;
		}

		@Override
		public void run() {
			System.out.println("WriterThread started");
			for (int i = 0; i < 20; i++) {
				Path p = location.resolve("file-" + i);
				testFiles.add(p);
				touchFile(p);
			}
			Scanner s = new Scanner(System.in);
			while (s.hasNextLine()) {
				String line = s.nextLine();
				if (line.equals("x")) {
					System.out.println("Exit");
					watchThread.shouldRun = false;
					watchThread.interrupt();
					break;
				} else {
					System.out.println("Touching files");
					touchFiles();
				}
			}
			s.close();
			System.out.println("WriterThread stopped");
		}
		
		private void touchFiles() {
			try {
				Files.list(location).forEach(WriterThread::touchFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private static byte[] CONTENT = "eccoqua".getBytes();

		private static void touchFile(Path f) {
			try {
				System.out.println("touch:" + f);
				Files.write(f, CONTENT);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public WatchTest(Path l) {
		location = l;
		watchThread = new WatchThread();
		watchThread.start();
		writerThread = new WriterThread(watchThread, l);
		writerThread.start();
	}
	
	public void join() {
		try {
			watchThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void logErr(String text) {
		System.out.println(text);
	}

	public void refresh() {
		try {
			refreshCount++;
			System.out.printf("Refresh %d for: %s\n", refreshCount, location);
			Files.list(location).forEach(f -> System.out.println("   " + f));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			logErr("Please specify a directory to use for the test");
			System.exit(1);
		}
		Path l = FileSystems.getDefault().getPath(args[0]);
		try {
			Files.createDirectories(l);
			WatchTest wt = new WatchTest(l);
			wt.join();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
