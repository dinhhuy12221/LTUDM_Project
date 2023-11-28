package Server.socket;


public class StopProcess implements Runnable{
    private Process process;

    public StopProcess(Process process) {
        this.process= process;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            process.destroy();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
