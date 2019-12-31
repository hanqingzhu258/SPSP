package com;

import java.io.Serializable;
import java.util.ArrayList;

import beans.Arc;
import beans.Employee;
import beans.Task;
import init.ReadFile;
import beans.Skill;

//�����������Ҫ������
//һ��ͨ�������ļ�����ȡ���񼯺ϡ�ְ�����ϡ��Լ������������˳��
//���Ǹ�������ֽ׶ο���ִ�е�����
//���Ǹ���֪ͨÿ������ͳһ���г�ʼ����Ϣ�ر�

public class AntGraph implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public  static  ArrayList<Task>tasks;	//�����б�
	public  ArrayList<Employee>employees;  //ְ���б�
	public  static  ArrayList<Arc>arcs;		//���б����ڴ洢���������ȼ���
	public  int [] employeeNumber_Task;	//ÿ������Ŀ�ʤ��ְ������
	
	//public  ArrayList<Task>avaliableTasks=new ArrayList<Task>();
	public  ArrayList<Task> avaliableTasks; //��ִ�е������б�ļ���
	
	private double [][] m_delta; 
	private double     m_dTau0; //��ʼ��Ϣ�ص�ֵ
	
	//��ʼ����Ϣ��ʱ�õ��ġ�·����������Ҫ���ǣ���Ϊdeltaֻ�Ǵ���Tsp�����и��ڵ�֮��ľ���
	public AntGraph(double [][] delta){	
		initSet(delta);	
	}
	
	//��Ϊ�ں��������л���Ҫ�õ���Щ����������Ϊ������ý����װ��һ�����巽��
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
	//��ʼ����ÿ���������Ϣ�أ�����Ϣ�ص����ɷ���
	public void resetTau(){
		//���ɳ�ʼ��Ϣ�ص�ֵ
		double dAverage = averageDelta();
		m_dTau0 = (double)1 / ((double)m_delta.length * (0.5 * dAverage));
		
        System.out.println("Average: " + dAverage);
        System.out.println("Tau0: " + m_dTau0);
        //��ÿ���������Ϣ�س�ʼ��
        for(Task task:tasks){
        	task.initTau(m_dTau0);
        }
		
	}
	//���ض�ά����m_delta����Ԫ�صĺ͵�ƽ��ֵ��m_delta���ܴ�����Ϣ��
	public double averageDelta(){
		return average(m_delta);
	}
	//���ض�ά����m_delta����Ԫ�صĺ͵�ƽ��ֵ��m_delta���ܴ�����Ϣ��
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
	 }*/         //����ʽ��Ϣ��������ͨ��·�����ȵĵ�����������Ӧ������ʽ��Ϣ
	
	//����֮������PSP������˵�����Ѱ��һ���Ƚ���Ч������ʽ��ϢҲ��һ��ֵ��˼��������
	//��ǰ����������ʽ��Ϣ����ƣ�ֱ��ʹ����Ϣ����Ϊ��
	
	//��ȡ��ʼ����Ϣ�ص�ֵ
	 public synchronized double getTau0(){
	 	return m_dTau0;
	 }
	 //��ȡ��ǰ��ִ�е�����
	 public ArrayList<Task> getAvaliableTasks(){
		 //System.out.println("���ڻ�ȡ��ִ�����񼯺�");
		 avaliableTasks=new ArrayList<Task>();
		 int logo=1;
		 for(Task task:tasks){
			 for(Arc arc:arcs){
				 if(task.getNumber()==arc.getLastNumber()&&arc.getFirstNumber()>=0){logo=-1;break;}
				
			 }
			 if(logo<0){}//��������ǰ����
			 else{avaliableTasks.add(task);}//������û��ǰ���㣬���Է���Ա��
			 logo=1;
		 }
		 //System.out.println("��ִ�����񼯺ϻ�ȡ���");
		 return avaliableTasks;
	 }
	 
	 public ArrayList<Task> getTasks(){
		 return tasks;
	 }
	 
	 public ArrayList<Arc> getArcs(){
		 return arcs;
	 }
	 //�ж��Ƿ��������������
	 public Task getTaskByNumber(int taskNumber){
		 
		 for(Task task:tasks){
			 if(task.getNumber()==taskNumber){
				 return task;
			 }
		 }
		 System.out.println("�����񲻴��ڣ�������������������ע�����޸ģ���������������");
		 return null;
	 }
	 
	 public int[] getEmployeeNumber_Task(){
		 return employeeNumber_Task;
	 }
	 
}
