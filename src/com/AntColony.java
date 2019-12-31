 package com;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import beans.Task;
import beans.Arc;
import psp.Ant4Psp;

//该类的作用主要有以下几点：
//维护管理graph
//创建蚂蚁并管理各个蚂蚁的爬行
//负责全局更新，但在该psp模型中全局更新什么是一个有待商榷的问题

public abstract class AntColony implements Observer, Serializable {

	protected PrintStream m_outs;

	protected AntGraph m_graph; //任务图，可以看做蚂蚁的爬寻路径的控制
	protected Ant[] m_ants;			//蚂蚁集合
	protected int m_nAnts;
	protected int m_nAntCounter;
	protected int m_nIterCounter;
	protected int m_nIterations;

	private int m_nID;

	private static int s_nIDCounter;

	private String result;
	/*private int removedTaskNumber;*/


	public AntColony(AntGraph graph, int nAnts, int nIterations) {
		m_graph = graph;
		// System.out.println("m_graph:address"+m_graph);
		// System.out.println("graph:address"+graph);
		
		//打印出现在可执行的任务序号
		for (Task task : m_graph.getAvaliableTasks()) {
			System.out.print(task.getNumber() + "号"+"\t");
		}
		System.out.println("");
		//给相应参数赋值
		m_nAnts = nAnts;
		m_nIterations = nIterations;
		s_nIDCounter++;
		m_nID = s_nIDCounter;
	}

