package particleEnvironment;

public class CircularBuffer {
	
	private double[] buffer;
	private double sum;
	private int tail;
	private int head;
	
	public CircularBuffer(int n){
		buffer = new double[n];
		sum = 0;
		
	}
	public void addSum(double arg){
		sum -= buffer[head];
		sum += arg;
		
		buffer[head++] = arg;
		head = head % buffer.length;
		if(tail <= buffer.length){
			tail++;
		}
	}
	
	public double avg(){
		if(head == tail){
			return(sum/head);
		}
		else{
			return(sum/buffer.length);
		}
		
	}

}
