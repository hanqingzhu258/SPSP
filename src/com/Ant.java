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

//�������Ҫ���� �����ǰ���״̬ת�ƹ��򣬱�����㣬�ҵ�һ�����ʵ�·����Ҳ����pathList
//ʵ��������Ϣ���б�ľֲ�����

public abstract class Ant extends Observable implements Runnable, Serializable {

	protected ArrayList<Integer> tasknumber;
	protected int TN;

	double min_finish_time = Double.MAX_VALUE;// ������������ʱ��
	protected static double time_best_value = 0;

	public AntGraph graph;

	protected int m_nAntID;

	public ArrayList<Task> avaliableTasks; // ��ִ�������б�
	private int count; // ��¼������m_pathList�е�λ��
	protected int TotalColumn; // ��ִ����������������

	protected int K = 0; // ����

	protected int nodePositionInTask = -1; // ��״̬ת�ƹ���transitionRule���õ��˸ò���

	// protected int [][][] m_path;
	protected int m_nCurNode;
	protected ArrayList<Integer> m_nStartNode = new ArrayList<Integer>();
	protected double m_dPathValue; // ��Ӧ��ָ��
	protected Observer m_observer;
	protected ArrayList<Integer> m_pathList = null;

	private static int s_nAntIDCounter = 0;
	private static PrintStream s_outs;

	public static AntColony s_antColony;

	public static ArrayList<Integer> TASKNUMBER = new ArrayList<Integer>();

	public static double s_dBestPathValue = Double.MAX_VALUE;
	public static ArrayList<Integer> s_bestPathList = new ArrayList<Integer>();
	// public static int [][][] s_bestPath=null; //�����������弰��ά����һ��ؽ�����������
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

		// final AntGraph graph=s_antColony.getGraph();//��ȡ����ͼ��
		// graph=s_antColony.getGraph();//��ȡ����ͼ��

		m_pathList = new ArrayList<Integer>();// �洢����ÿһ���ƶ��Ľ���ֵ���Ҹ�ֵ����{0,1,2,3,4,5,6,7,8,9}�е�һ��
		m_pathList.add(m_nCurNode);// �洢�������е�ÿһ�����
		count = 0;
		avaliableTasks = graph.getAvaliableTasks();// ��ȡ�����ڵ�ǰ�׶ο������е����񼯺�

		/*
		 * System.out.println("�����ڵ�ǰ�׶ο������е����񼯺�Ϊ��"); for(Task
		 * task:avaliableTasks){ System.out.print(task.getNumber()+"\t"); }
		 */

		K = avaliableTasks.get(0).getK();//��ȡTask���е�kֵ��K=9;

		TotalColumn = 0;//��ִ����������е���������Ϊ0

		for (Task task : avaliableTasks) {
			TotalColumn += task.getEmployees().size();//���п�ִ�е������Ӧ��Ա�����ĺ�
		}

