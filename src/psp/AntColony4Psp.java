package psp;

import java.util.ArrayList;
import java.util.Random;

import beans.Task;

import com.Ant;
import com.AntColony;
import com.AntGraph;

public class AntColony4Psp extends AntColony{

	protected static final double A=0.1;
	
	public AntColony4Psp(AntGraph graph,int ants,int iterations){
		super(graph,ants,iterations);
	}
	
	protected Ant[] createAnts(AntGraph graph, int nAnts)
    {
		//System.out.println("Graph:address"+graph);
		/*for(Task task:graph.getAvaliableTasks()){
			System.out.print(task.getNumber()+"\t");
		}*/
		
        Random ran = new Random(System.currentTimeMillis());
        Ant4Psp.reset();
        Ant4Psp.setAntColony(this);
        
      /*  for(Task task:Ant4Psp.s_antColony.getGraph().getAvaliableTasks()){
			System.out.print(task.getNumber()+"\t");
		}
       */ 
        Ant4Psp ant[] = new Ant4Psp[nAnts];
        
        
        
        for(int i = 0; i < nAnts; i++)
        {
            ant[i] = new Ant4Psp((int)(graph.getTasks().get(0).getK() * ran.nextDouble()), this);
        }
        
        return ant;
    }
	
	protected void globalUpdatingRule(){
		
		//System.out.println("正在进行更新！！！！！");
		
		double dEvaporation = 0;//信息素挥发量
        double dDeposition  = 0; //信息素累积量
        
        //待商榷,暂且忽略
        
        ArrayList<Integer> pathList=Ant4Psp.s_bestPathList;
        double bestValue=Ant4Psp.s_dBestPathValue;
        
       // System.out.println("bestpathvalue:"+bestValue);
        //System.out.println("pathList.size:"+pathList.size());
        
        int start=0;
        
        for(Task task:m_graph.getAvaliableTasks()){
        	
        	int count=task.getEmployees().size();
        	for(int i=start;i<start+count;i++){
        		

        		double perCost=task.getEmployees().get(i-start).getSalary()/(bestValue*(double)8000);

        		//System.out.println("initTau:"+task.getTau(pathList.get(i), i-start));
        		//System.out.println("salary:"+task.getEmployees().get(i-start).getSalary());
        		//System.out.println("bestvalue*8000="+bestValue*(double)8000);
        		//System.out.println("perCost::"+perCost);

               /*System.out.println(pathList.toString());*/

        		dEvaporation=(1-A)*task.getTau(pathList.get(i), i-start);
        		
        		//System.out.println("dEvaporation:"+dEvaporation);
        		
        		dDeposition=A*perCost;
        		
        		//dDeposition=(A+0.05)*task.getTau(pathList.get(i), i-start);
        		
        		//System.out.println("dDeposition:"+dDeposition);
        		
        		task.updateTau(pathList.get(i), i-start,dEvaporation+dDeposition);
        		/*
        		System.out.println("tau:"+task.getTau(pathList.get(i), i-start));
        		System.out.println("全局更新完毕！！！");
        		*/
        		//全局更新完毕
        	}	
        	start+=count;
        }
        
	}
	
}