	public synchronized void start() {
		//m_ants是Ant类型的一维数组
		m_ants = createAnts(m_graph, m_nAnts);

		m_nIterCounter = 0;
		//形成文件存储
		try {
			m_outs = new PrintStream(new FileOutputStream("data/results/" + m_nID
					+ "_" + m_graph.getTasks().size() + "x" + m_ants.length
					+ "x" + m_nIterations + "_colony.txt"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		while (m_nIterCounter < m_nIterations) {

			iteration();
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			synchronized (m_graph) {
				// System.out.println("正在进行更新！！！！！");
				//抽象函数如何运行
				//globalUpdatingRule();
			}

		}

		if (m_nIterCounter == m_nIterations) {

			synchronized (m_graph) {
				// System.out.println("正在进行更新！！！！！");
				//globalUpdatingRule();
			}

			/*
			 * for(Task task:m_graph.getTasks()){
			 * System.out.println("task"+task.getNumber()); for(int
			 * i=0;i<10;i++){ for(int j=0;j<task.getEmployees().size();j++){
			 * System.out.print(task.getTau(i, j)+"\t"); }
			 * System.out.println(""); } System.out.println("@@@@@@@@@@@@@"); }
			 */

			int start = 0;// 每个任务对应的起点在m_nCurNode中的位置

			// 应该在这里找出最先完成的工作，然后将该工作，从graph的tasks里将该任务剔除，然后将arcs里的值为该任务的number的firstNumber设置为-1
			// 这样graph在获取可执行的avaliableTasks时，就可以选出所有的前向结点已经完成的任务了
			// 还要其他任务的cost，即所需要的任务量减去员工在这几天所干的任务量
			ArrayList<Integer> pathList = Ant4Psp.s_bestPathList;

			// System.out.println("Ant4Psp.s_bestPathList.size()"+Ant4Psp.s_bestPathList.size());
			result="";
			for (Task task : m_graph.getAvaliableTasks()) {

				/*
				 * int em_eff=0;//所有职工的贡献量 int
				 * em_count=task.getEmployees().size(); for(int
				 * i=start;i<start+em_count;i++){ em_eff+=pathList.get(i); }
				 */

				double em_eff = 0;// 所有职工的贡献量
				double finish_time = 0;// 在该分配下该任务完成所需要的时间
				for (int i = start; i < start + task.getEmployees().size(); i++) {

					int employeeTotalEffort = Ant
							.getEmployeeEffortInAvaliableTasks(task
									.getEmployees().get(i - start).getNumber(),
									m_graph.getAvaliableTasks(), pathList);

					if (employeeTotalEffort > task.getK()) {
						em_eff += (double) pathList.get(i)/ employeeTotalEffort;//pathList.get(i)/ employeeTotalEffort获取某个任务职工号为i的员工贡献占自身总贡献的百分比
						// System.out.println("em_eff:"+em_eff);
					} else {
						em_eff += (double) pathList.get(i) / task.getK();
						// System.out.println("em_eff:"+em_eff);
					}
				}

				/*
				 * for(Task t:m_graph.getTasks()){
				 * if(t.getNumber()==task.getNumber()){
				 * t.setCost(task.getCost()-
				 * em_eff/(double)task.getK()*Ant4Psp.time_best_value); }
				 * }//找到该任务，并改变其相应的值
				 */



				for (Task t : m_graph.getTasks()) {
					if (t.getNumber() == task.getNumber()) {

						result+="任务"+t.getNumber()+"已完成的工作量为： "+(em_eff
								* Ant4Psp.time_best_value)+"；\r\n";

						t.setCost(task.getCost() - em_eff
								* Ant4Psp.time_best_value);

						result+="任务"+t.getNumber()+"剩余的工作量为： "+t.getCost()+"；\r\n";

						// System.out.println("t.cost:"+t.getCost());
						// System.out.println("Ant4Psp.time_best_value"+Ant4Psp.time_best_value);
					}
				}

				start += task.getEmployees().size();
			}

			for (int i = 0; i < Ant4Psp.TASKNUMBER.size(); i++) {

				System.out
						.println("**************************************************");
				// System.out.println("TASKNUMBER:"+Ant4Psp.TASKNUMBER.size());
				for (int number : Ant4Psp.TASKNUMBER) {
					System.out.print("任务"+number + "完成"+"\n");
				}
				System.out
						.println("**************************************************");

				// m_graph.getTasks().remove(Ant4Psp.TASKNUMBER.get(i));//graph的tasks里去除该任务

				// m_graph.getTasks().remove(Ant4Psp.TASKNUMBER.get(i));

				Task beifen = null;

				for (Task task : m_graph.getTasks()) {
					if (task.getNumber() == Ant4Psp.TASKNUMBER.get(i)) {
						// m_graph.getTasks().remove(task);
						beifen = task;
						break;
					}
				}
				m_graph.getTasks().remove(beifen);

				System.out.println("还剩"+m_graph.getTasks().size()+"个任务");
				// m_graph.getTasks().remove(0);
				// System.out.println(m_graph.getTasks().size());
				System.out.println("正在移除任务结点" + Ant4Psp.TASKNUMBER.get(i));

				/*removedTaskNumber=Ant4Psp.TASKNUMBER.get(i);*/

				System.out.println("正在修改弧");
				// for(Arc arc:m_graph.getArcs()){
				for (Arc arc : m_graph.getArcs()) {
					if (arc.getFirstNumber() == Ant4Psp.TASKNUMBER.get(i)) {
						arc.setFirstNumber(-1);
					}
				}
				System.out.println("弧更改完毕");
				// Ant4Psp.TASKNUMBER=new ArrayList<Integer>();
			}

			/*
			 * m_graph.getTasks().remove(Ant4Psp.TASKNUMBER);//graph的tasks里去除该任务
			 * for(Arc arc:m_graph.getArcs()){
			 * if(arc.getFirstNumber()==Ant4Psp.TASKNUMBER){
			 * arc.setFirstNumber(new Integer(-1)); } }
			 */

			m_outs.println("任务"+Ant4Psp.TASKNUMBER.toString()+"先且完成。");
			m_outs.println("\r\n"+result);

			m_outs.close();
		}

	}

	private void iteration() {
		m_nAntCounter = 0;
		m_nIterCounter++;
		m_outs.print(m_nIterCounter);
		for (int i = 0; i < m_ants.length; i++) {
			m_ants[i].start();
		}
	}

	public synchronized void update(Observable ant, Object obj) {

		m_nAntCounter++;

		if (m_nAntCounter == m_ants.length) {

			m_outs.println(";" + Ant.s_dBestPathValue + ";"
					+ Ant4Psp.s_bestPathList.toString() + ";####"
					+ Ant.time_best_value+"\r\n");
			/*
			 * for(int i:Ant4Psp.getBestPath()){ m_outs.print(i+"\t"); }
			 */
			notify();
		}
	}
	//返回任务图表
	public synchronized AntGraph getGraph() {
		return m_graph;
	}

	public int getAnts() {
		return m_ants.length;
	}

	public int getIterations() {
		return m_nIterations;
	}

	public int getIterationCounter() {
		return m_nIterCounter;
	}

	public int getID() {
		return m_nID;
	}

	public double getBestPathValue() {
		return Ant.s_dBestPathValue;
	}

	public ArrayList<Integer> getBestPathVector() {
		return Ant.s_bestPathList;
	}

	public int[] getBestPath() {
		return Ant.getBestPath();
	}

	public int getLastBestPathIteration() {
		return Ant.s_nLastBestPathIteration;
	}
	
	public double getBestTime(){
		return Ant.time_best_value;
	}

	public boolean done() {
		return m_nIterCounter == m_nIterations;
	}
//抽象方法
	protected abstract Ant[] createAnts(AntGraph graph, int ants);
//抽象方法
	protected abstract void globalUpdatingRule();
}
