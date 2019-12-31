package com;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import beans.Employee;
import beans.Skill;
import beans.Task;

//该类的主要作用 ，就是按照状态转移规则，遍历结点，找到一条合适的路径，也就是pathList
//实现任务信息素列表的局部更新

public abstract class Ant extends Observable implements Runnable, Serializable {

	protected ArrayList<Integer> tasknumber;
	protected int TN;

	double min_finish_time = Double.MAX_VALUE;// 任务的最早完成时间
	protected static double time_best_value = 0;

	public AntGraph graph;

	protected int m_nAntID;

	public ArrayList<Task> avaliableTasks; // 可执行任务列表
	private int count; // 记录蚂蚁在m_pathList中的位置
	protected int TotalColumn; // 可执行任务矩阵的列总数

	protected int K = 0; // 粒度

	protected int nodePositionInTask = -1; // 在状态转移规则transitionRule中用到了该参数

	// protected int [][][] m_path;
	protected int m_nCurNode;
	protected ArrayList<Integer> m_nStartNode = new ArrayList<Integer>();
	protected double m_dPathValue; // 适应度指标
	protected Observer m_observer;
	protected ArrayList<Integer> m_pathList = null;

	private static int s_nAntIDCounter = 0;
	private static PrintStream s_outs;

	public static AntColony s_antColony;

	public static ArrayList<Integer> TASKNUMBER = new ArrayList<Integer>();

	public static double s_dBestPathValue = Double.MAX_VALUE;
	public static ArrayList<Integer> s_bestPathList = new ArrayList<Integer>();
	// public static int [][][] s_bestPath=null; //这个数组的意义及其维护是一个亟待解决的问题
	public static int s_nLastBestPathIteration = 0;

	public static void setAntColony(AntColony antColony) {
		s_antColony = antColony;
	}

	public static void reset() {
		s_dBestPathValue = Double.MAX_VALUE;
		s_bestPathList = new ArrayList<Integer>();
		// s_bestPath = null;
		s_nLastBestPathIteration = 0;
		s_outs = null;
		TASKNUMBER = new ArrayList<Integer>();
		time_best_value = 0;
	}

	public Ant(int nStartNode, Observer observer) {
		s_nAntIDCounter++;
		m_nAntID = s_nAntIDCounter;
		m_nStartNode.add(nStartNode);
		m_observer = observer;
		m_nCurNode = nStartNode;
	}

