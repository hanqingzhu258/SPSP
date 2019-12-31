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

//�����������Ҫ�����¼��㣺
//ά������graph
//�������ϲ�����������ϵ�����
//����ȫ�ָ��£����ڸ�pspģ����ȫ�ָ���ʲô��һ���д���ȶ������

public abstract class AntColony implements Observer, Serializable {

	protected PrintStream m_outs;

	protected AntGraph m_graph; //����ͼ�����Կ������ϵ���Ѱ·���Ŀ���
	protected Ant[] m_ants;			//���ϼ���
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
		
		//��ӡ�����ڿ�ִ�е��������
		for (Task task : m_graph.getAvaliableTasks()) {
			System.out.print(task.getNumber() + "��"+"\t");
		}
		System.out.println("");
		//����Ӧ������ֵ
		m_nAnts = nAnts;
		m_nIterations = nIterations;
		s_nIDCounter++;
		m_nID = s_nIDCounter;
	}

	public synchronized void start() {
		//m_ants��Ant���͵�һά����
		m_ants = createAnts(m_graph, m_nAnts);

		m_nIterCounter = 0;
		//�γ��ļ��洢
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
				// System.out.println("���ڽ��и��£���������");
				//�������������
				//globalUpdatingRule();
			}

		}

		if (m_nIterCounter == m_nIterations) {

			synchronized (m_graph) {
				// System.out.println("���ڽ��и��£���������");
				//globalUpdatingRule();
			}

			/*
			 * for(Task task:m_graph.getTasks()){
			 * System.out.println("task"+task.getNumber()); for(int
			 * i=0;i<10;i++){ for(int j=0;j<task.getEmployees().size();j++){
			 * System.out.print(task.getTau(i, j)+"\t"); }
			 * System.out.println(""); } System.out.println("@@@@@@@@@@@@@"); }
			 */

			int start = 0;// ÿ�������Ӧ�������m_nCurNode�е�λ��

			// Ӧ���������ҳ�������ɵĹ�����Ȼ�󽫸ù�������graph��tasks�ｫ�������޳���Ȼ��arcs���ֵΪ�������number��firstNumber����Ϊ-1
			// ����graph�ڻ�ȡ��ִ�е�avaliableTasksʱ���Ϳ���ѡ�����е�ǰ�����Ѿ���ɵ�������
			// ��Ҫ���������cost��������Ҫ����������ȥԱ�����⼸�����ɵ�������
			ArrayList<Integer> pathList = Ant4Psp.s_bestPathList;

			// System.out.println("Ant4Psp.s_bestPathList.size()"+Ant4Psp.s_bestPathList.size());
			result="";
			for (Task task : m_graph.getAvaliableTasks()) {

				/*
				 * int em_eff=0;//����ְ���Ĺ����� int
				 * em_count=task.getEmployees().size(); for(int
				 * i=start;i<start+em_count;i++){ em_eff+=pathList.get(i); }
				 */

				double em_eff = 0;// ����ְ���Ĺ�����
				double finish_time = 0;// �ڸ÷����¸������������Ҫ��ʱ��
				for (int i = start; i < start + task.getEmployees().size(); i++) {

					int employeeTotalEffort = Ant
							.getEmployeeEffortInAvaliableTasks(task
									.getEmployees().get(i - start).getNumber(),
									m_graph.getAvaliableTasks(), pathList);

					if (employeeTotalEffort > task.getK()) {
						em_eff += (double) pathList.get(i)/ employeeTotalEffort;//pathList.get(i)/ employeeTotalEffort��ȡĳ������ְ����Ϊi��Ա������ռ�����ܹ��׵İٷֱ�
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
				 * }//�ҵ������񣬲��ı�����Ӧ��ֵ
				 */



				for (Task t : m_graph.getTasks()) {
					if (t.getNumber() == task.getNumber()) {

						result+="����"+t.getNumber()+"����ɵĹ�����Ϊ�� "+(em_eff
								* Ant4Psp.time_best_value)+"��\r\n";

						t.setCost(task.getCost() - em_eff
								* Ant4Psp.time_best_value);

						result+="����"+t.getNumber()+"ʣ��Ĺ�����Ϊ�� "+t.getCost()+"��\r\n";

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
					System.out.print("����"+number + "���"+"\n");
				}
				System.out
						.println("**************************************************");

				// m_graph.getTasks().remove(Ant4Psp.TASKNUMBER.get(i));//graph��tasks��ȥ��������

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

				System.out.println("��ʣ"+m_graph.getTasks().size()+"������");
				// m_graph.getTasks().remove(0);
				// System.out.println(m_graph.getTasks().size());
				System.out.println("�����Ƴ�������" + Ant4Psp.TASKNUMBER.get(i));

				/*removedTaskNumber=Ant4Psp.TASKNUMBER.get(i);*/

				System.out.println("�����޸Ļ�");
				// for(Arc arc:m_graph.getArcs()){
				for (Arc arc : m_graph.getArcs()) {
					if (arc.getFirstNumber() == Ant4Psp.TASKNUMBER.get(i)) {
						arc.setFirstNumber(-1);
					}
				}
				System.out.println("���������");
				// Ant4Psp.TASKNUMBER=new ArrayList<Integer>();
			}

			/*
			 * m_graph.getTasks().remove(Ant4Psp.TASKNUMBER);//graph��tasks��ȥ��������
			 * for(Arc arc:m_graph.getArcs()){
			 * if(arc.getFirstNumber()==Ant4Psp.TASKNUMBER){
			 * arc.setFirstNumber(new Integer(-1)); } }
			 */

			m_outs.println("����"+Ant4Psp.TASKNUMBER.toString()+"������ɡ�");
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
	//��������ͼ��
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
//���󷽷�
	protected abstract Ant[] createAnts(AntGraph graph, int ants);
//���󷽷�
	protected abstract void globalUpdatingRule();
}
