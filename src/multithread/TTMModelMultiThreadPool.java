package multithread;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import dataunit.Corpus;
import model.TargetedTopicModel;
import model.ModelParameter;

public class TTMModelMultiThreadPool {
	private int numberOfTherads = 1;
	private ExecutorService executor = null;
	ArrayList<Future<TargetedTopicModel>> futureList = new ArrayList<Future<TargetedTopicModel>>();
	public ArrayList<TargetedTopicModel> modelList = null;
	
	public TTMModelMultiThreadPool(int numberOfTherads) {
		super();
		this.numberOfTherads = numberOfTherads;
		executor = Executors.newFixedThreadPool(numberOfTherads);
		modelList = new ArrayList<TargetedTopicModel>();
	}
	
	public void addTask(Corpus corpus, ModelParameter param){
		Callable<TargetedTopicModel> callable = new TTMModelCallable(corpus, param);
		Future<TargetedTopicModel> future = executor.submit(callable);
		futureList.add(future);
	}
	
	public void awaitTermination(){
		try {
			executor.shutdown();
			executor.awaitTermination(60, TimeUnit.DAYS);
			for (Future<TargetedTopicModel> future : futureList) {
				TargetedTopicModel model = future.get();
				modelList.add(model);
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
}
