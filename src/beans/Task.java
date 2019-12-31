package beans;

import java.io.Serializable;
import java.util.ArrayList;

public class Task implements Serializable{

		private int k=9;//职工工作量分配的维度，k=9即员工可以有十种不同程度的付出，即0/9，1/9，……，9/9
		
		private int number=-2; //序号
		private double cost; //成本
		private ArrayList<Skill> skills=new ArrayList<Skill>();//技能列表
		private ArrayList<Employee> employees=new ArrayList<Employee>();//职工列表
		private boolean FINISHED=false;//任务是否已经被完成，默认未完成
		
		//private double [][] m_tau=new double [k+1][employees.size()];//信息素列表
		
		private ArrayList<ArrayList<Double>>m_tau=new ArrayList<ArrayList<Double>>();//相当于二维数组
		private ArrayList<Double> K0=new ArrayList<Double>();//一维数组
		private ArrayList<Double> K1=new ArrayList<Double>();
		private ArrayList<Double> K2=new ArrayList<Double>();
		private ArrayList<Double> K3=new ArrayList<Double>();
		private ArrayList<Double> K4=new ArrayList<Double>();
		private ArrayList<Double> K5=new ArrayList<Double>();
		private ArrayList<Double> K6=new ArrayList<Double>();
		private ArrayList<Double> K7=new ArrayList<Double>();
		private ArrayList<Double> K8=new ArrayList<Double>();
		private ArrayList<Double> K9=new ArrayList<Double>();
		
		public Task(){	
			m_tau.add(K0);
			m_tau.add(K1);
			m_tau.add(K2);
			m_tau.add(K3);
			m_tau.add(K4);
			m_tau.add(K5);
			m_tau.add(K6);
			m_tau.add(K7);
			m_tau.add(K8);
			m_tau.add(K9);
		}
		
		public void setNumber(int number){
			this.number=number;
		}
		public int getNumber(){
			return number;
		}
		
		public void setCost(double cost){
			this.cost=cost;
		}
		public double getCost(){
			return cost;
		}
		
		public void setSkills(ArrayList<Skill> skills){
			this.skills=skills;
		}
		public ArrayList<Skill> getSkills(){
			return skills;
		}
		
		public void setFinished(boolean finished){
			FINISHED=finished;
		}
		public boolean getFinished(){
			return FINISHED;
		}
		
		public void setEmployees(ArrayList<Employee> employees){
			this.employees=employees;
		}
		public ArrayList<Employee> getEmployees(){
			return employees;
		}

		//初始化每一个任务对应的信息素表
		public void initTau(double m_dTau0){
			//向k0、k1...数组里面加入m_dTau0
			for(int i=0;i<k+1;i++){
				for(int j=0;j<employees.size();j++){
					m_tau.get(i).add(m_dTau0);
				}
			}
			
		}
		//返回kr一维数组里面的第s-1个元素
		public synchronized double getTau(int r,int s){
			return m_tau.get(r).get(s);
		}
		//返回第r-1个员工的工资（薪酬）的倒数
		public synchronized double getHeristic(int r){
			
			//System.out.println("heristic:$$$$$$$$"+1/employees.get(r).getSalary());
			
			return 1/employees.get(r).getSalary();
		}
		//更新kr一维数组里面第s-1个元素，更新后值为value
		public synchronized void updateTau(int r, int s, double value)
	    {
			//System.out.println("正在更新信息素");
			//删除kr一维数组里面第s-1个元素
			m_tau.get(r).remove(s);
			//向kr一维数组里面第s-1个元素添加值为value的元素
			m_tau.get(r).add(s, value);
			//System.out.println("信息素更新完毕");
	    }
		
		public void setK(int k){
			this.k=k;
		}
		
		public int getK(){
			return k;
		}
		
}