		m_dPathValue = 0;// �Ը÷�ʽ��������������ɵ���������Ҫ��ʱ�� ���� ��Ӧ�Ⱥ���������ֵ
		min_finish_time = Double.MAX_VALUE;
		// ����ȶ��һ���㣺����Ӧ�Ⱥ�������ȷ��,����������ʱ�����Ϊ��Ӧ�Ⱥ���
	}

	public void start() {// ���������߳�
		graph = s_antColony.getGraph();// ��ȡ����ͼ��

		// System.out.println("graph:address"+graph);
		/*
		 * for(Task task:graph.getAvaliableTasks()){
		 * System.out.print(task.getNumber()+"\t"); } System.out.println("");
		 */
		init();
		Thread thread = new Thread(this);
		thread.setName("Ant" + m_nAntID);
		// System.out.println("Ant"+m_nAntID+"��ʼ������ȡ");
		thread.start();
	}

	public void run() {
		final AntGraph graph = s_antColony.getGraph();

		// System.out.println("graph:address"+graph);
		// System.out.println("��������ȡ·��Ϊ��");
		while (!end()) {
			int nNewNode;

			synchronized (graph) {
				// synchronized(new Integer(nodePositionInTask)){

				// if(count==TotalColumn-1){break;}

				// if(nodePositionInTask!=-1){nodePositionInTask=-1;}

				count++;// ��¼��ǰ����λ��

				int TaskPositionInAvaliableTasks = this
						.getTaskPositionInAvaliableTasks(count);

				// System.out.println("Ant"+m_nAntID+"*nodePositionInTask:"+nodePositionInTask);

				nNewNode = stateTransitionRule(TaskPositionInAvaliableTasks,
						nodePositionInTask);

				// nodePositionInTask=-1;
				// System.out.print(nNewNode);
				m_pathList.add(nNewNode);// ����½��

				// System.out.println("Ant"+m_nAntID+"*pathList.size:"+m_pathList.size());

				// System.out.print("Ant"+m_nAntID+"**"+m_pathList.size());

				// count++;//��¼��ǰ����λ��
			}

		}

		// ��Tsp������ÿ����һ������Ҫ����һ����·�����ȣ���m_dPathValue��ֵ����ͬ
		// �ڸ������У���Ҫ�����еĿɽӴ�������һ�飬Ȼ��������������������Ҫ��ʱ��
		// ����m_dPathValueֻ��������н�㶼�ѱ�����ʱ�Ÿ���һ��
		// _______________________________________________________________________
		if (m_pathList.size() == TotalColumn) {// �����н��������

			/*
			 * int start=0;//ÿ�������Ӧ�������m_nCurNode�е�λ��
			 * 
			 * for(Task task:avaliableTasks){
			 * 
			 * int em_eff=0;//����ְ���Ĺ����� int em_count=task.getEmployees().size();
			 * double finish_time=0;//�ڸ÷����¸������������Ҫ��ʱ�� for(int
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
			 * //����ɱ� start=0; double cost=0; for(Task t:avaliableTasks){
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
			int start = 0;// ÿ�������Ӧ�������m_nCurNode�е�λ��
			if (feasible(avaliableTasks, m_pathList)) {

				for (Task task : avaliableTasks) {

					double em_eff = 0;// ����ְ���Ĺ�����
					double finish_time = 0;// �ڸ÷����¸������������Ҫ��ʱ��
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
				// ����ɱ�
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

				// System.out.println("Ant"+m_nAntID+"��ȡ��m_dPathValue:"+m_dPathValue);

			}// ������ǿ��н�

			else {
				m_dPathValue = Double.MAX_VALUE;

				// System.out.println("���������н�");
				Random ran = new Random(System.currentTimeMillis());
				for (int i = 0; i < m_pathList.size(); i++) {
					m_pathList.remove(i);
					m_pathList
							.add(i, new Integer((int) (9 * ran.nextDouble())));
				}

			}

			// __________________________________________________________________________
			// ����Ϣ�ؽ��оֲ�����
			synchronized (graph) {
				localUpdatingRule(m_pathList, avaliableTasks);
			}

		}

		else {
			System.out.println("ע�⣡��ע�⣡��������ˣ�����������ˣ�������ս����Ա��Ѹ�ٳ��룡��������");
		}

		// ����һ�������ڲ��������

		// System.out.println("Ant"+m_nAntID+"��ʼ���������");

		synchronized (graph) {

			// System.out.println("Ant"+m_nAntID+"���ڸ��������");

			if (better(m_dPathValue, s_dBestPathValue)) {
				// if(better(0,s_dBestPathValue)){

				String result="";
				/*for (Task task:graph.getAvaliableTasks()){

					result+="����"+task.getNumber()+"��ְ������������£�\r\n";

					int startCount=0;
					for (int i=startCount;i<startCount+task.getEmployees().size();i++){

						+
								(double)s_bestPathList.get(startCount)/task.getK()+";\r\n";

					}


				}*/
				// System.out.println("Ant"+m_nAntID+"�����н��Ϊ��ǰ����");

				TASKNUMBER.clear();
				int start = 0;

				for (Task task : avaliableTasks) {

					result+="����"+task.getNumber()+"��ְ������������£�\r\n";

					double em_eff = 0;// ����ְ���Ĺ�����
					double finish_time = 0;// �ڸ÷����¸������������Ҫ��ʱ��
					for (int i = start; i < start + task.getEmployees().size(); i++) {

						int employeeTotalEffort = getEmployeeEffortInAvaliableTasks(
								task.getEmployees().get(i - start).getNumber(),
								avaliableTasks, m_pathList);

						if (employeeTotalEffort > task.getK()) {

							result+="ְ��"+task.getEmployees().get(i-start).getNumber()+"  �Ĺ���Ϊ��"+
									(double) m_pathList.get(i) / employeeTotalEffort+";\r\n";

							em_eff += (double) m_pathList.get(i)
									/ employeeTotalEffort;
						} else {

							result+="ְ��"+task.getEmployees().get(i-start).getNumber()+"  �Ĺ���Ϊ��"+
									(double) m_pathList.get(i) / task.getK()+";\r\n";

							em_eff += (double) m_pathList.get(i) / task.getK();
						}
					}
					if (em_eff == 0) {
						finish_time = Double.MAX_VALUE;

						result+="�������޷������\r\n";

						// System.out.println("finish_time:"+finish_time);
					} else {

						finish_time = task.getCost() / em_eff;
						result+="�������ÿ�������Ϊ�� "+em_eff+";\r\n";
						result+="��ɸ���������Ҫ��ʱ��Ϊ��"+finish_time+" \r\n";
						// System.out.println("finish_time:"+finish_time);
					}


					if (finish_time == min_finish_time) {
						TASKNUMBER.add(task.getNumber());
					}
					start += task.getEmployees().size();
				}

				result+="�÷��䷽ʽ����Ӧ��ָ��ֵΪ�� "+m_dPathValue+";\r\n";

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



				s_outs.println("Ant + " + m_nAntID + " �ڵ�"
					+ s_nLastBestPathIteration + "�ε���ʱ���һ���Ϻõķ��䷽���£� \r\n"
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

	// ��ȡĳһ��������������ڿ�ִ�������б�avaliableTasks���λ��
	public int getTaskPositionInAvaliableTasks(int Jcount) {

		int position = 0;
		int start = 0;
		// nodePositionInTask=-1;

		for (Task task : avaliableTasks) {

			int employeeCount = task.getEmployees().size();

			if (Jcount >= start && Jcount < (start + employeeCount)) {

				nodePositionInTask = Jcount - start;
				// count++;//��¼��ǰ����λ��

				// return position;
				break;
			} else {
				position++;
				start += employeeCount;
				// nodePositionInTask=0;
			}

		}
		// count++;//��¼��ǰ����λ��
		return position;
	}

	// ��ȡĳ��Ա����ĳ�׶��ܵĹ�����,�ڹ��������������ʱ��ʱ���õ�
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

			start += task.getEmployees().size();//������1����Ա������������һ�������ְ����ΪemployeeNumber�Ĺ���
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

	// �ж�һ�����Ƿ��ǿ��н�
	public boolean feasible(ArrayList<Task> avaliableTasks,
			ArrayList<Integer> pathList) {

		// System.out.println("pathlist.size:"+pathList.size());

		int start = 0;

		for (Task task : avaliableTasks) {

			int position = 0;
			// ����������Ҫ�ļ��ܵı�ż���
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

			if (skillNumber2.size() != 0) { /* System.out.println("�ý��ǲ����н�"); */
				return false;
			}
			start += task.getEmployees().size();
		}

		// System.out.println("�ý��ǿ��н�");
		return true;
	}

}