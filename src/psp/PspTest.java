package psp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import beans.Task;

import com.AntGraph;

public class PspTest {

	private static Random s_ran = new Random(System.currentTimeMillis());

	public static void main(String[] args) {

		System.out.println("AntColonySystem for PSP");

		int nAnts = 0;    
		int nIterations = 0;   
		int nRepetitions = 0;  
		int N = 0;	

		int StateNumber = 0;
		int state = -1;

		nAnts = 10;
		nIterations = 200;
		nRepetitions = 100;
		N = 20;
		state = 0;

		if (nAnts == 0 || nIterations == 0 || nRepetitions == 0) {

			System.out.println("One of the parameters is wrong");
			return;

		}

		double d[][] = new double[N][N];

		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				d[i][j] = s_ran.nextDouble();
				d[j][i] = d[i][j];
			}
		}

		AntGraph graph = new AntGraph(d);

		// System.out.println("graph:address"+graph);

		/*
		 * AntGraph graph = null; try{ ByteArrayOutputStream out=new
		 * ByteArrayOutputStream(); ObjectOutputStream objectOut=new
		 * ObjectOutputStream(out); objectOut.writeObject(graph0);
		 * ByteArrayInputStream in=new ByteArrayInputStream(out.toByteArray());
		 * ObjectInputStream objectIn=new ObjectInputStream(in);
		 * graph=(AntGraph)objectIn.readObject(); }catch(Exception e){}
		 */
		/*
		 * for(Task task:graph.getAvaliableTasks()){
		 * System.out.print(task.getNumber()+"\t"); }
		 */
//		int[] employeeNumber_task = graph.getEmployeeNumber_Task();
//		for (int i : employeeNumber_task) {
//			System.out.print("每个任务可胜任的员工数量"+i + "\t");
//		}
//		System.out.println("");
//
//		StateNumber = graph.getTasks().size();
//
//		System.out.println("StateNumber" +"共有"+StateNumber+"多任务");

		try {

			PrintStream outs2 = new PrintStream(new FileOutputStream(
					"data/results/" + graph.getTasks().size() + "x" + nAnts + "x"
							+ nIterations + "_results.txt"));

			// for(int i = 0; i < nRepetitions; i++)
			// {

			// graph.initSet(d);
			// graph.resetTau();锟斤拷一锟阶讹拷锟角凤拷要锟斤拷锟斤拷锟斤拷一锟阶段碉拷锟斤拷息锟截憋拷也锟斤拷一锟斤拷值锟矫癸拷注锟斤拷锟斤拷锟斤拷

			// for(int i=0;i<nRepetitions;i++){
			// while(state<StateNumber){

			double totalTime = 0;
			List<Integer> taskFinishedOrder=new ArrayList<Integer>();

			while (graph.getTasks().size() != 0) {

				System.out.println("AntColony" + state + "开始爬行");

				AntColony4Psp antColony = new AntColony4Psp(graph, nAnts,
						nIterations);
				antColony.start();
				
				totalTime+=antColony.getBestTime();
				
				outs2.println(state + "," + antColony.getBestPathValue() + ","
						+ antColony.getLastBestPathIteration() + ";^^^"
						+ antColony.getBestTime());
				state++;
 				// }
				// }
				for (Integer i:Ant4Psp.TASKNUMBER){
					taskFinishedOrder.add(i);
				}

				System.out
						.println("--------------------------------------------------------------------------------"
								+ "--------------------------------------------------------------------------------"
								+ "--------------------------------------------------------------------------------"
								+ "--------------------------------------------------------------------------------");
			}

			outs2.println("总时间为"+totalTime);
			outs2.println("任务的先后顺序"+taskFinishedOrder.toString());

			System.out.println("完成");
			outs2.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
