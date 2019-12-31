package com;

import java.io.Serializable;
import java.util.ArrayList;

import beans.Arc;
import beans.Employee;
import beans.Task;
import init.ReadFile;
import beans.Skill;

//该类的作用主要有三个
//一是通过解析文件，获取任务集合、职工集合、以及各任务的优先顺序
//二是负责管理，现阶段可以执行的任务
//三是负责通知每个任务统一进行初始化信息素表

public class AntGraph implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public  static  ArrayList<Task>tasks;	//任务列表
	public  ArrayList<Employee>employees;  //职工列表
	public  static  ArrayList<Arc>arcs;		//弧列表，用于存储任务间的优先级别
	public  int [] employeeNumber_Task;	//每个任务的可胜任职工数量
	
	//public  ArrayList<Task>avaliableTasks=new ArrayList<Task>();
	public  ArrayList<Task> avaliableTasks; //可执行的任务列表的集合
	
	private double [][] m_delta; 
	private double     m_dTau0; //初始信息素的值
	
	//初始化信息素时用到的“路径”参数需要考虑，因为delta只是代表Tsp问题中各节点之间的距离
	public AntGraph(double [][] delta){	
		initSet(delta);	
	}
	
	//因为在后续操作中还需要用到这些方法，所以为方便调用将其封装成一个整体方法
	public void initSet(double delta[][]){
		ReadFile readFile=new ReadFile("data/example/","T10-E5-S5.txt");
		readFile.init();
		readFile.sort();
		readFile.getEmployee_Task();
		tasks=readFile.getTasks();
		employees=readFile.getEmployees();
		arcs=readFile.getArcs();
		employeeNumber_Task=readFile.getEmployeeNumber_Task();
		
		this.m_delta=delta;
		resetTau();
	}
	//初始化，每个任务的信息素，及信息素的生成方法
	public void resetTau(){
		//生成初始信息素的值
		double dAverage = averageDelta();
		m_dTau0 = (double)1 / ((double)m_delta.length * (0.5 * dAverage));
		
        System.out.println("Average: " + dAverage);
        System.out.println("Tau0: " + m_dTau0);
        //对每个任务的信息素初始化
        for(Task task:tasks){
        	task.initTau(m_dTau0);
        }
		
	}
	//返回二维数组m_delta所有元素的和的平均值，m_delta可能代表信息素
	public double averageDelta(){
		return average(m_delta);
	}
	//返回二维数组m_delta所有元素的和的平均值，m_delta可能代表信息素
	private double average(double matrix[][])
    {
        double dSum = 0;
        for(int r = 0; r < matrix.length; r++)
        {
            for(int s = 0; s < matrix[0].length; s++)
            {
                dSum += matrix[r][s];
            }
        }
        
        double dAverage = dSum / (double)(matrix.length *matrix[0].length);
        
        return dAverage;
    }
	
	 /*public synchronized double delta(int r, int s){
		 return m_delta[r][s];
	 }*/
	 
	/* public synchronized double etha(int r, int s){
		 return ((double)1) / delta(r, s);
	 }*/         //启发式信息————通过路径长度的倒数来设置相应的启发式信息
	
	//换言之，对于PSP问题来说，如何寻找一个比较有效的启发式信息也是一个值得思考的问题
	//当前忽略了启发式信息的设计，直接使用信息素作为向导
	
	//获取初始化信息素的值
	 public synchronized double getTau0(){
	 	return m_dTau0;
	 }
	 //获取当前可执行的任务
	 public ArrayList<Task> getAvaliableTasks(){
		 //System.out.println("正在获取可执行任务集合");
		 avaliableTasks=new ArrayList<Task>();
		 int logo=1;
		 for(Task task:tasks){
			 for(Arc arc:arcs){
				 if(task.getNumber()==arc.getLastNumber()&&arc.getFirstNumber()>=0){logo=-1;break;}
				
			 }
			 if(logo<0){}//该任务有前向结点
			 else{avaliableTasks.add(task);}//该任务没有前向结点，可以分配员工
			 logo=1;
		 }
		 //System.out.println("可执行任务集合获取完毕");
		 return avaliableTasks;
	 }
	 
	 public ArrayList<Task> getTasks(){
		 return tasks;
	 }
	 
	 public ArrayList<Arc> getArcs(){
		 return arcs;
	 }
	 //判断是否存在这样的任务
	 public Task getTaskByNumber(int taskNumber){
		 
		 for(Task task:tasks){
			 if(task.getNumber()==taskNumber){
				 return task;
			 }
		 }
		 System.out.println("该任务不存在！！！！！！！！！请注意差错修改！！！！！！！！");
		 return null;
	 }
	 
	 public int[] getEmployeeNumber_Task(){
		 return employeeNumber_Task;
	 }
	 
}
