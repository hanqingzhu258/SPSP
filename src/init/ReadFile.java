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
	
	//通过init（）方法将文件中的数据全部存到相应的数组
	public void init(){
		// 创建文本输入流，读取文件   
		try{
			
			//System.out.println("the initial process is going to begin!");
			
			file=new File(absoluteRoute,fileName);
			fileReader=new FileReader(file);
			bufferedReader=new BufferedReader(fileReader);
			
			while((s=bufferedReader.readLine())!= null){
				
				//System.out.println("the word is being resolved!");
				
				if(s.startsWith("#")){continue;}//忽略起始的两行
				
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
				//将所有的任务极其相应的全部属性添加到名为“tasks”的动态数组中
				//忽略了任务数量的解析，但可通过获取tasks的长度获取
				//注意在tasks中并没有按照任务的编号进行排序，只是按照生成文件中的顺序存放
				if(words[0].equals("task")){
					//System.out.println("hhhhhhhhhhhhhhhhhhhhhh");
					if(words[1].equals("number")){/*System.out.println("跳出本次循环");*/continue;}//任务的总数量
					else{
						number1=Integer.parseInt(words[1]);
						//System.out.println(number1);
						
						//判断该任务是否是一个新的任务（通过任务编号属性即可得知）
						//如果不是新任务，就从数组中取出相应的已经存在的任务
						//如果是新任务，就直接new一个任务Task对象
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
						
				
						
						task.setNumber(number1);//任务编码
					
						//System.out.println(task.getNumber());
						
						//对该任务的技能属性进行操作
						if(words[2].equals("skill")){
							
							//System.out.println("result of skill is ok!");
							
							if(words[3].equals("number")){/*System.out.println("跳出本次循环");*/task=null;number1=-2;continue;}//该任务所需要的技能数
							else{
								Skill skill=new Skill();
								skill.setNumber(Integer.parseInt(words[3]));
								skill.setValue(Integer.parseInt(words[4]));
								
								/*System.out.println("skill编号"+skill.getNumber());
								System.out.println("该技能对应的值"+skill.getValue());
								*/
								task.getSkills().add(skill);
								
								/*
								for(Skill sk:task.getSkills()){
									System.out.println(sk.getValue());
								}//验证任务技能是否被成功存储
								*/
							}
						}
						
						//对该任务的花费属性进行操作
						else{
							task.setCost(Double.parseDouble(words[3]));
						}
						
						//如果任务已在数组中存在，那么替换原任务
						//如果任务不在数组中，那么就将该任务添加到数组中
						
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
							System.out.println("该任务共需要技能："+task.getSkills().size()+"项");
							for(Skill skil:task.getSkills()){
								System.out.println("skill编号"+skil.getNumber());
								System.out.println("该技能对应的值"+skil.getValue());
							}
							System.out.println("任务"+task.getNumber()+"成本为："+task.getCost());
						}
					}
					*/
					//验证通过
				
					task=null;
					number1=-2;
					
				}
				
				
				
				//------------------------------------------------------------------------------------------------------------------------------
				
				//将所有的职工极其相应的全部属性添加到名为“employees”的动态数组中
				//忽略了职工数量的解析，但可通过获取employees的长度获取
				//注意在employees中并没有按照职工的编号进行排序，只是按照生成文件中的顺序存放
				else if(words[0].equals("employee")){
					if(words[1].equals("number")){/*System.out.println("跳出本次循环");*/continue;}//人员的总数量
					else{
						number2=Integer.parseInt(words[1]);
						
						//判断该职工是否是一个新的职工（通过职工编号属性即可得知）
						//如果不是新职工，就从数组中取出相应的已经存在的职工
						//如果是新职工，就直接new一个职工Employee对象
						for(Employee employee:employees){
							if(employee.getNumber()==number2){
								this.employee=employee;
								break;
							}
						}
						if(employee==null){
							employee=new Employee();
						}
						
						employee.setNumber(number2);//职工编码
						
						//对该职工的技能属性进行操作
						if(words[2].equals("skill")){
							if(words[3].equals("number")){/*System.out.println("跳出本次循环");*/employee=null;number2=-2;continue;}//该职工所具备的技能数
							else{
								Skill skill=new Skill();
								skill.setNumber(Integer.parseInt(words[3]));
								skill.setValue(Integer.parseInt(words[4]));
								employee.getSkills().add(skill);
							}
						}
						
						//对该职工的薪酬属性进行操作
						else{
							employee.setSalary(Double.parseDouble(words[3]));
						}
						
						//如果职工已在数组中存在，那么替换原职工
						//如果职工不在数组中，那么就将该职工添加到数组中
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
				//将所有的弧极其相应的全部属性添加到名为“tpg”的动态数组中
				//忽略了弧数量的解析，但可通过获取tpg的长度获取
				//注意在tpg中并没有按照弧的编号进行排序，只是按照生成文件中的顺序存放
				else if(words[0].equals("graph")){
					if(words[2].equals("number")){/*System.out.println("跳出本次循环");*/continue;}
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
	
	//对获得三个动态数组进行排序
	public void sort(){
		Collections.sort(tasks, new Comparator<Task>(){
			public int compare(Task t1,Task t2){
				// TODO Auto-generated method stub
				return t1.getNumber()-t2.getNumber();
			}
		});//按照Task的number属性对tasks进行从小到大排序
		
		Collections.sort(employees, new Comparator<Employee>(){
			public int compare(Employee e1,Employee e2){
				// TODO Auto-generated method stub
				return e1.getNumber()-e2.getNumber();
			}
		});//按照Employee的number属性对employees进行从小到大排序
		
		Collections.sort(arcs, new Comparator<Arc>(){
			public int compare(Arc a1,Arc a2){
				// TODO Auto-generated method stub
				return a1.getNumber()-a2.getNumber();
			}
		});//按照Arc的number属性对arcs进行从小到大排序
		
		for(Task task:tasks){
			Collections.sort(task.getSkills(), new Comparator<Skill>(){
				public int compare(Skill s1,Skill s2){
					// TODO Auto-generated method stub
					return s1.getNumber()-s2.getNumber();
				}
			});//按照Skill的number属性对每一个Task和Employee中的skills数组进行从小到大排序
		}
		
		for(Employee employee:employees){
			Collections.sort(employee.getSkills(), new Comparator<Skill>(){
				public int compare(Skill s1,Skill s2){
					// TODO Auto-generated method stub
					return s1.getNumber()-s2.getNumber();
				}
			});//按照Skill的number属性对每一个Task和Employee中的skills数组进行从小到大排序
		}
	}

	//任务和员工的对应
	public void getEmployee_Task(){
		
		int logo=-1;
		
		// 将对某任务有贡献的员工挑选出来
		//即将能做某项任务的员工加入到该项任务中
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
		
		//对每个任务挑选后的员工按编号进行排序
		for(int i=0;i<tasks.size();i++){
			Collections.sort(tasks.get(i).getEmployees(), new Comparator<Employee>(){
				public int compare(Employee e1,Employee e2){
					// TODO Auto-generated method stub
					return e1.getNumber()-e2.getNumber();
				}
			});
		}
		
	}
	
	//获取每个任务所对应员工的数量
	public int[ ] getEmployeeNumber_Task(){
		employeeNumber_Task=new int[tasks.size()];
		for(int i=0;i<tasks.size();i++){
			employeeNumber_Task[i]=tasks.get(i).getEmployees().size();
			//System.out.println(employeeNumber_Task[i]);
		}
		return employeeNumber_Task;
	}
	
}
