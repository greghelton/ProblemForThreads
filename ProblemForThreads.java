
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
		for (int x = 0; x < 1000000; x++ ) {
			end += x;
		}
		endTime = System.nanoTime()/1000;
		incrCompletedThreadCount();
	}
	void incrCompletedThreadCount() {
		completedThreadCount++;
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
 				wait(); //wait () method releases the lock prior to waiting and reacquires prior to returning from wait ()
			}
		} catch( InterruptedException e ) {
			System.out.println("interruped");
		}
		
		endTime = System.nanoTime()/1000;
		long threadTotalTime = 0;
		for ( int x = 0; x < threads.length; x++ ) {
			long tempTime = threads[x].endTime - threads[x].startTime;
			threadTotalTime += tempTime;
			System.out.printf( "%n%17d - %17d = %5d", threads[x].endTime, threads[x].startTime, tempTime );
		}
		
		System.out.printf( "%n%45s", "=====");
		System.out.printf( "%n%38s %6d", " Total thread time =", threadTotalTime );
		System.out.printf( "%n%38s %6d", "Total program time =", (System.nanoTime() - startTime)/1000 );
		System.out.println();
	}

	public static void main(String[] args) {
		ProblemForThreads problem = new ProblemForThreads();
		problem.runExperiment();
	}
}