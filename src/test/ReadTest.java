package test;
import init.ReadFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import beans.*;


public class ReadTest {
	
	private static  ArrayList<Task>tasks;
	private static ArrayList<Employee>employees;
	private static ArrayList<Arc>arcs;
	private static int [] employeeNumber_Task;
	
	public static void main(String [] args){
		ReadFile readFile=new ReadFile("D:\\windows","1.txt");
		readFile.init();
		readFile.sort();
		tasks=readFile.getTasks();
		employees=readFile.getEmployees();
		readFile.getEmployee_Task();
		employeeNumber_Task=readFile.getEmployeeNumber_Task();
		arcs=readFile.getArcs();
	
		/*for(int i:employeeNumber_Task){
			System.out.print(i);
		}
		System.out.println("");
		
		for(int i=0;i<tasks.size();i++){
			System.out.print(i+":");
			for(Employee employee:tasks.get(i).getEmployees()){
				System.out.print(employee.getNumber()+"\t");
			}
			System.out.print("共"+tasks.get(i).getEmployees().size()+"个");
			System.out.println("");
		}
		*/
		
		
		System.out.println("验证任务是否已经全部被正确存储！");
		for(Task task:tasks){
			System.out.println("任务"+task.getNumber()+"所需effort为："+task.getCost());
			System.out.println("任务"+task.getNumber()+"共需要技能"+task.getSkills().size());
			for(Skill ski:task.getSkills()){
				
				System.out.println("任务"+task.getNumber()+"所需技能"+ski.getNumber()+"的值为："+ski.getValue());
			}
			System.out.println("任务"+task.getNumber()+"共需要职工"+employeeNumber_Task[task.getNumber()]);
			for(Employee employee:task.getEmployees()){
				
				System.out.println("任务"+task.getNumber()+"所需员工的编号为"+employee.getNumber());
			}
			
			System.out.println("");
		}
		
		System.out.println("----------------------------------------------------------------------");
		
		
		
		
		System.out.println("----------------------------------------------------------------------");
		System.out.println("验证职工是否已经全部被正确存储！");
		for(Employee employee:employees){
			System.out.println("职工"+employee.getNumber()+"薪酬为："+employee.getSalary());
			System.out.println("职工"+employee.getNumber()+"所具备技能"+employee.getSkills().size());
			for(Skill ski:employee.getSkills()){
				
				System.out.println("职工"+employee.getNumber()+"具备技能"+ski.getNumber()+"的值为："+ski.getValue());
			}
			System.out.println("");
		}
		
		
		System.out.println("----------------------------------------------------------------------");
		System.out.println("验证弧是否已经全部被正确存储！");
		for(Arc arc:arcs){
			System.out.println("弧"+arc.getNumber()+"是由结点"+arc.getFirstNumber()+"指向"+arc.getLastNumber());
		}
		
		
		System.out.println("Test is over!");
	
	}
}
