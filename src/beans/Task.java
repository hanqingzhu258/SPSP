package beans;

import java.io.Serializable;
import java.util.ArrayList;

public class Task implements Serializable{

		private int k=9;//ְ�������������ά�ȣ�k=9��Ա��������ʮ�ֲ�ͬ�̶ȵĸ�������0/9��1/9��������9/9
		
		private int number=-2; //���
		private double cost; //�ɱ�
		private ArrayList<Skill> skills=new ArrayList<Skill>();//�����б�
		private ArrayList<Employee> employees=new ArrayList<Employee>();//ְ���б�
		private boolean FINISHED=false;//�����Ƿ��Ѿ�����ɣ�Ĭ��δ���
		
		//private double [][] m_tau=new double [k+1][employees.size()];//��Ϣ���б�
		
		private ArrayList<ArrayList<Double>>m_tau=new ArrayList<ArrayList<Double>>();//�൱�ڶ�ά����
		private ArrayList<Double> K0=new ArrayList<Double>();//һά����
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

		//��ʼ��ÿһ�������Ӧ����Ϣ�ر�
		public void initTau(double m_dTau0){
			//��k0��k1...�����������m_dTau0
			for(int i=0;i<k+1;i++){
				for(int j=0;j<employees.size();j++){
					m_tau.get(i).add(m_dTau0);
				}
			}
			
		}
		//����krһά��������ĵ�s-1��Ԫ��
		public synchronized double getTau(int r,int s){
			return m_tau.get(r).get(s);
		}
		//���ص�r-1��Ա���Ĺ��ʣ�н�꣩�ĵ���
		public synchronized double getHeristic(int r){
			
			//System.out.println("heristic:$$$$$$$$"+1/employees.get(r).getSalary());
			
			return 1/employees.get(r).getSalary();
		}
		//����krһά���������s-1��Ԫ�أ����º�ֵΪvalue
		public synchronized void updateTau(int r, int s, double value)
	    {
			//System.out.println("���ڸ�����Ϣ��");
			//ɾ��krһά���������s-1��Ԫ��
			m_tau.get(r).remove(s);
			//��krһά���������s-1��Ԫ�����ֵΪvalue��Ԫ��
			m_tau.get(r).add(s, value);
			//System.out.println("��Ϣ�ظ������");
	    }
		
		public void setK(int k){
			this.k=k;
		}
		
		public int getK(){
			return k;
		}
		
}
