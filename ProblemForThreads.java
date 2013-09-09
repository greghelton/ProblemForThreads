
class AddMillion extends Thread {
	static int completedThreadCount = 0;
	Object caller;
	final int beginning;
	int end;
	final long startTime = System.nanoTime()/1000;
	long endTime;
	AddMillion( Object caller, int beginning, String name ) {
		super( name );
		this.caller = caller;
		this.beginning = beginning;
	}
	@Override
	public void run() {
		end = beginning;
		for (int x = 0; x < 500; x++ ) {
			try {sleep( (int)(Math.random() * 10) );} catch(InterruptedException e) {}
			end += x;
		}
		endTime = System.nanoTime()/1000;
		incrCompletedThreadCount();
	}
	void incrCompletedThreadCount() {
		completedThreadCount++;
		System.out.printf( "%s %21s %17d %n", getName(), "incrCompletedThread()", endTime );
		synchronized(caller) {
			caller.notifyAll();
		}
	}
}

public class ProblemForThreads extends Thread {

	public void runExperiment() {
		final long startTime = System.nanoTime();
		final long endTime;
		int[] primes = { 19, 23, 29, 31, 37 };
		AddMillion[] threads = new AddMillion[primes.length];

		for (int x = 0; x < primes.length; x++ ) { 
			AddMillion m = new AddMillion( this, primes[x], Integer.toString(primes[x]) );
			threads[x] = m;
			m.start();
		}
		try {
			synchronized( this ) {
				System.out.println( "waiting" );
				int interruptedCount = 0;
				while (interruptedCount < threads.length ) {
 					wait(); // releases the lock prior to waiting and reacquires prior to returning from wait ()
					interruptedCount++;
				}
				System.out.printf( "%24s %17d%n", "out of wait()", System.nanoTime()/1000 );
			}
		} catch( InterruptedException e ) {
			System.out.println("interruped #2");
		}
		
		endTime = System.nanoTime();
		long threadTotalTime = 0;
		for ( int x = 0; x < threads.length; x++ ) {
			long tempTime = threads[x].endTime - threads[x].startTime;
			threadTotalTime += tempTime;
			System.out.printf( "%n%s %17d - %17d = %5d", threads[x].getName(), threads[x].endTime, threads[x].startTime, tempTime );
		}
		
		System.out.printf( "%n%50s", "=========");
		System.out.printf( "%n%41s %8d", " Total thread time =", threadTotalTime );
		System.out.printf( "%n");
		System.out.printf( "%n%32s %17d ", "Program End Time", endTime/1000 );
		System.out.printf( "%n%32s %17d ", "Program Start Time", startTime/1000 );
		System.out.printf( "%n%50s", "=========");
		System.out.printf( "%n%41s %8d", "Total program time =", (endTime - startTime)/1000 );
		System.out.println();
	}

	public static void main(String[] args) {
		ProblemForThreads problem = new ProblemForThreads();
		problem.runExperiment();
	}
}