package psp;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Observer;
import java.util.Random;

import beans.Task;

import com.Ant;
import com.AntGraph;

public class Ant4Psp extends Ant{
	
	private static final double B=2;
	private static final double Q0=0.8;
	private static final double R=0.1;
	
	private static final Random s_randGen=new Random(System.currentTimeMillis());
	
	//protected Hashtable<Integer,Integer>m_nodesToVisitTbl; //对于该模型来说，此变量没有必要
	
	public Ant4Psp (int startNode,Observer observer){
		super(startNode, observer);
	}
	
	public int stateTransitionRule(int taskPosition,int nodePosition){
		
		final AntGraph graph=s_antColony.getGraph();
		ArrayList<Task>tasks=graph.getAvaliableTasks();
		
		int MaxNode=-1;//{0,1,2,3,4,5,6,7,8,9}
		
		double q=s_randGen.nextDouble();
		
		if(q<=Q0){//Exploitation
			
			//System.out.println("按照第一种方式转移结点");
			MaxNode=one_select(taskPosition,nodePosition);
		}
		else{//Exploration
			//System.out.println("按照第二种方式转移结点");
			MaxNode=another_select(taskPosition,nodePosition);
		}
		
		if(MaxNode<0){
			throw new RuntimeException("MaxNode=-1");
		}
		
		return MaxNode;
	}
	
	public int one_select(int taskPosition,int nodePosition){
		
		//System.out.println("&&&&&&&&&&&&&&&&&&&");
		
		double dMaxVal=-1;
		double dVal;
		int nNode;
		int MaxNode = 0;
		
		int [] pl=new int [999999];
		
		//System.arraycopy((ArrayList<Integer>) m_pathList.clone(),0,pl,0,m_pathList.size());
		
		for(int i=0;i<m_pathList.size();i++){
			
			pl[i]=m_pathList.get(i);
			
		}
		
		Task task=avaliableTasks.get(taskPosition);
		
		int ad=getEmployeeCurrentEffort(task.getEmployees().get(nodePosition).getNumber(),avaliableTasks,pl);
		
		double totalEffort=(double)ad/task.getK();
		double TOTAL=0;
		
		if(totalEffort>0.5){// 以员工已经分配过的工作量为启发式信息  
			
			double delta=totalEffort-0.5;
			
			for(int i=0;i<task.getK()+1;i++){
				TOTAL+=((double)i/task.getK()+delta);
			}
			
			for(int i=0;i<K+1;i++){
				
				dVal=task.getTau(i, nodePosition)+((double)(10-i-1)/task.getK()+delta)/TOTAL;
				
				if(dVal>dMaxVal){
					dMaxVal=dVal;
					MaxNode=i;
				}
				
			}
	
		}
		else{
			for(int i=0;i<task.getK()+1;i++){
				TOTAL+=((double)i/task.getK()+totalEffort);
			}
			
			for(int i=0;i<K+1;i++){
				
				dVal=task.getTau(i, nodePosition)+((double)i/task.getK()+totalEffort)/TOTAL;
				
				if(dVal>dMaxVal){
					dMaxVal=dVal;
					MaxNode=i;
				}
				
			}
			
		}
		/*
		for(int i=0;i<K+1;i++){
			
			//dVal=task.getTau(i, nodePosition)+task.getHeristic(nodePosition)*1000;
			dVal=task.getTau(i, nodePosition)+task.getCost();
			//System.out.println("Ant"+m_nAntID+"dVal:"+dVal);
			
			if(dVal>dMaxVal){
				dMaxVal=dVal;
				MaxNode=i;
			}
			
		}
		*/
		//System.out.println(MaxNode);
		
		//int MaxNode=(int) ((int)9*s_randGen.nextDouble());
		
		return MaxNode;
	}
	
	public int another_select(int taskPosition,int nodePosition){

		//System.out.println("@@@@@@@@@@@@");
		double dSum=0;
		int nNode=0;
		int MaxNode = 0;
		
		Task task=avaliableTasks.get(taskPosition);
		
		int [] pl=new int [999999];
		
		for(int i=0;i<m_pathList.size();i++){
			pl[i]=m_pathList.get(i);	
		}
		
		int ad=getEmployeeCurrentEffort(task.getEmployees().get(nodePosition).getNumber(),avaliableTasks,pl);
		
		double totalEffort=(double)ad/task.getK();
		double TOTAL=0;
		
		if(totalEffort>0.5){// 以员工已经分配过的工作量为启发式信息  
			
			double delta=totalEffort-0.5;
			
			for(int i=0;i<task.getK()+1;i++){
				TOTAL+=((double)i/task.getK()+delta);
			}
			
			for(int i=0;i<K+1;i++){
				dSum+=(task.getTau(i, nodePosition)+((double)(10-i-1)/task.getK()+delta)/TOTAL);
			}	
			
			if(dSum==0) throw new RuntimeException("SUM=0");
			
			double dAverage=dSum/(double)(K+1);
			
			for(int i=0;i<K+1;i++){
				
				if((task.getTau(i, nodePosition)+((double)(10-i-1)/task.getK()+delta)/TOTAL)>dAverage){
					MaxNode=i;
				}
				
			}
		}
		else{
			
			for(int i=0;i<task.getK()+1;i++){
				TOTAL+=((double)i/task.getK()+totalEffort);
			}
			
			for(int i=0;i<K+1;i++){
				dSum+=(task.getTau(i, nodePosition)+((double)i/task.getK()+totalEffort)/TOTAL);
			}	
			
			if(dSum==0) throw new RuntimeException("SUM=0");
			
			double dAverage=dSum/(double)(K+1);
			
			for(int i=0;i<K+1;i++){
				
				if((task.getTau(i, nodePosition)+((double)i/task.getK()+totalEffort)/TOTAL)>dAverage){
					MaxNode=i;
				}
				
			}
			
		}

		//System.out.println(MaxNode);
		 
		/*int MaxNode=(int) (9*s_randGen.nextDouble());*/
		return MaxNode;
	}
	
	public void localUpdatingRule(ArrayList<Integer> pathList,ArrayList<Task>avaliableTasks){
		
		//System.out.println("正在进行局部更新");
		
		//AntGraph graph=s_antColony.getGraph();
		int start=0;
		
		/*System.out.println("pathlist.size"+pathList.size());
		System.out.print("可执行任务编号");
		for(Task task:avaliableTasks){
			System.out.print(task.getNumber()+"\t");
		}
		*/
		for(Task task:avaliableTasks){
			
			double val=0;
			
			//System.out.println("Ant"+m_nAntID+"task"+task.getNumber()+":\t");
			
			for(int i=start;i<start+task.getEmployees().size();i++){
				
				/*
				System.out.print("task"+task.getNumber()+".employees.size"+task.getEmployees().size()+"\t");
				
				System.out.print("Ant"+m_nAntID+"pathList"+pathList.get(i)+"**"+i);
				
				System.out.println("");
				*/
				
				val=((double)1-R)*task.getTau(pathList.get(i), i-start)+R*graph.getTau0();
				
				//graph.getTaskByNumber(task.getNumber()).updateTau(pathList.get(i), i, val);
				task.updateTau(pathList.get(i), i-start, val);
				
				//System.out.println("tau"+task.getTau(pathList.get(i), i-start));
				
			}
			
			start+=task.getEmployees().size();
			
			//System.out.println(start);
			
		}
		//待定
		
		
	}
	
	public boolean better(double dPathValue1,double dPathValue2){
		return dPathValue1<dPathValue2;
	}
	
	public boolean end(){//待定
		return m_pathList.size()==TotalColumn;
	}
	
}