	public void init() {
		if (s_outs == null) {
			try {
				s_outs = new PrintStream(new FileOutputStream("data/results/"
						+ s_antColony.getID() + "_"
						+ s_antColony.getGraph().getTasks().size() + "x"
						+ s_antColony.getAnts() + "x"
						+ s_antColony.getIterations() + "_ants.txt"));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		// final AntGraph graph=s_antColony.getGraph();//获取任务图表
		// graph=s_antColony.getGraph();//获取任务图表

		m_pathList = new ArrayList<Integer>();// 存储蚂蚁每一次移动的结点的值，且该值属于{0,1,2,3,4,5,6,7,8,9}中的一个
		m_pathList.add(m_nCurNode);// 存储蚂蚁爬行的每一个结点
		count = 0;
		avaliableTasks = graph.getAvaliableTasks();// 获取蚂蚁在当前阶段可以爬行的任务集合

		/*
		 * System.out.println("蚂蚁在当前阶段可以爬行的任务集合为："); for(Task
		 * task:avaliableTasks){ System.out.print(task.getNumber()+"\t"); }
		 */

		K = avaliableTasks.get(0).getK();//获取Task类中的k值，K=9;

		TotalColumn = 0;//可执行任务矩阵列的总数现在为0

		for (Task task : avaliableTasks) {
			TotalColumn += task.getEmployees().size();//所有可执行的任务对应的员工数的和
		}

		m_dPathValue = 0;// 以该方式分配所能最早完成的任务所需要的时间 或者 适应度函数的最优值
		min_finish_time = Double.MAX_VALUE;
		// 待商榷的一个点：即适应度函数怎样确定,在这里是以时间最短为适应度函数
	}

	public void start() {// 启动蚂蚁线程
		graph = s_antColony.getGraph();// 获取任务图表

		// System.out.println("graph:address"+graph);
		/*
		 * for(Task task:graph.getAvaliableTasks()){
		 * System.out.print(task.getNumber()+"\t"); } System.out.println("");
		 */
		init();
		Thread thread = new Thread(this);
		thread.setName("Ant" + m_nAntID);
		// System.out.println("Ant"+m_nAntID+"开始进行爬取");
		thread.start();
	}

	public void run() {
		final AntGraph graph = s_antColony.getGraph();

		// System.out.println("graph:address"+graph);
		// System.out.println("该蚂蚁爬取路径为：");
		while (!end()) {
			int nNewNode;

			synchronized (graph) {
				// synchronized(new Integer(nodePositionInTask)){

				// if(count==TotalColumn-1){break;}

				// if(nodePositionInTask!=-1){nodePositionInTask=-1;}

				count++;// 记录当前结点的位置

				int TaskPositionInAvaliableTasks = this
						.getTaskPositionInAvaliableTasks(count);

				// System.out.println("Ant"+m_nAntID+"*nodePositionInTask:"+nodePositionInTask);

				nNewNode = stateTransitionRule(TaskPositionInAvaliableTasks,
						nodePositionInTask);

				// nodePositionInTask=-1;
				// System.out.print(nNewNode);
				m_pathList.add(nNewNode);// 添加新结点

				// System.out.println("Ant"+m_nAntID+"*pathList.size:"+m_pathList.size());

				// System.out.print("Ant"+m_nAntID+"**"+m_pathList.size());

				// count++;//记录当前结点的位置
			}

		}

		// 与Tsp问题中每到达一个结点就要更新一次总路径长度（即m_dPathValue的值）不同
		// 在该问题中，需要将所有的可接触结点遍历一遍，然后求出最先完成任务所需要的时间
		// 所以m_dPathValue只在最后所有结点都已遍历完时才更新一次
		// _______________________________________________________________________
		if (m_pathList.size() == TotalColumn) {// 对爬行结果的评价

			/*
			 * int start=0;//每个任务对应的起点在m_nCurNode中的位置
			 * 
			 * for(Task task:avaliableTasks){
			 * 
			 * int em_eff=0;//所有职工的贡献量 int em_count=task.getEmployees().size();
			 * double finish_time=0;//在该分配下该任务完成所需要的时间 for(int
			 * i=start;i<start+em_count;i++){ em_eff+=m_pathList.get(i); }
			 * if(em_eff==0){ finish_time=Double.MAX_VALUE;
			 * 
			 * 
			 * } else{
			 * 
			 * finish_time=task.getCost()/(em_eff/(double)task.getK());
			 * 
			 * 
			 * } if(finish_time<min_finish_time){ min_finish_time=finish_time;
			 * 
			 * 
			 * } start+=em_count; }
			 * 
			 * 
			 * //计算成本 start=0; double cost=0; for(Task t:avaliableTasks){
			 * for(int i=start;i<start+t.getEmployees().size();i++){
			 * 
			 * double salary=t.getEmployees().get(i-start).getSalary(); double
			 * dedication=m_pathList.get(i)/(double)t.getK()*min_finish_time;
			 * 
			 * 
			 * 
			 * cost+=(salary/(double)5000*dedication);
			 * 
			 * } start+=t.getEmployees().size(); }
			 * 
			 * 
			 * m_dPathValue=0.5*min_finish_time+0.5*cost;
			 * 
			 * cost=0;
			 */
			int start = 0;// 每个任务对应的起点在m_nCurNode中的位置
			if (feasible(avaliableTasks, m_pathList)) {

				for (Task task : avaliableTasks) {

					double em_eff = 0;// 所有职工的贡献量
					double finish_time = 0;// 在该分配下该任务完成所需要的时间
					for (int i = start; i < start + task.getEmployees().size(); i++) {

						int employeeTotalEffort = getEmployeeEffortInAvaliableTasks(
								task.getEmployees().get(i - start).getNumber(),
								avaliableTasks, m_pathList);

						// System.out.println("Ant"+m_nAntID+"employeeTotalEffort:"+employeeTotalEffort);

						if (employeeTotalEffort > task.getK()) {
							em_eff += (double) m_pathList.get(i)
									/employeeTotalEffort;
							/*
							 * System.out.println("Ant"+m_nAntID+
							 * " m_pathList.get(i):"+m_pathList.get(i));
							 * System.out
							 * .println("Ant"+m_nAntID+" employeeTotalEffort:"
							 * +employeeTotalEffort);
							 * System.out.println("Ant"+m_nAntID
							 * +" em_eff:"+em_eff);
							 */
						} else {
							em_eff += (double) m_pathList.get(i) / task.getK();
							/*
							 * System.out.println("Ant"+m_nAntID+
							 * " m_pathList.get(i):"+m_pathList.get(i));
							 * System.out
							 * .println("Ant"+m_nAntID+" employeeTotalEffort:"
							 * +employeeTotalEffort);
							 * System.out.println("Ant"+m_nAntID
							 * +" em_eff:"+em_eff);
							 */
						}
					}
					if (em_eff == 0) {
						finish_time = Double.MAX_VALUE;
					} else {
						finish_time = task.getCost() / em_eff;
					}
					if (finish_time < min_finish_time) {
						min_finish_time = finish_time;
					}
					start += task.getEmployees().size();
				}

				/*
				 * }
				 * 
				 * else{ min_finish_time=Double.MAX_VALUE; }
				 */
				// 计算成本
				start = 0;
				double cost = 0;
				for (Task t : avaliableTasks) {
					for (int i = start; i < start + t.getEmployees().size(); i++) {

						double salary = t.getEmployees().get(i - start)
								.getSalary();

						int employeeTotalEffort = getEmployeeEffortInAvaliableTasks(
								t.getEmployees().get(i - start).getNumber(),
								avaliableTasks, m_pathList);
						double dedication = 0;
						if (employeeTotalEffort > t.getK()) {
							dedication = (double) m_pathList.get(i)
									/ (employeeTotalEffort*t.getK()) * min_finish_time;
						} else {
							dedication = (double) m_pathList.get(i) / t.getK()
									* min_finish_time;
						}

						cost += (salary / (double) 5000 * dedication);

					}
					start += t.getEmployees().size();
				}

				m_dPathValue = 0.5 * min_finish_time + 0.5 * cost;

				cost = 0;

				// System.out.println("Ant"+m_nAntID+"爬取的m_dPathValue:"+m_dPathValue);

			}// 如果解是可行解

			else {
				m_dPathValue = Double.MAX_VALUE;

				// System.out.println("修正不可行解");
				Random ran = new Random(System.currentTimeMillis());
				for (int i = 0; i < m_pathList.size(); i++) {
					m_pathList.remove(i);
					m_pathList
							.add(i, new Integer((int) (9 * ran.nextDouble())));
				}

			}

			// __________________________________________________________________________
			// 对信息素进行局部更新
			synchronized (graph) {
				localUpdatingRule(m_pathList, avaliableTasks);
			}

		}

		else {
			System.out.println("注意！！注意！！程序崩了！！！程序崩了！！！非战斗人员请迅速撤离！！！！！");
		}

		// 更新一下蚂蚁内部的类变量

		// System.out.println("Ant"+m_nAntID+"开始更新类变量");

		synchronized (graph) {

			// System.out.println("Ant"+m_nAntID+"正在更新类变量");

			if (better(m_dPathValue, s_dBestPathValue)) {
				// if(better(0,s_dBestPathValue)){

				String result="";
				/*for (Task task:graph.getAvaliableTasks()){

					result+="任务"+task.getNumber()+"的职工分配矩阵如下：\r\n";

					int startCount=0;
					for (int i=startCount;i<startCount+task.getEmployees().size();i++){

						+
								(double)s_bestPathList.get(startCount)/task.getK()+";\r\n";

					}


				}*/
				// System.out.println("Ant"+m_nAntID+"的爬行结果为当前最优");

				TASKNUMBER.clear();
				int start = 0;

				for (Task task : avaliableTasks) {

					result+="任务"+task.getNumber()+"的职工分配矩阵如下：\r\n";

					double em_eff = 0;// 所有职工的贡献量
					double finish_time = 0;// 在该分配下该任务完成所需要的时间
					for (int i = start; i < start + task.getEmployees().size(); i++) {

						int employeeTotalEffort = getEmployeeEffortInAvaliableTasks(
								task.getEmployees().get(i - start).getNumber(),
								avaliableTasks, m_pathList);

						if (employeeTotalEffort > task.getK()) {

							result+="职工"+task.getEmployees().get(i-start).getNumber()+"  的贡献为："+
									(double) m_pathList.get(i) / employeeTotalEffort+";\r\n";

							em_eff += (double) m_pathList.get(i)
									/ employeeTotalEffort;
						} else {

							result+="职工"+task.getEmployees().get(i-start).getNumber()+"  的贡献为："+
									(double) m_pathList.get(i) / task.getK()+";\r\n";

							em_eff += (double) m_pathList.get(i) / task.getK();
						}
					}
					if (em_eff == 0) {
						finish_time = Double.MAX_VALUE;

						result+="该任务无法被完成\r\n";

						// System.out.println("finish_time:"+finish_time);
					} else {

						finish_time = task.getCost() / em_eff;
						result+="该任务的每天完成量为： "+em_eff+";\r\n";
						result+="完成该任务所需要的时间为："+finish_time+" \r\n";
						// System.out.println("finish_time:"+finish_time);
					}


					if (finish_time == min_finish_time) {
						TASKNUMBER.add(task.getNumber());
					}
					start += task.getEmployees().size();
				}

				result+="该分配方式的适应度指标值为： "+m_dPathValue+";\r\n";

				// System.out.println("~~~TASKNUMBER.size:"+TASKNUMBER.size());

				s_dBestPathValue = m_dPathValue;
				s_bestPathList = m_pathList;

				// System.out.println("bestPathList.size:"+s_bestPathList);

				s_nLastBestPathIteration = s_antColony.getIterationCounter();

				/*
				 * System.out.print("Ant"+m_nAntID+"BestPathList:"); for(int
				 * i:s_bestPathList){ System.out.print(i); }
				 */

				// min_finish_time=0;
				time_best_value = min_finish_time;



				s_outs.println("Ant + " + m_nAntID + " 在第"
					+ s_nLastBestPathIteration + "次迭代时获得一个较好的分配方如下： \r\n"
						+ result);

			}

		}

		m_observer.update(this, null);

		if (s_antColony.done()) {
			s_outs.close();
		}

	}

	protected abstract boolean better(double dPathValue, double dBestPathValue);

	public abstract int stateTransitionRule(int r, int s);

	public abstract void localUpdatingRule(ArrayList<Integer> pathList,
			ArrayList<Task> avaliableTasks);

	public abstract boolean end();

	public static int[] getBestPath() {
		int nBestPathArray[] = new int[s_bestPathList.size()];
		for (int i = 0; i < s_bestPathList.size(); i++) {
			nBestPathArray[i] = ((Integer) s_bestPathList.get(i)).intValue();
		}

		return nBestPathArray;
	}

	public String toString() {
		return "Ant " + m_nAntID + ":" + m_nCurNode;
	}

	// 获取某一结点所属的任务在可执行任务列表avaliableTasks里的位置
	public int getTaskPositionInAvaliableTasks(int Jcount) {

		int position = 0;
		int start = 0;
		// nodePositionInTask=-1;

		for (Task task : avaliableTasks) {

			int employeeCount = task.getEmployees().size();

			if (Jcount >= start && Jcount < (start + employeeCount)) {

				nodePositionInTask = Jcount - start;
				// count++;//记录当前结点的位置

				// return position;
				break;
			} else {
				position++;
				start += employeeCount;
				// nodePositionInTask=0;
			}

		}
		// count++;//记录当前结点的位置
		return position;
	}

	// 获取某个员工在某阶段总的工作量,在估计任务的最短完成时间时会用到
	protected static int getEmployeeEffortInAvaliableTasks(int employeeNumber,
			ArrayList<Task> avaliableTasks, ArrayList<Integer> pathList) {

		int effort = 0;
		int start = 0;

		for (Task task : avaliableTasks) {
			int position = 0;

			for (Employee employee : task.getEmployees()) {

				if (employee.getNumber() == employeeNumber) {
					effort += pathList.get(start + position);
					break;
				}
				position++;
			}

			start += task.getEmployees().size();//将任务1其他员工跳过，找下一个任务的职工号为employeeNumber的贡献
		}

		return effort;
	}

	public int getEmployeeCurrentEffort(int employeeNumber,
			ArrayList<Task> avaliableTasks, int[] pathList) {

		int effort = 0;
		int start = 0;

		for (Task task : avaliableTasks) {
			int position = 0;

			for (Employee employee : task.getEmployees()) {

				if (employee.getNumber() == employeeNumber) {
					effort += pathList[start + position];
					break;
				}
				position++;
			}

			start += task.getEmployees().size();
		}

		return effort;

	}

	// 判断一个解是否是可行解
	public boolean feasible(ArrayList<Task> avaliableTasks,
			ArrayList<Integer> pathList) {

		// System.out.println("pathlist.size:"+pathList.size());

		int start = 0;

		for (Task task : avaliableTasks) {

			int position = 0;
			// 该任务所需要的技能的编号集合
			ArrayList<Integer> skillNumber = new ArrayList<Integer>();
			for (Skill skill : task.getSkills()) {
				skillNumber.add(skill.getValue());
			}

			ArrayList<Integer> skillNumber2 = (ArrayList<Integer>) skillNumber.clone();

			for (Employee employee : task.getEmployees()) {

				if (pathList.get(start + position) == 0) {
				} else {
					for (Skill skill : employee.getSkills()) {
						if (skillNumber.contains(new Integer(skill.getValue()))) {
							if (skillNumber2.contains(new Integer(skill
									.getValue()))) {
								skillNumber2.remove(new Integer(skill
										.getValue()));
							}
						}
					}
				}

				if (skillNumber2.size() == 0) {
					break;
				}
				position++;
			}

			if (skillNumber2.size() != 0) { /* System.out.println("该解是不可行解"); */
				return false;
			}
			start += task.getEmployees().size();
		}

		// System.out.println("该解是可行解");
		return true;
	}

}