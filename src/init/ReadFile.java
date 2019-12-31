package init;

import beans.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class ReadFile implements Serializable{
	
	private File file;
	private FileReader fileReader;
	private BufferedReader bufferedReader;
	
	private String absoluteRoute=null;
	private String fileName=null;
	
	private ArrayList<Task> tasks=new ArrayList<Task>();
	private ArrayList<Employee>employees=new ArrayList<Employee>();
	private ArrayList<Arc>arcs=new ArrayList<Arc>();
	private int [ ] employeeNumber_Task;
	
	private String regex1="[=]";
	//private String regex="[.\\p{Punct}\\p{Blank}]";
	private String regex2="[.]";
	private String regex3="[\\p{Blank}]";
	private String s=null;
	
	private int number1=-2;
	private int number2=-2;
	
	private Task task=null;
	private Employee employee=null;
	
	
	public ReadFile(String absoluteRoute,String fileName){
		this.absoluteRoute=absoluteRoute;
		this.fileName=fileName;
		//System.out.println("the object is being created!");
	}
	
	//ͨ��init�����������ļ��е�����ȫ���浽��Ӧ������
	public void init(){
		// �����ı�����������ȡ�ļ�   
		try{
			
			//System.out.println("the initial process is going to begin!");
			
			file=new File(absoluteRoute,fileName);
			fileReader=new FileReader(file);
			bufferedReader=new BufferedReader(fileReader);
			
			while((s=bufferedReader.readLine())!= null){
				
				//System.out.println("the word is being resolved!");
				
				if(s.startsWith("#")){continue;}//������ʼ������
				
				String [] w1=s.split(regex1);
				String [] w2=w1[0].split(regex2);
				String [] w3=w1[1].split(regex3);
				
				//String [ ] words=s.split(regex);
				String [] words=new String[5];
				System.arraycopy(w2, 0, words, 0, w2.length);
				System.arraycopy(w3, 0, words, w2.length,w3.length );
				
				/*
				System.out.println("");
				for(String word:words){
					System.out.print(word+" ");
				}
				System.out.println("");
				*/
				
				//-------------------------------------------------------------------------------------------------------------------------
				//�����е���������Ӧ��ȫ��������ӵ���Ϊ��tasks���Ķ�̬������
				//���������������Ľ���������ͨ����ȡtasks�ĳ��Ȼ�ȡ
				//ע����tasks�в�û�а�������ı�Ž�������ֻ�ǰ��������ļ��е�˳����
				if(words[0].equals("task")){
					//System.out.println("hhhhhhhhhhhhhhhhhhhhhh");
					if(words[1].equals("number")){/*System.out.println("��������ѭ��");*/continue;}//�����������
					else{
						number1=Integer.parseInt(words[1]);
						//System.out.println(number1);
						
						//�жϸ������Ƿ���һ���µ�����ͨ�����������Լ��ɵ�֪��
						//������������񣬾ʹ�������ȡ����Ӧ���Ѿ����ڵ�����
						//����������񣬾�ֱ��newһ������Task����
						for(Task task:tasks){
							if(task.getNumber()==number1){
								this.task=task;
								//System.out.println("---"+task.getNumber());
								break;
							}
						}
						if(task==null){
							task=new Task();
							//System.out.println("---!!!"+task.getNumber());
						}
						
				
						
						task.setNumber(number1);//�������
					
						//System.out.println(task.getNumber());
						
						//�Ը�����ļ������Խ��в���
						if(words[2].equals("skill")){
							
							//System.out.println("result of skill is ok!");
							
							if(words[3].equals("number")){/*System.out.println("��������ѭ��");*/task=null;number1=-2;continue;}//����������Ҫ�ļ�����
							else{
								Skill skill=new Skill();
								skill.setNumber(Integer.parseInt(words[3]));
								skill.setValue(Integer.parseInt(words[4]));
								
								/*System.out.println("skill���"+skill.getNumber());
								System.out.println("�ü��ܶ�Ӧ��ֵ"+skill.getValue());
								*/
								task.getSkills().add(skill);
								
								/*
								for(Skill sk:task.getSkills()){
									System.out.println(sk.getValue());
								}//��֤�������Ƿ񱻳ɹ��洢
								*/
							}
						}
						
						//�Ը�����Ļ������Խ��в���
						else{
							task.setCost(Double.parseDouble(words[3]));
						}
						
						//����������������д��ڣ���ô�滻ԭ����
						//��������������У���ô�ͽ���������ӵ�������
						
					}
					
					int logo=-1;
					for(Task task:tasks){
						if(task.getNumber()==number1){
							task=this.task;
							logo=number1;
							break;
						}
					}
					if(logo==-1){tasks.add(task);}
					else{logo=-1;}
					

					
					/*
					System.out.println("---------------------------------------");
					for(Task task:tasks){
						if(task.getNumber()==number1){
							System.out.println(task.getNumber());
							System.out.println("��������Ҫ���ܣ�"+task.getSkills().size()+"��");
							for(Skill skil:task.getSkills()){
								System.out.println("skill���"+skil.getNumber());
								System.out.println("�ü��ܶ�Ӧ��ֵ"+skil.getValue());
							}
							System.out.println("����"+task.getNumber()+"�ɱ�Ϊ��"+task.getCost());
						}
					}
					*/
					//��֤ͨ��
				
					task=null;
					number1=-2;
					
				}
				
				
				
				//------------------------------------------------------------------------------------------------------------------------------
				
				//�����е�ְ��������Ӧ��ȫ��������ӵ���Ϊ��employees���Ķ�̬������
				//������ְ�������Ľ���������ͨ����ȡemployees�ĳ��Ȼ�ȡ
				//ע����employees�в�û�а���ְ���ı�Ž�������ֻ�ǰ��������ļ��е�˳����
				else if(words[0].equals("employee")){
					if(words[1].equals("number")){/*System.out.println("��������ѭ��");*/continue;}//��Ա��������
					else{
						number2=Integer.parseInt(words[1]);
						
						//�жϸ�ְ���Ƿ���һ���µ�ְ����ͨ��ְ��������Լ��ɵ�֪��
						//���������ְ�����ʹ�������ȡ����Ӧ���Ѿ����ڵ�ְ��
						//�������ְ������ֱ��newһ��ְ��Employee����
						for(Employee employee:employees){
							if(employee.getNumber()==number2){
								this.employee=employee;
								break;
							}
						}
						if(employee==null){
							employee=new Employee();
						}
						
						employee.setNumber(number2);//ְ������
						
						//�Ը�ְ���ļ������Խ��в���
						if(words[2].equals("skill")){
							if(words[3].equals("number")){/*System.out.println("��������ѭ��");*/employee=null;number2=-2;continue;}//��ְ�����߱��ļ�����
							else{
								Skill skill=new Skill();
								skill.setNumber(Integer.parseInt(words[3]));
								skill.setValue(Integer.parseInt(words[4]));
								employee.getSkills().add(skill);
							}
						}
						
						//�Ը�ְ����н�����Խ��в���
						else{
							employee.setSalary(Double.parseDouble(words[3]));
						}
						
						//���ְ�����������д��ڣ���ô�滻ԭְ��
						//���ְ�����������У���ô�ͽ���ְ����ӵ�������
						int logo=-1;
						for(Employee employee:employees){
							if(employee.getNumber()==number2){
								employee=this.employee;
								logo=number2;
								break;
							}
						}
						if(logo==-1){employees.add(employee);}
						else{logo=-1;}
					}
					employee=null;
					number2=-2;
				}
				
				//------------------------------------------------------------------------------------------------------------------------------			
				//�����еĻ�������Ӧ��ȫ��������ӵ���Ϊ��tpg���Ķ�̬������
				//�����˻������Ľ���������ͨ����ȡtpg�ĳ��Ȼ�ȡ
				//ע����tpg�в�û�а��ջ��ı�Ž�������ֻ�ǰ��������ļ��е�˳����
				else if(words[0].equals("graph")){
					if(words[2].equals("number")){/*System.out.println("��������ѭ��");*/continue;}
					else{
						Arc arc=new Arc();
						arc.setNumber(Integer.parseInt(words[2]));
						arc.setFirstNumber(Integer.parseInt(words[3]));
						arc.setLastNumber(Integer.parseInt(words[4]));
						arcs.add(arc);
					}
				}	
			}
		}
		catch(IOException e){
			System.out.println("Reading file is fail!");
		}	
	}
	
	
	public ArrayList<Task> getTasks(){
		return tasks;
	}
	public ArrayList<Employee> getEmployees(){
		return employees;
	}
	public ArrayList<Arc> getArcs(){
		return arcs;
	}
	
	//�Ի��������̬�����������
	public void sort(){
		Collections.sort(tasks, new Comparator<Task>(){
			public int compare(Task t1,Task t2){
				// TODO Auto-generated method stub
				return t1.getNumber()-t2.getNumber();
			}
		});//����Task��number���Զ�tasks���д�С��������
		
		Collections.sort(employees, new Comparator<Employee>(){
			public int compare(Employee e1,Employee e2){
				// TODO Auto-generated method stub
				return e1.getNumber()-e2.getNumber();
			}
		});//����Employee��number���Զ�employees���д�С��������
		
		Collections.sort(arcs, new Comparator<Arc>(){
			public int compare(Arc a1,Arc a2){
				// TODO Auto-generated method stub
				return a1.getNumber()-a2.getNumber();
			}
		});//����Arc��number���Զ�arcs���д�С��������
		
		for(Task task:tasks){
			Collections.sort(task.getSkills(), new Comparator<Skill>(){
				public int compare(Skill s1,Skill s2){
					// TODO Auto-generated method stub
					return s1.getNumber()-s2.getNumber();
				}
			});//����Skill��number���Զ�ÿһ��Task��Employee�е�skills������д�С��������
		}
		
		for(Employee employee:employees){
			Collections.sort(employee.getSkills(), new Comparator<Skill>(){
				public int compare(Skill s1,Skill s2){
					// TODO Auto-generated method stub
					return s1.getNumber()-s2.getNumber();
				}
			});//����Skill��number���Զ�ÿһ��Task��Employee�е�skills������д�С��������
		}
	}

	//�����Ա���Ķ�Ӧ
	public void getEmployee_Task(){
		
		int logo=-1;
		
		// ����ĳ�����й��׵�Ա����ѡ����
		//��������ĳ�������Ա�����뵽����������
		for(int i=0;i<tasks.size();i++){
			for(int j=0;j<employees.size();j++){
				for(int k=0;k<employees.get(j).getSkills().size();k++){
					for(int m=0;m<tasks.get(i).getSkills().size();m++){
						if(tasks.get(i).getSkills().get(m).getValue()==employees.get(j).getSkills().get(k).getValue()){
							tasks.get(i).getEmployees().add(employees.get(j));
							logo=1;
							break;
						}
					}
					if(logo>0){logo=-1;break;}
				}
			}
		}
		
		//��ÿ��������ѡ���Ա������Ž�������
		for(int i=0;i<tasks.size();i++){
			Collections.sort(tasks.get(i).getEmployees(), new Comparator<Employee>(){
				public int compare(Employee e1,Employee e2){
					// TODO Auto-generated method stub
					return e1.getNumber()-e2.getNumber();
				}
			});
		}
		
	}
	
	//��ȡÿ����������ӦԱ��������
	public int[ ] getEmployeeNumber_Task(){
		employeeNumber_Task=new int[tasks.size()];
		for(int i=0;i<tasks.size();i++){
			employeeNumber_Task[i]=tasks.get(i).getEmployees().size();
			//System.out.println(employeeNumber_Task[i]);
		}
		return employeeNumber_Task;
	}
	
}
