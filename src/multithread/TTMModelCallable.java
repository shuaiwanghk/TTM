package multithread;

import java.util.concurrent.Callable;

import model.TargetedTopicModel;
import model.ModelParameter;
import dataunit.Corpus;

public class TTMModelCallable implements Callable<TargetedTopicModel>{
	private Corpus corpus = null;
	private ModelParameter param = null;
	
	public TTMModelCallable(Corpus corpus, ModelParameter param)  {
		super();
		this.param = param;
		this.corpus = corpus;
	}
	
	public TargetedTopicModel call() throws Exception{
		
		TargetedTopicModel ttm = new TargetedTopicModel(corpus, param);
		ttm.run();
		
		return ttm;
	}
	
	
	
	
}
