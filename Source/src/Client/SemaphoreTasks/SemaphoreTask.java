package Client.SemaphoreTasks;

import java.util.TimerTask;
import java.util.concurrent.Semaphore;

public class SemaphoreTask extends TimerTask {
  private Semaphore semaphore;

  public SemaphoreTask (Semaphore semaphore){
    this.semaphore = semaphore;
  }

  @Override
  public void run() {
    semaphore.release();
  }
}
